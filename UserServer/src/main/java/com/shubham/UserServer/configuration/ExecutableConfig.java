package com.shubham.UserServer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ExecutableConfig {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public PasswordEncoder getPE(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }
}
