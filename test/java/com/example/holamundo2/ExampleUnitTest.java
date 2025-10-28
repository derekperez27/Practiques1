package com.example.holamundo2;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void testSuma() {
        int resultado = 2 + 3;
        assertEquals(5, resultado);
    }

    @Test
    public void testValidarEmail() {
        String email = "test@example.com";
        assertTrue(email.contains("@") && email.endsWith(".com"));
    }
}
