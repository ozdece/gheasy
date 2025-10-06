package com.ozdece.gheasy.ui.models.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.github.repository.model.RepositoryOwner;

import java.util.Optional;

public final class OwnerTreeNode implements GithubRepositoryTreeNode {

    private final RootRepositoryNode rootNode;
    private final ImmutableList<RepositoryTreeNode> repositoryTreeNodes;
    private final RepositoryOwner owner;

    public OwnerTreeNode(
            RootRepositoryNode rootNode,
            RepositoryOwner repositoryOwner,
            ImmutableSet<GithubRepository> repositories) {
       this.rootNode = rootNode;
       this.owner = repositoryOwner;
       this.repositoryTreeNodes = repositories.stream()
               .map(githubRepository -> new RepositoryTreeNode(this, githubRepository))
               .collect(ImmutableList.toImmutableList());
    }

    @Override
    public RepositoryTreeNodeType getType() {
        return RepositoryTreeNodeType.OWNER;
    }

    public ImmutableList<RepositoryTreeNode> getRepositories() {
        return repositoryTreeNodes;
    }

    public RepositoryOwner getOwner() {
       return owner;
    }

    @Override
    public Optional<GithubRepositoryTreeNode> getParent() {
        return Optional.of(rootNode);
    }

    public int repositoryCount() {
       return repositoryTreeNodes.size();
    }
}
