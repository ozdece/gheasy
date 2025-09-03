package com.ozdece.gheasy.ui.frames;

import com.ozdece.gheasy.datetime.ZoneBasedDateTimeFormatter;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.issues.model.IssueStatus;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestStatus;
import com.ozdece.gheasy.github.repository.GithubRepositoryService;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.ResourceLoader;
import com.ozdece.gheasy.ui.SwingScheduler;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FrmMainDashboard extends JFrame {

    private final GithubUser user;
    private final GithubRepository githubRepository;

    private final GithubRepositoryService githubRepositoryService;

    private final JTree trRepoNavigator = new JTree();

    private final JComboBox<GithubRepository> cmbGithubRepositories = new JComboBox<>();
    private final JComboBox<PullRequestStatus> chbActivePassivePRs = new JComboBox<>();
    private final JComboBox<IssueStatus> chbIssueStatus = new JComboBox<>();
    private final JComboBox<String> chbPullRequestLabels = new JComboBox<>();
    private final JComboBox<String> chbIssueLabels = new JComboBox<>();

    private final JLabel lblPullRequestCount = new JLabel("0");
    private final JLabel lblIssuesCount = new JLabel("0");
    private final JLabel lblBranch = new JLabel("<branch_name>");
    private final JLabel lblLastSyncTime = new JLabel("Last Sync Time: xxx");
    private final JLabel lblLastRelease = new JLabel();
    private final JLabel lblLicense = new JLabel();
    private final JLabel lblRepoStars = new JLabel();

    private final JTextField txtSearchPullRequest = new JTextField();
    private final JTextField txtSearchIssues = new JTextField();

    private final JTable tblPullRequests = new JTable();
    private final JTable tblIssues = new JTable();


    public FrmMainDashboard(GithubUser user, GithubRepository githubRepository, GithubRepositoryService githubRepositoryService) {
        super(String.format("Gheasy | %s Dashboard, User: %s", githubRepository.nameWithOwner(), user.fullName().orElse(user.username())));

        setBounds(250, 250, 1350, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.user = user;
        this.githubRepository = githubRepository;
        this.githubRepositoryService = githubRepositoryService;

        setLayout(new BorderLayout());
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCentralPanel(), BorderLayout.CENTER);

        loadDashboardData();
    }

    private JComponent buildCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);

        final JLabel lblOwnerWithName = new JLabel(githubRepository.nameWithOwner());

        lblOwnerWithName.setFont(Fonts.boldFontWithSize(20));

        lblBranch.setFont(Fonts.withSize(14));
        lblBranch.setForeground(Color.YELLOW.darker());

        final JLabel lblPrimaryLanguage = new JLabel(githubRepository.primaryLanguage().name());
        lblPrimaryLanguage.setFont(Fonts.withSize(14));

        lblLastRelease.setFont(Fonts.withSize(14));
        lblLicense.setFont(Fonts.withSize(14));
        lblRepoStars.setFont(Fonts.withSize(14));

        ResourceLoader.loadImage("images/star-icon.png")
                .map(ImageIcon::new)
                .ifPresent(lblRepoStars::setIcon);

        ResourceLoader.loadImage("images/release-icon.png")
                .map(ImageIcon::new)
                .ifPresent(lblLastRelease::setIcon);

        final JComponent pullRequestPanel = buildPullRequestPanel();
        final JComponent issuesPanel = buildIssuesPanel();

        final JToolBar tbBottomBar = new JToolBar();
        tbBottomBar.setLayout(new FlowLayout(FlowLayout.LEADING));

        tbBottomBar.add(lblLastSyncTime);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblOwnerWithName)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lblPrimaryLanguage)
                                        .addGap(10)
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
                                        .addComponent(pullRequestPanel)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(issuesPanel)
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
                                        .addComponent(lblPrimaryLanguage)
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
                        .addComponent(pullRequestPanel)
                        .addGap(15)
                        .addComponent(issuesPanel)
                        .addGap(5)
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

    private JComponent buildIssuesPanel() {
        final JPanel issuesPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(issuesPanel);

        issuesPanel.setBorder(BorderFactory.createTitledBorder("Issues"));

        final JLabel lblIssues = new JLabel("Issues: ");
        final JButton btnViewAll = new JButton("View All on GitHub");
        final JLabel lblSearchIssues = new JLabel("Search: ");
        final JLabel lblIssueStatus = new JLabel("Issue Status: ");
        final JLabel lblIssueLabels = new JLabel("Labels: ");
        final JScrollPane spIssues = new JScrollPane(tblIssues);

        lblIssues.setFont(Fonts.withSize(14));
        lblIssuesCount.setFont(Fonts.withSize(14));

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblIssues)
                                        .addComponent(lblIssuesCount)
                                        .addGap(50)
                                        .addComponent(btnViewAll)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblSearchIssues)
                                        .addComponent(txtSearchIssues, 200, 200, Short.MAX_VALUE)
                                        .addGap(15)
                                        .addComponent(lblIssueStatus)
                                        .addComponent(chbIssueStatus, 100, 100, 100)
                                        .addGap(15)
                                        .addComponent(lblIssueLabels)
                                        .addComponent(chbIssueLabels, 100, 100, 100)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(spIssues)
                                        .addGap(10)
                        )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(5)
                        .addGroup(
                                groupLayout
                                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblIssues)
                                        .addComponent(lblIssuesCount)
                                        .addComponent(btnViewAll)
                        )
                        .addGap(10)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblSearchIssues)
                                        .addComponent(txtSearchIssues,24, 24, 24)
                                        .addComponent(lblIssueStatus)
                                        .addComponent(chbIssueStatus, 24, 24, 24)
                                        .addComponent(lblIssueLabels)
                                        .addComponent(chbIssueLabels, 24, 24, 24)
                        )
                        .addGap(7)
                        .addComponent(spIssues)
                        .addGap(5)
        );

        issuesPanel.setLayout(groupLayout);
        return issuesPanel;
    }

    private JComponent buildLeftPanel() {
        final JPanel leftPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(leftPanel);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addComponent(trRepoNavigator, 250, 250, Integer.MAX_VALUE)
                        .addComponent(cmbGithubRepositories)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(trRepoNavigator, 10, 10, Integer.MAX_VALUE)
                        .addGap(3)
                        .addComponent(cmbGithubRepositories, 30, 30, 30)
                        .addGap(2)
        );

        leftPanel.setLayout(groupLayout);
        return leftPanel;
    }

    private void loadDashboardData() {
        final File repoDirectory = new File(githubRepository.directoryPath());
        githubRepositoryService
                .getRepositoryMetadata(repoDirectory)
                .publishOn(SwingScheduler.edt())
                .subscribe(metadata -> {
                    lblBranch.setText(String.format("Active Branch: %s", metadata.currentBranch()));
                    //TODO: Make last release label a HyperLink component
                    lblLastRelease.setText(String.format("Last Release: %s on %s",
                            metadata.latestRelease().name(),
                            ZoneBasedDateTimeFormatter.toFormattedString(metadata.latestRelease().publishedAt())));
                    lblLicense.setText(String.format("License: %s", metadata.license()));
                    lblRepoStars.setText(String.format("%d stars", metadata.starCount()));
                });
    }

}
