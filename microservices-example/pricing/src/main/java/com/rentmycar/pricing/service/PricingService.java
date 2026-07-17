package com.rentmycar.pricing.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.rentmycar.pricing.model.PriceInfo;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class PricingService {

    Table<String, String, String> pricingTable = HashBasedTable.create();

    @PostConstruct
    public void init()  {
        pricingTable.put("Maruti Baleno", "Sigma 1.2", "6.21");
        pricingTable.put("Maruti Baleno", "Delta 1.2", "7.22");
        pricingTable.put("Maruti Baleno", "Sigma 1.3", "7.58");
        pricingTable.put("Maruti Baleno", "Zeta 1.2", "7.9");

        pricingTable.put("Tata Nexon", "XE", "7.67");
        pricingTable.put("Tata Nexon", "XM", "8.68");
        pricingTable.put("Tata Nexon", "KARZ", "8.78");
        pricingTable.put("Tata Nexon", "XT Plus", "9.44");

        pricingTable.put("Huyndai Venue", "E", "6.7");
        pricingTable.put("Huyndai Venue", "S", "7.4");
        pricingTable.put("Huyndai Venue", "S Turbo", "8.46");
        pricingTable.put("Huyndai Venue", "SX Turbo", "9.79");

        pricingTable.put("Ford EcoSport", "Petrol Ambiente", "8.04");
        pricingTable.put("Ford EcoSport", "Petrol Trend", "8.84");
        pricingTable.put("Ford EcoSport", "Petrol Titanium", "9.63");
        pricingTable.put("Ford EcoSport", "Sports Petrol", "11.08");

    }

    public PriceInfo getPriceInfo(String brand, String model)   {
        String price = pricingTable.get(brand, model);
        if(price != null)
            return PriceInfo.builder().brand(brand).model(model).currentPrice(price).build();
        return null;
    }

    public Table getAllPriceInfo()  {
        return this.pricingTable;
    }
}
