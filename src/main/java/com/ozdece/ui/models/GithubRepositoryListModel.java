package com.ozdece.ui.models;

import com.google.common.collect.ImmutableList;
import com.ozdece.github.repository.model.GithubRepository;

import javax.swing.*;

public class GithubRepositoryListModel extends AbstractListModel<GithubRepository> {

    private final ImmutableList<GithubRepository> githubRepositories;

    public GithubRepositoryListModel(ImmutableList<GithubRepository> githubRepositories) {
        this.githubRepositories = githubRepositories;
    }

    @Override
    public int getSize() {
        return githubRepositories.size();
    }

    @Override
    public GithubRepository getElementAt(int index) {
        return githubRepositories.get(index);
    }
}
