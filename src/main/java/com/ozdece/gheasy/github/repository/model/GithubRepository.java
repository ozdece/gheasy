package com.ozdece.gheasy.github.repository.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public record GithubRepository(
        String id,
        String name,
        String nameWithOwner,
        String directoryPath,
        Optional<String> description,
        String url,
        RepositoryOwner owner,
        ZonedDateTime createdAt,
        Optional<LicenseInfo> licenseInfo,
        PrimaryLanguage primaryLanguage,
        RepositoryVisibility visibility,
        boolean isArchived
) {
    public GithubRepository withDirectoryPath(String directoryPath) {
        return new GithubRepository(
                id,
                name,
                nameWithOwner,
                directoryPath,
                description,
                url,
                owner,
                createdAt,
                licenseInfo,
                primaryLanguage,
                visibility,
                isArchived
        );
    }
}
