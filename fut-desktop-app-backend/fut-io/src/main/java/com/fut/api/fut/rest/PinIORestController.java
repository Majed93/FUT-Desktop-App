package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.domain.PinResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Handle pin service operations
 */
@RequestMapping("/io/pin")
@RestController
@Slf4j
public class PinIORestController {

    private final IFutClient futClient;

    public PinIORestController(IFutClient futClient) {
        this.futClient = futClient;
    }

    @GetMapping
    @ResponseBody
    public Integer pinCount() {
        return futClient.getFutRequestFactories().getPinService().getPinRequestCount();
    }

    @PostMapping
    @ResponseBody
    public PinResponse sendPinEvent(@RequestBody PinEventId pinEvent) throws Exception {
        return futClient.getFutRequestFactories().getPinService().sendPinEvent(pinEvent).get();
    }

    /**
     * This aligns the pin counts.
     * Only use when using the simulator otherwise will not work.
     *
     * @throws ConfigurationException Thrown with cannot ready config file.
     * @throws HttpException          Thrown when simulator does not response with 200 OK HTTP status code.
     */
    @GetMapping("/alignPin")
    @ResponseStatus(HttpStatus.OK)
    public void setPinManually() throws ConfigurationException, HttpException {
        RestTemplate restTemplate = new RestTemplate();
        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("application.properties")));
        } catch (ConfigurationException e) {
            log.error("Unable to load properties file.");
            throw new ConfigurationException(e);
        }

        String futSimEndpoint = config.getString("fut.simulator.endpoint");

        if (futSimEndpoint != null && !futSimEndpoint.isEmpty()) {
            Integer pinCount = futClient.getFutRequestFactories().getPinService().getPinRequestCount();
            ResponseEntity<Void> getEntity = restTemplate.getForEntity(futSimEndpoint + "/train/pinCount/" + pinCount, Void.class);
            if (getEntity.getStatusCode() != HttpStatus.OK) {
                throw new HttpException("Incorrect status code" + getEntity.getStatusCode());
            }
        } else {
            log.error("No endpoint configured. Please contact supplier.");
        }

    }
}
