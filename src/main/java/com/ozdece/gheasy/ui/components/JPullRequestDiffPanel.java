package com.ozdece.gheasy.ui.components;

import javax.swing.*;
import java.awt.*;

public class JPullRequestDiffPanel extends JPanel {

    public JPullRequestDiffPanel(int additions, int deletions) {
        super();

        setLayout(new FlowLayout(FlowLayout.CENTER));

        final JLabel lblAdditions = new JLabel();
        final JLabel lblDeletions = new JLabel();

        if (additions > 0) {
            lblAdditions.setText("+" + additions);
        } else {
            lblAdditions.setText(String.valueOf(additions));
        }

        if (deletions > 0) {
            lblDeletions.setText("-" + deletions);
        } else {
            lblDeletions.setText(String.valueOf(deletions));
        }

        final JLabel lblSeparator = new JLabel("/");

        lblAdditions.setForeground(Color.GREEN.darker());
        lblDeletions.setForeground(Color.RED);

        add(lblAdditions);
        add(lblSeparator);
        add(lblDeletions);

        setOpaque(true);
    }

}
