package com.ozdece.gheasy.ui.models;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PullRequestsTableModel extends AbstractTableModel {

    private final String[] COLUMNS = {"#", "Active", "Title", "Author", "Created At", "Labels", "Tasks", "Changed Files", "Diff"};

    private final List<PullRequest> pullRequests;

    public PullRequestsTableModel(ImmutableList<PullRequest> pullRequests) {
        this.pullRequests = new ArrayList<>(pullRequests);
    }

    @Override
    public int getRowCount() {
        return pullRequests.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final PullRequest pullRequest = pullRequests.get(rowIndex);

        switch (columnIndex) {
            case 0: return pullRequest.pullRequestNumber();
            case 1: return pullRequest.isDraft();
            case 2: return pullRequest.title();
            case 3: return pullRequest.author();
            case 4: return pullRequest.createdAt();
            case 5: return pullRequest.labels();
            case 6: return pullRequest.statusCheckRollup();
            case 7: return pullRequest.changedFiles();
            // We render diff in renderer
            case 8: return "";
            default: return null;
        }
    }
}
