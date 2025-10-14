package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.pullrequest.model.PullRequestLabel;

import javax.swing.*;

public class PullRequestLabelComboBoxModel extends DefaultComboBoxModel<PullRequestLabel> {

    private final ImmutableList<PullRequestLabel> labels;

    public PullRequestLabelComboBoxModel(ImmutableSet<PullRequestLabel> labels) {
        this.labels = labels.asList();
    }

    @Override
    public int getSize() {
        return labels.size();
    }

    @Override
    public PullRequestLabel getElementAt(int index) {
        return labels.get(index);
    }
}
