package com.sopromadze.blogapi;

import com.sopromadze.blogapi.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.Jsr310Converters;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring-Boot-Blog-REST-API",
                description = "A RESTful webservice for managing blog posts, built with Spring Boot. Includes JWT-based authentication and MySQL for data storage.",
                summary = "Build Restful CRUD API for a blog using Spring Boot, Mysql, JPA and Hibernate.",
                version = "1.0.1"
        )
)

@SpringBootApplication
@EntityScan(basePackageClasses = {BlogApiApplication.class, Jsr310Converters.class})

public class BlogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}