package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.repository.model.Repository;

import javax.swing.*;

public class RepositoryListModel extends AbstractListModel<Repository> {

    private final ImmutableList<Repository> repositories;

    public RepositoryListModel(ImmutableList<Repository> repositories) {
        this.repositories = repositories;
    }

    @Override
    public int getSize() {
        return repositories.size();
    }

    @Override
    public Repository getElementAt(int index) {
        return repositories.get(index);
    }
}
