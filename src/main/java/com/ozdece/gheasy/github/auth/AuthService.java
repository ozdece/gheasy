package com.ozdece.gheasy.github.auth;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import reactor.core.publisher.Mono;


public interface AuthService {
    Mono<GithubUser> getLoggedInUser();
    Mono<ImmutableList<GithubOwner>> getRepositoryOwners();
}
