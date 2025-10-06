package com.ozdece.gheasy.ui.models.tree;

import java.util.Optional;

public record RepositoryTreeNodeLeaf(RepositoryTreeNode repositoryTreeNode, RepositoryTreeNodeType getType) implements GithubRepositoryTreeNode {

    @Override
    public Optional<GithubRepositoryTreeNode> getParent() {
        return Optional.of(repositoryTreeNode);
    }
}
