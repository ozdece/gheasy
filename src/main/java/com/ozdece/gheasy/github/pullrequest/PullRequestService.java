package com.ozdece.gheasy.github.pullrequest;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import reactor.core.publisher.Mono;

import java.io.File;

public interface PullRequestService {
    Mono<ImmutableList<PullRequest>> getPullRequests(File repositoryDirectory);

}
