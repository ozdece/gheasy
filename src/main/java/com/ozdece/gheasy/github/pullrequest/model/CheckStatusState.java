package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum CheckStatusState {
    COMPLETED,
    IN_PROGRESS,
    PENDING,
    QUEUED,
    REQUESTED,
    WAITING,
    @JsonEnumDefaultValue
    UNKNOWN
}
