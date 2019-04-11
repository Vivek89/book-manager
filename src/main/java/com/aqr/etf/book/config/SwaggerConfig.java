package com.aqr.etf.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() throws UnknownHostException {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.aqr.etf.book"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }


    private ApiInfo metaData() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();
        return new ApiInfo(
                "Book Manager REST API",
                "Spring Boot REST API for AQR Book Manager",
                "1.0",
                "Terms of Service",
                new Contact("Vivek Mishra", hostname, "vivek_k_mishra@ymail.com"),
                "Apache License Version 2.0", "API License URL",
                Collections.emptyList());
    }


}
