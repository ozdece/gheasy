package com.ozdece.gheasy;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.auth.GhAuthService;
import com.ozdece.gheasy.github.auth.logic.GhAuthServiceImpl;
import com.ozdece.gheasy.github.repository.GithubRepositoryService;
import com.ozdece.gheasy.github.repository.logic.GithubRepositoryServiceImpl;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.image.logic.ImageServiceImpl;
import com.ozdece.gheasy.process.ProcessService;
import com.ozdece.gheasy.process.ProcessServiceImpl;
import com.ozdece.gheasy.ui.frames.FrmRepository;

import static io.vavr.API.Try;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

public class GheasyApplication {

    public static final String CONFIG_FOLDER_PATH = System.getProperty("user.home") + "/.gheasy";
    public static final String VERSION = Optional.ofNullable(GheasyApplication.class.getPackage().getImplementationVersion()).orElse("<unknown>");

    private static final ProcessService processService = new ProcessServiceImpl();
    private static final GhAuthService ghAuthService = new GhAuthServiceImpl(processService);
    private static final ImageService imageService = new ImageServiceImpl(processService);
    private static final GithubRepositoryService githubRepositoryService = new GithubRepositoryServiceImpl(processService);

    private static final ImmutableSet<String> MANDATORY_APPS_TO_BE_PRESENT = ImmutableSet.of("git", "gh");

    public static void main(String[] args) {
        //Set up the theme
        FlatDarkLaf.setup();
        final File configFolder = new File(CONFIG_FOLDER_PATH);

        if (!configFolder.exists()) {
            if (configFolder.mkdir()){
              //TODO: Replace this with logging
              System.out.printf("Gheasy config folder is created at %s%n", CONFIG_FOLDER_PATH);
            } else {
               System.err.println("An error occurred while creating the config folder of Gheasy. Exiting....");
               System.exit(-5);
            }
        }

        MANDATORY_APPS_TO_BE_PRESENT.forEach(app -> {
                    //TODO: Check programs for each operating system
                    final ProcessBuilder commandCheckProcessBuilder = new ProcessBuilder(ImmutableList.of("which", app));

                    final Integer commandExitCode = Try(() -> processService.getProcessExitCode(commandCheckProcessBuilder))
                            // It's okay to give non-zero number here as we'll accept only zero return codes
                            .getOrElse(-1);

                    if (commandExitCode != 0) {
                        JOptionPane.showMessageDialog(null,
                                String.format("%s seems not to be installed on your computer. Make sure that %s is installed and %s executable is accessible through shell.", app, app, app),
                                "Gheasy | Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.exit(-1);
                }});

        ghAuthService.getLoggedInUser()
                .doOnError(err -> {
                    JOptionPane.showMessageDialog(null,
                            "Gheasy cannot be used without gh being authorized.\n\nTo be able to use the app, please login your account using \"gh auth login\" in a shell.",
                            "Gheasy | Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                })
                .subscribe(githubUser -> {
                    final FrmRepository frmRepository = new FrmRepository(imageService, githubRepositoryService, githubUser);
                    frmRepository.updateGithubAvatar();
                    frmRepository.setVisible(true);
                });

    }
}