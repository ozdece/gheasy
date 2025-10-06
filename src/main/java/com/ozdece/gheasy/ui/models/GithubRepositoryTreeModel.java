package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryOwner;
import com.ozdece.gheasy.github.repository.model.RepositoryStats;
import com.ozdece.gheasy.ui.models.tree.*;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GithubRepositoryTreeModel implements TreeModel {

    private final RootRepositoryNode root;
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public GithubRepositoryTreeModel(ImmutableSet<Repository> githubRepositories) {
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

    public void updateRepositoryStats(JComponent updaterComponent, Repository repository, RepositoryStats repositoryStats) {
       List<GithubRepositoryTreeNode> pathList = new ArrayList<>(5);
       pathList.add(root);

       for (int i = 0 ; i < root.getOwners().size(); i++) {
           final OwnerTreeNode ownerNode = root.getOwners().get(i);

           final ImmutableList<RepositoryTreeNode> repoNodes = ownerNode.getRepositories();

           for (int j = 0 ; j < repoNodes.size(); j++) {
               final RepositoryTreeNode repositoryTreeNode = repoNodes.get(j);

               if (repositoryTreeNode.getGithubRepository().equals(repository)) {
                   repositoryTreeNode.setRepositoryStats(repositoryStats);

                   pathList.add(ownerNode);
                   pathList.add(repositoryTreeNode);

                   final TreeModelEvent treeModelEvent = new TreeModelEvent(updaterComponent, pathList.toArray());

                   treeModelListeners
                           .forEach(listener -> listener.treeNodesChanged(treeModelEvent));
                   return;
               }
           }

       }

    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}

    private RootRepositoryNode buildRepositoryTree(ImmutableSet<Repository> githubRepositories) {
        final Map<RepositoryOwner, ImmutableSet<Repository>> repositoryMap = githubRepositories.stream()
                .collect(Collectors.groupingBy(
                        Repository::owner,
                        ImmutableSet.toImmutableSet()));

        return new RootRepositoryNode(repositoryMap);
    }

}
