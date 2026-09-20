package com.trilhamais.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao OpenAPI 3.0 / Swagger UI para a aplicacao Trilha+.
 * Configura esquema de seguranca Bearer JWT e metadados institucionais da API.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trilha+ API — Documentação Oficial")
                        .description("Back-end RESTful da plataforma de estudos Trilha+, desenvolvida para UNINASSAU Caruaru. " +
                                "Permite autenticação com JWT, gestão de matérias e tópicos, dúvidas com IA Groq (Llama 3.3), " +
                                "simulados avaliativos e diagnóstico inteligente de desempenho acadêmico.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT retornado pelo endpoint /api/auth/login")));
    }
}
