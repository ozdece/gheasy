package com.ozdece.github.auth.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.github.auth.GhAuthService;
import com.ozdece.github.auth.model.GithubUser;
import com.ozdece.process.ProcessResponse;
import com.ozdece.process.ProcessService;
import reactor.core.publisher.Mono;

public final class GhAuthServiceImpl implements GhAuthService {

    private static final ImmutableList<String> userQueryCommand = ImmutableList.of("gh", "api", "user");
    private final ProcessService processService;

    public GhAuthServiceImpl(ProcessService processService) {
       this.processService = processService;
    }

    @Override
    public Mono<GithubUser> getLoggedInUser() {
        return Mono.fromCallable(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder(userQueryCommand);

            return processService.getThenParseProcessOutput(processBuilder, GithubUser.class, ProcessResponse.API);
        });
    }

}
