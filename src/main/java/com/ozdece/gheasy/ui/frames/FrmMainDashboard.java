package com.ozdece.gheasy.ui.frames;

import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.datetime.ZoneBasedDateTimeFormatter;
import com.ozdece.gheasy.github.auth.AuthService;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryStats;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.ui.DialogTitles;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.ResourceLoader;
import com.ozdece.gheasy.ui.SwingScheduler;
import com.ozdece.gheasy.ui.models.RepositoryTreeModel;
import com.ozdece.gheasy.ui.models.tree.GithubRepositoryTreeNode;
import com.ozdece.gheasy.ui.models.tree.RepositoryTreeNode;
import com.ozdece.gheasy.ui.models.tree.RepositoryTreeNodeLeaf;
import com.ozdece.gheasy.ui.models.tree.RepositoryTreeNodeType;
import com.ozdece.gheasy.ui.tabpanels.PullRequestPanel;
import com.ozdece.gheasy.ui.renderers.RepositoryTreeRenderer;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;

import static io.vavr.API.Tuple;

public class FrmMainDashboard extends JFrame {

    private final GithubUser githubUser;

    private final RepositoryService repositoryService;
    private final PullRequestService pullRequestService;
    private final ImageService imageService;
    private final AuthService authService;
    private final Config config;

    private final JTree trRepoNavigator = new JTree();

    private final JLabel lblLastSyncTime = new JLabel("Last Sync Time: xxx");
    private final JLabel lblLastRelease = new JLabel();
    private final JLabel lblLicense = new JLabel();
    private final JLabel lblRepoStars = new JLabel();
    private final JLabel lblGithubUser = new JLabel();
    private final JLabel lblPrimaryLanguage = new JLabel();
    private final JLabel lblOwnerWithName = new JLabel("<repository>");

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private static final Logger logger = LoggerFactory.getLogger(FrmMainDashboard.class);

    public FrmMainDashboard(
            GithubUser githubUser,
            RepositoryService repositoryService,
            PullRequestService pullRequestService,
            ImageService imageService,
            AuthService authService,
            Config config
    ) {
        super(String.format("Gheasy | Dashboard, User: %s", githubUser.fullName().orElse(githubUser.username())));

        setBounds(250, 250, 1350, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.githubUser = githubUser;
        this.repositoryService = repositoryService;
        this.pullRequestService = pullRequestService;
        this.imageService = imageService;
        this.authService = authService;
        this.config = config;

        setLayout(new BorderLayout());
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCentralPanel(), BorderLayout.CENTER);

        setJMenuBar(buildMenuBar());

        updateGithubAvatar();
        loadNavigatorTreeModel();
    }

    private JComponent buildCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);


        final String githubUserFullNameText = githubUser.fullName()
                .map(fullName -> "%s (%s)".formatted(fullName, githubUser.username()))
                .orElse(githubUser.username());

        tabbedPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        lblOwnerWithName.setFont(Fonts.boldFontWithSize(20));

        lblGithubUser.setText(githubUserFullNameText);

        lblGithubUser.setFont(Fonts.withSize(14));
        lblPrimaryLanguage.setFont(Fonts.withSize(14));
        lblLastRelease.setFont(Fonts.withSize(14));
        lblLicense.setFont(Fonts.withSize(14));
        lblRepoStars.setFont(Fonts.withSize(14));

        final JToolBar tbBottomBar = new JToolBar();
        tbBottomBar.setLayout(new FlowLayout(FlowLayout.LEADING));

        ResourceLoader.loadImage("images/star-icon.png")
                .map(ImageIcon::new)
                .ifPresent(lblRepoStars::setIcon);

        tbBottomBar.add(lblLastSyncTime);

        setRepositoryLabelsVisible(false);

        trRepoNavigator.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2)
                    return;

                final TreePath maybePath = trRepoNavigator.getPathForLocation(e.getX(), e.getY());

                Optional.ofNullable(maybePath)
                        .ifPresent(path -> {
                            final GithubRepositoryTreeNode node = (GithubRepositoryTreeNode) path.getLastPathComponent();
                            loadTab(node);
                        });
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblOwnerWithName)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lblGithubUser)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblPrimaryLanguage)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblLastRelease)
                                        .addGap(14)
                                        .addComponent(lblRepoStars)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lblLicense)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(tabbedPane)
                                        .addGap(10)
                        )
                        .addComponent(tbBottomBar)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblOwnerWithName)
                                        .addComponent(lblGithubUser)
                        )
                        .addGap(5)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblLastRelease)
                                        .addComponent(lblRepoStars)
                                        .addComponent(lblLicense)
                        )
                        .addGap(5)
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblPrimaryLanguage)
                        )
                        .addGap(5)
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(tabbedPane, 500, 500, Integer.MAX_VALUE)
                                        .addGap(10)
                        )
                        .addComponent(tbBottomBar)
        );

        centralPanel.setLayout(groupLayout);
        return centralPanel;
    }

    private void setRepositoryLabelsVisible(boolean visible) {
        lblPrimaryLanguage.setVisible(visible);
        lblOwnerWithName.setVisible(visible);
        lblLicense.setVisible(visible);
        lblLastRelease.setVisible(visible);
        lblRepoStars.setVisible(visible);
    }

    private JComponent buildLeftPanel() {
        final JPanel leftPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(leftPanel);

        final JScrollPane spRepoNavigator = new JScrollPane(trRepoNavigator);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addComponent(spRepoNavigator, 250, 250, Integer.MAX_VALUE)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(spRepoNavigator, 10, 10, Integer.MAX_VALUE)
                        .addGap(2)
        );

        leftPanel.setLayout(groupLayout);
        return leftPanel;
    }

    private JMenuBar buildMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        final JMenu repositoryMenu = new JMenu("Repository");
        final JMenuItem addRepositoryMenuItem = new JMenuItem("Add");

        final JMenu pullRequestsMenu = new JMenu("Pull Requests");
        final JMenu issuesMenu = new JMenu("Issues");
        final JMenu helpMenu = new JMenu("Help");

        addRepositoryMenuItem.addActionListener(e -> {
            final DlgAddRepository dlgAddRepository = new DlgAddRepository(
                    this,
                    repositoryService,
                    authService,
                    imageService,
                    config
            );

            dlgAddRepository.addRepositoryListener(addedRepo -> this.loadNavigatorTreeModel());
            dlgAddRepository.setVisible(true);
        });

        repositoryMenu.add(addRepositoryMenuItem);

        menuBar.add(repositoryMenu);
        menuBar.add(pullRequestsMenu);
        menuBar.add(issuesMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void updateGithubAvatar() {
        final int avatarSize = config.getInt("gheasy.images.avatar-scaled-image-size");;

        imageService.saveImage(githubUser.avatarUrl(), avatarSize, avatarSize, "%s_user_avatar.png".formatted(githubUser.username()))
                .doOnError(err -> logger.error("An error occurred while saving Github avatar!", err))
                .publishOn(SwingScheduler.edt())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(maybeImageIcon ->
                        maybeImageIcon
                                .ifPresent(imageFile -> lblGithubUser.setIcon(new ImageIcon(imageFile.getAbsolutePath())))
                );
    }

    private void loadNavigatorTreeModel() {
        repositoryService.getBookmarkedRepositories()
                .publishOn(SwingScheduler.edt())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(githubRepositories -> {
                    final RepositoryTreeModel model = new RepositoryTreeModel(githubRepositories);
                    setupRepositoryStats(githubRepositories, model);
                    trRepoNavigator.setModel(model);
                    trRepoNavigator.setCellRenderer(new RepositoryTreeRenderer(imageService));
                });
    }

    private void setupRepositoryStats(ImmutableSet<Repository> repositories, RepositoryTreeModel repositoryTreeModel) {
        Flux.fromIterable(repositories)
                .flatMap(repo ->
                        repositoryService.getRepositoryStats(repo)
                                .map(stats -> Tuple(repo, stats))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(statsTuple ->
                        repositoryTreeModel.updateRepositoryStats(trRepoNavigator, statsTuple._1, statsTuple._2));
    }

    private void loadTab(GithubRepositoryTreeNode node) {
       switch (node) {
           case RepositoryTreeNodeLeaf leaf
                   when leaf.getType() == RepositoryTreeNodeType.PULL_REQUEST -> loadRepositoryPullRequestsTab(leaf);
           default -> {}
       }
    }

    private void loadRepositoryPullRequestsTab(RepositoryTreeNodeLeaf leaf) {
        final RepositoryStats repositoryStats = leaf.repositoryTreeNode().getRepositoryStats();
        final Repository repository = leaf.repositoryTreeNode().getGithubRepository();

        final PullRequestPanel pullRequestPanel = new PullRequestPanel(pullRequestService, repository, repositoryStats);

        repositoryService.getRepositoryMetadata(repository)
                .doOnError(err -> {
                    logger.error("Unable to retrieve repository metadata for the repository {}", repository.name(), err);
                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to retrieve repository metatada for the repository %s/%s\n%s"
                                    .formatted(
                                            repository.owner().name(),
                                            repository.name(),
                                            err.getMessage()
                                    ),
                            DialogTitles.OPTION_PANE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                })
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(SwingScheduler.edt())
                .subscribe(metadata -> {
                    tabbedPane.add("%s/%s Pull Requests".formatted(repository.owner().name(), repository.name()), pullRequestPanel);

                    lblOwnerWithName.setText("%s/%s".formatted(repository.owner().name(), repository.name()));
                    imageService.getImageFile("%s.png".formatted(repository.owner().name()))
                                    .ifPresent(imageFile -> lblOwnerWithName.setIcon(new ImageIcon(imageFile.getAbsolutePath())));

                    lblRepoStars.setText("%d stars".formatted(metadata.starCount()));
                    metadata.license()
                            .ifPresent(license -> lblLicense.setText("License: %s".formatted(license)));
                    metadata.latestRelease()
                            .ifPresent(latestRelease -> {
                                final String latestReleaseDate = ZoneBasedDateTimeFormatter.toFormattedString(latestRelease.publishedAt());
                                lblLastRelease.setText("Latest Release: %s released at %s".formatted(latestRelease.name(), latestReleaseDate));
                            });
                    lblPrimaryLanguage.setText("Primary Language: %s".formatted(repository.primaryLanguage()));

                    setRepositoryLabelsVisible(true);

                    if (metadata.latestRelease().isEmpty()) {
                        lblLastRelease.setVisible(false);
                    }

                    if (metadata.license().isEmpty()) {
                        lblLicense.setVisible(false);
                    }

                });

    }

}
