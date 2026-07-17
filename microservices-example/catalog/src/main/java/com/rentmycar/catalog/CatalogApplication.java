package com.rentmycar.catalog;

import com.rentmycar.catalog.service.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class CatalogApplication implements CommandLineRunner {

	@Autowired
	DataLoader dataService;

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		dataService.loadData();
	}

	public void logback()	{

	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
