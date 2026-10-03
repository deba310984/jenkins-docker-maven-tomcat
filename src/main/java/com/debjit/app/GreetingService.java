package com.debjit.app;

/**
 * Small piece of business logic, deliberately separated from the servlet so it
 * can be unit-tested without a servlet container.
 */
public class GreetingService {

    /**
     * Build a greeting for the given name, falling back to "World" when the
     * name is null or blank.
     */
    public String greet(String name) {
        String target = (name == null || name.isBlank()) ? "World" : name.trim();
        return "Hello, " + target + "!";
    }
}
