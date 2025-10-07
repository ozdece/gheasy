package com.ozdece.gheasy.github.organization.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.organization.OrganizationService;
import com.ozdece.gheasy.github.organization.model.Organization;
import com.ozdece.gheasy.process.ProcessResponse;
import com.ozdece.gheasy.process.ProcessService;
import reactor.core.publisher.Mono;

public class OrganizationServiceImpl implements OrganizationService {

    private final ProcessService processService;

    private final ImmutableList<String> allOrganizationsCommand = ImmutableList.of("gh", "api", "/user/orgs", "--paginate");

    public OrganizationServiceImpl(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public Mono<ImmutableList<Organization>> getOrganizations() {
        final ProcessBuilder processBuilder = new ProcessBuilder(allOrganizationsCommand);
        final TypeReference<ImmutableList<Organization>> typeReference = new TypeReference<>(){};

        return Mono.fromCallable(() -> processService.getThenParseProcessOutput(processBuilder, typeReference, ProcessResponse.API));
    }
}