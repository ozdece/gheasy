package com.ozdece.gheasy.ui.renderers;

import com.google.common.collect.ImmutableMap;
import com.ozdece.gheasy.github.auth.model.GithubOwner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public class AvailableOwnerListCellRenderer implements ListCellRenderer<GithubOwner> {

    private final ImmutableMap<GithubOwner, Optional<File>> ownersWithAvatarsMap;
    private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public AvailableOwnerListCellRenderer(ImmutableMap<GithubOwner, Optional<File>> ownersWithAvatarsMap) {
        this.ownersWithAvatarsMap = ownersWithAvatarsMap;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GithubOwner> list, GithubOwner owner, int index, boolean isSelected, boolean cellHasFocus) {
        final JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                list,
                owner,
                index,
                isSelected,
                cellHasFocus
        );

        label.setText(owner.name());

        Optional.ofNullable(ownersWithAvatarsMap.get(owner))
                .flatMap(Function.identity())
                .ifPresent(file -> label.setIcon(new ImageIcon(file.getAbsolutePath())));

        return label;
    }
}
