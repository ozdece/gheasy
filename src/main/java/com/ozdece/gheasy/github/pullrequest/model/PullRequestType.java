package com.ozdece.gheasy.github.pullrequest.model;

public enum PullRequestType {
    ACTIVE,
    DRAFT;

    @Override
    public String toString() {
        return switch (this) {
            case ACTIVE -> "Active";
            case DRAFT -> "Draft";
        };
    }
}
