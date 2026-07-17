package com.rentmycar.pricing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceInfo {

     private String brand;
     private String model;
     private String currentPrice;
}
