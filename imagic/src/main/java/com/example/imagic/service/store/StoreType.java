package com.example.imagic.service.store;

import java.util.Arrays;

public enum StoreType {
    FILE_STORE(1), DB_STORE(2), MINIO_STORE(3), S3_STORE(4);

    private final int storeType;

    StoreType(int storeType) {
        this.storeType = storeType;
    }

    public int getStoreType() {
        return this.storeType;
    }

    public int getStoreType(String name) {
        StoreType storeType = Arrays.stream(values()).filter(value -> value.name().equals(name)).findFirst().orElse(null);
        return storeType.getStoreType();
    }
}
