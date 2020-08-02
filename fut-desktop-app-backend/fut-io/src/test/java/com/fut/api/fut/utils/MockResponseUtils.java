package com.fut.api.fut.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.domain.*;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utils to provide mock responses.
 */
public final class MockResponseUtils {

    /**
     * Helper to convert object to string.
     *
     * @param mockResponse mock response to convert
     * @return String of object.
     * @throws Exception Handle errors.
     */
    public static String objectToString(Object mockResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return objectMapper.writeValueAsString(mockResponse);
    }

    /**
     * Helper function to return mock trade pile.
     */
    public static AuctionResponse generateMockTradePile() throws Exception {
        return (AuctionResponse) generateMockedResponse("tradePileResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock watch list.
     */
    public static AuctionResponse generateMockWatchList() throws Exception {
        return (AuctionResponse) generateMockedResponse("watchListResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock bid request.
     */
    public static AuctionResponse generateMockBidRequest() throws Exception {
        return (AuctionResponse) generateMockedResponse("bidItemResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock bin request.
     */
    public static AuctionResponse generateMockBinRequest() throws Exception {
        return (AuctionResponse) generateMockedResponse("binItemResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock move to trade pile.
     */
    public static SendToPile generateMockMoveToTradeRequest() throws Exception {
        return (SendToPile) generateMockedResponse("moveItemToTradeResponse.json", SendToPile.class);
    }

    /**
     * Helper function to return mock list auction.
     */
    public static ListAuctionResponse generateMockListItem() throws Exception {
        return (ListAuctionResponse) generateMockedResponse("listItemResponse.json", ListAuctionResponse.class);
    }

    /**
     * Helper function to return mock pile sizes.
     */
    public static Object generateMockPileSize() throws Exception {
        return generateMockedResponse("userMassInfoResponse.json", Object.class);
    }

    /**
     * Helper function to return mock plie size with no client data.
     */
    public static Object generateMockPileSizeNoClientData() throws Exception {
        return generateMockedResponse("userMassInfoNullPileSizeClientDataResponse.json", Object.class);
    }

    /**
     * Helper function to return mock pile size no entries.
     */
    public static Object generateMockPileSizeNoEntries() throws Exception {
        return generateMockedResponse("userMassInfoNullEntriesResponse.json", Object.class);
    }

    /**
     * Helper function to return mock relist request.
     */
    public static RelistResponse generateMockRelist() throws Exception {
        return (RelistResponse) generateMockedResponse("reListResponse.json", RelistResponse.class);
    }

    /**
     * Helper function to return mock search result
     */
    public static AuctionResponse generateMockSearchResult() throws Exception {
        return (AuctionResponse) generateMockedResponse("searchResultResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock trade status.
     */
    public static AuctionResponse generateMockTradeStatus() throws Exception {
        return (AuctionResponse) generateMockedResponse("tradeStatusResponse.json", AuctionResponse.class);
    }

    /**
     * Helper function to return mock price range request.
     */
    public static PriceRange[] generateMockPriceRange() throws Exception {
        return (PriceRange[]) generateMockedResponse("priceRangeResponse.json", PriceRange[].class);
    }

    /**
     * Helper function to return home page
     */
    public static Object generateMockHomePage() throws Exception {
        return generateMockHtmlPage("homePage.html");
    }

    /**
     * Helper function to return is logged in string.
     */
    public static String generateMockIsLoggedIn() throws Exception {
        return generateStringOfJsonFile("isLoggedIn.json");
    }

    /**
     * Return the secondary code.
     */
    public static AuthToken generateMockSecondaryAuthResponse() throws Exception {
        return (AuthToken) generateMockedResponse("secondaryCode.json", AuthToken.class);
    }

    /**
     * Helper function to return sign in page.
     */
    public static Object generateMockSignIn() throws Exception {
        return generateMockHtmlPage("signin.html");
    }


    /**
     * Helper function to return login html before redirect which contains some javascript.
     */
    public static Object generateMockLoginHtmlBeforeRedirect() throws Exception {
        return generateMockHtmlPage("loginHtmlBeforeRedirect.html");
    }

    /**
     * Helper function to return enter code page.
     */
    public static Object generateMockEnterCodePage() throws Exception {
        return generateMockHtmlPage("enterCodePage.html");
    }

    /**
     * Helper function to return gateway request with pid details.
     */
    public static String generateMockGatewayPid() throws Exception {
        return generateStringOfJsonFile("gateway.pid.json");
    }

    /**
     * Helper function to return id auth code (bearer token).
     */
    public static String generateMockIDAuthCode() throws Exception {
        return generateStringOfJsonFile("IdAuthCode.json");
    }

    /**
     * Helper function to return mock totw response.
     */
    public static String generateMockTOTW() throws Exception {
        return generateStringOfJsonFile("totwResponse.json");
    }

    /**
     * Helper function to return session id.
     */
    public static String generateMockSessionId() throws Exception {
        return generateStringOfJsonFile("sessionId.json");
    }

    /**
     * Helper function to return mock shards
     */
    public static Shards generateMockShards() throws Exception {
        return (Shards) generateMockedResponse("shards.json", Shards.class);
    }

    /**
     * Helper function to return mock user accounts
     */
    public static UserAccounts generateMockUserAccounts() throws Exception {
        return (UserAccounts) generateMockedResponse("userAccounts.json", UserAccounts.class);
    }

    /**
     * Helper function to return mock answer correct
     */
    public static ValidateResponse generateMockAnswerCorrect() throws Exception {
        return (ValidateResponse) generateMockedResponse("answerCorrect.json", ValidateResponse.class);
    }


    /**
     * Helper function to return mock answer correct
     */
    public static ValidateResponse generateMockAlreadyAnswered() throws Exception {
        return (ValidateResponse) generateMockedResponse("alreadyAnswered.json", ValidateResponse.class);
    }

    /**
     * Helper function to return mock answer correct
     */
    public static ValidateResponse generateMockAnswerWrong() throws Exception {
        return (ValidateResponse) generateMockedResponse("answerWrong.json", ValidateResponse.class);
    }

    /**
     * Helper function to return mock answer correct
     */
    public static Object generateMockNotAnsweredYet() throws Exception {
        return (Object) generateMockedResponse("getQuestionNotAnswered.json", Object.class);
    }

    /**
     * Helper function to build Object from json file
     *
     * @param file File name
     * @param type Object type
     * @return Object
     * @throws Exception Handle errors
     */
    private static Object generateMockedResponse(String file, Class<?> type) throws Exception {
        URL resource = MockResponseUtils.class.getClassLoader().getResource(file);
        assert resource != null;
        String json = new String(Files.readAllBytes(Paths.get(resource.toURI())), Charset.forName("UTF-8"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper.readValue(json, type);
    }

    /**
     * Helper function to return mock html pages.
     *
     * @param file File to load
     * @return HTML String
     * @throws Exception handle any errors.
     */
    private static Object generateMockHtmlPage(String file) throws Exception {
        URL resource = MockResponseUtils.class.getClassLoader().getResource(file);
        assert resource != null;
        return new String(Files.readAllBytes(Paths.get(resource.toURI())), Charset.forName("UTF-8"));
    }

    private static String generateStringOfJsonFile(String file) throws Exception {
        URL resource = MockResponseUtils.class.getClassLoader().getResource(file);
        assert resource != null;
        return new String(Files.readAllBytes(Paths.get(resource.toURI())), Charset.forName("UTF-8")).replace("\n", "").replace("\r", "");

    }

}
