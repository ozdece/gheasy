package com.ozdece.gheasy.ui.frames;

import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestStatus;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryStats;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.ResourceLoader;
import com.ozdece.gheasy.ui.SwingScheduler;
import com.ozdece.gheasy.ui.models.GithubRepositoryTreeModel;
import com.ozdece.gheasy.ui.renderers.GithubRepositoryTreeRenderer;
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
            ImageService imageService
    ) {
        super(String.format("Gheasy | Dashboard, User: %s", githubUser.fullName().orElse(githubUser.username())));

        setBounds(250, 250, 1350, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.githubUser = githubUser;
        this.repositoryService = repositoryService;
        this.pullRequestService = pullRequestService;
        this.imageService = imageService;

        setLayout(new BorderLayout());
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCentralPanel(), BorderLayout.CENTER);

        loadDashboardData();
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

        final JComponent pullRequestPanel = buildPullRequestPanel();

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
                        //.addGroup(
                        //        groupLayout.createSequentialGroup()
                        //                .addGap(10)
                        //                .addComponent(pullRequestPanel)
                        //                .addGap(10)
                        //)
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
                        //.addGap(5)
                        //.addComponent(pullRequestPanel)
                        //.addGap(5)
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

    private void updateGithubAvatar() {
        imageService.saveGitHubAvatar(githubUser.avatarUrl())
                .doOnError(err -> logger.error("An error occurred while saving Github avatar!", err))
                .publishOn(SwingScheduler.edt())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(maybeImageIcon -> maybeImageIcon.ifPresent(lblGithubUser::setIcon));
    }

    private void loadNavigatorTreeModel() {
        repositoryService.getBookmarkedRepositories()
                .publishOn(SwingScheduler.edt())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(githubRepositories -> {
                    final GithubRepositoryTreeModel model = new GithubRepositoryTreeModel(githubRepositories);
                    trRepoNavigator.setModel(model);
                    trRepoNavigator.setCellRenderer(new GithubRepositoryTreeRenderer());

                    final Repository repository = githubRepositories.stream().findAny().get();

                    model.updateRepositoryStats(trRepoNavigator, repository, new RepositoryStats(1, 5));
                });
    }

    private void loadDashboardData() {
    //    final File repoDirectory = new File(githubRepository.directoryPath());
    //    repositoryService
    //            .getRepositoryMetadata(repoDirectory)
    //            .publishOn(SwingScheduler.edt())
    //            .subscribeOn(Schedulers.boundedElastic())
    //            .subscribe(metadata -> {
    //                lblBranch.setText(String.format("Active Branch: %s", metadata.currentBranch()));
    //                //TODO: Make last release label a HyperLink component

    //                metadata.latestRelease().ifPresent(latestRelease -> {
    //                    lblLastRelease.setText(String.format("Last Release: %s on %s",
    //                            latestRelease.name(),
    //                            ZoneBasedDateTimeFormatter.toFormattedString(latestRelease.publishedAt())));

    //                    ResourceLoader.loadImage("images/release-icon.png")
    //                            .map(ImageIcon::new)
    //                            .ifPresent(lblLastRelease::setIcon);
    //                });

    //                metadata.license().ifPresent(license -> {
    //                    lblLicense.setText(String.format("License: %s", license));
    //                });

    //                lblRepoStars.setText(String.format("%d stars", metadata.starCount()));
    //            });

    //    pullRequestService.getPullRequests(repoDirectory)
    //            .subscribeOn(Schedulers.boundedElastic())
    //            .publishOn(SwingScheduler.edt())
    //            .subscribe(pullRequests -> {
    //                tblPullRequests.setModel(new PullRequestsTableModel(pullRequests));
    //            });
    }

}
