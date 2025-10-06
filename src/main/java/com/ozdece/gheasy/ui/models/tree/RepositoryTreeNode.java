package com.ozdece.gheasy.ui.models.tree;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryStats;

import java.util.Optional;

public final class RepositoryTreeNode implements GithubRepositoryTreeNode {

    private final Repository repository;
    private final OwnerTreeNode parent;
    private RepositoryStats repositoryStats = new RepositoryStats(0, 0);

    private final ImmutableList<RepositoryTreeNodeLeaf> leafs = ImmutableList.of(
            new RepositoryTreeNodeLeaf(this, RepositoryTreeNodeType.PULL_REQUEST),
            new RepositoryTreeNodeLeaf(this, RepositoryTreeNodeType.ISSUE)
    );

    public RepositoryTreeNode(OwnerTreeNode owner, Repository repository) {
        this.repository = repository;
        this.parent = owner;
    }

    @Override
    public RepositoryTreeNodeType getType() {
        return RepositoryTreeNodeType.REPOSITORY;
    }

    @Override
    public Optional<GithubRepositoryTreeNode> getParent() {
        return Optional.of(parent);
    }

    public ImmutableList<RepositoryTreeNodeLeaf> getLeafs() {
        return leafs;
    }

    public int leafCount() {
        return leafs.size();
    }

    public Repository getGithubRepository() {
        return repository;
    }

    public RepositoryStats getRepositoryStats() {
        return repositoryStats;
    }

    public void setRepositoryStats(RepositoryStats repositoryStats) {
        this.repositoryStats = repositoryStats;
    }
}
