package com.ozdece.gheasy.ui.renderers;

import com.ozdece.gheasy.github.repository.model.RepositoryStats;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.ui.Fonts;
import com.ozdece.gheasy.ui.models.tree.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class RepositoryTreeRenderer implements TreeCellRenderer {

    private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    private final ImageService imageService;

    public RepositoryTreeRenderer(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean hasFocus) {
        final JLabel label = (JLabel) defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, hasFocus);
        final GithubRepositoryTreeNode node = (GithubRepositoryTreeNode) value;

        switch (node) {
            case OwnerTreeNode ownerTreeNode -> {
                label.setText(ownerTreeNode.getOwner().name());
                final boolean isBoldFont = ownerTreeNode.getRepositories().stream()
                        .map(RepositoryTreeNode::getRepositoryStats)
                        .anyMatch(repoStats -> repoStats.pullRequestCount() > 0 || repoStats.issuesCount() > 0);

                if (isBoldFont) {
                    label.setFont(Fonts.BOLD_FONT);
                } else {
                    label.setFont(Fonts.DEFAULT_FONT);
                }

                imageService
                        .getImageFile(ownerTreeNode.getOwner().name() + ".png")
                        .ifPresent(imageFile -> label.setIcon(new ImageIcon(imageFile.getAbsolutePath())));
            }
            case RepositoryTreeNode repositoryTreeNode -> {
                final RepositoryStats repositoryStats = repositoryTreeNode.getRepositoryStats();
                final int statsSum = repositoryStats.issuesCount() + repositoryStats.pullRequestCount();

                label.setText("%s (%d)".formatted(repositoryTreeNode.getGithubRepository().name(), statsSum));

                if (repositoryStats.pullRequestCount() > 0 || repositoryStats.issuesCount() > 0) {
                    label.setFont(Fonts.BOLD_FONT);
                } else {
                    label.setFont(Fonts.DEFAULT_FONT);
                }

                imageService
                        .getImageFile(repositoryTreeNode.getGithubRepository().owner().name() + ".png")
                        .ifPresent(imageFile -> label.setIcon(new ImageIcon(imageFile.getAbsolutePath())));
            }
            case RepositoryTreeNodeLeaf leaf -> {
                final String text = switch (leaf.getType()) {
                    case PULL_REQUEST -> "Pull Requests (%d)".formatted(leaf.repositoryTreeNode().getRepositoryStats().pullRequestCount());
                    case ISSUE -> "Issues (%d)".formatted(leaf.repositoryTreeNode().getRepositoryStats().issuesCount());
                    default -> "";
                };

                switch (leaf.getType()) {
                    case PULL_REQUEST: {
                        if (leaf.repositoryTreeNode().getRepositoryStats().pullRequestCount() > 0) {
                           label.setFont(Fonts.BOLD_FONT);
                        } else {
                            label.setFont(Fonts.DEFAULT_FONT);
                        }
                        break;
                    }
                    case ISSUE: {
                        if (leaf.repositoryTreeNode().getRepositoryStats().issuesCount() > 0) {
                            label.setFont(Fonts.BOLD_FONT);
                        } else {
                            label.setFont(Fonts.DEFAULT_FONT);
                        }
                        break;
                    }
                    default: {
                        label.setFont(Fonts.DEFAULT_FONT);
                        break;
                    }
                }

                label.setText(text);
            }
            case RootRepositoryNode rootRepositoryNode -> label.setText("Repositories");
        }

        return label;
    }
}
