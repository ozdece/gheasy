package com.ozdece.gheasy.github.pullrequest

import com.google.common.collect.ImmutableList
import com.ozdece.gheasy.github.pullrequest.logic.PullRequestServiceImpl
import com.ozdece.gheasy.github.pullrequest.model.PullRequest;
import com.ozdece.gheasy.mocks.InMemoryProcessService;
import com.ozdece.gheasy.process.ProcessService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier;
import spock.lang.Specification;

class PullRequestServiceSpec extends Specification {
    final ProcessService processService = new InMemoryProcessService()
    final PullRequestService pullRequestService = new PullRequestServiceImpl(processService)

    def "should get pull requests by repository name"() {
        given: 'A repository name'
        final String repository = "repo"

        when: 'Retrieving pull requests by name'
        Mono<ImmutableList<PullRequest>> result = pullRequestService.getPullRequests(repository)

        then: 'pull requests should be retrieved'
        StepVerifier.create(result)
        .assertNext {prs ->
            assert prs.size() == 2
            assert prs.get(0).id() == "id-1"
            assert prs.get(1).id() == "id-2"
        }
        .verifyComplete()
    }

    def "should get number of assigned pull requests"() {
        given: 'A repository name'
        final String repository = "repo"

        when: 'Retrieving the number of assigned PRs'
        Mono<Integer> result = pullRequestService.getAssignedPullRequestCount(repository)

        then: 'A number should be retrieved'
        StepVerifier.create(result)
        .assertNext {num -> assert num == 1}
        .verifyComplete()
    }

}