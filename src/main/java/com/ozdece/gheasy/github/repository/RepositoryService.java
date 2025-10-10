package com.ozdece.gheasy.github.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.model.RepositoryMetadata;
import reactor.core.publisher.Mono;

public interface RepositoryService {
    Mono<Repository> insertBookmark(Repository repository);
    Mono<ImmutableSet<Repository>> removeBookmark(Repository repository);
    Mono<ImmutableSet<Repository>> getBookmarkedRepositories();
    Mono<RepositoryMetadata> getRepositoryMetadata(String repository);
    Mono<ImmutableList<Repository>> searchRepositoriesByOwner(GithubOwner githubOwner, String query);
}