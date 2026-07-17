package com.example.imagic.service;

import com.example.imagic.aop.Monitored;
import com.example.imagic.config.Config;
import com.example.imagic.error.ExceptionResponse;
import com.example.imagic.error.exception.ApplicationException;
import com.example.imagic.error.exception.ErrorCode;
import com.example.imagic.service.store.BaseStore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DownloadService {

    @Autowired
    Config config;

    Logger log = LoggerFactory.getLogger(DownloadService.class);

    @Monitored
    public Triplet download(String url, String directoryName) {

        Pair nameContents = getExtension(url);
        String localNewName = getLocalNewName();
        String name = localNewName + "." + nameContents.getValue1();
        String path = BaseStore.DEFAULT_BASE_PATH + "/" + directoryName + "/" + name;

        try  {
            InputStream in = new URL(url).openStream();
            Files.copy(in, Paths.get(path));
            long size = Files.size(Paths.get(path));
            if(size > config.getStoragePolicy().getMaxSize()) {
                log.error("URL:{},Error:{}, Size for URL is greater then the Limit :{}", url, "download error",
                        FileUtils.byteCountToDisplaySize(config.getStoragePolicy().getMaxSize()));
                throw new ApplicationException(
                        new ExceptionResponse(ErrorCode.E_NS.getErrorCode(), "Size greater then allowed"));
            }
        }
        catch (IOException ioe) {
            log.error("Exception:{},opName:{},url:{}",ioe.getMessage(),"download",url);
            ApplicationException ae = new ApplicationException();
            ae.setExceptionResponse(
                    new ExceptionResponse(ErrorCode.E_MU.getErrorCode(), "Malformed URL Exception"));
            throw ae;
        }
        catch (Exception ex) {
            log.error("Exception:{},opName:{},url:{}",ex.getMessage(),"download",url);
            throw ex;
        }

        return new Triplet(localNewName, nameContents.getValue1(), path);
    }

    private String getLocalNewName() {
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(8);
        return randomAlphanumeric;
    }

    private Pair getExtension(String strUrl) {
        String extension = FilenameUtils.getExtension(strUrl);
        String baseName = FilenameUtils.getBaseName(strUrl);
        String name = FilenameUtils.getName(strUrl);
        return new Pair(baseName, extension);
    }

}
