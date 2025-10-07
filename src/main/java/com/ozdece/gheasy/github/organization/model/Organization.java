package com.ozdece.gheasy.github.organization.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Organization(
        String id,
        @JsonProperty("login")
        String name,
        String url,
        String avatarUrl
) {
}
