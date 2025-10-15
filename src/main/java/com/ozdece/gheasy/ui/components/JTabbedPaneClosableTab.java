package com.ozdece.gheasy.ui.components;

import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.image.ImageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

public class JTabbedPaneClosableTab extends JPanel {

    private final JTabbedPane tabbedPane;

    public JTabbedPaneClosableTab(JTabbedPane tabbedPane, String title, Optional<File> maybeIconFile) {
       super();

       this.tabbedPane = tabbedPane;

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        final JLabel lblTitle = new JLabel(title);
        maybeIconFile
                .ifPresent(file -> lblTitle.setIcon(new ImageIcon(file.getAbsolutePath())));

        add(lblTitle);

        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        final JButton closeButton = new JButton("X");

        closeButton.addActionListener(this::onCloseButtonClicked);
        add(closeButton);
    }

    private void onCloseButtonClicked(ActionEvent e) {
        final int tabIndex = tabbedPane.indexOfTabComponent(JTabbedPaneClosableTab.this);

        if (tabIndex != -1) {
            tabbedPane.remove(tabIndex);
        }
    }

}
