package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.github.repository.model.RepositoryOwner;
import com.ozdece.gheasy.ui.models.tree.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GithubRepositoryTreeModel implements TreeModel {

    private final GithubRepositoryTreeNode root;
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public GithubRepositoryTreeModel(ImmutableSet<GithubRepository> githubRepositories) {
       this.root = this.buildRepositoryTree(githubRepositories);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        final GithubRepositoryTreeNode treeNode = (GithubRepositoryTreeNode) parent;

        return switch (treeNode) {
            case OwnerTreeNode ownerTreeNode -> ownerTreeNode.getRepositories().get(index);
            case RepositoryTreeNode repositoryTreeNode -> repositoryTreeNode.getLeafs().get(index);
            case RootRepositoryNode rootRepositoryNode -> rootRepositoryNode.getOwners().get(index);
            case RepositoryTreeNodeLeaf leaf -> null;
        };
    }

    @Override
    public int getChildCount(Object parent) {
        final GithubRepositoryTreeNode treeNode = (GithubRepositoryTreeNode) parent;

        return switch (treeNode) {
            case OwnerTreeNode ownerTreeNode -> ownerTreeNode.repositoryCount();
            case RepositoryTreeNode repositoryTreeNode -> repositoryTreeNode.leafCount();
            case RootRepositoryNode rootRepositoryNode -> rootRepositoryNode.getOwners().size();
            case RepositoryTreeNodeLeaf leaf -> 0;
        };
    }

    @Override
    public boolean isLeaf(Object node) {
        GithubRepositoryTreeNode treeNode = (GithubRepositoryTreeNode) node;

        return switch (treeNode.getType()) {
            case PULL_REQUEST, ISSUE -> true;
            default -> false;
        };
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        GithubRepositoryTreeNode treeNode = (GithubRepositoryTreeNode) parent;
        return switch (treeNode){
            case OwnerTreeNode ownerTreeNode -> ownerTreeNode.getRepositories().indexOf(child);
            case RepositoryTreeNode repositoryTreeNode -> repositoryTreeNode.getLeafs().indexOf(child);
            case RepositoryTreeNodeLeaf leaf -> -1;
            case RootRepositoryNode rootRepositoryNode -> rootRepositoryNode.getOwners().indexOf(child);
        };
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}

    private GithubRepositoryTreeNode buildRepositoryTree(ImmutableSet<GithubRepository> githubRepositories) {
        final Map<RepositoryOwner, ImmutableSet<GithubRepository>> repositoryMap = githubRepositories.stream()
                .collect(Collectors.groupingBy(
                        GithubRepository::owner,
                        ImmutableSet.toImmutableSet()));

        return new RootRepositoryNode(repositoryMap);
    }

}
