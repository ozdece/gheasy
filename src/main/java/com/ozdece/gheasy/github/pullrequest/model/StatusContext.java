package com.ozdece.gheasy.github.pullrequest.model;

public record StatusContext(
        StatusState state,
        String context
) implements StatusCheckRollup {

    @Override
    public boolean isSuccessful() {
        return state == StatusState.SUCCESS;
    }
}
