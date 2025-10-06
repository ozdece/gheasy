package com.ozdece.gheasy;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.auth.GhAuthService;
import com.ozdece.gheasy.github.auth.logic.GhAuthServiceImpl;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.logic.PullRequestServiceImpl;
import com.ozdece.gheasy.github.repository.GithubRepositoryService;
import com.ozdece.gheasy.github.repository.logic.GithubRepositoryServiceImpl;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.image.logic.ImageServiceImpl;
import com.ozdece.gheasy.process.ProcessService;
import com.ozdece.gheasy.process.ProcessServiceImpl;
import com.ozdece.gheasy.ui.frames.FrmMainDashboard;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ozdece.gheasy.ui.DialogTitles.OPTION_PANE_ERROR_TITLE;
import static io.vavr.API.Try;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

public class GheasyApplication {

    private static final Logger logger = LoggerFactory.getLogger(GheasyApplication.class);

    public static final String CONFIG_FOLDER_PATH = System.getProperty("user.home") + "/.gheasy";
    public static final String VERSION = Optional.ofNullable(GheasyApplication.class.getPackage().getImplementationVersion()).orElse("<unknown>");

    private static final Config appConfig = ConfigFactory.load();

    private static final ProcessService processService = new ProcessServiceImpl();
    private static final GhAuthService ghAuthService = new GhAuthServiceImpl(processService);
    private static final ImageService imageService = new ImageServiceImpl(processService, appConfig);
    private static final GithubRepositoryService githubRepositoryService = new GithubRepositoryServiceImpl(processService, GheasyApplication.CONFIG_FOLDER_PATH);
    private static final PullRequestService pullRequestService = new PullRequestServiceImpl(processService);

    private static final ImmutableSet<String> MANDATORY_APPS_TO_BE_PRESENT = ImmutableSet.of("git", "gh");

    public static void main(String[] args) {
        //Set up the theme
        FlatLightLaf.setup();

        final File configFolder = new File(CONFIG_FOLDER_PATH);

        if (!configFolder.exists()) {
            if (configFolder.mkdir()){
                logger.info("Gheasy config folder is created at {}", CONFIG_FOLDER_PATH);
            } else {
                logger.error("An error occurred while creating the config folder of Gheasy. Exiting....");
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
                                OPTION_PANE_ERROR_TITLE,
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.exit(-1);
                }});

        ghAuthService.getLoggedInUser()
                .doOnError(err -> {
                    JOptionPane.showMessageDialog(null,
                            "Gheasy cannot be used without gh being authorized.\n\nTo be able to use the app, please login your account using \"gh auth login\" in a shell.",
                            OPTION_PANE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                })
                .subscribe(githubUser -> {
                    final FrmMainDashboard frmMainDashboard = new FrmMainDashboard(githubUser, githubRepositoryService, pullRequestService, imageService);
                    frmMainDashboard.setVisible(true);
                });

    }
}