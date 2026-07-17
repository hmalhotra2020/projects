package com.example.imagic.service.resize;

import com.example.imagic.aop.Monitored;
import com.example.imagic.config.AppConfig;
import com.example.imagic.service.ClientDataService;
import com.example.imagic.service.DownloadService;
import com.example.imagic.service.store.BaseStore;
import com.example.imagic.service.store.StoreFactory;
import org.apache.commons.io.IOUtils;
import org.gm4java.im4java.GMBatchCommand;
import org.im4java.core.*;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ResizerServiceGMImpl {

    Logger logger = LoggerFactory.getLogger(ResizerServiceGMImpl.class);

    @Autowired
    DownloadService downloadService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    StoreFactory storeFactory;

    @Autowired
    ClientDataService clientDataService;

    @Monitored
    public String resize(String clientId, String url, int width, int height) {
        BaseStore store = storeFactory.getStore(appConfig.getStoreType());
        String directoryName = store.directoryName(clientId);
        byte[] bytes = null;

        Triplet<String,String,String> fileNameTriplet = downloadService.download(url, directoryName);
        ConvertCmd cmd = new ConvertCmd();
        //GMBatchCommand cmd = new GMBatchCommand();
        //IMOps imOps = new IMOperation();
        //GMOps gmOps = new GMOperation();
        GMOperation op = new GMOperation();
        String targetFileName = BaseStore.DEFAULT_BASE_PATH + "/" + directoryName + "/" + fileNameTriplet.getValue0() + "_" + width + "_" + height + "." + fileNameTriplet.getValue1();

        op.addImage(fileNameTriplet.getValue2());
        op.resize(width, height);
        op.addImage(Paths.get(targetFileName).toString());

        try {
            cmd.run(op);
        }catch(Exception e) {
            e.printStackTrace();
        }

        return targetFileName;
    }
}
