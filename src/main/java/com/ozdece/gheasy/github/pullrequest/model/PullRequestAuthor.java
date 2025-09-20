package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record PullRequestAuthor(
        String id,

        @JsonProperty("login")
        String username,

        @JsonProperty("name")
        Optional<String> fullName
) {}
