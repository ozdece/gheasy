package com.ozdece.gheasy.mocks

import com.fasterxml.jackson.core.type.TypeReference
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.ozdece.gheasy.github.auth.model.GithubUser
import com.ozdece.gheasy.github.auth.model.UserType
import com.ozdece.gheasy.github.organization.model.Organization
import com.ozdece.gheasy.github.pullrequest.model.MergeStateStatus
import com.ozdece.gheasy.github.pullrequest.model.MergeableState
import com.ozdece.gheasy.github.pullrequest.model.PullRequest
import com.ozdece.gheasy.github.pullrequest.model.PullRequestAuthor
import com.ozdece.gheasy.github.pullrequest.model.PullRequestStatus
import com.ozdece.gheasy.github.repository.RepositoryServiceSpec

import com.ozdece.gheasy.github.repository.model.Repository
import com.ozdece.gheasy.github.repository.model.RepositoryOwner
import com.ozdece.gheasy.github.repository.model.RepositoryVisibility
import com.ozdece.gheasy.github.repository.model.response.RepositoryMetadataResponse
import com.ozdece.gheasy.process.ProcessResponse
import com.ozdece.gheasy.process.ProcessService

import java.time.ZonedDateTime
import java.util.stream.Collectors

class InMemoryProcessService implements ProcessService {

    @Override
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass) throws IOException, InterruptedException {
        return mockParseResult(processBuilder, resultObjectClass, ProcessResponse.CLI)
    }

    @Override
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, TypeReference<T> typeReference) throws IOException, InterruptedException {
        return mockParseResult(processBuilder, typeReference.getClass(), ProcessResponse.CLI)
    }

    @Override
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, TypeReference<T> typeReference, ProcessResponse processResponse) throws IOException, InterruptedException {
        return mockParseResult(processBuilder, typeReference.getClass(), processResponse)
    }

    @Override
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException {
        return mockParseResult(processBuilder, resultObjectClass, processResponse)
    }

    @Override
    String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        final String command = processBuilder.command().stream().collect(Collectors.joining(" "))
        final String processDirectory = processBuilder.directory().getAbsolutePath()

        return switch (command) {
            case "git remote get-url origin" -> {
                if (processDirectory == RepositoryServiceSpec.VALID_GITHUB_REPO_PATH) {
                    "git@github.com"
                }
                else null
            }
            case "git branch --show-current" -> {
                if (processDirectory == RepositoryServiceSpec.VALID_GITHUB_REPO_PATH) {
                    "master"
                } else null
            }
        }
    }

    @Override
    int getProcessExitCode(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        return 0
    }

    private static <T> T mockParseResult(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException {
        final String command = processBuilder.command().stream().collect(Collectors.joining(" "))
        return switch (command) {
            case "gh api user" -> (T) new GithubUser("id", "testUser", "http://example.com/", "http://example.com/avatar.png", Optional.empty(), UserType.USER)
            case "gh repo view --json id,createdAt,description,homepageUrl,isArchived,isPrivate," +
                    "licenseInfo,name,nameWithOwner,owner,primaryLanguage,url,visibility" -> {
                (T) newGithubRepository("id")
            }
            case "gh repo view repo --json latestRelease,licenseInfo,stargazerCount" -> {
                (T) new RepositoryMetadataResponse(Optional.empty(), Optional.empty(), 1)
            }
            case "gh pr list --repo repo --search \"assignee:@me OR review-requested:@me\" " +
                    "--limit 1000 " +
                    "--json id,assignees,additions,author,changedFiles,closed,createdAt,deletions,isDraft,labels,mergeStateStatus,mergeable,number,state,statusCheckRollup,title,updatedAt,url" ->
                (T) ImmutableList.of(newPullRequest("id-1"), newPullRequest("id-2"))
            case "gh pr list --repo repo --search \"assignee:@me OR review-requested:@me\" --limit 1000 | wc -l"->
                (T) 1
            case "gh api /user/orgs --paginate" ->
                (T) ImmutableList.of(newGithubOrganization("id-1"), newGithubOrganization("id-2"))
            case "gh search repos --owner=Org \"testrepo\" --limit 1000" ->
                (T) ImmutableSet.of(newGithubRepository("id-search"))
            default -> null
        }
    }

    private static Organization newGithubOrganization(String id) {
        return new Organization(
                id,
                "org",
                "url",
                "url"
        )
    }

    private static PullRequest newPullRequest(String id) {
       return new PullRequest(
               id,
               new PullRequestAuthor("id", "test", Optional.empty()),
               "Title",
               false,
               false,
               "url",
               0,
               0,
               0,
               0,
               ZonedDateTime.now(),
               Optional.empty(),
               PullRequestStatus.OPEN,
               ImmutableSet.of(),
               ImmutableList.of(),
               MergeStateStatus.CLEAN,
               MergeableState.MERGEABLE
       )
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
                RepositoryVisibility.PUBLIC,
        )
    }
}
