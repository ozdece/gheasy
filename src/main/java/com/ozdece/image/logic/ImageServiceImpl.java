package com.ozdece.image.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.GheasyApplication;
import com.ozdece.image.ImageService;
import com.ozdece.process.ProcessService;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import static io.vavr.API.*;

public class ImageServiceImpl implements ImageService {

    private final ProcessService processService;

    //TODO: Read this from a config file
    private final int SCALED_IMAGE_SIZE = 20;

    private final String GITHUB_AVATAR_PATH = GheasyApplication.CONFIG_FOLDER_PATH + "/github_avatar.png";

    public ImageServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<Optional<ImageIcon>> saveGitHubAvatar(String avatarUrl) {
        return Mono.fromCallable(() -> {
                    final ImmutableList<String> ffmpegDownloadCommand = ImmutableList.of(
                            "ffmpeg",
                            "-y",
                            "-i",
                            avatarUrl,
                            "-vf",
                            String.format("scale=%d:%d", SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE),
                            GITHUB_AVATAR_PATH
                    );
                    final ProcessBuilder processBuilder = new ProcessBuilder(ffmpegDownloadCommand);

                    return processService.getProcessExitCode(processBuilder);
                })
                .map(exitCode -> exitCode == 0 ? Optional.of(new ImageIcon(GITHUB_AVATAR_PATH)) : Optional.empty());
    }

}
