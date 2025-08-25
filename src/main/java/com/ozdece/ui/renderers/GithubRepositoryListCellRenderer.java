package com.ozdece.ui.renderers;

import com.ozdece.github.repository.model.GithubRepository;
import com.ozdece.ui.Fonts;
import com.ozdece.ui.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class GithubRepositoryListCellRenderer implements ListCellRenderer<GithubRepository> {

    private final JLabel lblName = new JLabel();
    private final JLabel lblVisibility = new JLabel();
    private final JLabel lblArchived = new JLabel();
    private final JLabel lblPrimaryLanguage = new JLabel();
    private final JLabel lblDirectoryPath = new JLabel();
    private final JLabel lblDescription = new JLabel();

    @Override
    public Component getListCellRendererComponent(
            JList<? extends GithubRepository> list,
            GithubRepository githubRepository,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        final JPanel panel = new JPanel();

        lblName.setText(githubRepository.nameWithOwner());
        lblName.setFont(Fonts.boldFontWithSize(14));

        lblPrimaryLanguage.setText(githubRepository.primaryLanguage().name());
        lblPrimaryLanguage.setFont(Fonts.boldFontWithSize(12));

        lblDirectoryPath.setText("Path: " + githubRepository.directoryPath());
        lblDirectoryPath.setFont(Fonts.BOLD_FONT);

        lblDescription.setFont(Fonts.ITALIC_FONT);
        githubRepository.description().ifPresent(lblDescription::setText);

        lblVisibility.setFont(Fonts.ITALIC_FONT);
        switch (githubRepository.visibility()) {
            case INTERNAL -> {
                lblVisibility.setText("Internal");
                ResourceLoader.loadImage("images/internal.png")
                        .map(ImageIcon::new)
                        .ifPresent(lblVisibility::setIcon);
            }
            case PRIVATE -> {
                lblVisibility.setText("Private");
                ResourceLoader.loadImage("images/private.png")
                        .map(ImageIcon::new)
                        .ifPresent(lblVisibility::setIcon);
            }
            case PUBLIC -> {
                lblVisibility.setText("Public");
                ResourceLoader.loadImage("images/public.png")
                        .map(ImageIcon::new)
                        .ifPresent(lblVisibility::setIcon);
            }
        }

        if (githubRepository.isArchived()) {
            lblArchived.setFont(Fonts.ITALIC_FONT);
            lblArchived.setText("Archived Repository");
            ResourceLoader.loadImage("images/archived.png")
                    .map(ImageIcon::new)
                    .ifPresent(lblVisibility::setIcon);
        }

        panel.setLayout(generateLayout(panel));

        if (isSelected) {
           panel.setBackground(list.getSelectionBackground());
        } else {
            panel.setBackground(list.getBackground());
        }

        return panel;
    }

    private GroupLayout generateLayout(JPanel panel) {
        final GroupLayout groupLayout = new GroupLayout(panel);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(15)
                                        .addComponent(lblName)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lblPrimaryLanguage)
                                        .addGap(15)
                        )
                        .addGroup(
                                groupLayout
                                        .createSequentialGroup()
                                        .addGap(15)
                                        .addComponent(lblDescription)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(15)
                                        .addComponent(lblDirectoryPath)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(15)
                                        .addComponent(lblVisibility)
                                        .addGap(5)
                                        .addComponent(lblArchived)
                        )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblName)
                                        .addComponent(lblPrimaryLanguage)
                        )
                        .addGap(6)
                        .addComponent(lblDescription)
                        .addGap(5)
                        .addComponent(lblDirectoryPath)
                        .addGap(5)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblVisibility)
                                        .addComponent(lblArchived)
                        )
                        .addGap(10)
        );

        return groupLayout;
    }
}
