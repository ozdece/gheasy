package com.ozdece.gheasy.github.auth.logic;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.auth.AuthService;
import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.auth.model.UserType;
import com.ozdece.gheasy.github.organization.OrganizationService;
import com.ozdece.gheasy.github.organization.model.Organization;
import com.ozdece.gheasy.process.ProcessResponse;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

public final class AuthServiceImpl implements AuthService {

    private static final ImmutableList<String> userQueryCommand = ImmutableList.of("gh", "api", "user");
    private final ProcessService processService;
    private final OrganizationService organizationService;

    public AuthServiceImpl(
            ProcessService processService,
            OrganizationService organizationService
    ) {
       this.processService = processService;
       this.organizationService = organizationService;
    }

    @Override
    public Mono<GithubUser> getLoggedInUser() {
        return Mono.fromCallable(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder(userQueryCommand);

            return processService.getThenParseProcessOutput(processBuilder, GithubUser.class, ProcessResponse.API);
        });
    }

    @Override
    public Mono<ImmutableList<GithubOwner>> getRepositoryOwners() {
        return organizationService.getOrganizations()
                .map(this::toGithubOwner)
                .zipWith(getLoggedInUser(), (orgOwners, user) -> organizationOwnersWithUser(user, orgOwners));
    }

    private ImmutableList<GithubOwner> organizationOwnersWithUser(GithubUser user, ImmutableList<GithubOwner> organizations) {
        final GithubOwner userOwner = new GithubOwner(user.type(), user.username(), user.avatarUrl());

        return ImmutableList.<GithubOwner>builder()
                .addAll(organizations)
                .add(userOwner)
                .build();
    }

    private ImmutableList<GithubOwner> toGithubOwner(ImmutableList<Organization> organizations) {
       return organizations.stream()
               .map(org -> new GithubOwner(UserType.ORGANIZATION, org.name(), org.avatarUrl()))
               .collect(ImmutableList.toImmutableList());
    }

}
