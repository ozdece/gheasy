package com.ozdece.github.auth;

import com.ozdece.github.auth.model.GithubUser;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface GhAuthService {

    Mono<Optional<GithubUser>> getLoggedInUser();

}
