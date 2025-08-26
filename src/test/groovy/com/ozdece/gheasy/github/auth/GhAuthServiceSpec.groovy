package com.ozdece.gheasy.github.auth

import com.ozdece.gheasy.github.auth.logic.GhAuthServiceImpl
import com.ozdece.gheasy.github.auth.model.GithubUser
import com.ozdece.gheasy.mocks.InMemoryProcessService
import com.ozdece.gheasy.process.ProcessService
import spock.lang.Specification

class GhAuthServiceSpec extends Specification {

    final ProcessService processService = new InMemoryProcessService()
    final GhAuthService ghAuthService = new GhAuthServiceImpl(processService)

    def "should get Github user via gh api user command"() {
        when: 'logged in user is requested'
        final GithubUser githubUser = ghAuthService.getLoggedInUser().block()

        then: 'Github user should be retrieved successfully'
        githubUser.id() == "id"
        githubUser.username() == "testUser"
    }
}
