package com.ozdece.ui.renderers;

import com.ozdece.github.repository.model.GithubRepository;

import javax.swing.*;
import java.awt.*;

public class GithubRepositoryListCellRenderer implements ListCellRenderer<GithubRepository> {

    private final JLabel lblName = new JLabel();
    private final JLabel lblProgrammingLanguage = new JLabel();
    private final JLabel lblVisibility = new JLabel();
    private final JLabel lblPrivateRepo = new JLabel();
    private final JLabel lblArchived = new JLabel();
    private final JLabel lblOwner = new JLabel();

    @Override
    public Component getListCellRendererComponent(
            JList<? extends GithubRepository> list,
            GithubRepository githubRepository,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        final JPanel panel = new JPanel();

        lblName.setText(githubRepository.name());
        lblOwner.setText(githubRepository.owner().name());

        panel.setLayout(generateLayout(panel));

        if (isSelected) {
           panel.setBackground(list.getSelectionBackground());
        } else {
            panel.setBackground(list.getBackground());
        }

        // name, primary language, visibility, isPrivate, isArchived, owner
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
                                        .addGap(10)
                                        .addComponent(lblOwner)
                        )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblName)
                                        .addComponent(lblOwner)
                        )
        );

        return groupLayout;
    }
}
