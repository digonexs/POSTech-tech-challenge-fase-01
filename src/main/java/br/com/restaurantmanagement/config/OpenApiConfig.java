package br.com.restaurantmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management API")
                        .version("1.0.0")
                        .description("""
                                API de gestão compartilhada para restaurantes — POSTech Fase 01.

                                **Autenticação:** Faça POST em `/api/v1/auth/login` para obter um token JWT.
                                Clique em **Authorize** e informe: `Bearer <token>`.

                                Rotas públicas (sem token): `POST /api/v1/users` e `POST /api/v1/auth/login`.
                                """)
                        .contact(new Contact()
                                .name("POSTech - Arquitetura e Desenvolvimento Java")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT obtido no endpoint de login")
                        )
                );
    }
}
