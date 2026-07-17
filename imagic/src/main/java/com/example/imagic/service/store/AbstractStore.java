package com.example.imagic.service.store;

import java.io.File;

public class AbstractStore implements BaseStore {

    @Override
    public String directoryName(String clientId) {
        return null;
    }

    @Override
    public Boolean storeFile(String clientId, File image) {
        return null;
    }
}
