package com.ozdece.gheasy.github.repository.model.response;

import com.ozdece.gheasy.github.repository.model.LatestRelease;

public record GhRepositoryMetadataResponse(LicenseInfo licenseInfo, LatestRelease latestRelease, int stargazerCount) {}

