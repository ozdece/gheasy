package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PullRequestLabel(
        String id,
        String name,
        @JsonProperty("color")
        String hexColorCode
) {}
