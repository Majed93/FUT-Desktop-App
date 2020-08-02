package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.SendToPile;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.exceptions.FutException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Handles a single item being moved.
 */

@AllArgsConstructor
public class SendToPileRequest extends FutRequestBase implements IFutRequest<SendToPile> {

    private PileItemDto pileItem;

    private String pile;

    private Boolean sendOptions;

    @Override
    public Future<SendToPile> PerformRequestAsync() throws FutException {
        String uri = baseResources.getFutHome() + Resources.ListItem;
        Platform platform = getBasePlatform();

        SendToPile resp;
        // Create content
        String content = constructContent();

        if (getBaseAppVersion() == AppVersion.WebApp) {
            if (sendOptions) {
                try {
                    sendGenericUtasOptions(platform, uri, HttpMethod.PUT, true);
                } catch (Exception e) {
                    throw new FutException("Error sending options in send item to pile: " + e.getMessage());
                }
            } else {
                addCommonHeaders(platform);
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the PUT request now.
            HttpMessage putResp = sendPutRequest(uri, content, true, false);
            resp = (SendToPile) deserializeAsync(putResp, SendToPile.class).get();
        } catch (Exception ex) {
            throw new FutException("Error sending PUT request for moving item to " + pile, ex);
        }

        return new AsyncResult<>(resp);
    }

    /**
     * Construct the content
     *
     * @return Item data content as string
     */
    private String constructContent() {
        if (Objects.equals(pileItem.getTradeId(), Resources.SEND_TO_TRADE_UNASSIGNED)) {
            return "{\"itemData\":[{\"id\":" + pileItem.getId() + ",\"pile\":\"" + pileItem.getPile() + "\"}]}";
        } else {
            return "{\"itemData\":[{\"id\":" + pileItem.getId() + ",\"pile\":\"" + pileItem.getPile() + "\",\"tradeId\":\"" + pileItem.getTradeId() + "\"}]}";
        }
    }
}
