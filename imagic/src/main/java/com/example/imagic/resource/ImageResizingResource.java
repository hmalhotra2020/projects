package com.example.imagic.resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*
ImageResizingController
	string url
	String[] urls
	MultiPartFile image
	MultiPartFile images[]
	string url, type
	String[] urls, types
	string url, type, size
	String[] urls, types, sizes
*/
@RestController
public interface ImageResizingResource extends BaseController   {

    @GetMapping(value = "/v1/resize")
    public void resize(HttpServletResponse response,
                       @RequestHeader("clientId") String clientId,
                       @RequestParam("url") String url, @RequestParam("width") int width,
                       @RequestParam("height") int height) throws IOException;

    @GetMapping("/v1/resize1")
    public void resizeGM(HttpServletResponse response,
                               @RequestHeader("clientId") String clientId,
                       @RequestParam("url") String url,
                       @RequestParam("width") int width,
                       @RequestParam("height") int height);

    @GetMapping("/v1/resize2")
    public void resizeInMemoryVips(HttpServletResponse response,
                               @RequestParam("url") String url,
                               @RequestParam("width") int width,
                               @RequestParam("height") int height);

    @GetMapping("/v1/resize3")
    public List<String> resize(@RequestParam("image") MultipartFile file, @RequestParam("width") int width, @RequestParam("height") int height);

    @GetMapping("/v1/resize4")
    public List<String> resize(@RequestParam("images") MultipartFile file[], @RequestParam("width") int width, @RequestParam("height") int height);

}
