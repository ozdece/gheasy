package com.ozdece.gheasy.github.repository.model;

public record GithubRepositoryMetadata(
        LatestRelease latestRelease,
        int starCount,
        String license,
        String currentBranch
) {}
