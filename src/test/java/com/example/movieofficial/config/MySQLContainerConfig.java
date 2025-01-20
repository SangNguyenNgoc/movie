package com.example.movieofficial.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MySQLContainerConfig {

    @Bean
    @ServiceConnection
    MySQLContainer<?> mySQLContainer() {
        var containers = new MySQLContainer<>("mysql:8.0");
        containers.withInitScript("data/insertsql/schema.sql");
        return containers;
    }
}
