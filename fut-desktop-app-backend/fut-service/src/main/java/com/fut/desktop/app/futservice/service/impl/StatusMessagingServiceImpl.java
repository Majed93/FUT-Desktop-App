package com.fut.desktop.app.futservice.service.impl;

import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.futservice.constants.WSUrl;
import com.fut.desktop.app.futservice.service.base.StatusMessagingService;
import com.fut.desktop.app.restObjects.StatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class StatusMessagingServiceImpl implements StatusMessagingService {

    /**
     * Used to make calls to our messaging endpoints
     */
    private final SimpMessagingTemplate stomp;

    /**
     * Constructor.
     *
     * @param stomp Handle to {@link SimpMessagingTemplate}
     */
    public StatusMessagingServiceImpl(SimpMessagingTemplate stomp) {
        this.stomp = stomp;
    }

    @Override
    public void sendStatus(String msg, String endpoint) {
        StatusMessage statusMsg = new StatusMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + msg);
        log.info("Status: " + statusMsg.getMessage());
        stomp.convertAndSend(endpoint, statusMsg);
    }

    @Override
    public void sendAuction(AuctionResponse response, String endpoint) {
        log.debug("Auction info from StatusMessagingService: {}", response);
        stomp.convertAndSend(endpoint, response);
    }

    @Override
    public void sendActionNeeded(int code) {
        log.debug("Action code: {}", code);
        stomp.convertAndSend(WSUrl.ACTION_NEEDED, code);
    }

    @Override
    public void promptModal() {
        StatusMessage msg = new StatusMessage("TOO_MANY_ITEMS");
        log.info("Sending modal message: " + msg);
        stomp.convertAndSend(WSUrl.AUTOBID_TOOMANY_MODAL, msg);
    }
}
