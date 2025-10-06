package com.ozdece.gheasy.github.pullrequest.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

import java.io.File;

public class PullRequestServiceImpl implements PullRequestService {

    private final ProcessService processService;

    private static final String ASSIGNED_PRS_SEARCH_QUERY = "\"assignee:@me OR review-requested:@me\"";
    private static final int PR_LIMIT = 1000;

    public PullRequestServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<ImmutableList<PullRequest>> getPullRequests(String repository) {
        final ProcessBuilder processBuilder = new ProcessBuilder(getOpenPullRequestsCommand(repository));
        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, new TypeReference<>(){}));
    }

    @Override
    public Mono<Integer> getAssignedPullRequestCount(String repository) {
        final ProcessBuilder processBuilder = new ProcessBuilder(getAssignedPullRequestCommand(repository));

        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, new TypeReference<>() {}));
    }

    private ImmutableList<String> getOpenPullRequestsCommand(String repository) {
       return ImmutableList.of(
               "gh",
               "pr",
               "list",
               "--repo",
               repository,
               "--search",
               ASSIGNED_PRS_SEARCH_QUERY,
               "--limit",
               String.valueOf(PR_LIMIT),
               "--json",
               "id,assignees,additions,author,changedFiles,closed,createdAt,deletions,isDraft,labels,mergeStateStatus,mergeable,number,state,statusCheckRollup,title,updatedAt,url");
    }

    private ImmutableList<String> getAssignedPullRequestCommand(String repository) {
        return ImmutableList.of("gh", "pr", "list", "--repo", repository, "--search", ASSIGNED_PRS_SEARCH_QUERY, "--limit", String.valueOf(PR_LIMIT), "|", "wc", "-l");
    }
}
