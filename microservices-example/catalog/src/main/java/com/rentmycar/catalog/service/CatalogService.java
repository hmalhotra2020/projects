package com.rentmycar.catalog.service;

import com.rentmycar.catalog.config.Config;
import com.rentmycar.catalog.model.CarInfo;
import com.rentmycar.catalog.model.PriceRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Slf4j
public class CatalogService {

    private List<CarInfo> carInfoList = new ArrayList<>();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Config config;

    public String getPriceInfo(PriceRequest priceRequest)   {
        log.info("Getting price from url: {}", config.getPricingUrl());
        ResponseEntity<String> response = restTemplate.postForEntity(
                config.getPricingUrl(),
                priceRequest, String.class);
        return response.getBody();
    }

}
