package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.auth.model.GithubOwner;

import javax.swing.*;

public class GithubOwnerListModel extends AbstractListModel<GithubOwner> {

    private final ImmutableList<GithubOwner> githubOwners;

    public GithubOwnerListModel(ImmutableList<GithubOwner> githubOwners) {
       this.githubOwners = githubOwners;
    }

    @Override
    public int getSize() {
        return githubOwners.size();
    }

    @Override
    public GithubOwner getElementAt(int index) {
        return githubOwners.get(index);
    }
}
