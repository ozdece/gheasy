package com.ozdece.gheasy.github.repository.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.github.repository.model.GithubRepository;
import com.ozdece.gheasy.github.repository.GithubRepositoryService;
import com.ozdece.gheasy.github.repository.model.GithubRepositoryMetadata;
import com.ozdece.gheasy.github.repository.model.response.GhRepositoryMetadataResponse;
import com.ozdece.gheasy.github.repository.model.response.LicenseInfo;
import com.ozdece.gheasy.json.GheasyObjectMapper;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;

public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    private final ProcessService processService;

    private final String bookmarkFilePath;

    private static final JsonMapper jsonMapper = GheasyObjectMapper.getDefaultJsonMapper();

    public GithubRepositoryServiceImpl(ProcessService processService, String configFolderPath) {
        this.processService = processService;
        bookmarkFilePath = configFolderPath + "/bookmarks.json";
    }

    @Override
    public Mono<GithubRepository> upsertBookmark(GithubRepository githubRepository) {
        return getBookmarkedRepositories()
                .map(repositories -> updateBookmarks(repositories, githubRepository))
                .flatMap(this::saveBookmarkChanges)
                .thenReturn(githubRepository);
    }

    @Override
    public Mono<ImmutableSet<GithubRepository>> removeBookmark(GithubRepository githubRepository) {
        return getBookmarkedRepositories()
                .map(repos -> removeBookmark(repos, githubRepository))
                .flatMap(updatedBookmarks ->
                        saveBookmarkChanges(updatedBookmarks)
                                .thenReturn(updatedBookmarks)
                );
    }

    @Override
    public Mono<ImmutableSet<GithubRepository>> getBookmarkedRepositories() {
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
                final ImmutableSet<GithubRepository> repositories = jsonMapper.readValue(bookmarkFileBytes, new TypeReference<>() {});

                return repositories;
            }
        });
    }

    @Override
    public Mono<GithubRepositoryMetadata> getRepositoryMetadata(String repository) {
        return getRepositoryMetadataResponse(repository)
                .map(response -> new GithubRepositoryMetadata(
                        response.latestRelease(),
                        response.stargazerCount(),
                        response.licenseInfo().map(LicenseInfo::name))
                );
    }

    private Mono<GhRepositoryMetadataResponse> getRepositoryMetadataResponse(String repository) {
       final ProcessBuilder processBuilder = new ProcessBuilder(getGhGetRepoMetadataCommand(repository));

       return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, GhRepositoryMetadataResponse.class));
    }

    private ImmutableSet<GithubRepository> removeBookmark(ImmutableSet<GithubRepository> githubRepositories, GithubRepository githubRepository) {
        return githubRepositories.stream()
                .filter(repo -> !repo.id().equals(githubRepository.id()))
                .collect(ImmutableSet.toImmutableSet());
    }

    private Mono<Void> saveBookmarkChanges(ImmutableSet<GithubRepository> githubRepositories) {
        return Mono.fromCallable(() -> {
                    final byte[] jsonBytes = jsonMapper.writeValueAsBytes(githubRepositories);
                    final File bookmarkFile = new File(bookmarkFilePath);

                    return Files.write(bookmarkFile.toPath(), jsonBytes);
                })
                .then();
    }

    private ImmutableSet<GithubRepository> updateBookmarks(ImmutableSet<GithubRepository> githubRepositories, GithubRepository githubRepository) {
        final boolean repoExist = githubRepositories.stream()
                .anyMatch(repo -> repo.id().equals(githubRepository.id()));

        if (repoExist) {
            return githubRepositories.stream()
                    .map(repo -> repo.id().equals(githubRepository.id()) ? githubRepository : repo)
                    .collect(ImmutableSet.toImmutableSet());
        } else {
            return ImmutableSet.<GithubRepository>builder()
                    .addAll(githubRepositories)
                    .add(githubRepository)
                    .build();
        }
    }

    private ImmutableList<String> getGhGetRepoMetadataCommand(String repository) {
        return ImmutableList.of("gh", "repo", "view", repository, "--json", "latestRelease,licenseInfo,stargazerCount");
    }

}