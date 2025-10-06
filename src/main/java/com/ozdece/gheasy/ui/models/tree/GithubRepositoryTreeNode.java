package com.ozdece.gheasy.ui.models.tree;

import java.util.Optional;

public sealed interface GithubRepositoryTreeNode permits OwnerTreeNode, RepositoryTreeNode, RepositoryTreeNodeLeaf, RootRepositoryNode {
    RepositoryTreeNodeType getType();
    Optional<GithubRepositoryTreeNode> getParent();
}
