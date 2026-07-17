package org.example.tug;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.tug.service.UrlService;
import org.junit.Assert;

@Data
@Slf4j
public class App {

    public static void main(String[] args) {

        String originalUrl = "https://dzone.com/";
        UrlService urlService = new UrlService();

        String tinyUrl = urlService.getTinyUrl(originalUrl);
        String reverseUrl = urlService.getOriginalUrl(tinyUrl);

        log.info("TinyUrl: {}", tinyUrl);
        log.info("Back to Original: {}", reverseUrl);

        if(!originalUrl.equals(reverseUrl)) { // collision
            tinyUrl = urlService.getTinyUrl(originalUrl);
            reverseUrl = urlService.getOriginalUrl(tinyUrl);
        }

        Assert.assertEquals("Wrong URL retrieved", originalUrl, reverseUrl);
        reverseUrl = urlService.getOriginalUrl2(tinyUrl);
        Assert.assertEquals("Wrong URL retrieved", originalUrl, reverseUrl);
    }

}
