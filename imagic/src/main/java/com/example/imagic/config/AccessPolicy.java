package com.example.imagic.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AccessPolicy {
    private Integer rateLimit;
}
