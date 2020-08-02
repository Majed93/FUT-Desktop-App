package com.fut.desktop.app.futsimulator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.domain.pinEvents.Event;
import com.fut.desktop.app.domain.pinEvents.PinEvent;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.service.PinManagementService;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.ResponseReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handle pin service operations
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class PinMockRestController {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final PinManagementService pinManagementService;

    @Autowired
    public PinMockRestController(PinManagementService pinManagementService) {
        this.pinManagementService = pinManagementService;
    }

    @PostMapping("/pin-river.data.ea.com/pinEvents")
    public String sendPinEvent(@RequestHeader HttpHeaders requestHeaders, @RequestBody Object pinEvent) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);

        Assert.assertNotNull("Pin event body is empty", pinEvent);
        String json = objectMapper.writeValueAsString(pinEvent);
        json = json.replaceAll(String.valueOf("unknown"), String.valueOf(999999));
        // Check pinEvent
        PinEvent pinEventConverted = objectMapper.readValue(json, PinEvent.class);

        setPinCount(pinEventConverted);
        assertPinEventId(pinEventConverted);

        return ResponseReaderUtil.generateStringOfFile("responses/json/pinResponse.json");
    }

    /**
     * Check that the pin event pgid is in order.
     *
     * @param pinEventConverted pin event to assert.
     */
    private void assertPinEventId(PinEvent pinEventConverted) {
        Event event = pinEventConverted.getEvents().get(0);
        String pgId = event.getPgid();
        if (pgId != null) {
            PinEventId pinEventId = PinEventId.fromValue(pgId);
            Assert.assertNotNull("Invalid pin event id!", pinEventId);
            Assert.assertNotNull("Event Session id is empty", pinEventConverted.getSid());
            Assert.assertNotNull("PID inside core is empty", event.getCore().getPid());
            Assert.assertNotNull("Nucleus is empty", event.getCore().getPidm().getNucleus());
        }
    }

    @GetMapping(RestConstants.TRAIN + "pinCount/{newPinCount}")
    @ResponseStatus(HttpStatus.OK)
    public void setPinCountManually(@PathVariable("newPinCount") Integer newPinCount) {
        pinManagementService.setPinCount(newPinCount - 1);
    }

    /**
     * Get the pin count.
     *
     * @param pinEvent PinEvent count.
     */
    private void setPinCount(PinEvent pinEvent) {
        // For each event increment. Because each event has it's own pin count.
        // Incrementing before so it can be compared.
        pinEvent.getEvents().forEach(e -> pinManagementService.increment());

        List<Event> events = pinEvent.getEvents();
        AtomicBoolean pinOnStartup = new AtomicBoolean(false);
        AtomicReference<Integer> newPinCount = new AtomicReference<>(0);

        events.forEach(ev -> {
            if (ev.getCore().getEn().equals(PinEventId.WebApp_BootStart.getPinEventId())) {
                pinOnStartup.set(true);
            }
            newPinCount.set(ev.getCore().getS());
        });

        if (pinOnStartup.get()) {
            pinManagementService.resetPin();
        } else {
            Integer predictedPinCount = pinManagementService.getPinCount();
            // Doing this because when we login by just setting the values, no pins have actually been set, just a mocked pin count.
            Assert.assertEquals("Pin counts don't add up.. Current pin count: " + predictedPinCount + " actual is: " + newPinCount.get(),
                    predictedPinCount, newPinCount.get());
        }

    }
}
