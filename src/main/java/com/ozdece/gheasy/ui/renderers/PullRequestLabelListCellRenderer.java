package com.ozdece.gheasy.ui.renderers;

import com.ozdece.gheasy.github.pullrequest.model.PullRequestLabel;
import com.ozdece.gheasy.ui.components.JGithubLabel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class PullRequestLabelListCellRenderer implements ListCellRenderer<PullRequestLabel> {

    @Override
    public Component getListCellRendererComponent(JList<? extends PullRequestLabel> list, PullRequestLabel value, int index, boolean isSelected, boolean cellHasFocus) {
        return Optional.ofNullable(value)
                .map(label -> new JGithubLabel(value.name(), value.hexColorCode()))
                .orElse(new JGithubLabel(""));
    }
}
