package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.auth.model.GithubOwner;

import javax.swing.*;

public class AvailableOwnerComboBoxModel extends DefaultComboBoxModel<GithubOwner> {

    private final ImmutableList<GithubOwner> owners;

    public AvailableOwnerComboBoxModel(ImmutableList<GithubOwner> owners) {
        this.owners = owners;
    }

    @Override
    public int getSize() {
        return owners.size();
    }

    @Override
    public GithubOwner getElementAt(int index) {
        return owners.get(index);
    }
}
