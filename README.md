#  API REST Documentada com Spring Web e Swagger

1. Criando uma REST API
2. RestController
3. Documentando nossa API com Swagger
4. Habilitando o tratamento de exceções de negócio com handlers

## Descrição

Este projeto demonstra a criação de uma API RESTful com Spring Boot. Ele inclui configuração para documentação de API usando Springdoc OpenAPI (Swagger).

## Requisitos

- Java 17
- Maven 3.8.1+

## Configuração do Projeto

### `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>dio</groupId>
    <artifactId>my-first-web-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>my-first-web-api</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Springdoc OpenAPI for Swagger Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.6.0</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-ui</artifactId>
            <version>1.7.0</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-webmvc-core</artifactId>
            <version>1.7.0</version>
        </dependency>
        <!-- Spring Boot Starter Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Estrutura do Projeto

A estrutura básica do projeto é a seguinte:

```
src/main/java
|-- dio
|   |-- web
|       |-- api
|           |-- controller
|               |-- MyController.java
|           |-- doc
|               |-- SwaggerConfig.java
|           |-- handler
|               |-- GlobalExceptionHandler.java
|-- application
    |-- MyApplication.java
```

## Anotações e Configurações

### `SwaggerConfig.java`

```java
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
```

### `GlobalExceptionHandler.java`

```java
package dio.web.api.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseError responseError(String message, HttpStatus statusCode) {
        ResponseError responseError = new ResponseError();
        responseError.setStatus("error");
        responseError.setError(message);
        responseError.setStatusCode(statusCode.value());
        return responseError;
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleGeneral(Exception e, WebRequest request) {
        if (e.getClass().isAssignableFrom(UndeclaredThrowableException.class)) {
            UndeclaredThrowableException exception = (UndeclaredThrowableException) e;
            return handleBusinessException((BusinessException) exception.getUndeclaredThrowable(), request);
        } else {
            String message = messageSource.getMessage("error.server", new Object[]{e.getMessage()}, null);
            ResponseError error = responseError(message, HttpStatus.INTERNAL_SERVER_ERROR);
            return handleExceptionInternal(e, error, headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @ExceptionHandler({BusinessException.class})
    private ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(), HttpStatus.CONFLICT);
        return handleExceptionInternal(e, error, headers(), HttpStatus.CONFLICT, request);
    }
}
```
## Como Rodar o Projeto

1. **Clone o repositório:**

```bash
git clone https://github.com/seu-usuario/my-first-web-api.git
```

2. **Navegue até o diretório do projeto:**

```bash
cd my-first-web-api
```

3. **Compile e execute o projeto usando Maven:**

```bash
mvn spring-boot:run
```

4. **Acesse a API:**

Abra seu navegador e acesse `http://localhost:8080` para ver a resposta "Welcome to my Spring Boot Web API".

5. **Documentação da API:**

Acesse a documentação da API gerada pelo Swagger em `http://localhost:8080/swagger-ui/index.html`.

