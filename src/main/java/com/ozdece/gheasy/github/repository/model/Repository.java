package com.ozdece.gheasy.github.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Optional;

public record Repository(
        String id,
        String name,
        Optional<String> description,
        String url,
        RepositoryOwner owner,
        ZonedDateTime createdAt,
        @JsonProperty("language")
        String primaryLanguage,
        RepositoryVisibility visibility
) {}
