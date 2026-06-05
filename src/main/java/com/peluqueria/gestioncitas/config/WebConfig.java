package com.peluqueria.gestioncitas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de seguridad CORS para la aplicación
 * Controla qué orígenes pueden acceder a la API REST
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Orígenes permitidos (desarrollo local)
                // En producción, especificar solo el dominio real
                .allowedOriginPatterns("*")  // Permitir todos los orígenes en desarrollo
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(false)  // Cambiado a false para mayor compatibilidad
                .maxAge(3600);
    }
}