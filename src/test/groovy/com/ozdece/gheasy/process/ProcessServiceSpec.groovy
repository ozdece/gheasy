package com.ozdece.gheasy.process

import spock.lang.Specification

import java.nio.file.Files

class ProcessServiceSpec extends Specification {

    static String TEST_FILES_DIR = System.getProperty("java.io.tmpdir") + "/gheasy"

    static final String CORRUPTED_JSON_FILE_PATH = TEST_FILES_DIR + "/corrupted_json_file.json"
    static final String JSON_FILE_PATH = TEST_FILES_DIR + "/json_file.json"
    static final String SNAKE_CASE_JSON_FILE_PATH = TEST_FILES_DIR + "/snake_case_json.json"
    static final String CAMEL_CASE_JSON_FILE_PATH = TEST_FILES_DIR + "/camel_case_json.json"

    final ProcessService processService = new ProcessServiceImpl()

    def setupSpec() {
        final File tmpFile = new File(TEST_FILES_DIR);

        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }

        String output =  "{"
        final File corruptedJsonFile = new File(CORRUPTED_JSON_FILE_PATH)
        Files.write(corruptedJsonFile.toPath(), output.getBytes())
        System.out.println("Wrote \"${output}\" into ${corruptedJsonFile.getAbsolutePath()}")

        output = "true"
        final File booleanJsonFile = new File(JSON_FILE_PATH)
        Files.write(booleanJsonFile.toPath(), "true".getBytes())
        System.out.println("Wrote \"${output}\" into ${booleanJsonFile.getAbsolutePath()}")

        final String snakeCaseJson = "{\"name_surname\": \"John Doe\"}"
        final File snakeCaseJsonFile = new File(SNAKE_CASE_JSON_FILE_PATH)
        Files.write(snakeCaseJsonFile.toPath(), snakeCaseJson.getBytes())
        System.out.println("Wrote \"${snakeCaseJson}\" into ${snakeCaseJsonFile.getAbsolutePath()}")

        final String camelCaseJson = "{\"nameSurname\": \"John Doe\"}"
        final File camelCaseJsonFile = new File(CAMEL_CASE_JSON_FILE_PATH)
        Files.write(camelCaseJsonFile.toPath(), camelCaseJson.getBytes())
        System.out.println("Wrote \"${camelCaseJson}\" into ${camelCaseJsonFile.getAbsolutePath()}")
    }

    def "should not get and parse process output if output is not a valid JSON"() {
        given: 'A Process Builder'
        final ProcessBuilder processBuilder = new ProcessBuilder("cat", CORRUPTED_JSON_FILE_PATH)

        when: 'process output is read and trying to be parsed'
        processService.getThenParseProcessOutput(processBuilder, String.class)

        then: 'IOException is thrown'
        final def exception = thrown(IOException)
        exception.getMessage().startsWith("Cannot deserialize value of type `java.lang.String` from Object value")
    }

    def "should throw NonZeroExitCodeException if the command execution status is non-zero"() {
        given: 'A Process Builder'
        // grep command requires parameters
        final ProcessBuilder processBuilder = new ProcessBuilder("grep")

        when: 'process output is read and trying to be parsed'
        processService.getThenParseProcessOutput(processBuilder, String.class)

        then: 'IOException is thrown'
        thrown(NonZeroExitCodeException)
    }

    def "should throw IOException if the command does not exist or anything related"() {
        given: 'A Process Builder'
        // grep command requires parameters
        final ProcessBuilder processBuilder = new ProcessBuilder("broken_command")

        when: 'process output is read and trying to be parsed'
        processService.getThenParseProcessOutput(processBuilder, String.class)

        then: 'IOException is thrown'
        thrown(IOException)
    }

    def "should get and parse process output if output is a valid JSON"() {
        given: 'A Process Builder'
        final ProcessBuilder processBuilder = new ProcessBuilder("cat", JSON_FILE_PATH)

        when: 'process output is read and trying to be parsed'
        final boolean result = processService.getThenParseProcessOutput(processBuilder, Boolean.class)

        then: 'Boolean result should be parsed and parsed as true'
        result
    }

    def "should get and parse process output with different types of process responses (gh CLI and API responses)"() {
        given: 'A Process Builder for each snake case and camel case responses'
        final ProcessBuilder processBuilder = new ProcessBuilder("cat", filePath)

        when: 'process output is read and trying to be parsed'
        final Person person = processService.getThenParseProcessOutput(processBuilder, Person.class, processResponse)

        then: 'output should be parsed to Person object successfully'
        person.nameSurname() == "John Doe"

        where:
        filePath                  | processResponse
        SNAKE_CASE_JSON_FILE_PATH | ProcessResponse.API
        CAMEL_CASE_JSON_FILE_PATH | ProcessResponse.CLI
    }

    def "should get process output as String successfully"() {
        given: 'A process builder'
        final ProcessBuilder processBuilder = new ProcessBuilder("cat", JSON_FILE_PATH)

        when: 'process output is being parsed to String'
        final String output = processService.getProcessOutput(processBuilder)

        then: 'output should be as expected'
        output == "true"
    }

    def "should get process exit code successfully"() {
        given: 'A process builder'
        final ProcessBuilder processBuilder = new ProcessBuilder("cat", JSON_FILE_PATH)

        when: 'process output is being parsed to String'
        final int exitCode = processService.getProcessExitCode(processBuilder)

        then: 'output should be as expected'
        exitCode == 0
    }

    def cleanupSpec() {
        final File tmpFile = new File(TEST_FILES_DIR);
        boolean filesDeleted = tmpFile.deleteDir()

        if (filesDeleted) {
            println "Temporary files deleted successfully."
        } else {
            System.err.println("Deleting temporary files failed.")
        }

    }
}

record Person(String nameSurname) {}