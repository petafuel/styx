package net.petafuel.styx.api.util;

import net.petafuel.styx.api.exception.HealthCheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class HealthCheckUnitTest {

    private static final String testProp = "healthcheck.test.prop";

    private static Stream<Arguments> provideCorrectValidations() {
        return Stream.of(
                Arguments.of("test string", HealthCheck.CHECK.STRING),
                Arguments.of("123", HealthCheck.CHECK.INTEGER),
                Arguments.of("false", HealthCheck.CHECK.BOOL),
                Arguments.of("true", HealthCheck.CHECK.BOOL),
                Arguments.of("./.gitignore", HealthCheck.CHECK.FILE_EXISTS)
        );
    }

    @BeforeEach
    public void clearSystemProperties() {
        System.clearProperty(testProp);
        HealthCheck.reset();
    }

    @ParameterizedTest
    @MethodSource("provideCorrectValidations")
    void testValidation(String value, HealthCheck.CHECK check) {
        System.setProperty(testProp, value);
        HealthCheck.addCheck(testProp, check);
        Assertions.assertDoesNotThrow(HealthCheck::validateConfiguration);
    }

    @ParameterizedTest
    @EnumSource(HealthCheck.CHECK.class)
    void testEmptyStringValidation(HealthCheck.CHECK check) {
        System.setProperty(testProp, "");
        HealthCheck.addCheck(testProp, check);
        Assertions.assertThrows(HealthCheckException.class, HealthCheck::validateConfiguration);
    }

    @ParameterizedTest
    @EnumSource(HealthCheck.CHECK.class)
    void testNullStringValidation(HealthCheck.CHECK check) {
        HealthCheck.addCheck(testProp, check);
        Assertions.assertThrows(HealthCheckException.class, HealthCheck::validateConfiguration);
    }

    @ParameterizedTest
    @EnumSource(HealthCheck.CHECK.class)
    void testNonPresentStringOptionalValidation(HealthCheck.CHECK check) {
        HealthCheck.addOptionalCheck(testProp, check);
        Assertions.assertDoesNotThrow(HealthCheck::validateConfiguration);
    }

    @ParameterizedTest
    @EnumSource(HealthCheck.CHECK.class)
    void testPresentStringOptionalValidation(HealthCheck.CHECK check) {
        System.setProperty(testProp, "");
        HealthCheck.addOptionalCheck(testProp, check);
        Assertions.assertThrows(HealthCheckException.class, HealthCheck::validateConfiguration);
    }
}
