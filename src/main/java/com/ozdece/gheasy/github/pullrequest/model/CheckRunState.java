package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum CheckRunState {
    ACTION_REQUIRED,
    CANCELLED,
    COMPLETED,
    FAILURE,
    IN_PROGRESS,
    NEUTRAL,
    PENDING,
    QUEUED,
    SKIPPED,
    STALE,
    STARTUP_FAILURE,
    SUCCESS,
    TIMED_OUT,
    WAITING,
    @JsonEnumDefaultValue
    UNKNOWN
}
