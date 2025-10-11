package com.ozdece.gheasy.github.pullrequest.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.pullrequest.PullRequestService;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

public class PullRequestServiceImpl implements PullRequestService {

    private final ProcessService processService;

    private static final String ASSIGNED_PRS_SEARCH_QUERY = "is:open AND author:@me OR review-requested:@me";
    private static final int PR_LIMIT = 1000;

    public PullRequestServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<ImmutableList<PullRequest>> getPullRequests(Repository repository) {
        final ProcessBuilder processBuilder = new ProcessBuilder(getOpenPullRequestsCommand(repository));
        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, new TypeReference<>(){}));
    }

    @Override
    public Mono<Integer> getAssignedPullRequestCount(Repository repository) {
        final ProcessBuilder processBuilder = new ProcessBuilder(getAssignedPullRequestCountCommand(repository));

        return Mono.fromCallable(() -> processService.getProcessOutput(processBuilder))
                .map(output -> output.isEmpty() ? 0 : output.split("\n").length);
    }

    private ImmutableList<String> getOpenPullRequestsCommand(Repository repository) {
       return ImmutableList.of(
               "gh",
               "pr",
               "list",
               "--repo",
               "%s/%s".formatted(repository.owner().name(), repository.name()),
               "--search",
               ASSIGNED_PRS_SEARCH_QUERY,
               "--limit",
               String.valueOf(PR_LIMIT),
               "--json",
               "id,assignees,additions,author,changedFiles,closed,createdAt,deletions,isDraft,labels,mergeStateStatus,mergeable,number,state,statusCheckRollup,title,updatedAt,url");
    }

    private ImmutableList<String> getAssignedPullRequestCountCommand(Repository repository) {
        return ImmutableList.of("gh", "pr", "list", "--repo", "%s/%s".formatted(repository.owner().name(), repository.name()), "--search", ASSIGNED_PRS_SEARCH_QUERY, "--limit", String.valueOf(PR_LIMIT));
    }
}
