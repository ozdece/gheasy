package com.ozdece.gheasy.github.auth

import com.google.common.collect.ImmutableList
import com.ozdece.gheasy.github.auth.logic.AuthServiceImpl
import com.ozdece.gheasy.github.auth.model.GithubOwner
import com.ozdece.gheasy.github.auth.model.GithubUser
import com.ozdece.gheasy.github.auth.model.UserType
import com.ozdece.gheasy.github.organization.OrganizationService
import com.ozdece.gheasy.github.organization.logic.OrganizationServiceImpl
import com.ozdece.gheasy.mocks.InMemoryProcessService
import com.ozdece.gheasy.process.ProcessService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class AuthServiceSpec extends Specification {

    final ProcessService processService = new InMemoryProcessService()
    final OrganizationService organizationService = new OrganizationServiceImpl(processService)
    final AuthService authService = new AuthServiceImpl(processService, organizationService)

    def "should get Github user via gh api user command"() {
        when: 'logged in user is requested'
        final GithubUser githubUser = authService.getLoggedInUser().block()

        then: 'Github user should be retrieved successfully'
        githubUser.id() == "id"
        githubUser.username() == "testUser"
    }

    def "should get all github owners including the user"() {
        when: 'ALl owners are requested'
        Mono<ImmutableList<GithubOwner>> result = authService.getRepositoryOwners()

        then: 'All owners including the user should be present'
        StepVerifier.create(result)
        .assertNext {list ->
            assert list.size() == 3
            final GithubOwner userOwner = list.get(2)

            assert userOwner.userType() == UserType.USER

            final GithubOwner orgOwner = list.get(0)
            assert orgOwner.userType() == UserType.ORGANIZATION
        }
        .verifyComplete()
    }
}
