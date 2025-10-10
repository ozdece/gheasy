package com.ozdece.gheasy.ui.renderers;

import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.ui.Fonts;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public class RepositoryListCellRenderer implements ListCellRenderer<Repository> {

    private final Optional<File> repositoryOwnerIconFile;

    public RepositoryListCellRenderer(Optional<File> repositoryOwnerIconFile) {
       this.repositoryOwnerIconFile = repositoryOwnerIconFile;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Repository> list, Repository repository, int index, boolean isSelected, boolean cellHasFocus) {
        final JPanel card = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(card);

        final JLabel lblRepoTitle = new JLabel(repository.name());
        final JLabel lblLanguage = new JLabel(repository.primaryLanguage());
        final JLabel lblDescription = new JLabel(repository.description().orElse(""));

        repositoryOwnerIconFile.ifPresent(ownerIconFile -> lblRepoTitle.setIcon(new ImageIcon(ownerIconFile.getAbsolutePath())));

        lblRepoTitle.setFont(Fonts.boldFontWithSize(16));

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblRepoTitle)
                                        .addGap(0,0, Integer.MAX_VALUE)
                                        .addComponent(lblLanguage)
                                        .addGap(10)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(10)
                                        .addComponent(lblDescription)
                        )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(5)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblRepoTitle)
                                        .addComponent(lblLanguage)
                        )
                        .addGap(5)
                        .addComponent(lblDescription)
                        .addGap(5)
        );

        card.setLayout(groupLayout);

        if (isSelected) {
            lblRepoTitle.setForeground(Color.WHITE);
            lblLanguage.setForeground(Color.WHITE);
            lblDescription.setForeground(Color.WHITE);
            card.setBackground(list.getSelectionBackground());
            card.setForeground(list.getSelectionForeground());
        } else {
            card.setBackground(list.getBackground());
            card.setForeground(Color.WHITE);
            lblRepoTitle.setForeground(Color.GREEN.darker());
            lblLanguage.setForeground(Color.BLACK);
            lblDescription.setForeground(Color.BLACK);
        }

        return card;
    }

}
