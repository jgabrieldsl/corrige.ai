package com.corrigeai.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica para todos os endpoints
                .allowedOrigins("*") // Permite qualquer origem
                .allowedMethods("*") // Permite todos os métodos
                .allowedHeaders("*") // Permite todos os headers
                .allowCredentials(false); // Deve ser false quando allowedOrigins é "*"
    }
}