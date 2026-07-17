package com.example.imagic.service.store;

import com.example.imagic.service.IStore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
public interface BaseStore extends IStore {

    String preferedStore = "FS";
    String DEFAULT_BASE_PATH = "/Users/hemantm/work/tmp";

    default String defaultDirectoryName(String clientId)  {
        String name = clientId + "-" + RandomStringUtils.randomAlphanumeric(8);
        return name;
    }

    String directoryName(String clientId);
    Boolean storeFile(String clientId, File image);

}
