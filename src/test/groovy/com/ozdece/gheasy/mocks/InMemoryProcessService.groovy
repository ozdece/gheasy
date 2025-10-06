package com.ozdece.gheasy.mocks

import com.fasterxml.jackson.core.type.TypeReference
import com.ozdece.gheasy.github.auth.model.GithubUser
import com.ozdece.gheasy.github.auth.model.UserType
import com.ozdece.gheasy.github.repository.GithubRepositoryServiceSpec
import com.ozdece.gheasy.github.repository.model.GithubRepository
import com.ozdece.gheasy.github.repository.model.PrimaryLanguage
import com.ozdece.gheasy.github.repository.model.RepositoryOwner
import com.ozdece.gheasy.github.repository.model.RepositoryVisibility
import com.ozdece.gheasy.github.repository.model.response.GhRepositoryMetadataResponse
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
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException {
        return mockParseResult(processBuilder, resultObjectClass, processResponse)
    }

    @Override
    String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        final String command = processBuilder.command().stream().collect(Collectors.joining(" "))
        final String processDirectory = processBuilder.directory().getAbsolutePath()

        return switch (command) {
            case "git remote get-url origin" -> {
                if (processDirectory == GithubRepositoryServiceSpec.VALID_GITHUB_REPO_PATH) {
                    "git@github.com"
                }
                else null
            }
            case "git branch --show-current" -> {
                if (processDirectory == GithubRepositoryServiceSpec.VALID_GITHUB_REPO_PATH) {
                    "master"
                } else null
            }
        }
    }

    @Override
    int getProcessExitCode(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        return 0
    }

    private <T> T mockParseResult(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException {
        final String command = processBuilder.command().stream().collect(Collectors.joining(" "))
        return switch (command) {
            case "gh api user" -> (T) new GithubUser("id", "testUser", "http://example.com/", "http://example.com/avatar.png", Optional.empty(), UserType.USER)
            case "git rev-parse --is-inside-work-tree" -> {
                final String processDirectory = processBuilder.directory().getAbsolutePath()

                if (processDirectory == GithubRepositoryServiceSpec.VALID_GITHUB_REPO_PATH) {
                   (T) true
                } else if (processDirectory == GithubRepositoryServiceSpec.INVALID_GITHUB_REPO_PATH) {
                   (T) true
                }
                else null
            }
            case "gh repo view --json id,createdAt,description,homepageUrl,isArchived,isPrivate," +
                    "licenseInfo,name,nameWithOwner,owner,primaryLanguage,url,visibility" -> {
                (T) newGithubRepository("id")
            }
            case "gh repo view --json latestRelease,licenseInfo,stargazerCount" -> {
                (T) new GhRepositoryMetadataResponse(Optional.empty(), Optional.empty(), 1)
            }
            default -> null
        }
    }

    private static GithubRepository newGithubRepository(String id) {
        return new GithubRepository(
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
