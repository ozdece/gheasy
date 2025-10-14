package com.ozdece.gheasy.ui.renderers;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.datetime.ZoneBasedDateTimeFormatter;
import com.ozdece.gheasy.github.pullrequest.model.CheckRunState;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestLabel;
import com.ozdece.gheasy.github.pullrequest.model.StatusCheckRollup;
import com.ozdece.gheasy.ui.Fonts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.stream.Collectors;

public class PullRequestsTableCellRenderer implements TableCellRenderer {

    private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
    private final ImmutableList<PullRequest> pullRequests;

    public PullRequestsTableCellRenderer(ImmutableList<PullRequest> pullRequests) {
        this.pullRequests = pullRequests;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final PullRequest pullRequest = pullRequests.get(row);

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
                final JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setOpaque(true);
                progressBar.setBorderPainted(false);
                progressBar.setStringPainted(true);

                final long statusCheckCount = pullRequest.statusCheckRollup().size();
                final long completedCheckCount = pullRequest.statusCheckRollup().stream()
                        .filter(StatusCheckRollup::isSuccessful)
                        .count();
                final double perc = ((double) completedCheckCount / statusCheckCount) * 100;

                progressBar.setValue((int)perc);
                progressBar.setString("%d/%d tasks succeeded".formatted(completedCheckCount, statusCheckCount));

                progressBar.setForeground(new Color(46, 204, 113)); // A nice green
                if (isSelected) {
                    progressBar.setBackground(table.getSelectionBackground());
                }

                return progressBar;
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
                        final String text = pullRequest.labels().stream()
                                .map(PullRequestLabel::name)
                                .collect(Collectors.joining(", "));

                        label.setText(text);

                        //TODO: Create a custom label panel
                        //TODO: Create a utility for converting String hexColorCode to Color object
                        final Border line = new LineBorder(Color.BLUE, 2, true);
                        final Border padding = new EmptyBorder(10, 15, 10, 15);
                        final Border roundedBorder = new CompoundBorder(line, padding);

                        label.setBorder(roundedBorder);
                        label.setBackground(Color.MAGENTA.darker());
                    }
                    case 7 -> label.setHorizontalAlignment(SwingConstants.RIGHT);
                }

                return label;
            }
        }
    }
}
