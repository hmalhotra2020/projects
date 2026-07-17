package com.rentmycar.catalog.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmycar.catalog.model.CarInfo;
import com.rentmycar.catalog.model.PriceRequest;
import com.rentmycar.catalog.service.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@Slf4j
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    @GetMapping("/")
    public ResponseEntity<?> getAvailableCars()    {
        return new ResponseEntity<List<CarInfo>>(catalogService.getCarInfoList(), HttpStatus.OK);
    }

    @PostMapping("/car/price")
    public ResponseEntity<?> getBasePrice(@RequestBody PriceRequest priceRequest)
    throws Exception {

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String json = catalogService.getPriceInfo(priceRequest);
        CarInfo carInfo = objectMapper.readValue(json, CarInfo.class);

        log.info("carInfo :{}", carInfo);

        return new ResponseEntity<CarInfo>(carInfo, HttpStatus.OK);
    }

}
