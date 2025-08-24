package com.ozdece.github.auth;

import com.ozdece.github.auth.model.GithubUser;
import reactor.core.publisher.Mono;


public interface GhAuthService {

    Mono<GithubUser> getLoggedInUser();

}
