package com.georgiytitov.movies;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "Movie Explorer API",
            version = "1.0",
            description = "Movie feeds, details and locally identified users' favorites."))
public class MovieApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieApplication.class, args);
  }
}
