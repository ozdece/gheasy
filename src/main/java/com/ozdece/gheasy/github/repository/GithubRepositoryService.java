package com.ozdece.gheasy.github.repository;

import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.github.repository.model.GithubRepositoryMetadata;
import reactor.core.publisher.Mono;

import java.io.File;

public interface GithubRepositoryService {
    Mono<GithubRepository> upsertBookmark(GithubRepository githubRepository);
    Mono<ImmutableSet<GithubRepository>> removeBookmark(GithubRepository githubRepository);
    Mono<ImmutableSet<GithubRepository>> getBookmarkedRepositories();
    Mono<GithubRepositoryMetadata> getRepositoryMetadata(String repository);
}