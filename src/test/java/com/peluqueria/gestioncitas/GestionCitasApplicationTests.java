package com.peluqueria.gestioncitas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test básico para verificar que el contexto de Spring Boot se carga correctamente
 */
@SpringBootTest
@ActiveProfiles("test")
class GestionCitasApplicationTests {

    @Test
    void contextLoads() {
        // Este test verifica que el contexto de la aplicación se carga sin errores
    }

}