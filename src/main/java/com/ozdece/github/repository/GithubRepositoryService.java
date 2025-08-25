package com.ozdece.github.repository;

import com.google.common.collect.ImmutableSet;
import com.ozdece.github.repository.model.GithubRepository;
import reactor.core.publisher.Mono;

import java.io.File;

public interface GithubRepositoryService {
    Mono<Void> isGitHubRepo(File repositoryDirectory);
    Mono<GithubRepository> get(File repositoryDirectory);
    Mono<GithubRepository> upsertBookmark(GithubRepository githubRepository);
    Mono<ImmutableSet<GithubRepository>> removeBookmark(GithubRepository githubRepository);
    Mono<ImmutableSet<GithubRepository>> getBookmarkedRepositories();
}