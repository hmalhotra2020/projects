package com.example.imagic.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Data
public class AppConfig {

    @Autowired
    private Environment env;

    @Value("${storeType:1}")
    private Integer storeType;

}
