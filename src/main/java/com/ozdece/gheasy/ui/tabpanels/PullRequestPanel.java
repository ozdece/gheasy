package com.ozdece.gheasy.ui.tabpanels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestLabel;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestType;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryStats;
import com.ozdece.gheasy.ui.DialogTitles;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.SwingScheduler;
import com.ozdece.gheasy.ui.models.PullRequestLabelComboBoxModel;
import com.ozdece.gheasy.ui.models.PullRequestsTableModel;
import com.ozdece.gheasy.ui.renderers.PullRequestLabelListCellRenderer;
import com.ozdece.gheasy.ui.renderers.PullRequestsTableCellRenderer;
import com.ozdece.gheasy.web.URLBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

public class PullRequestPanel extends JPanel implements TabPanel {

    private final PullRequestService pullRequestService;

    private final JTable tblPullRequests = new JTable();

    private final JLabel lblPullRequestCount = new JLabel("0");

    private final JTextField txtSearchPullRequest = new JTextField();

    private final JComboBox<PullRequestType> cmbActivePassivePRs = new JComboBox<>();
    private final JComboBox<PullRequestLabel> cmbPullRequestLabels = new JComboBox<>();

    private final Repository repository;
    private final RepositoryStats repositoryStats;

    private static final Logger logger = LoggerFactory.getLogger(PullRequestPanel.class);

    public PullRequestPanel(PullRequestService pullRequestService, Repository repository, RepositoryStats repositoryStats) {
        this.pullRequestService = pullRequestService;
        this.repository = repository;
        this.repositoryStats = repositoryStats;

        setupPanel();
        loadPullRequests();
    }

    private void setupPanel() {

        final GroupLayout groupLayout = new GroupLayout(this);

        final JLabel lblPullRequests = new JLabel("Pull Requests: ");
        final JButton btnViewAll = new JButton("View All on GitHub");
        final JLabel lblSearchPullRequest = new JLabel("Search: ");
        final JLabel lblActivePassivePRs = new JLabel("Active/Draft PRs: ");
        final JLabel lblPullRequestLabels = new JLabel("Labels: ");
        final JScrollPane spPullRequests = new JScrollPane(tblPullRequests);

        lblPullRequests.setFont(Fonts.withSize(14));
        lblPullRequestCount.setFont(Fonts.withSize(14));

        lblPullRequestCount.setText(String.valueOf(repositoryStats.pullRequestCount()));

        tblPullRequests.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblPullRequests.setRowHeight(tblPullRequests.getRowHeight() + 3);

        Arrays.stream(PullRequestType.values())
                        .forEach(cmbActivePassivePRs::addItem);

        cmbActivePassivePRs.setSelectedIndex(-1);

        btnViewAll.addActionListener(e -> {
            final String url = "%s/pulls".formatted(repository.url());
            final URI uri = URI.create(url);
            URLBrowser.browse(uri)
                    .doOnError(err -> {
                        logger.error("An error occurred while opening the URL {}", url, err);
                        JOptionPane.showMessageDialog(
                                null,
                                "An error occurred while opening the URL %s\n%s".formatted(url, err.getMessage()),
                                DialogTitles.OPTION_PANE_ERROR_TITLE,
                                JOptionPane.ERROR_MESSAGE
                        );
                    })
                    .subscribeOn(Schedulers.parallel())
                    .subscribe();
        });

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
                                        .addComponent(cmbActivePassivePRs, 100, 100, 100)
                                        .addGap(15)
                                        .addComponent(lblPullRequestLabels)
                                        .addComponent(cmbPullRequestLabels, 100, 100, 100)
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
                                        .addComponent(cmbActivePassivePRs, 24, 24, 24)
                                        .addComponent(lblPullRequestLabels)
                                        .addComponent(cmbPullRequestLabels, 24, 24, 24)
                        )
                        .addGap(7)
                        .addComponent(spPullRequests)
                        .addGap(5)
        );

        this.setLayout(groupLayout);
    }

    private void loadPullRequests() {
        pullRequestService.getPullRequests(repository)
                .doOnError(err -> {
                    logger.error("Unable to retrieve pull requests for the repository {}", repository.name(), err);
                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to retrieve pull requests for the repository \"%s\"\n%s"
                                    .formatted(repository.name(), err.getMessage()),
                            DialogTitles.OPTION_PANE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                })
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(SwingScheduler.edt())
                .subscribe(pullRequests -> {
                    final PullRequestsTableModel model = new PullRequestsTableModel(pullRequests);
                    final PullRequestsTableCellRenderer renderer = new PullRequestsTableCellRenderer(pullRequests);

                    tblPullRequests.setModel(model);
                    tblPullRequests.setDefaultRenderer(Object.class, renderer);

                    setupPullRequestLabelComboBox(pullRequests);

                    arrangeColumnSizes();
                });
    }

    private void setupPullRequestLabelComboBox(ImmutableList<PullRequest> pullRequests) {
       final ImmutableSet<PullRequestLabel> labels = pullRequests.stream()
               .map(PullRequest::labels)
               .filter(lbls -> !lbls.isEmpty())
               .flatMap(Collection::stream)
               .collect(ImmutableSet.toImmutableSet());

       final PullRequestLabelComboBoxModel model = new PullRequestLabelComboBoxModel(labels);
       cmbPullRequestLabels.setModel(model);
       cmbPullRequestLabels.setRenderer(new PullRequestLabelListCellRenderer());
    }

    private void arrangeColumnSizes() {
        tblPullRequests.getColumnModel().getColumn(0).setMinWidth(50);
        tblPullRequests.getColumnModel().getColumn(1).setMinWidth(30);
        tblPullRequests.getColumnModel().getColumn(2).setMinWidth(500);
        tblPullRequests.getColumnModel().getColumn(3).setMinWidth(200);
        tblPullRequests.getColumnModel().getColumn(4).setMinWidth(160);
        tblPullRequests.getColumnModel().getColumn(5).setMinWidth(150);
        tblPullRequests.getColumnModel().getColumn(6).setMinWidth(120);
        tblPullRequests.getColumnModel().getColumn(7).setMinWidth(30);
        tblPullRequests.getColumnModel().getColumn(8).setMinWidth(60);
    }

    @Override
    public String getTabId() {
        return "PullRequests-%s".formatted(repository.id());
    }
}
