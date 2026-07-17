package com.rentmycar.catalog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Data
@Component
public class Config {

    private String pricingUrl;

}
