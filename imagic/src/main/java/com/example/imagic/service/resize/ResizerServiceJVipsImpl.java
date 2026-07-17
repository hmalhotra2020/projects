package com.example.imagic.service.resize;

import com.criteo.vips.VipsContext;
import com.criteo.vips.VipsImage;
import com.criteo.vips.enums.VipsImageFormat;
import com.example.imagic.aop.Monitored;
import com.example.imagic.config.AppConfig;
import com.example.imagic.service.ClientDataService;
import com.example.imagic.service.DownloadService;
import com.example.imagic.service.store.StoreFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ResizerServiceJVipsImpl {

    Logger logger = LoggerFactory.getLogger(ResizerService.class);

    @Autowired
    DownloadService downloadService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    StoreFactory storeFactory;

    @Autowired
    ClientDataService clientDataService;

    @PostConstruct
    public void init()  {
        VipsContext.setLeak(true);
    }

    @Monitored
    public byte[] resize(String url, int width, int height) {

        byte[] contents = null, bytes = null;
        try {
            contents = IOUtils.toByteArray(new URL(url));
            VipsImage image = new VipsImage(contents, contents.length);
            logger.info("Orig size to w:{}, h:{}", image.getWidth(), image.getHeight());
            image.resize(width,height,false);
            logger.info("Resized to w:{}, h:{}", image.getWidth(), image.getHeight());
            bytes = image.writeToArray(VipsImageFormat.JPG, false);
            image.release();
        }catch(Exception e) {
            logger.error("Error processing the file: {}", url);
            e.printStackTrace();
        }

        return bytes;
    }
}
