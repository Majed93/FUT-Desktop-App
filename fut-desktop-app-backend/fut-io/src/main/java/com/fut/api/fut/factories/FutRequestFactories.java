package com.fut.api.fut.factories;

import com.fut.api.fut.requests.*;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.parameters.SearchParameters;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Slf4j
public class FutRequestFactories {

    public static Resources webResources = new Resources(AppVersion.WebApp);
    public static Resources mobileResources = new Resources(AppVersion.CompanionApp);
    public Resources resources;
    private String phishingToken;

    private String sessionId;

    private String nucleusId;

    private String personaId;

    private String dob;

    private IHttpClient httpClient;

    private AppVersion appVersion;

    private BasicCookieStore cookieStore;

    private Platform platform;

    private PinService pinService;

    private int pageSize;

    private String authBearerToken;

    private String mobileAuthToken;

    private String mobileSessionId;

    /**
     * Used by the mobile app.
     */
    private String refreshToken;

    // When adding new properties they must be add to setSharedProperties method
    // and save in the rest controller.

    /**
     * Timeout factor
     */
    @Value("${fut.timeout}")
    private Integer timeoutFactor;

    public FutRequestFactories(PinService pinService) {
        this.pinService = pinService;
        this.httpClient = new HttpClientImpl();
        cookieStore = httpClient.getCookies();
        httpClient.setFollowRedirect();
        pinService.setHttpClient(httpClient);
    }

    public FutRequestFactories(PinService pinService, BasicCookieStore basicCookieStore) {
        this.pinService = pinService;
        this.httpClient = new HttpClientImpl();
        if (basicCookieStore != null) {
            httpClient.setCookies(basicCookieStore);
        }
        httpClient.setFollowRedirect();
        cookieStore = httpClient.getCookies();
        pinService.setHttpClient(httpClient);
    }

    public IHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Set required variable in base.
     *
     * @param req .
     * @return Request base with default variables set.
     */
    private FutRequestBase setSharedRequestProperties(FutRequestBase req) {
        req.setBasePhishingToken(phishingToken);
        req.setBaseSessionId(sessionId);
        req.setHttpClient(httpClient);
        req.setBaseResources(resources);
        req.setBaseNucleusId(nucleusId);
        req.setBasePersonaId(personaId);
        req.setBaseAppVersion(appVersion);
        req.setBasePlatform(platform);
        req.setBaseDob(dob);
        req.setBaseAuthBearerToken(authBearerToken);
        req.setBaseMobileAuthBearerToken(mobileAuthToken);
        req.setTimerFactor(req.timerFactor);
        req.setPageSize(pageSize);
        req.setBaseMobileSessionId(mobileSessionId);
        req.setBaseRefreshToken(refreshToken);

        return req;
    }

    /**
     * Set the base resources.
     *
     * @param appVersion The app version
     */
    public void setBaseResources(AppVersion appVersion) {
        resources = new Resources(appVersion);
        if (appVersion == AppVersion.WebApp) {
            resources = webResources;
        } else {
            resources = mobileResources;
        }
    }

    /**
     * Return the session id depending on the app version used.
     *
     * @return the correct session id.
     */
    public String getSessionId() {
        if (appVersion == AppVersion.WebApp) {
            return this.sessionId;
        } else {
            return this.mobileSessionId;
        }
    }

    /**
     * Perform login
     *
     * @param loginDetails           .
     * @param iTwoFactorCodeProvider .
     * @return LoginResponse.
     */
    public IFutRequest<LoginResponse> loginRequest(LoginDetails loginDetails, ITwoFactorCodeProvider iTwoFactorCodeProvider) {
        if (loginDetails.getPlatform() == Platform.Xbox360 || loginDetails.getPlatform() == Platform.XboxOne) {
            webResources.FutHome = Resources.FutHomeXbox;
            mobileResources.FutHome = Resources.FutHomeXbox;
        }

        this.setBaseResources(loginDetails.getAppVersion());

        LoginRequest loginRequest = new LoginRequest(loginDetails, iTwoFactorCodeProvider, pinService);
        loginRequest.setBaseResources(this.resources);

        loginRequest.setBaseAppVersion(loginDetails.getAppVersion());

        if (getPhishingToken() != null && !getPhishingToken().isEmpty()) {
            loginRequest.setBasePhishingToken(getPhishingToken());
        }

        loginRequest.setBasePlatform(loginDetails.getPlatform());
        loginRequest.setBaseAppVersion(appVersion);
        loginRequest.setHttpClient(httpClient);
        loginRequest.SetCookies(cookieStore);
        loginRequest.setBaseAuthBearerToken(authBearerToken);
        loginRequest.setBaseMobileAuthBearerToken(mobileAuthToken);
        loginRequest.setBaseRefreshToken(refreshToken);

        return loginRequest;
    }

    public IFutRequest<CreditsResponse> getCredits() {
        return (CreditsRequest) setSharedRequestProperties(new CreditsRequest());
    }

    public IFutRequest<AuctionResponse> getTradePile() {
        return (TradePileRequest) setSharedRequestProperties(new TradePileRequest());
    }

    public IFutRequest<AuctionResponse> getWatchList() {
        return (WatchListRequest) setSharedRequestProperties(new WatchListRequest());
    }

    public IFutRequest<AuctionResponse> getSearch(SearchParameters params) {
        return (SearchRequest) setSharedRequestProperties(new SearchRequest(params));
    }

    public IFutRequest<Boolean> removeSold() {
        return (RemoveSoldRequest) setSharedRequestProperties(new RemoveSoldRequest());
    }

    public IFutRequest<RelistResponse> reListAll() {
        return (ReListRequest) setSharedRequestProperties(new ReListRequest());
    }

    public IFutRequest<ListAuctionResponse> listItem(AuctionDetails auctionDetails, Boolean sendOptions) {
        return (ListAuctionRequest) setSharedRequestProperties(new ListAuctionRequest(auctionDetails, sendOptions));
    }

    public IFutRequest<AuctionResponse> getTradeStatus(Collection<Long> tradeIds) {
        return (TradeStatusRequest) setSharedRequestProperties(new TradeStatusRequest(tradeIds));

    }

    public IFutRequest<Boolean> removeFromWatchList(Collection<Long> tradeIds) {
        return (RemoveFromWatchlistRequest) setSharedRequestProperties(new RemoveFromWatchlistRequest(tradeIds));
    }

    public IFutRequest<SendToPile> moveItemToPile(PileItemDto pileItem, String pile, Boolean sendOptions) {
        return (SendToPileRequest) setSharedRequestProperties(new SendToPileRequest(pileItem, pile, sendOptions));
    }

    public IFutRequest<Boolean> getTOTW() {
        return (TotwRequest) setSharedRequestProperties(new TotwRequest());
    }

    public IFutRequest<List<PileSize>> getPileSizes() {
        return (PileSizeRequest) setSharedRequestProperties(new PileSizeRequest());
    }

    public IFutRequest<String> remoteConfig() {
        return (RemoteConfigRequest) setSharedRequestProperties(new RemoteConfigRequest());
    }

    public IFutRequest<AuctionResponse> bidItem(long tradeId, Long bidAmount) {
        return (BidItemRequest) setSharedRequestProperties(new BidItemRequest(tradeId, bidAmount));
    }

    public IFutRequest<PriceRange[]> priceRange(Long itemId) {
        return (PriceRangeRequest) setSharedRequestProperties(new PriceRangeRequest(itemId));
    }

    public IFutRequest<HomeWrapper> getHomeHubRequest() {
        return (HomeHubRequest) setSharedRequestProperties(new HomeHubRequest());
    }

    public IFutRequest<AuctionResponse> getUnassignedPile() {
        return (UnassignedPileRequest) setSharedRequestProperties(new UnassignedPileRequest());
    }
}
