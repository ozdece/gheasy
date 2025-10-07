package com.ozdece.gheasy.github.organization;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.organization.model.Organization;
import reactor.core.publisher.Mono;

public interface OrganizationService {
    Mono<ImmutableList<Organization>> getOrganizations();
}
