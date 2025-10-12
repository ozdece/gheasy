package com.ozdece.gheasy.ui.panels;

import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestStatus;
import com.ozdece.gheasy.ui.Fonts;

import javax.swing.*;

public class PullRequestPanel extends JPanel {

    private final PullRequestService pullRequestService;

    private final JTable tblPullRequests = new JTable();

    private final JLabel lblPullRequestCount = new JLabel("0");

    private final JTextField txtSearchPullRequest = new JTextField();

    private final JComboBox<PullRequestStatus> cmbActivePassivePRs = new JComboBox<>();
    private final JComboBox<String> cmbPullRequestLabels = new JComboBox<>();

    public PullRequestPanel(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
        setupPanel();
    }

    private void setupPanel() {

        final GroupLayout groupLayout = new GroupLayout(this);

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

}
