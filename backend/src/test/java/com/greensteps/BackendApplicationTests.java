package com.greensteps;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class BackendApplicationMainTest {

    @Test
    void main_doesNotThrow() {
        assertThatCode(() -> SpringApplication.from(BackendApplication::main).with().run())
                .doesNotThrowAnyException();
    }
}