package com.fut.desktop.app.futservice.socket;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.futservice.constants.WSUrl;
import com.fut.desktop.app.restObjects.StatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
public class SocketController {

    /**
     * Used to send back auction responses
     *
     * @param response Incoming response
     * @return Auction Response
     */
    @MessageMapping("/player")
    @SendTo({WSUrl.PLAYERSEARCH_START})
    public AuctionResponse playerResponse(AuctionResponse response) {
        log.debug("Auction Response: {}", response);
        return response;
    }

    @MessageMapping("/test")
    @SendTo("/test")
    public String test(Object tset) {
        log.info("{} | {}", LocalDateTime.now(), tset);
        return tset.toString();
    }

    /**
     * Message socket for listening to status messages.
     *
     * @param message Message
     * @return message
     */
    @MessageMapping("/status")
    @SendTo({WSUrl.PLAYERSEARCH_STATUS, WSUrl.PLAYERLISTING_STATUS, WSUrl.AUTOBID_TOOMANY_MODAL})
    public StatusMessage status(StatusMessage message) {
        message.setMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + message.getMessage());
        log.info("Status: " + message.getMessage());
        return new StatusMessage(message.getMessage());
    }

    @MessageMapping("/userAction")
    @SendTo(WSUrl.ACTION_NEEDED)
    public int actionNeeded(int actionCode) {
        log.debug("Action code {}", FutErrorCode.fromValue(actionCode));
        return actionCode;
    }
}
