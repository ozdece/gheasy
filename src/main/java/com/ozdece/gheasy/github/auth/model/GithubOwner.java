package com.ozdece.gheasy.github.auth.model;

public record GithubOwner(
        UserType userType,
        String name,
        String avatarUrl
        ) {}
