package com.ozdece.gheasy.ui.models.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.github.repository.model.RepositoryOwner;

import java.util.Map;
import java.util.Optional;

public final class RootRepositoryNode implements GithubRepositoryTreeNode {

    private final ImmutableList<OwnerTreeNode> owners;

    public RootRepositoryNode(Map<RepositoryOwner, ImmutableSet<GithubRepository>> repositoryMap) {
       owners = repositoryMap.entrySet().stream()
               .map(entry -> this.buildOwnerTreeNode(this, entry))
               .collect(ImmutableList.toImmutableList());
    }

    @Override
    public RepositoryTreeNodeType getType() {
        return RepositoryTreeNodeType.ROOT;
    }

    @Override
    public Optional<GithubRepositoryTreeNode> getParent() {
        return Optional.empty();
    }

    public ImmutableList<OwnerTreeNode> getOwners() {
        return owners;
    }

    private OwnerTreeNode buildOwnerTreeNode(RootRepositoryNode rootNode, Map.Entry<RepositoryOwner, ImmutableSet<GithubRepository>> entry) {
        final OwnerTreeNode ownerTreeNode = new OwnerTreeNode(rootNode, entry.getKey(), entry.getValue());
        return ownerTreeNode;
    }
}
