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

    private static final ImmutableList<String> openPullRequestsCommand = ImmutableList.of("gh", "pr", "list", "--json", "id,assignees,additions,author,changedFiles,closed,createdAt,deletions,isDraft,labels,mergeStateStatus,mergeable,number,state,statusCheckRollup,title,updatedAt,url");

    public PullRequestServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<ImmutableList<PullRequest>> getPullRequests(File repositoryDirectory) {
        final ProcessBuilder processBuilder = new ProcessBuilder(openPullRequestsCommand)
                .directory(repositoryDirectory);
        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, new TypeReference<>(){}));
    }
}
