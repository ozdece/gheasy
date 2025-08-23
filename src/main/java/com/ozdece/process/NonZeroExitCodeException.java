package com.ozdece.process;

public class NonZeroExitCodeException extends RuntimeException {
    public NonZeroExitCodeException(String command, int nonZeroExitCode) {
        super(String.format("\"%s\" command returned exit code %d", command, nonZeroExitCode));
    }
}
