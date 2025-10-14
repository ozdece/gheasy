package com.ozdece.gheasy.ui.renderers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.datetime.ZoneBasedDateTimeFormatter;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.components.JGithubLabel;
import com.ozdece.gheasy.ui.components.JLabelsPanel;
import com.ozdece.gheasy.ui.components.JPullRequestDiffPanel;
import com.ozdece.gheasy.ui.components.JStatusCheckProgressBar;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PullRequestsTableCellRenderer implements TableCellRenderer {

    private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
    private final ImmutableList<PullRequest> pullRequests;

    public PullRequestsTableCellRenderer(ImmutableList<PullRequest> pullRequests) {
        this.pullRequests = pullRequests;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final PullRequest pullRequest = pullRequests.get(table.convertRowIndexToModel(row));

        switch (column) {
            case 1 -> {
                final JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(!pullRequest.isDraft());

                checkBox.setOpaque(true);

                checkBox.setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                } else {
                    checkBox.setBackground(table.getBackground());
                    checkBox.setForeground(table.getForeground());
                }

                return checkBox;
            }
            case 6 -> {
                final ImmutableSet<JGithubLabel> labels = pullRequest.labels().stream()
                        .map(pullRequestLabel -> new JGithubLabel(pullRequestLabel.name(), pullRequestLabel.hexColorCode()))
                        .collect(ImmutableSet.toImmutableSet());
                final JLabelsPanel labelsPanel = new JLabelsPanel(labels);
                if (isSelected) {
                    labelsPanel.setBackground(table.getSelectionBackground());
                } else {
                    labelsPanel.setBackground(table.getBackground());
                }

                return labelsPanel;
            }
            case 7 -> {
                final JStatusCheckProgressBar progressBar = new JStatusCheckProgressBar(pullRequest.statusCheckRollup());

                if (isSelected) {
                    progressBar.setBackground(table.getSelectionBackground());
                }

                return progressBar;
            }
            case 9 -> {
                final JPullRequestDiffPanel diffPanel = new JPullRequestDiffPanel(pullRequest.additions(), pullRequest.deletions());

                if (isSelected) {
                    diffPanel.setBackground(table.getSelectionBackground());
                } else {
                    diffPanel.setBackground(table.getBackground());
                }

                return diffPanel;
            }
            default -> {
                final JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.LEFT);

                switch (column) {
                    case 0 -> label.setHorizontalAlignment(SwingConstants.CENTER);
                    case 2 -> label.setFont(Fonts.BOLD_FONT);
                    case 3 -> {
                        final String text = pullRequest.author().fullName()
                                .filter(fullName -> !fullName.isEmpty())
                                .map(fullName -> "%s (%s)".formatted(fullName, pullRequest.author().username()))
                                .orElse(pullRequest.author().username());

                        label.setText(text);
                    }
                    case 4 -> {
                        final String text = ZoneBasedDateTimeFormatter.toFormattedString(pullRequest.createdAt());
                        label.setText(text);
                    }
                    case 5 -> {
                        final String text = pullRequest.updatedAt()
                                .map(ZoneBasedDateTimeFormatter::toFormattedString)
                                .orElse("");
                        label.setText(text);
                    }
                    case 8 -> label.setHorizontalAlignment(SwingConstants.RIGHT);
                }

                return label;
            }
        }
    }
}
