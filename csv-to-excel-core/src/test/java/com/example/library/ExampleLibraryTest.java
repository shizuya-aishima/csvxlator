package com.example.library;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleLibraryTest {
    @Test
    void testGetGreeting() {
        ExampleLibrary library = new ExampleLibrary();
        assertEquals("Hello from My Library!", library.getGreeting());
    }
} 