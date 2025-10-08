package com.ozdece.gheasy.ui.renderers;

import com.ozdece.gheasy.ui.models.tree.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class RepositoryTreeRenderer implements TreeCellRenderer {

    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean hasFocus) {
        final JLabel label = (JLabel) defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, hasFocus);
        final GithubRepositoryTreeNode node = (GithubRepositoryTreeNode) value;

        switch (node) {
            case OwnerTreeNode ownerTreeNode -> {
                label.setText(ownerTreeNode.getOwner().name());
            }
            case RepositoryTreeNode repositoryTreeNode -> {
                label.setText(repositoryTreeNode.getGithubRepository().name());
            }
            case RepositoryTreeNodeLeaf leaf -> {
                final String text = switch (leaf.getType()) {
                    case PULL_REQUEST -> "Pull Requests (%d)".formatted(leaf.repositoryTreeNode().getRepositoryStats().pullRequestCount());
                    case ISSUE -> "Issues (%d)".formatted(leaf.repositoryTreeNode().getRepositoryStats().issuesCount());
                    default -> "";
                };

                label.setText(text);
            }
            case RootRepositoryNode rootRepositoryNode -> {
                label.setText("Repositories");
            }
        }

        return label;
    }
}
