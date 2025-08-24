package com.ozdece.github.repository.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public record GithubRepository(
        String id,
        String name,
        Optional<String> description,
        String url,
        RepositoryOwner owner,
        ZonedDateTime createdAt,
        Optional<ZonedDateTime> updatedAt,
        Optional<LicenseInfo> licenseInfo,
        PrimaryLanguage primaryLanguage,
        RepositoryVisibility visibility,
        int forkCount,
        boolean isPrivate,
        boolean isArchived
) {}
