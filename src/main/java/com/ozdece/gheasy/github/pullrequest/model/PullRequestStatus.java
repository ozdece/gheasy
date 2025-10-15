package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum PullRequestStatus {
    OPEN,
    MERGED,
    CLOSED,
    @JsonEnumDefaultValue
    UNKNOWN
}
