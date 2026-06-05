package com.peluqueria.gestioncitas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;

/**
 * Clase principal de la aplicación Spring Boot
 * Sistema de Gestión de Citas para Peluquería
 * 
 * @SpringBootApplication es una anotación compuesta que incluye:
 * - @Configuration: Indica que la clase contiene definiciones de beans
 * - @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot
 * - @ComponentScan: Escanea el paquete actual y subpaquetes buscando componentes
 * 
 * @author TFG DAM
 * @version 1.0.0
 */
@SpringBootApplication
public class GestionCitasApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(GestionCitasApplication.class, args);
        System.out.println("\n" +
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║                                                              ║\n" +
            "║   🌟 Sistema de Gestión de Citas - Peluquería 🌟            ║\n" +
            "║                                                              ║\n" +
            "║   ✅ Aplicación iniciada correctamente                      ║\n" +
            "║   🌐 API REST disponible en: http://localhost:8080          ║\n" +
            "║   📚 Documentación: /api/docs                                ║\n" +
            "║                                                              ║\n" +
            "║   Endpoints disponibles:                                     ║\n" +
            "║   • GET    /api/clientes                                     ║\n" +
            "║   • GET    /api/servicios                                    ║\n" +
            "║   • GET    /api/citas                                        ║\n" +
            "║                                                              ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n"
        );
    }

    /**
     * Bean de ModelMapper para mapear entre entidades y DTOs
     * 
     * ModelMapper facilita la conversión automática entre objetos
     * de diferentes tipos que tienen propiedades con nombres similares
     * 
     * @return Instancia configurada de ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configuración permisiva: ignora ambigüedades en el mapeo
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }
}