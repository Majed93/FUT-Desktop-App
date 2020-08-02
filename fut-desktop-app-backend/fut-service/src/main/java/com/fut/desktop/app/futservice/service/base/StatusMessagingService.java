package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.domain.AuctionResponse;

public interface StatusMessagingService {

    /**
     * Send status to desired endpoint.
     *
     * @param msg Message to send.
     * @param endpoint Endpoint to send the message to.
     */
    void sendStatus(String msg, String endpoint);

    /**
     * Send auction info to endpoint.
     *
     * @param response Auction response to send.
     */
    void sendAuction(AuctionResponse response, String endpoint);

    /**
     * Send action needed code.
     *
     * @param code The code to send
     */
    void sendActionNeeded(int code);

    /**
     * Send message to invoke modal from client.
     */
    void promptModal();
}
