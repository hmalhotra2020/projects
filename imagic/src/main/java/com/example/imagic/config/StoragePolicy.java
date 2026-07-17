package com.example.imagic.config;

import com.example.imagic.service.store.BaseStore;
import com.example.imagic.service.store.StoreType;
import lombok.Data;

@Data
public class StoragePolicy {
    private Integer ttl;
    private Long maxSize = 31457280l;
    private StoreType storeType = StoreType.DB_STORE;
    private String dirPath = BaseStore.DEFAULT_BASE_PATH;

}
