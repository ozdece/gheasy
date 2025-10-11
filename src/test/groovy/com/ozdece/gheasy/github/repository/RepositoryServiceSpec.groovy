package com.ozdece.gheasy.github.repository

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.ozdece.gheasy.github.auth.model.GithubOwner
import com.ozdece.gheasy.github.auth.model.UserType
import com.ozdece.gheasy.github.pullrequest.PullRequestService
import com.ozdece.gheasy.github.pullrequest.logic.PullRequestServiceImpl
import com.ozdece.gheasy.github.repository.logic.RepositoryServiceImpl
import com.ozdece.gheasy.github.repository.model.RepositoryMetadata

import com.ozdece.gheasy.github.repository.model.Repository
import com.ozdece.gheasy.github.repository.model.RepositoryOwner
import com.ozdece.gheasy.github.repository.model.RepositoryVisibility
import com.ozdece.gheasy.mocks.InMemoryProcessService
import com.ozdece.gheasy.process.ProcessService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.ZonedDateTime

class RepositoryServiceSpec extends Specification {

    static String TEST_FILES_DIR = System.getProperty("java.io.tmpdir") + "/gheasy"

    final ProcessService processService = new InMemoryProcessService()
    final PullRequestService pullRequestService = new PullRequestServiceImpl(processService);
    final RepositoryService repositoryService = new RepositoryServiceImpl(processService, pullRequestService, TEST_FILES_DIR)

    def setupSpec() {
        final File tmpFile = new File(TEST_FILES_DIR)

        if (!tmpFile.exists()) {
            tmpFile.mkdir()
        }
    }

    def cleanupSpec() {
        final File tmpFile = new File(TEST_FILES_DIR)
        boolean filesDeleted = tmpFile.deleteDir()

        if (filesDeleted) {
            println "Temporary files deleted successfully."
        } else {
            System.err.println("Deleting temporary files failed.")
        }

    }

    def "should add a new bookmark if it doesn't exist"() {
        given: 'A new Repository'
        final Repository githubRepository = newGithubRepository(UUID.randomUUID().toString())

        when: 'A new github repository is being bookmarked'
        Mono<Repository> result = repositoryService.insertBookmark(githubRepository)

        then: 'A new bookmark is added'
        StepVerifier.create(result)
                .assertNext {gr -> assert gr.id() == githubRepository.id()}
                .verifyComplete()

        StepVerifier.create(repositoryService.getBookmarkedRepositories())
        .assertNext { bookmarks ->
            assert bookmarks.size() == 1
            assert bookmarks.toList().get(0).id() == githubRepository.id()
        }
        .verifyComplete()
    }

    def "should throw an error if a bookmark already exist"() {
        given: 'A new github repository'
        final Repository githubRepository = newGithubRepository(UUID.randomUUID().toString())

        and: 'A new github repository is being bookmarked'
        StepVerifier.create(repositoryService.insertBookmark(githubRepository))
                .assertNext {gr -> assert gr.id() == githubRepository.id()}
                .verifyComplete()

        when: 'An existing bookmark is trying to be re-added'
        Mono<Repository> result = repositoryService.insertBookmark(githubRepository)

        then: 'The bookmark is updated successfully'
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify()
    }

    def "should remove a bookmark if it exists"() {
        given: 'A new Repository'
        final Repository githubRepository = newGithubRepository(UUID.randomUUID().toString())

        and: 'A new github repository is being bookmarked'
        Mono<Repository> insertResult = repositoryService.insertBookmark(githubRepository)
        StepVerifier.create(insertResult)
                .assertNext {gr -> assert gr.id() == githubRepository.id()}
                .verifyComplete()

        when: 'An existing bookmark is trying to be removed'
        Mono<ImmutableSet<Repository>> removeResult = repositoryService.removeBookmark(githubRepository)

        then: 'The existing bookmark is removed'
        StepVerifier.create(removeResult)
        .assertNext {bookmarks ->
            assert !bookmarks.stream().anyMatch {r -> r.id() == githubRepository.id() }
        }
        .verifyComplete()
    }

    def "should throw an error when trying to remove a bookmark that does not exist"() {
        given: 'A new Repository'
        final Repository githubRepository = newGithubRepository(UUID.randomUUID().toString())

        when: 'trying to remove a bookmark that does not exist'
        Mono<ImmutableSet<Repository>> result = repositoryService.removeBookmark(githubRepository)

        then: 'ImmutableSet should remain as-is with successful complete'
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify()
    }

    def "should retrieve the repository metadata successfully"() {
        when: 'trying to fetch the Github repository metadata'
        Mono<RepositoryMetadata> metadataRepository = repositoryService.getRepositoryMetadata(newGithubRepository("test-id"))

        then: 'Metadata should be retrieved successfully'
        StepVerifier.create(metadataRepository)
        .assertNext {metadata ->
            assert metadata.starCount() == 1
        }
        .verifyComplete()
    }

    def "should retrieve repositories for an owner by a search query"() {
        given: 'a GithubOwner and a search query'
        final String query = "testrepo"
        final GithubOwner githubOwner = new GithubOwner(UserType.ORGANIZATION, "Org", "")

        when: 'repositories are being retrieved'
        final Mono<ImmutableList<Repository>> result = repositoryService.searchRepositoriesByOwner(githubOwner, query)

        then: 'Search results should be present'
        StepVerifier.create(result)
        .assertNext {list ->
            assert list.size() == 1
            assert list.get(0).id() == "id-search"
        }
        .verifyComplete()
    }

    private static Repository newGithubRepository(String id) {
        return new Repository(
                id,
                "gheasy",
                Optional.empty(),
                "https://example.com",
                new RepositoryOwner("id", "ozdece"),
                ZonedDateTime.now(),
                "Java",
                RepositoryVisibility.PUBLIC
        )
    }
}
