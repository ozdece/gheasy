package com.ozdece.gheasy.process;

import java.io.IOException;

public interface ProcessService {

    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass) throws IOException, InterruptedException;
    <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException;
    String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException;

    int getProcessExitCode(ProcessBuilder processBuilder) throws IOException, InterruptedException;

}
