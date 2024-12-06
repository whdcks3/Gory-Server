package com.whdcks3.portfolio.gory_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String securitrySchemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("고리 API")
                        .description("고리 앱 API 문서")
                        .version("1.0.0")
                        .contact(new Contact().email("sj012944@gmail.com").url("https://seojongchan-dev.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitrySchemeName))
                .components(new Components().addSecuritySchemes(securitrySchemeName,
                        new SecurityScheme()
                                .name(securitrySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
