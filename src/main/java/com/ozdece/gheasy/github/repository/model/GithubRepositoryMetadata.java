package com.ozdece.gheasy.github.repository.model;

import java.util.Optional;

public record GithubRepositoryMetadata(
        Optional<LatestRelease> latestRelease,
        int starCount,
        Optional<String> license,
        String currentBranch
) {}
