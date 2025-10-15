package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum MergeStateStatus {
    BEHIND,
    BLOCKED,
    CLEAN,
    DIRTY,
    DRAFT,
    HAS_HOOKS,
    @JsonEnumDefaultValue
    UNKNOWN,
    UNSTABLE
}
