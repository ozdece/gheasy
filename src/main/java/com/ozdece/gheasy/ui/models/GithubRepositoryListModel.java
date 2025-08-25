package com.ozdece.gheasy.ui.models;

import com.ozdece.gheasy.github.repository.model.GithubRepository;

import javax.swing.*;
import java.util.List;

public class GithubRepositoryListModel extends AbstractListModel<GithubRepository> {

    private final List<GithubRepository> githubRepositories;

    public GithubRepositoryListModel(List<GithubRepository> githubRepositories) {
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

    public void upsertGithubRepository(GithubRepository githubRepository) {
        boolean newItem = true;

        for (int i = 0 ; i < githubRepositories.size() ; i++) {
           final GithubRepository repo = githubRepositories.get(i);

           if (repo.id().equals(githubRepository.id())) {
               githubRepositories.set(i, githubRepository);
               newItem = false;

               fireContentsChanged(this, i, i);
               break;
           }
        }

        if (newItem) {
            final int newElementIndex = githubRepositories.size();
            githubRepositories.add(githubRepository);

            fireContentsChanged(this, newElementIndex, newElementIndex);
        }

    }

}
