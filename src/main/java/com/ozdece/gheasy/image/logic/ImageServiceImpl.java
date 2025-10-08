package com.ozdece.gheasy.image.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.util.Optional;

import static com.ozdece.gheasy.GheasyApplication.IMAGES_FOLDER_PATH;

public class ImageServiceImpl implements ImageService {

    private final ProcessService processService;

    public ImageServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<Optional<File>> saveImage(String avatarUrl, int width, int height) {
        return Mono.fromCallable(() -> {
                    final String fileName = new URI(avatarUrl).toURL().getFile()
                            .replaceAll("[^a-zA-Z0-9]+", "") + ".png";
                    final String fileOutputPath = IMAGES_FOLDER_PATH + File.separator + fileName;

                    final ProcessBuilder processBuilder = generateImageProcessor(avatarUrl, width, height, fileOutputPath);

                    return new SaveImageResult(fileOutputPath, processService.getProcessExitCode(processBuilder));
                })
                .map(result -> result.exitCode() == 0 ? Optional.of(new File(result.outputPath())) : Optional.empty());
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
