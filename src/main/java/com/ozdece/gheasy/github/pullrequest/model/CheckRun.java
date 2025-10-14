package com.ozdece.gheasy.github.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckRun(
        @JsonProperty("conclusion")
        CheckRunState checkRunState,
        @JsonProperty("status")
        CheckStatusState checkStatusState
) implements StatusCheckRollup {
    @Override
    public boolean isSuccessful() {
        return checkRunState == CheckRunState.SUCCESS;
    }
}
