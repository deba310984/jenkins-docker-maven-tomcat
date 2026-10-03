package com.debjit.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    void greetsNamedUser() {
        assertEquals("Hello, Debjit!", service.greet("Debjit"));
    }

    @Test
    void trimsWhitespace() {
        assertEquals("Hello, Debjit!", service.greet("  Debjit  "));
    }

    @Test
    void fallsBackToWorldWhenNull() {
        assertEquals("Hello, World!", service.greet(null));
    }

    @Test
    void fallsBackToWorldWhenBlank() {
        assertEquals("Hello, World!", service.greet("   "));
    }
}
