package com.ozdece.gheasy.ui.models.state;

public enum PullRequestActiveStatus {
    ALL,
    ACTIVE,
    DRAFT;

    @Override
    public String toString() {
        return switch (this) {
            case ALL -> "All";
            case ACTIVE -> "Active";
            case DRAFT -> "Draft";
        };
    }
}
