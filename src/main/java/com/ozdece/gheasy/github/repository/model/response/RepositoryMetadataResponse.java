package com.ozdece.gheasy.github.repository.model.response;

import com.ozdece.gheasy.github.repository.model.LatestRelease;

import java.util.Optional;

public record RepositoryMetadataResponse(Optional<LicenseInfoResponse> licenseInfo, Optional<LatestRelease> latestRelease, int stargazerCount) {}

