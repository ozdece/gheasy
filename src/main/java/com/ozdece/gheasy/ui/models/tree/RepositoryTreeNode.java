package com.ozdece.gheasy.ui.models.tree;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.repository.model.GithubRepository;

import java.util.Optional;

public final class RepositoryTreeNode implements GithubRepositoryTreeNode {

    private final GithubRepository githubRepository;
    private final OwnerTreeNode parent;

    private final ImmutableList<RepositoryTreeNodeLeaf> leafs = ImmutableList.of(
            new RepositoryTreeNodeLeaf(this, RepositoryTreeNodeType.PULL_REQUEST),
            new RepositoryTreeNodeLeaf(this, RepositoryTreeNodeType.ISSUE)
    );

    public RepositoryTreeNode(OwnerTreeNode owner, GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
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

    public GithubRepository getGithubRepository() {
        return githubRepository;
    }

}
