package com.ozdece.process;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ozdece.json.GheasyObjectMapper;
import io.vavr.control.Either;

import java.io.InputStream;

import static io.vavr.API.*;

public class ProcessServiceImpl implements ProcessService {

    private static final JsonMapper jsonMapper = GheasyObjectMapper.getJsonMapper();

    @Override
    public <T> Either<Throwable, T> getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass) {
        final String commandStr = String.join(" ", processBuilder.command());

        return Try(() -> {
            System.out.printf("Running command: %s\n", commandStr);
            final Process process = processBuilder.start();
            try (final InputStream inputStream = process.getInputStream()) {
                final byte[] bytes = inputStream.readAllBytes();

                final int exitCode = process.waitFor();

                if (exitCode != 0) {
                    // If the exit code is not zero, then throw non zero exit code exception
                    throw new NonZeroExitCodeException(commandStr, exitCode);
                }

                return jsonMapper.readValue(bytes, resultObjectClass);
            }
        }).toEither();
    }

    @Override
    public Either<Throwable, Integer> getProcessExitCode(ProcessBuilder processBuilder) {
        final String commandStr = String.join(" ", processBuilder.command());

        return Try(() -> {
            System.out.printf("Running command: %s\n", commandStr);
            final Process process = processBuilder.start();

            return process.waitFor();
        }).toEither();
    }
}
