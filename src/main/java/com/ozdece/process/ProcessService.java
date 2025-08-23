package com.ozdece.process;

import io.vavr.control.Either;

public interface ProcessService {

    <T> Either<Throwable, T> getThenParseProcessOutput(ProcessBuilder processBuilder, Class<T> resultObjectClass);
    Either<Throwable, Integer> getProcessExitCode(ProcessBuilder processBuilder);

}
