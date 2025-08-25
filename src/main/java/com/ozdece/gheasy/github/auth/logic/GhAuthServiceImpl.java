package com.ozdece.gheasy.github.auth.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.auth.GhAuthService;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.process.ProcessResponse;
import com.ozdece.gheasy.process.ProcessService;
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
