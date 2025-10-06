package com.ozdece.gheasy.github.repository

import com.google.common.collect.ImmutableSet
import com.ozdece.gheasy.github.repository.logic.RepositoryServiceImpl
import com.ozdece.gheasy.github.repository.model.RepositoryMetadata
import com.ozdece.gheasy.github.repository.model.PrimaryLanguage
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
    final RepositoryService githubRepositoryService = new RepositoryServiceImpl(processService, TEST_FILES_DIR)

    static String VALID_GITHUB_REPO_PATH = "${TEST_FILES_DIR}/valid_repo"
    static String INVALID_GIT_REPO_PATH = "${TEST_FILES_DIR}/invalid_git_repo"
    static String INVALID_GITHUB_REPO_PATH = "${TEST_FILES_DIR}/invalid_github_repo"

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
        final Repository githubRepository = newGithubRepository("new-id")

        when: 'A new github repository is being bookmarked'
        Mono<Repository> result = githubRepositoryService.upsertBookmark(githubRepository)

        then: 'A new bookmark is added'
        StepVerifier.create(result)
                .assertNext {gr -> assert gr.id() == "new-id"}
                .verifyComplete()

        StepVerifier.create(githubRepositoryService.getBookmarkedRepositories())
        .assertNext { bookmarks ->
            assert bookmarks.size() == 1
            assert bookmarks.toList().get(0).id() == "new-id"
        }
        .verifyComplete()
    }

    def "should update a bookmark if it already exist"() {
        given: 'A new github repository'
        final Repository githubRepository = newGithubRepository("new-id")

        and: 'A new github repository is being bookmarked'
        StepVerifier.create(githubRepositoryService.upsertBookmark(githubRepository))
                .assertNext {gr -> assert gr.id() == "new-id"}
                .verifyComplete()

        when: 'An existing bookmark is being updated'
        final Repository updated = githubRepository.withDirectoryPath("directory_path")
        Mono<Repository> result = githubRepositoryService.upsertBookmark(updated)

        then: 'The bookmark is updated successfully'
        StepVerifier.create(result)
                .assertNext {gr ->
                    assert gr.id() == "new-id"
                    assert gr.directoryPath() == "directory_path"
                }
                .verifyComplete()

        StepVerifier.create(githubRepositoryService.getBookmarkedRepositories())
                .assertNext { bookmarks ->
                    assert bookmarks.size() == 1
                    assert bookmarks.toList().get(0).id() == "new-id"
                }
                .verifyComplete()
    }

    def "should remove a bookmark if it exists"() {
        given: 'A new Repository'
        final Repository githubRepository = newGithubRepository("new-id")

        and: 'A new github repository is being bookmarked'
        Mono<Repository> upsertResult = githubRepositoryService.upsertBookmark(githubRepository)
        StepVerifier.create(upsertResult)
                .assertNext {gr -> assert gr.id() == "new-id"}
                .verifyComplete()

        StepVerifier.create(githubRepositoryService.getBookmarkedRepositories())
                .assertNext { bookmarks ->
                    assert bookmarks.size() == 1
                    assert bookmarks.toList().get(0).id() == "new-id"
                }
                .verifyComplete()

        when: 'An existing bookmark is trying to be removed'
        Mono<ImmutableSet<Repository>> removeResult = githubRepositoryService.removeBookmark(githubRepository)

        then: 'The existing bookmark is removed'
        StepVerifier.create(removeResult)
        .assertNext {bookmarks -> assert bookmarks.isEmpty()}
        .verifyComplete()
    }

    def "should do nothing and complete successfully if parameter github repository is not found in bookmarks"() {
        given: 'A new Repository'
        final Repository githubRepository = newGithubRepository("new-id")

        when: 'trying to remove a bookmark that does not exist'
        Mono<ImmutableSet<Repository>> result = githubRepositoryService.removeBookmark(githubRepository)

        then: 'ImmutableSet should remain as-is with successful complete'
        StepVerifier.create(result)
                .assertNext {bookmarks -> assert bookmarks.isEmpty()}
                .verifyComplete()
    }

    def "should retrieve the repository metadata successfully"() {
        when: 'trying to fetch the Github repository metadata'
        Mono<RepositoryMetadata> metadataRepository = githubRepositoryService.getRepositoryMetadata(new File(VALID_GITHUB_REPO_PATH));

        then: 'Metadata should be retrieved successfully'
        StepVerifier.create(metadataRepository)
        .assertNext {metadata ->
            assert metadata.currentBranch() == "master"
            assert metadata.starCount() == 1
        }
        .verifyComplete()
    }

    private static Repository newGithubRepository(String id) {
        return new Repository(
                id,
                "gheasy",
                "ozdece/gheasy",
                "/tmp",
                Optional.empty(),
                "https://example.com",
                new RepositoryOwner("id", "ozdece"),
                ZonedDateTime.now(),
                new PrimaryLanguage("Java"),
                RepositoryVisibility.PUBLIC,
                false
        )
    }
}
