package com.fut.desktop.app.futservice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.domain.CreditsResponse;
import com.fut.desktop.app.utils.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Handle user operations
 */
@RequestMapping("/user")
@RestController
@Slf4j
public class UserRestController {

    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    private ObjectMapper mapper = new ObjectMapper();

    private RestTemplate rest = new RestTemplate();

    @GetMapping(value = "/credits")
    @ResponseBody
    public CreditsResponse login() {
        ResponseEntity<CreditsResponse> creditResponse = rest.getForEntity(URI.create(futServiceEndpoint + "/user/credits"), CreditsResponse.class);

        return creditResponse.getBody();
    }
}
