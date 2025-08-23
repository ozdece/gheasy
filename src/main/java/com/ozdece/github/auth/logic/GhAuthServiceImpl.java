package com.ozdece.github.auth.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.github.auth.GhAuthService;
import com.ozdece.github.auth.model.GithubUser;
import com.ozdece.process.ProcessService;
import io.vavr.control.Either;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class GhAuthServiceImpl implements GhAuthService {

    private static final ImmutableList<String> userQueryCommand = ImmutableList.of("gh", "api", "user");
    private final ProcessService processService;

    public GhAuthServiceImpl(ProcessService processService) {
       this.processService = processService;
    }

    @Override
    public Mono<Optional<GithubUser>> getLoggedInUser() {
        return Mono.fromCallable(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder(userQueryCommand);

            final Either<Throwable, GithubUser> maybeGithubUser = processService.getThenParseProcessOutput(processBuilder, GithubUser.class);

            if (maybeGithubUser.isLeft()) {
                //TODO: Add proper logging
                System.err.println("Retrieving GitHub User through gh was not successful. " + maybeGithubUser.getLeft().getMessage());
            }

            return maybeGithubUser.toJavaOptional();
        });
    }

}
