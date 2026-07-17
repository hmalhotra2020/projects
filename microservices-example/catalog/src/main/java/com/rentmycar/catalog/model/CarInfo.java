package com.rentmycar.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarInfo {

     private String brand;
     private String engine;
     private String mileage;
     private String[] models;

     Float currentPrice;
}
