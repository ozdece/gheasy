package com.ozdece.github.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepositoryOwner(
        String id,
        @JsonProperty("login") String name
) {}
