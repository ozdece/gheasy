package com.ozdece.gheasy.github.repository.model;

import java.time.ZonedDateTime;

public record LatestRelease(String name, String url, ZonedDateTime publishedAt) {}
