package com.rentmycar.pricing.web;

import com.google.common.collect.Table;
import com.rentmycar.pricing.dto.PriceRequest;
import com.rentmycar.pricing.model.PriceInfo;
import com.rentmycar.pricing.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class PricingController {

    @Autowired
    PricingService pricingService;

    @PostMapping("/price")
    public ResponseEntity<?> getPriceInfoForCars(@RequestBody PriceRequest priceRequest)    {
        
        return new ResponseEntity<PriceInfo>(
                pricingService.getPriceInfo(
                        priceRequest.getBrand(), priceRequest.getModel()
                ), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllPriceInfo()    {
        Table allPriceInfo = pricingService.getAllPriceInfo();

        return new ResponseEntity<Collection>(allPriceInfo.values(), HttpStatus.OK);
    }

}
