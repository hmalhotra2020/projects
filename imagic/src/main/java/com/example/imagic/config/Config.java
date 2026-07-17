package com.example.imagic.config;

import com.example.imagic.service.store.StoreFactory;
import com.example.imagic.service.store.impl.FileStore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@ConfigurationProperties
@Data
@Validated
public class Config {

    private boolean skipStoragePolicy;

    private boolean skipAccessPolicy;

    @Valid
    private StoragePolicy storagePolicy = new StoragePolicy();

    private AccessPolicy accessPolicy;

    @Autowired
    FileStore fileStore;

    @Bean(name = "storeFactory")
    public StoreFactory storeFactory() {
        StoreFactory factory = new StoreFactory();
        factory.setStore(fileStore);
        return factory;
    }
}
