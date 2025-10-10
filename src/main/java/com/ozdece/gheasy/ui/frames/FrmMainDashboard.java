package com.ozdece.gheasy.ui.frames;

import com.ozdece.gheasy.github.auth.AuthService;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestStatus;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.ResourceLoader;
import com.ozdece.gheasy.ui.SwingScheduler;
import com.ozdece.gheasy.ui.models.GithubRepositoryTreeModel;
import com.ozdece.gheasy.ui.renderers.RepositoryTreeRenderer;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import java.awt.*;

public class FrmMainDashboard extends JFrame {

    private final GithubUser githubUser;

    private final RepositoryService repositoryService;
    private final PullRequestService pullRequestService;
    private final ImageService imageService;
    private final AuthService authService;
    private final Config config;

    private final JTree trRepoNavigator = new JTree();

    private final JComboBox<Repository> cmbGithubRepositories = new JComboBox<>();
    private final JComboBox<PullRequestStatus> chbActivePassivePRs = new JComboBox<>();
    private final JComboBox<String> chbPullRequestLabels = new JComboBox<>();

    private final JLabel lblPullRequestCount = new JLabel("0");
    private final JLabel lblBranch = new JLabel("<branch_name>");
    private final JLabel lblLastSyncTime = new JLabel("Last Sync Time: xxx");
    private final JLabel lblLastRelease = new JLabel();
    private final JLabel lblLicense = new JLabel();
    private final JLabel lblRepoStars = new JLabel();
    private final JLabel lblGithubUser = new JLabel();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JTextField txtSearchPullRequest = new JTextField();

    private final JTable tblPullRequests = new JTable();

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

        final JLabel lblOwnerWithName = new JLabel("<repository>");

        final String githubUserFullNameText = githubUser.fullName()
                .map(fullName -> "%s (%s)".formatted(fullName, githubUser.username()))
                .orElse(githubUser.username());

        tabbedPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        lblOwnerWithName.setFont(Fonts.boldFontWithSize(20));

        lblGithubUser.setText(githubUserFullNameText);
        lblGithubUser.setFont(Fonts.withSize(14));

        lblBranch.setFont(Fonts.withSize(14));
        lblBranch.setForeground(Color.YELLOW.darker());

        final JLabel lblPrimaryLanguage = new JLabel("Primary Language");
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
                                        .addGap(14)
                                        .addComponent(lblBranch)
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
                                        .addComponent(lblBranch)
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

    private JComponent buildPullRequestPanel() {
        final JPanel pullRequestPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(pullRequestPanel);

        pullRequestPanel.setBorder(BorderFactory.createTitledBorder("Pull Requests"));

        final JLabel lblPullRequests = new JLabel("Pull Requests: ");
        final JButton btnViewAll = new JButton("View All on GitHub");
        final JLabel lblSearchPullRequest = new JLabel("Search: ");
        final JLabel lblActivePassivePRs = new JLabel("Active/Completed PRs: ");
        final JLabel lblPullRequestLabels = new JLabel("Labels: ");
        final JScrollPane spPullRequests = new JScrollPane(tblPullRequests);

        lblPullRequests.setFont(Fonts.withSize(14));
        lblPullRequestCount.setFont(Fonts.withSize(14));

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblPullRequests)
                                        .addComponent(lblPullRequestCount)
                                        .addGap(50)
                                        .addComponent(btnViewAll)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblSearchPullRequest)
                                        .addComponent(txtSearchPullRequest, 200, 200, Short.MAX_VALUE)
                                        .addGap(15)
                                        .addComponent(lblActivePassivePRs)
                                        .addComponent(chbActivePassivePRs, 100, 100, 100)
                                        .addGap(15)
                                        .addComponent(lblPullRequestLabels)
                                        .addComponent(chbPullRequestLabels, 100, 100, 100)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(spPullRequests)
                                        .addGap(10)
                        )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(5)
                        .addGroup(
                                groupLayout
                                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblPullRequests)
                                        .addComponent(lblPullRequestCount)
                                        .addComponent(btnViewAll)
                        )
                        .addGap(10)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblSearchPullRequest)
                                        .addComponent(txtSearchPullRequest,24, 24, 24)
                                        .addComponent(lblActivePassivePRs)
                                        .addComponent(chbActivePassivePRs, 24, 24, 24)
                                        .addComponent(lblPullRequestLabels)
                                        .addComponent(chbPullRequestLabels, 24, 24, 24)
                        )
                        .addGap(7)
                        .addComponent(spPullRequests)
                        .addGap(5)
        );

        pullRequestPanel.setLayout(groupLayout);
        return pullRequestPanel;
    }

    private JComponent buildLeftPanel() {
        final JPanel leftPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(leftPanel);

        final JScrollPane spRepoNavigator = new JScrollPane(trRepoNavigator);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addComponent(spRepoNavigator, 250, 250, Integer.MAX_VALUE)
                        .addComponent(cmbGithubRepositories)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(spRepoNavigator, 10, 10, Integer.MAX_VALUE)
                        .addGap(3)
                        .addComponent(cmbGithubRepositories, 30, 30, 30)
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
                    final GithubRepositoryTreeModel model = new GithubRepositoryTreeModel(githubRepositories);
                    trRepoNavigator.setModel(model);
                    trRepoNavigator.setCellRenderer(new RepositoryTreeRenderer());
                });
    }
}
