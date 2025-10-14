package com.ozdece.gheasy;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.auth.AuthService;
import com.ozdece.gheasy.github.auth.logic.AuthServiceImpl;
import com.ozdece.gheasy.github.organization.OrganizationService;
import com.ozdece.gheasy.github.organization.logic.OrganizationServiceImpl;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.logic.PullRequestServiceImpl;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.logic.RepositoryServiceImpl;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.image.logic.ImageServiceImpl;
import com.ozdece.gheasy.notification.NotificationService;
import com.ozdece.gheasy.notification.logic.NotifySendNotificationService;
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
    public static final String IMAGES_FOLDER_PATH = System.getProperty("user.home") + "/.gheasy/images";
    public static final String VERSION = Optional.ofNullable(GheasyApplication.class.getPackage().getImplementationVersion()).orElse("<unknown>");

    private static final Config appConfig = ConfigFactory.load();

    private static final ProcessService processService = new ProcessServiceImpl();
    private static final OrganizationService organizationService = new OrganizationServiceImpl(processService);
    private static final AuthService authService = new AuthServiceImpl(processService, organizationService);
    private static final ImageService imageService = new ImageServiceImpl(processService);
    private static final PullRequestService pullRequestService = new PullRequestServiceImpl(processService);
    private static final RepositoryService repositoryService = new RepositoryServiceImpl(processService, pullRequestService, GheasyApplication.CONFIG_FOLDER_PATH);
    private static final NotificationService notificationService = new NotifySendNotificationService(processService);

    private static final ImmutableSet<String> MANDATORY_APPS_TO_BE_PRESENT = ImmutableSet.of("git", "gh");

    public static void main(String[] args) {
        //Set up the theme
        FlatDarkLaf.setup();

        ImmutableSet.of(CONFIG_FOLDER_PATH, IMAGES_FOLDER_PATH)
                .forEach(path -> {
                    final File configFolder = new File(path);
                    if (!configFolder.exists()) {
                        if (configFolder.mkdir()){
                            logger.info("Gheasy config folder is created at {}", path);
                        } else {
                            logger.error("An error occurred while creating the config folder of Gheasy. Exiting....");
                            System.exit(-5);
                        }
                    }
                });


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

        authService.getLoggedInUser()
                .doOnError(err -> {
                    JOptionPane.showMessageDialog(null,
                            "Gheasy cannot be used without gh being authorized.\n\nTo be able to use the app, please login your account using \"gh auth login\" in a shell.",
                            OPTION_PANE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                })
                .subscribe(githubUser -> {
                    final FrmMainDashboard frmMainDashboard = new FrmMainDashboard(githubUser, repositoryService, pullRequestService, imageService, authService, appConfig);
                    frmMainDashboard.setVisible(true);
                });

    }
}