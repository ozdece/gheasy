package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.time.ZonedDateTime;
import java.util.Optional;

//TODO: Add assignees
public record PullRequest(
        String id,
        PullRequestAuthor author,
        String title,
        boolean isDraft,
        boolean closed,
        String url,
        int changedFiles,
        int additions,
        int deletions,
        @JsonProperty("number")
        int pullRequestNumber,
        ZonedDateTime createdAt,
        Optional<ZonedDateTime> updatedAt,
        @JsonProperty("state")
        PullRequestStatus status,
        ImmutableSet<PullRequestLabel> labels,
        ImmutableList<StatusCheckRollup> statusCheckRollup,
        MergeStateStatus mergeStateStatus,
        @JsonProperty("mergeable")
        MergeableState mergeableState
) {}
