package com.ozdece.gheasy.github.organization

import com.google.common.collect.ImmutableList
import com.ozdece.gheasy.github.organization.logic.OrganizationServiceImpl
import com.ozdece.gheasy.github.organization.model.Organization
import com.ozdece.gheasy.mocks.InMemoryProcessService
import com.ozdece.gheasy.process.ProcessService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class OrganizationServiceSpec extends Specification {

    private final ProcessService processService = new InMemoryProcessService()
    private final OrganizationService githubOrganizationService = new OrganizationServiceImpl(processService)

    def "should retrieve organizations of the user"() {
        when: 'organizations are being retrieved'
        Mono<ImmutableList<Organization>> result = githubOrganizationService.getOrganizations()

        then: 'Organizations should be retrieved'
        StepVerifier.create(result)
        .assertNext {list ->
            list.size() == 2
            list.get(0).id() == "id-1"
            list.get(1).id() == "id-2"
        }
    }

}
