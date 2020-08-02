package com.fut.desktop.app.futservice.service.impl;

import com.fut.desktop.app.futservice.service.base.PinService;
import com.fut.desktop.app.utils.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Implementation of {@link PinService}
 */
@Slf4j
@Service
public class PinServiceImpl implements PinService {

    /**
     * Handle to {@link RestTemplate}
     */
    private final RestTemplate restTemplate;


    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    /**
     * Constructor.
     *
     * @param restTemplate Handle to {@link RestTemplate}
     */
    public PinServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer getPinCount() {
        ResponseEntity<Integer> pinCount = restTemplate.getForEntity(URI.create(futServiceEndpoint + "/pin"), Integer.class);
        log.debug("Response of getting pin count: {}", pinCount);
        return pinCount.getBody();
    }
}
