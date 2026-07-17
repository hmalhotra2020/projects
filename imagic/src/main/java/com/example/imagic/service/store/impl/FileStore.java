package com.example.imagic.service.store.impl;

import com.example.imagic.model.CustomerStore;
import com.example.imagic.service.CommonDBService;
import com.example.imagic.service.store.BaseStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileStore implements BaseStore  {

    @Autowired
    CommonDBService commonDbService;

    @Override
    public String directoryName(String clientId)  {
        String dirName;
        CustomerStore customerStore;
        customerStore = commonDbService.getClientDirectory();
        if(customerStore == null || customerStore.getDirectory() == null) {
            dirName = this.defaultDirectoryName(clientId);

            customerStore = CustomerStore
                                                .builder()
                                                .directory(dirName)
                                                .preferedFileStore(BaseStore.preferedStore).build();

            createDirectory(BaseStore.DEFAULT_BASE_PATH + "/" + customerStore.getDirectory());
        }
        return customerStore.getDirectory();
    }

    @Override
    public Boolean storeFile(String clientId, File image) {
        return null;
    }

    private void createDirectory(String dirName) {
        //FileUtils.forceMkdir();
        //Files.createDirectory(Paths.get(dirName));
        //FileUtils.forceMkdir(new File(dirName));
        File theDir = new File(dirName);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        // chk to see if any exception
    }
}
