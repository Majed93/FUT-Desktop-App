package com.fut.api.fut.requests;

import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * This contains a collection of requests in order to achieve the 'home hub' sequence.
 * <p>
 * Order as follows
 * All OPTIONS request first
 * Followed by GET requests. OPTIONS and GET requests must be same timestamp in accordance with each other.
 * - Settings
 * - Live message
 * - Trade pile
 * - Purchased
 * - mm/game/message
 * - Squad
 * - Champions Hub
 * - Rivals
 * - Active message
 * - Club stats/staff
 */
@Slf4j
public class HomeHubRequest extends FutRequestBase implements IFutRequest<HomeWrapper> {
    /**
     * Timestamp to be tracked to ensure the requests are '1' apart.
     */
    private long startingDateTime;

    @Override
    public Future<HomeWrapper> PerformRequestAsync() throws FutException {
        try {
            // Send settings request first - both OPTIONS and GET
            settingRequest();

            // *********************************************************

            if (getBaseAppVersion() == AppVersion.WebApp) {
                // Options requests first

                // Live message
                getLiveMessages(true);

                // trade pile
                getTradePile(true).get();

                // purchased
                getUnassignedPile(true);

                // mm/game/message
                getMessageList(true);

                //squad
                getSquads(true);

                // champion hub
                getChampionsHub(true);

                // user objectives
                getUserObjectives(true);

                // rivals/weekendleague
                getWeekendLeagueStatus(true);

                // active message
                getActiveMessage(true);

                // club stats/staff
                getClubStatsStaff(true);
            } else {
                addCommonMobileHeaders(getBasePlatform());
            }

            // *********************************************************
            // Now send actual ones ************************************

            // Live message
            getLiveMessages(false);

            // trade pile
            AuctionResponse tradePile = getTradePile(false).get();

            // purchased
            AuctionResponse unassignedItems = getUnassignedPile(false).get();

            //squad
            getSquads(false);

            // champion hub
            getChampionsHub(false);

            // user objectives
            getUserObjectives(false);


            // rivals/weekendleague
            getWeekendLeagueStatus(false);

            // active message
            getActiveMessage(false);

            // club stats/staff
            getClubStatsStaff(false);

            // https://utas.external.s3.fut.ea.com/ut/game/fifa19/sqbt/user/hub?scope=mini
            getSqbt();

            // game/message
            getMessageList(false);

            // https://utas.external.s3.fut.ea.com/ut/game/fifa19/rivals/user/prizeDetails
            getPrizeDetails();

            return new AsyncResult<>(new HomeWrapper(tradePile, unassignedItems));
        } catch (Exception ex) {
            log.error("Error in HomeHub request {}", ex.getMessage());
            log.error("Error in {}", ex.getCause());
            throw new FutException("Error sending a request in the HomeHub: " + ex.getMessage());
        }
    }

    /**
     * Execute live message request
     */
    private void getLiveMessages(Boolean sendOptionsOnly) throws Exception {
        startingDateTime = DateTimeExtensions.ToUnixTime();
        String liveMessageUrl = getBaseResources().getFutHome() + Resources.LIVE_MESSAGE + startingDateTime;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, liveMessageUrl, true, true);
    }

    /**
     * Get trade pile.
     *
     * @param sendOptionsOnly If true the only options request is sent.
     * @return The trade pile response {@link AuctionResponse}.
     * @throws Exception .
     */
    private Future<AuctionResponse> getTradePile(Boolean sendOptionsOnly) throws Exception {
        String tradePileUrl = baseResources.getFutHome() + baseResources.TradePile;
        Future<HttpMessage> futureGetMessage = sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, tradePileUrl, false, true);

        if (futureGetMessage != null) {
            AuctionResponse auctionResponse;
            // Now send actual request
            try {
                auctionResponse = (AuctionResponse) deserializeAsync(futureGetMessage.get(), AuctionResponse.class).get();
            } catch (Exception ex) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - trade pile {}", ex.getCause());
//                throw new FutException("Error sending GET request for getting trade pile", ex);
                return null;
            }

            return new AsyncResult<>(auctionResponse);
        } else {
            log.warn("The response was null for trade pile in home hub request");
            return new AsyncResult<>(new AuctionResponse());
        }
    }

    /**
     * Get unassigned pile
     *
     * @return Unassigned pile
     * @throws Exception Handle any exception
     */
    private Future<AuctionResponse> getUnassignedPile(Boolean sendOptionsOnly) throws Exception {
        String unassignedPileUrl = getBaseResources().getFutHome() + Resources.PurchasedItems;
        Future<HttpMessage> futureGetMessage = sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, unassignedPileUrl, false, true);
        if (futureGetMessage != null) {
            AuctionResponse unassignedPile;

            PurchasedItemsResponse purchasedItemsResponse = (PurchasedItemsResponse) deserializeAsync(futureGetMessage.get(), PurchasedItemsResponse.class).get();

            List<AuctionInfo> auctionInfos = new ArrayList<>();

            for (ItemData itemData : purchasedItemsResponse.getItemData()) {
                AuctionInfo auctionInfo = new AuctionInfo();
                auctionInfo.setItemData(itemData);
                auctionInfos.add(auctionInfo);
            }

            unassignedPile = new AuctionResponse();
            unassignedPile.setAuctionInfo(auctionInfos);
            return new AsyncResult<>(unassignedPile);
        } else {
            log.warn("The response was null for unassigned pile in home hub request");
            return new AsyncResult<>(new AuctionResponse());
        }
    }

    /**
     * Get message list.
     *
     * @throws Exception Handle any exceptions
     */
    private void getMessageList(Boolean sendOptionsOnly) throws Exception {
        startingDateTime = startingDateTime + 1;
        String messageListUrl = getBaseResources().getFutHome() + Resources.MESSAGE_LIST + startingDateTime;

        // Search place the url with the nucleus ID
        messageListUrl = messageListUrl.replaceAll(Resources.NUCLEUS_ID_PLACEHOLDER, getBasePersonaId());


        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, messageListUrl, true, false);
    }

    /**
     * Get squads.
     *
     * @throws Exception Handle any exceptions
     */
    private void getSquads(Boolean sendOptionsOnly) throws Exception {
        startingDateTime = startingDateTime + 1;
        String squadsUrl = getBaseResources().getFutHome() +
                Resources.SQUADS +
                getBasePersonaId() +
                Resources.TIME_EXT +
                startingDateTime;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, squadsUrl, true, false);
    }

    /**
     * Get champions hub.
     *
     * @throws Exception Handle any exception
     */
    private void getChampionsHub(Boolean sendOptionsOnly) throws Exception {
        String championsHubUrl = getBaseResources().getFutHome() + Resources.CHAMPIONS_HUB;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, championsHubUrl, false, true);
    }


    /**
     * Get user objectives.
     *
     * @throws Exception Handle exceptions
     */
    private void getUserObjectives(Boolean sendOptionsOnly) throws Exception {
        String userObjectives = getBaseResources().getFutHome() + Resources.USER_OBJECTIVES;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, userObjectives, false, true);
    }

    /**
     * Get weekend league status.
     *
     * @param sendOptionsOnly If true then will only send options request, otherwise only the get request.
     * @throws Exception Throw any exception.
     */
    private void getWeekendLeagueStatus(Boolean sendOptionsOnly) throws Exception {
        String url = getBaseResources().getFutHome() + Resources.RIVAL_WEEKEND_LEAGUE;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, url, false, true);
    }

    /**
     * Get active message.
     *
     * @param sendOptionsOnly If true then will only send options request, otherwise only the get request.
     * @throws Exception Throw any exception.
     */
    private void getActiveMessage(Boolean sendOptionsOnly) throws Exception {
        startingDateTime = startingDateTime + 1;
        String activeMessageUrl = getBaseResources().getFutHome() + Resources.ACTIVE_MESSAGE + startingDateTime;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, activeMessageUrl, true, true);
    }

    /**
     * Gets club staff and stats.
     *
     * @throws Exception Handle any errors.
     */
    private void getClubStatsStaff(Boolean sendOptionsOnly) throws Exception {
        startingDateTime = startingDateTime + 1;
        String clubStatsStaffUrl = getBaseResources().getFutHome() + Resources.CLUB_STATS_STAFF + startingDateTime;
        sendRequestsWithNucleusIdAndShortenedAcceptHeaders(sendOptionsOnly, clubStatsStaffUrl, true, true);
    }

    /**
     * Send https://utas.external.s3.fut.ea.com/ut/game/fifa19/settings?_=1545765871412 request.
     *
     * @throws Exception .
     */
    private void settingRequest() throws Exception {
        String url = baseResources.getFutHome() + Resources.SETTINGS + DateTimeExtensions.ToUnixTime();
        Platform platform = getBasePlatform();

        if (getBaseAppVersion().equals(AppVersion.WebApp)) {
            // Options for settings
            try {
                sendGenericUtasOptions(platform, url, HttpMethod.GET, true);
            } catch (Exception e) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - settings {}", e.getCause());
//                throw new FutException("Error sending options for settings." + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(getBasePlatform());
        }
        sendGetRequest(url, true, false);
    }

    /**
     * Send https://utas.external.s3.fut.ea.com/ut/game/fifa19/sqbt/user/hub?scope=mini
     *
     * @throws Exception .
     */
    private void getSqbt() throws Exception {
        String url = baseResources.getFutHome() + Resources.SQBT;
        Platform platform = getBasePlatform();

        if (getBaseAppVersion().equals(AppVersion.WebApp)) {
            // Options for settings
            try {
                sendGenericUtasOptions(platform, url, HttpMethod.GET, false);
            } catch (Exception e) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - sqbt {}", e.getCause());
//                throw new FutException("Error sending options for sqbt." + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(getBasePlatform());
        }
        sendGetRequest(url, false, true);
    }

    /**
     * Send https://utas.external.s3.fut.ea.com/ut/game/fifa19/rivals/user/prizeDetails
     *
     * @throws Exception .
     */
    private void getPrizeDetails() throws Exception {
        String url = baseResources.getFutHome() + Resources.PRIZE_DETAILS;
        Platform platform = getBasePlatform();

        if (getBaseAppVersion().equals(AppVersion.WebApp)) {
            // Options for settings
            try {
                sendGenericUtasOptions(platform, url, HttpMethod.GET, false);
            } catch (Exception e) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - prize details {}", e.getCause());
//                throw new FutException("Error sending options for prize details." + e.getMessage());
            }
        } else {
            addCommonMobileHeaders(getBasePlatform());
        }
        sendGetRequest(url, false, true);
    }


    // ************************* HELPER METHODS ************************* //

    /**
     * Helper method to send GET request with nucleusId and Accept: * / * header.
     *
     * @param sendOptionsOnly       If true the only send the options request.
     * @param url                   The url to send.
     * @param requiresNucleusId     If true the request requires nucleus Id.
     * @param shortenedAcceptHeader If true then the accept header added '* / *'
     * @throws FutException throw the exception.
     */
    private Future<HttpMessage> sendRequestsWithNucleusIdAndShortenedAcceptHeaders(Boolean sendOptionsOnly, String url, Boolean requiresNucleusId, Boolean shortenedAcceptHeader) throws FutException {
        if (sendOptionsOnly) {
            Platform platform = getBasePlatform();
            try {
                sendGenericUtasOptions(platform, url, HttpMethod.GET, requiresNucleusId);
            } catch (Exception e) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - {} {}", url, e.getCause());
//                throw new FutException("Error sending options for: " + url + " | " + e.getMessage());
            }
            return null; // Fine here because we don't care about options response
        } else {
            if (getBaseAppVersion().equals(AppVersion.CompanionApp)) {
                addRequestWithHeader();
            }
            // Now send actual request
            HttpMessage response;
            try {
                response = sendGetRequest(url, requiresNucleusId, shortenedAcceptHeader);
            } catch (Exception ex) {
                // Not throwing exception because we'll still have logged in by here
                log.error("Error requesting in HomeHubRequest - {} {}", url, ex.getCause());
                return null;
//                throw new FutException("Error sending GET request for getting for: " + url + " | ", ex);
            }
            log.debug("Response for {} {}", url, response);
            return new AsyncResult<>(response);
        }
    }
}
