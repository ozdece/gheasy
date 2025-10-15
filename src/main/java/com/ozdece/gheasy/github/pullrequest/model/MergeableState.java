package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum MergeableState {
    CONFLICTING,
    MERGEABLE,
    @JsonEnumDefaultValue
    UNKNOWN
}
