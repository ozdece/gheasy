package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__typename")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StatusContext.class, name = "StatusContext"),
        @JsonSubTypes.Type(value = CheckRun.class, name = "CheckRun")
})
public sealed interface StatusCheckRollup permits StatusContext, CheckRun {
    boolean isSuccessful();
}
