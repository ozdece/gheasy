package com.ozdece.gheasy.github.repository.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public record Repository(
        String id,
        String name,
        String nameWithOwner,
        String directoryPath,
        Optional<String> description,
        String url,
        RepositoryOwner owner,
        ZonedDateTime createdAt,
        PrimaryLanguage primaryLanguage,
        RepositoryVisibility visibility,
        boolean isArchived
) {
    public Repository withDirectoryPath(String directoryPath) {
        return new Repository(
                id,
                name,
                nameWithOwner,
                directoryPath,
                description,
                url,
                owner,
                createdAt,
                primaryLanguage,
                visibility,
                isArchived
        );
    }
}
