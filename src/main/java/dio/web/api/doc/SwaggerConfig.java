package dio.web.api.doc;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

    private Contact contato() {
        return new Contact()
                .name("Seu nome")
                .url("http://www.seusite.com.br")
                .email("voce@seusite.com.br");
    }

    private Info informacoesApi() {
        return new Info()
                .title("Title - Rest API")
                .description("API exemplo de uso de Springboot REST API")
                .version("1.0")
                .termsOfService("Termo de uso: Open Source")
                .contact(this.contato())
                .license(new io.swagger.v3.oas.models.info.License()
                        .name("Licença - Sua Empresa")
                        .url("http://www.seusite.com.br"));
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(this.informacoesApi());
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("spring-public")
                .pathsToMatch("/**")
                .packagesToScan("dio.web.api.controller")
                .build();
    }
}
