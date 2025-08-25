package com.ozdece.github.repository.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.GheasyApplication;
import com.ozdece.github.repository.model.GithubRepository;
import com.ozdece.github.repository.GithubRepositoryService;
import com.ozdece.json.GheasyObjectMapper;
import com.ozdece.process.ProcessService;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;

public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    private final ImmutableList<String> gitLocalRepoCheckCommand = ImmutableList.of("git", "rev-parse", "--is-inside-work-tree");
    private final ImmutableList<String> gitGithubRepoCheckCommand = ImmutableList.of("git", "remote", "get-url", "origin");
    private final ImmutableList<String> githubRepositoryViewCommand = ImmutableList.of("gh", "repo", "view", "--json",
            "id,createdAt,description,forkCount,homepageUrl,isArchived,isEmpty,isPrivate,licenseInfo,name,nameWithOwner,owner,primaryLanguage,pullRequests,updatedAt,url,visibility");

    private final ProcessService processService;

    private final String bookmarkFilePath = GheasyApplication.CONFIG_FOLDER_PATH + "/bookmarks.json";

    private static final JsonMapper jsonMapper = GheasyObjectMapper.getDefaultJsonMapper();

    public GithubRepositoryServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<Void> isGitHubRepo(File repositoryDirectory) {
        return checkIfGitRepository(repositoryDirectory)
                .then(checkIfGithubRepository(repositoryDirectory))
                .then();
    }

    @Override
    public Mono<GithubRepository> get(File repositoryDirectory) {
        final ProcessBuilder processBuilder = new ProcessBuilder(githubRepositoryViewCommand)
                .directory(repositoryDirectory);

        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, GithubRepository.class)
                .withDirectoryPath(repositoryDirectory.getAbsolutePath()));
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
                //TODO: Write a validator of this of the objects of the set as they might get updated by user manually.
                final ImmutableSet<GithubRepository> repositories = jsonMapper.readValue(bookmarkFileBytes, new TypeReference<>() {});

                return repositories;
            }
        });
    }

    private Mono<Void> checkIfGitRepository(File repositoryDirectory) {
        final ProcessBuilder gitLocalRepoCheckProcess = new ProcessBuilder(gitLocalRepoCheckCommand)
                .directory(repositoryDirectory);

        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(gitLocalRepoCheckProcess, Boolean.class))
                .filter(isGitRepo -> isGitRepo)
                .switchIfEmpty(Mono.error(new IllegalStateException("Unexpected response retrieved from git command while checking the local git repository.")))
                .then();
    }

    private Mono<Void> checkIfGithubRepository(File repositoryDirectory) {
        final ProcessBuilder gitGithubRepoCheckProcess = new ProcessBuilder(gitGithubRepoCheckCommand)
                .directory(repositoryDirectory);

        return Mono.fromCallable(() -> processService.getProcessOutput(gitGithubRepoCheckProcess))
                .filter(commandOutput -> commandOutput.contains("github.com"))
                .switchIfEmpty(Mono.error(new IllegalStateException("Unexpected response retrieved from git command while checking the local git repository.")))
                .then();
    }

}