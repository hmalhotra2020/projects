package com.example.imagic.service.store;

import com.example.imagic.service.store.impl.FileStore;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class StoreFactory {

    private BaseStore store;

    public BaseStore getStore(Integer storeType) {
        if(storeType == StoreType.FILE_STORE.getStoreType())
            return store;
        return null;
    }
}
