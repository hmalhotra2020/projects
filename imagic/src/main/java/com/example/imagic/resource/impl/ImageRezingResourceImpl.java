package com.example.imagic.resource.impl;

import com.example.imagic.aop.Monitored;
import com.example.imagic.resource.ImageResizingResource;
import com.example.imagic.service.resize.ResizerService;
import com.example.imagic.service.resize.ResizerServiceGMImpl;
import com.example.imagic.service.resize.ResizerServiceJVipsImpl;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ImageRezingResourceImpl implements ImageResizingResource {

    Logger logger = LoggerFactory.getLogger(ImageRezingResourceImpl.class);

    @Autowired
    ResizerService resizerService;

    @Autowired
    ResizerServiceGMImpl resizerServiceGMImpl;

    @Autowired
    ResizerServiceJVipsImpl resizerServiceJVips;

    @Override
    @Timed(value = "resize", description = "Resize method with single URL", extraTags = {"level","1"})
    @Monitored
    public void resize(HttpServletResponse response, String clientId, String url, int width, int height) throws IOException {
        logger.info("opName={},url={},width={},height={}", "resize", url, width, height);
        String filePath = resizerService.resize(clientId, url, width, height);
        try {
            InputStream in = Files.newInputStream(Paths.get(filePath));
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        } catch (IOException e) {
            logger.error("type=error, opName={}, err={}, url={}", "resize", e.getMessage(), url);
            throw e;
        }
    }

    @Override
    @Timed(value = "resizeInMemory", description = "Resize method with single URL", extraTags = {"level","1"})
    @Monitored
    public void resizeGM(HttpServletResponse response, String clientId, String url, int width, int height) {
        logger.info("opName={},url={},width={},height={}", "resize", url, width, height);
        String filePath = resizerServiceGMImpl.resize(clientId, url, width, height);
        try {
            InputStream in = Files.newInputStream(Paths.get(filePath));
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        } catch (IOException e) {
            logger.error("type=error, opName={}, err={}, url={}", "resize", e.getMessage(), url);
            e.printStackTrace();
        }
    }

    @Override
    @Timed(value = "resizeInMemory", description = "Resize method with single URL", extraTags = {"level","1"})
    @Monitored
    public void resizeInMemoryVips(HttpServletResponse response, String url, int width, int height) {
        logger.info("opName={},url={},width={},height={}", "resize", url, width, height);
        byte[] bytes = resizerServiceJVips.resize(url, width, height);
        try {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        } catch (IOException e) {
            logger.error("type=error, opName={}, err={}, url={}", "resize", e.getMessage(), url);
        }
    }

    public List<String> resize(MultipartFile file, int width, int height) {
        return null;
    }

    public List<String> resize(MultipartFile[] file, int width, int height) {
        return null;
    }

}
