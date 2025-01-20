package com.example.movieofficial.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.support.converter.JsonMessageConverter;

//@Configuration
public class KafkaConfig {

//    @Bean
    public JsonMessageConverter converter() {
        return new JsonMessageConverter();
    }

//    @Bean
    public NewTopic statisticTrips() {
        return new NewTopic("create-image", 1, (short) 1);
    }
}
