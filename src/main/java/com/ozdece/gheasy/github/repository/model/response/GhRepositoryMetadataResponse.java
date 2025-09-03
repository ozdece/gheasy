package com.ozdece.gheasy.github.repository.model.response;

import com.ozdece.gheasy.github.repository.model.LatestRelease;

import java.util.Optional;

public record GhRepositoryMetadataResponse(Optional<LicenseInfo> licenseInfo, Optional<LatestRelease> latestRelease, int stargazerCount) {}

