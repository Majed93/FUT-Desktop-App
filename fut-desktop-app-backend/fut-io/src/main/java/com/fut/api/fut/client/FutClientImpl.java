package com.fut.api.fut.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.factories.FutRequestFactories;
import com.fut.api.fut.requests.IFutRequest;
import com.fut.api.fut.requests.PinService;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.parameters.SearchParameters;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

@Component
@Slf4j
@Scope("singleton")
public class FutClientImpl implements IFutClient {

    private FutRequestFactories futRequestFactories;

    private PinService pinService;

    /**
     * Value from property file
     */
    @Value("${fut.timeout}")
    private Integer timeoutFactor;

    public FutClientImpl() {
        pinService = new PinService();
        futRequestFactories = new FutRequestFactories(pinService);
    }

    @Override
    public Integer getTimeoutFactor() {
        return timeoutFactor;
    }

    @Override
    public void setTimeoutFactor(Integer timeoutFactor) {
        this.timeoutFactor = timeoutFactor;
    }

    public void newInstance(BasicCookieStore basicCookieStore) {
        pinService = new PinService();
        futRequestFactories = new FutRequestFactories(pinService, basicCookieStore);
    }

    @Override
    public FutRequestFactories getFutRequestFactories() {
        return futRequestFactories;
    }

    /**
     * Get remote config and assign following values.
     * <p>
     * Page size - 25, 35 etc. value without +1.
     *
     * @throws Exception Handle exception
     */
    public void remoteConfig() throws Exception {
        // Get the json for the config.
        String remoteConfig = futRequestFactories.remoteConfig().PerformRequestAsync().get();

        // Parse the json.
        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(remoteConfig);

        try {
            int pageSize = Integer.parseInt(jsonNode.get("itemsPerPage").get("transferMarket").toString());
            futRequestFactories.setPageSize(pageSize);
        } catch (Exception ex) {
            log.error("Error getting remote config." + ex.getMessage());
        }
    }

    @Override
    public Future<HomeWrapper> homeHub(Boolean sendTrade) throws Exception {
        return futRequestFactories.getHomeHubRequest().PerformRequestAsync();
    }

    @Override
    public Future<LoginAuctionWrapper> login(LoginDetails loginDetails, ITwoFactorCodeProvider iTwoFactorCodeProvider) throws Exception {
        //TODO: check if login details are null.
        //futRequestFactories.setPinRequestsCount(0);

        // Set the resources from the app version
        futRequestFactories.setAppVersion(loginDetails.getAppVersion());
        futRequestFactories.setBaseResources(loginDetails.getAppVersion());

        // Get remote config to set number of pages to return.
        remoteConfig();

        pinService.setBaseAppVersion(loginDetails.getAppVersion());
        pinService.resetPinCount();

        IFutRequest<LoginResponse> loginRequest;
        LoginResponse loginResponse;
        List<PileSize> pileSizes;
        HomeWrapper homeWrapper;
        //Seems to get trade pile once logged in, only for mobile though.
        loginRequest = futRequestFactories.loginRequest(loginDetails, iTwoFactorCodeProvider);

        // Do the login
        loginResponse = loginRequest.PerformRequestAsync().get();

        futRequestFactories.setPhishingToken(loginResponse.getPhishingToken());
        futRequestFactories.setSessionId(loginResponse.getSessionId());
        futRequestFactories.setNucleusId(loginResponse.getNucleusId());
        futRequestFactories.setPersonaId(loginResponse.getPersonaId());
        futRequestFactories.setDob(loginResponse.getDob());
        futRequestFactories.setPlatform(loginDetails.getPlatform());
        futRequestFactories.setAuthBearerToken(loginResponse.getAuthBearerToken());
        futRequestFactories.setMobileAuthToken(loginResponse.getMobileAuthBearerToken());
        futRequestFactories.setMobileSessionId(loginResponse.getMobileSessionId());
        futRequestFactories.setRefreshToken(loginResponse.getRefreshToken());

        // Get TOTW
        getTOTW();
        // Get the pile sizes now
        pileSizes = getPileSizes();

        // We're at the home hub now
        homeWrapper = homeHub(true).get();
        //Get trade pile get
        //set the above stuff and send a pin event.
        pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                PinEventId.App_Home, futRequestFactories.getDob(), loginDetails.getPlatform());
        pinService.sendPinEvent(PinEventId.App_Home).get();
        // End pin event.

        return new AsyncResult<>(new LoginAuctionWrapper(loginResponse, homeWrapper, pileSizes));
    }

    /**
     * Called before getting usermassinfo - on login.
     *
     * @throws Exception Throw any exception.
     */
    private void getTOTW() throws Exception {
        futRequestFactories.getTOTW().PerformRequestAsync().get();
    }

    /**
     * Do not allow this to be called via interface. Strictly to be used only after login.
     *
     * @return Pile sizes.
     */
    private List<PileSize> getPileSizes() throws Exception {
        return futRequestFactories.getPileSizes().PerformRequestAsync().get();
    }

    @Override
    public Future<CreditsResponse> getCredits() throws Exception {
        return new AsyncResult<>(futRequestFactories.getCredits().PerformRequestAsync().get());
    }

    @Override
    public Future<AuctionResponse> getTradePile(boolean sendPin) throws Exception {
        AuctionResponse auctionResponse = futRequestFactories.getTradePile().PerformRequestAsync().get();

        //Need to send a pin event to Transfer main page and going to the trade pile
        //Must have appropriate timer to avoid 'bot' like behaviour.
        if (sendPin) {
            // Send Hub - Transfer
            pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                    PinEventId.App_HubTransfers, futRequestFactories.getDob(), futRequestFactories.getPlatform());
            pinService.sendPinEvent(PinEventId.App_HubTransfers).get();

            //Wait for a second
            //NOTE: Doing this because the timeout factor isn't being set properly in this context.
            Random random = new Random();
            Thread.sleep(random.nextInt((753 - 350) + 1) + 350 * timeoutFactor);

            // Send Hub - Transfer List
            pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                    PinEventId.App_TransferList, futRequestFactories.getDob(), futRequestFactories.getPlatform());
            pinService.sendPinEvent(PinEventId.App_TransferList).get();
        }

        return new AsyncResult<>(auctionResponse);
    }

    @Override
    public Future<AuctionResponse> getWatchList(boolean sendPin) throws Exception {
        AuctionResponse auctionResponse = futRequestFactories.getWatchList().PerformRequestAsync().get();

        // Pin event not sent when removing items by the looks of it.
        if (sendPin) {
            // Send Hub - Transfer
            pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                    PinEventId.App_HubTransfers, futRequestFactories.getDob(), futRequestFactories.getPlatform());
            pinService.sendPinEvent(PinEventId.App_HubTransfers).get();

            //Wait for a second
            SleepUtil.sleep(456, 876);

            // Send Hub - Transfer List
            pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                    PinEventId.App_TransferTargets, futRequestFactories.getDob(), futRequestFactories.getPlatform());
            pinService.sendPinEvent(PinEventId.App_TransferTargets).get();
        }

        return new AsyncResult<>(auctionResponse);
    }

    @Override
    public Future<AuctionResponse> search(SearchParameters params, Boolean isFirst) throws Exception {
        if (params == null) {
            throw new NullPointerException("Search params are null");
        }

        // Send a pin event to the Transfer hub page.
        // Must have timer to avoid 'bot' like behaviour

        // If it's the first one in a while then we don't need to do this since we've basically click 'back'
        // Send Hub - Transfer
        if (isFirst) {
            pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                    PinEventId.App_HubTransfers, futRequestFactories.getDob(), futRequestFactories.getPlatform());
            PinResponse hubResponse = pinService.sendPinEvent(PinEventId.App_HubTransfers).get();

            //Wait for a second
            Random random = new Random();
            Thread.sleep(random.nextInt((654 - 354) + 1) + 354 * timeoutFactor);
        }

        // Send Hub - Transfer Search
        pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                PinEventId.Generic_TransferMarketSearch, futRequestFactories.getDob(), futRequestFactories.getPlatform());
        pinService.sendPinEvent(PinEventId.Generic_TransferMarketSearch).get();

        // Do search
        AuctionResponse searchResponse = futRequestFactories.getSearch(params).PerformRequestAsync().get();

        // Send Transfer Market Results - List View
        pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                PinEventId.App_TransferMarketResults, futRequestFactories.getDob(), futRequestFactories.getPlatform());
        pinService.sendPinEvent(PinEventId.App_TransferMarketResults).get();

        return new AsyncResult<>(searchResponse);

    }

    @Override
    public Future<Boolean> removeSold() throws Exception {
        boolean removeSold = futRequestFactories.removeSold().PerformRequestAsync().get();
        return new AsyncResult<>(removeSold);
    }

    @Override
    public Future<RelistResponse> getReListAll() throws Exception {
        RelistResponse relistResponse = futRequestFactories.reListAll().PerformRequestAsync().get();
        return new AsyncResult<>(relistResponse);
    }

    @Override
    public Future<ListAuctionResponse> listItem(AuctionDetails auctionDetails, Boolean sendOptions) throws Exception {
        ListAuctionResponse listAuctionResponse = futRequestFactories.listItem(auctionDetails, sendOptions).PerformRequestAsync().get();
        return new AsyncResult<>(listAuctionResponse);
    }

    @Override
    public Future<AuctionResponse> tradeStatus(Collection<Long> tradeIds) throws Exception {
        if (tradeIds == null || tradeIds.isEmpty()) {
            throw new FutErrorException(new FutError("Trade ids empty", FutErrorCode.BadRequest, "Trade ids empty", "Trade ids empty: " + tradeIds, ""));
        }

        AuctionResponse auctionResponse = futRequestFactories.getTradeStatus(tradeIds).PerformRequestAsync().get();
        return new AsyncResult<>(auctionResponse);
    }

    @Override
    public Future<Boolean> removeFromWatchList(Collection<Long> tradeIds) throws Exception {
        Boolean result = futRequestFactories.removeFromWatchList(tradeIds).PerformRequestAsync().get();
        return new AsyncResult<>(result);
    }

    @Override
    public Future<SendToPile> moveItemToPile(PileItemDto pileItem, String pile, Boolean sendOptions) throws Exception {
        SendToPile result = futRequestFactories.moveItemToPile(pileItem, pile, sendOptions).PerformRequestAsync().get();
        return new AsyncResult<>(result);
    }

    @Override
    public Future<AuctionResponse> bidItem(long tradeId, Long bidAmount) throws Exception {
        AuctionResponse result = futRequestFactories.bidItem(tradeId, bidAmount).PerformRequestAsync().get();
        return new AsyncResult<>(result);
    }

    @Override
    public Future<PriceRange> priceRange(Long itemId) throws Exception {
        PriceRange[] priceRanges = futRequestFactories.priceRange(itemId).PerformRequestAsync().get();

        if (priceRanges.length > 0) {
            return new AsyncResult<>(priceRanges[0]);
        }

        return null;
    }

    @Override
    public Future<AuctionResponse> getUnassignedPile() throws Exception {
        AuctionResponse unassignedPile = futRequestFactories.getUnassignedPile().PerformRequestAsync().get();

        // Send Unassigned Items - List View
        pinService.setProperties(Long.parseLong(futRequestFactories.getNucleusId()), futRequestFactories.getPersonaId(), futRequestFactories.getSessionId(),
                PinEventId.UnassignedItems, futRequestFactories.getDob(), futRequestFactories.getPlatform());
        pinService.sendPinEvent(PinEventId.UnassignedItems).get();

        return new AsyncResult<>(unassignedPile);
    }
}
