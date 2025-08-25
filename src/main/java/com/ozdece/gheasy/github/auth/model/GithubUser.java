package com.ozdece.gheasy.github.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record GithubUser(
        @JsonProperty("node_id")
        String id,
        @JsonProperty("login")
        String username,
        @JsonProperty("html_url")
        String profileUrl,
        String avatarUrl,
        @JsonProperty("name")
        Optional<String> fullName,
        UserType type
) {}
