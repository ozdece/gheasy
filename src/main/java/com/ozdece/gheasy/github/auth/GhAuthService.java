package com.ozdece.gheasy.github.auth;

import com.ozdece.gheasy.github.auth.model.GithubUser;
import reactor.core.publisher.Mono;


public interface GhAuthService {

    Mono<GithubUser> getLoggedInUser();

}
