package com.ozdece.gheasy.github.repository.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.RepositoryMetadata;
import com.ozdece.gheasy.github.repository.model.response.RepositoryMetadataResponse;
import com.ozdece.gheasy.github.repository.model.response.LicenseInfoResponse;
import com.ozdece.gheasy.json.GheasyObjectMapper;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;

public class RepositoryServiceImpl implements RepositoryService {

    private final ProcessService processService;

    private final String bookmarkFilePath;

    private static final JsonMapper jsonMapper = GheasyObjectMapper.getDefaultJsonMapper();
    private static final int REPO_QUERY_LIMIT = 1000;

    public RepositoryServiceImpl(ProcessService processService, String configFolderPath) {
        this.processService = processService;
        bookmarkFilePath = configFolderPath + "/bookmarks.json";
    }

    @Override
    public Mono<Repository> insertBookmark(Repository repository) {
        return getBookmarkedRepositories()
                .filter(repositories -> !this.isBookmarkExist(repositories, repository))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Repository %s already bookmarked in Gheasy".formatted(repository.name()))))
                .map(repositories -> updateBookmarkList(repositories, repository))
                .flatMap(this::saveBookmarkChanges)
                .thenReturn(repository);
    }

    @Override
    public Mono<ImmutableSet<Repository>> removeBookmark(Repository repository) {
        return getBookmarkedRepositories()
                .filter(repositories -> this.isBookmarkExist(repositories, repository))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Repository %s does not exist in bookmarks".formatted(repository.name()))))
                .map(repos -> removeBookmark(repos, repository))
                .flatMap(updatedBookmarks ->
                        saveBookmarkChanges(updatedBookmarks)
                                .thenReturn(updatedBookmarks)
                );
    }

    @Override
    public Mono<ImmutableSet<Repository>> getBookmarkedRepositories() {
        return Mono.fromCallable(() -> {
            final File bookmarkFile = new File(bookmarkFilePath);

            if (!bookmarkFile.exists()) {
                final boolean fileCreated = bookmarkFile.createNewFile();

                if (!fileCreated) {
                    throw new IllegalStateException("The bookmark file cannot be created!");
                }

                //Write empty array into the file after its created.
                Files.write(bookmarkFile.toPath(), "[]".getBytes());
                return ImmutableSet.of();
            } else {
                final byte[] bookmarkFileBytes = Files.readAllBytes(bookmarkFile.toPath());
                final ImmutableSet<Repository> repositories = jsonMapper.readValue(bookmarkFileBytes, new TypeReference<>() {});

                return repositories;
            }
        });
    }

    @Override
    public Mono<RepositoryMetadata> getRepositoryMetadata(String repository) {
        return getRepositoryMetadataResponse(repository)
                .map(response -> new RepositoryMetadata(
                        response.latestRelease(),
                        response.stargazerCount(),
                        response.licenseInfo().map(LicenseInfoResponse::name))
                );
    }

    @Override
    public Mono<ImmutableSet<Repository>> searchRepositoriesByOwner(GithubOwner githubOwner, String query) {
        final ProcessBuilder processBuilder = new ProcessBuilder(getSearchRepoByOwnerCommand(githubOwner, query));
        final TypeReference<ImmutableSet<Repository>> typeReference = new TypeReference<>(){};

        return Mono.fromCallable(() ->
            processService.getThenParseProcessOutput(processBuilder, typeReference)
        );
    }

    private Mono<RepositoryMetadataResponse> getRepositoryMetadataResponse(String repository) {
       final ProcessBuilder processBuilder = new ProcessBuilder(getRepoMetadataCommand(repository));

       return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, RepositoryMetadataResponse.class));
    }

    private ImmutableSet<Repository> removeBookmark(ImmutableSet<Repository> githubRepositories, Repository repository) {
        return githubRepositories.stream()
                .filter(repo -> !repo.id().equals(repository.id()))
                .collect(ImmutableSet.toImmutableSet());
    }

    private Mono<Void> saveBookmarkChanges(ImmutableSet<Repository> githubRepositories) {
        return Mono.fromCallable(() -> {
                    final byte[] jsonBytes = jsonMapper.writeValueAsBytes(githubRepositories);
                    final File bookmarkFile = new File(bookmarkFilePath);

                    return Files.write(bookmarkFile.toPath(), jsonBytes);
                })
                .then();
    }

    private boolean isBookmarkExist(ImmutableSet<Repository> repositories, Repository repository) {
        return repositories.stream()
                .anyMatch(r -> r.id().equals(repository.id()));
    }

    private ImmutableSet<Repository> updateBookmarkList(ImmutableSet<Repository> githubRepositories, Repository repository) {
        return ImmutableSet.<Repository>builder()
                .addAll(githubRepositories)
                .add(repository)
                .build();
    }

    private ImmutableList<String> getRepoMetadataCommand(String repository) {
        return ImmutableList.of("gh", "repo", "view", repository, "--json", "latestRelease,licenseInfo,stargazerCount");
    }

    private ImmutableList<String> getSearchRepoByOwnerCommand(GithubOwner owner, String query) {
        return ImmutableList.of("gh", "search", "repos", "--owner=%s".formatted(owner.name()), "\"%s\"".formatted(query), "--limit", String.valueOf(REPO_QUERY_LIMIT));
    }

}