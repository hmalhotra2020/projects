package com.rentmycar.catalog.service;

import com.rentmycar.catalog.model.CarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataLoader {

    @Autowired
    CatalogService catalogService;

    public void loadData()  {
        CarInfo carInfo1 = CarInfo.builder()
                .brand("Maruti Baleno")
                .engine("998 to 1248 cc")
                .mileage("19.56 to 27.39 kmpl")
                .models(new String[] {"Sigma 1.2", "Delta 1.2", "Sigma 1.3", "Zeta 1.2"})
                .build();
        CarInfo carInfo2 = CarInfo.builder()
                .brand("Tata Nexon")
                .engine("1198 to 1497 cc")
                .mileage("17.88 to 23.97 kmpl")
                .models(new String[] {"XE", "XM", "KARZ", "XT Plus"})
                .build();
        if(catalogService.getCarInfoList().size() == 0) {
            catalogService.getCarInfoList().add(carInfo1);
            catalogService.getCarInfoList().add(carInfo2);
        }
    }

}
