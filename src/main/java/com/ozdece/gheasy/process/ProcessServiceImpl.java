package com.ozdece.gheasy.process;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ozdece.gheasy.json.GheasyObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ProcessServiceImpl implements ProcessService {

    private static final JsonMapper snakeCaseJsonMapper = GheasyObjectMapper.getSnakeCaseJsonMapper();
    private static final JsonMapper defaultJsonMapper = GheasyObjectMapper.getDefaultJsonMapper();

    @Override
    public <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass) throws IOException, InterruptedException {
        return defaultJsonMapper.readValue(getProcessOutputBytes(processBuilder), resultObjectClass);
    }

    @Override
    public <T> T getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass, ProcessResponse processResponse) throws IOException, InterruptedException {
        final JsonMapper mapper = switch (processResponse) {
            case API -> snakeCaseJsonMapper;
            case CLI -> defaultJsonMapper;
        };

        return mapper.readValue(getProcessOutputBytes(processBuilder), resultObjectClass);
    }

    @Override
    public String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        return new String(getProcessOutputBytes(processBuilder), StandardCharsets.UTF_8);
    }

    private byte[] getProcessOutputBytes(ProcessBuilder processBuilder) throws IOException, InterruptedException {

        final String commandStr = String.join(" ", processBuilder.command());
        System.out.printf("Running command: %s\n", commandStr);

        final Process process = processBuilder.start();

        try (final InputStream inputStream = process.getInputStream()) {
            final byte[] bytes = inputStream.readAllBytes();
            final int exitCode = process.waitFor();

            if (exitCode != 0) {
                // If the exit code is not zero, then throw non zero exit code exception
                throw new NonZeroExitCodeException(commandStr, exitCode);
            }

            return bytes;
        }
    }

    @Override
    public int getProcessExitCode(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        final String commandStr = String.join(" ", processBuilder.command());
        System.out.printf("Running command: %s\n", commandStr);
        final Process process = processBuilder.start();

        return process.waitFor();
    }
}
