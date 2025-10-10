package com.ozdece.gheasy.image.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static com.ozdece.gheasy.GheasyApplication.IMAGES_FOLDER_PATH;

public class ImageServiceImpl implements ImageService {

    private final ProcessService processService;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<Optional<File>> saveImage(String avatarUrl, int width, int height, String filename) {
        return Mono.fromCallable(() -> {
                    final String fileOutputPath = IMAGES_FOLDER_PATH + File.separator + filename;
                    final File outputFile = new File(fileOutputPath);

                    if (outputFile.exists()) {
                        final BasicFileAttributes attributes = Files.readAttributes(outputFile.toPath(), BasicFileAttributes.class);
                        final LocalDateTime fileCreatedAt = LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                        if (fileCreatedAt.plusDays(1).isAfter(LocalDateTime.now())) {
                            logger.debug("Skipped downloading image \"{}\" as it was downloaded before.", avatarUrl);
                            return new SaveImageResult(fileOutputPath, 0);
                        }
                    }

                    final ProcessBuilder processBuilder = generateImageProcessor(avatarUrl, width, height, fileOutputPath);
                    return new SaveImageResult(fileOutputPath, processService.getProcessExitCode(processBuilder));
                })
                .map(result -> result.exitCode() == 0 ? Optional.of(new File(result.outputPath())) : Optional.empty());
    }

    @Override
    public Optional<File> getImageFile(String filename) {
        final String imageFilePath = IMAGES_FOLDER_PATH + File.separator + filename;

        return Optional.of(new File(imageFilePath))
                .filter(File::exists);
    }

    private ProcessBuilder generateImageProcessor(String avatarUrl, int width, int height, String fileOutputPath) {
        final ImmutableList<String> ffmpegDownloadCommand = ImmutableList.of(
                "ffmpeg",
                "-y",
                "-i",
                avatarUrl,
                "-vf",
                String.format("scale=%d:%d", width, height),
                fileOutputPath
        );
        return new ProcessBuilder(ffmpegDownloadCommand);
    }

}

record SaveImageResult(String outputPath, int exitCode) {}
