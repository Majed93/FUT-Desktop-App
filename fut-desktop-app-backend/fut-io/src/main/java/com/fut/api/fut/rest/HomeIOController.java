package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.requests.HttpClientImpl;
import com.fut.api.fut.requests.IHttpClient;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.concurrent.Future;

@RequestMapping("/io")
@RestController
@Slf4j
public class HomeIOController {

    private final FileUtils fileUtils;

    private final IHttpClient httpClient = new HttpClientImpl();

    private final IFutClient futClient;

    @Autowired
    public HomeIOController(FileUtils fileUtils, IFutClient futClient) {
        this.fileUtils = fileUtils;
        this.futClient = futClient;
    }

    @GetMapping
    public String index() {
        log.info("user dir: " + System.getProperty("user.dir"));
        log.info("parent: " + new File(".").getAbsoluteFile().getParent());

        fileUtils.readFile(1989386178, FileUtils.cookieExt);

        try {
            Future<HttpMessage> httpMessageFuture = httpClient.GetAsync("http://google.com");
            HttpMessage httpMessage = httpMessageFuture.get();

            log.info("Google resp code: {}", httpMessage.getResponse().getStatusLine());
            log.info("Google resp: {}", httpMessage.getContent());
        } catch (Exception ex) {
            log.error("Error getting google: " + ex.getMessage());
            ex.printStackTrace();
        }
        log.info("Testing in fut-io: " + DateTimeExtensions.ToUnixTime());
        return "in fut-io";
    }

    @GetMapping("/futSim")
    public String testFutSim() {
        try {
            futClient.remoteConfig();
        } catch (Exception e) {
            log.error("Error {}", e);
        }

        return "test";
    }

    @GetMapping("/homeHub")
    public Boolean homeHub() throws Exception {
        futClient.homeHub(false).get();
        return true;
    }
}
