package com.fut.api.fut.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Pile;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.PileSize;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Will get usermassinfo.
 */
public class PileSizeRequest extends FutRequestBase implements IFutRequest<List<PileSize>> {
    @Override
    public Future<List<PileSize>> PerformRequestAsync() throws FutException {
        Platform platform = getBasePlatform();
        String uri = baseResources.getFutHome() + Resources.userInfo;

        if (getBaseAppVersion() == AppVersion.WebApp) {
            try {
                sendGenericUtasOptions(platform, uri, HttpMethod.GET, false);
            } catch (Exception e) {
                throw new FutException("Error sending options in pile size." + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(platform);
        }

        try {
            //Send the GET request now for usermassinfo.
            HttpMessage getResp = sendGetRequest(uri, false, true);

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(getResp.getContent());
            JsonNode pileSizeClientData = jsonNode.get("pileSizeClientData");

            if (pileSizeClientData != null) {
                JsonNode entries = pileSizeClientData.get("entries");
                if (entries != null) {
                    List<PileSize> piles = mapper.readValue(entries.toString(), new TypeReference<List<PileSize>>() {
                    });

                    // Add unassigned manually.
                    int unassignedPileStr = Integer.parseInt(jsonNode.get("userInfo").get("unassignedPileSize").toString());
                    PileSize unassigned = new PileSize(Pile.UnassignedPileSize, unassignedPileStr);
                    piles.add(unassigned);

                    return new AsyncResult<>(piles);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new FutException("Error sending GET request for pile size, ex");
        }
    }
}
