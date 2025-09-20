package com.ozdece.gheasy.process;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public interface ProcessService {

    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass) throws IOException, InterruptedException;
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, TypeReference<T> typeReference) throws IOException, InterruptedException;
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException;
    String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException;

    int getProcessExitCode(ProcessBuilder processBuilder) throws IOException, InterruptedException;

}
