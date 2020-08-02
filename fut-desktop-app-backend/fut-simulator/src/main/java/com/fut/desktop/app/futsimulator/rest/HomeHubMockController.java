package com.fut.desktop.app.futsimulator.rest;

import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.ResponseReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * Handle home hub operations like usermassinfo, squads, objectives etc.
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class HomeHubMockController {

    /**
     * Default pile sizes
     */
    private Integer tradePileSize = 100; // replace 9999 with this
    private Integer watchListSize = 50; // replace 7777 with this
    private Integer unassignedPileCurrently = 0; // replace 5555 with this

    /**
     * Set the trade pile
     */
    @GetMapping("setTradePile/{tradePileSize}")
    public void setTradePileSize(@PathVariable("tradePileSize") Integer tradePileSize) {
        this.tradePileSize = tradePileSize;
    }

    /**
     * Set the trade pile
     */
    @GetMapping("setWatchList/{watchListSize}")
    public void setWatchListSize(@PathVariable("watchListSize") Integer watchListSize) {
        this.watchListSize = watchListSize;
    }

    /**
     * Set the trade pile
     */
    @GetMapping("setunassignedPileCurrently/{unassignedPileCurrentSize}")
    public void setUnassignedPileCurrentlySize(@PathVariable("unassignedPileCurrentSize") Integer unassignedPileCurrentSize) {
        this.unassignedPileCurrently = unassignedPileCurrentSize;
    }

    /**
     * Get the user mass info
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/usermassinfo")
    public String userMassInfo(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);
        String userMassInfoResp = ResponseReaderUtil.generateStringOfFile("responses/json/userMassInfo.json");

        userMassInfoResp = userMassInfoResp.replaceAll("9999", String.valueOf(tradePileSize));
        userMassInfoResp = userMassInfoResp.replaceAll("7777", String.valueOf(watchListSize));
        userMassInfoResp = userMassInfoResp.replaceAll("5555", String.valueOf(unassignedPileCurrently));

        return userMassInfoResp;
    }

    /**
     * Get live messages
     */
    @GetMapping(value = "/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/livemessage/template{screen}{_}")
    public String liveMessages(@RequestHeader HttpHeaders requestHeaders,
                               @RequestParam("screen") String screen,
                               @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("Screen is null", screen);
        Assert.assertNotNull("Timestamp is null", timestamp);

        return ResponseReaderUtil.generateStringOfFile("responses/json/liveMessages.json");
    }

    /**
     * Get squads
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/squad/0/user/{nucleus}{_}")
    public String squads(@RequestHeader HttpHeaders requestHeaders,
                         @PathVariable("nucleus") String nucleus,
                         @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("nucleus in the url is null", nucleus);
        Assert.assertNotNull("Timestamp is null", timestamp);

        return ResponseReaderUtil.generateStringOfFile("responses/json/squads.json");
    }

    /**
     * Get message list
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/message/list/template{nucPersId}{screen}{_}")
    public String messageList(@RequestHeader HttpHeaders requestHeaders,
                              @RequestParam("nucPersId") String nucPersId,
                              @RequestParam("screen") String screen,
                              @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("nucleus in the url is null", nucPersId);
        Assert.assertNotNull("Screen is null", screen);
        Assert.assertNotNull("Timestamp is null", timestamp);

        return ResponseReaderUtil.generateStringOfFile("responses/json/messageList.json");
    }


    /**
     * Get champions hub
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/champion/user/hub{scope}")
    public String championsHub(@RequestHeader HttpHeaders requestHeaders,
                               @RequestParam("scope") String scope) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        Assert.assertNotNull("Scope is null", scope);

        return ResponseReaderUtil.generateStringOfFile("responses/json/championsHub.json");
    }

    /**
     * Get user objectives
     */
    @GetMapping("/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/user/dynamicobjectives{scope}")
    public String userObjectives(@RequestHeader HttpHeaders requestHeaders,
                                 @RequestParam("scope") String scope) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        Assert.assertNotNull("Scope is null", scope);

        return ResponseReaderUtil.generateStringOfFile("responses/json/userObjectives.json");
    }

    /**
     * Get club stats and staff
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/club/stats/staff{_}")
    public String clubStatsStaff(@RequestHeader HttpHeaders requestHeaders,
                                 @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);
        Assert.assertNotNull("Timestamp is null", timestamp);
        return ResponseReaderUtil.generateStringOfFile("responses/json/clubStatsStaff.json");
    }

    /**
     * Get totw
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/totw{start}{count}{sku_c}{_}")
    public String totw(@RequestHeader HttpHeaders requestHeaders, @RequestParam("start") String start,
                       @RequestParam("count") String count, @RequestParam("sku_c") String sku_c,
                       @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("Start is null", start);
        Assert.assertNotNull("Count is null", count);
        Assert.assertNotNull("sku_c is null", sku_c);
        Assert.assertNotNull("Timestamp is null", timestamp);

        return ResponseReaderUtil.generateStringOfFile("responses/json/totw.json");
    }

    /**
     * Get settings
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/settings{_}")
    public String settings(@RequestHeader HttpHeaders requestHeaders,
                           @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("Timestamp is null", timestamp);
        return ResponseReaderUtil.generateStringOfFile("responses/json/settings.json");
    }

    /**
     * /utas.external.s3.fut.ea.com/ut/game/fifa19/rivals/weekendleague/status
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/rivals/weekendleague/status")
    public String weekendLeagueStatus(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);
        return ResponseReaderUtil.generateStringOfFile("responses/json/weekendLeague.json");
    }

    /**
     * /utas.external.s3.fut.ea.com/ut/game/fifa19/rivals/user/prizeDetails
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/rivals/user/prizeDetails")
    public String prizeDetails(@RequestHeader HttpHeaders requestHeaders) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);
        return "{}";
    }

    /**
     * https://utas.external.s3.fut.ea.com/ut/game/fifa19/sqbt/user/hub?scope=mini
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/sqbt/user/hub{scope}")
    public String sqbt(@RequestHeader HttpHeaders requestHeaders, @RequestParam("scope") String scope) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        return ResponseReaderUtil.generateStringOfFile("responses/json/sqbt.json");
    }

    /**
     * https://utas.external.s3.fut.ea.com/ut/game/fifa19/activeMessage?_=1545765871416
     */
    @GetMapping("/utas.external.*.fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/activeMessage{_}")
    public String activeMessage(@RequestHeader HttpHeaders requestHeaders,
                                @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        Assert.assertNotNull("Timestamp is null", timestamp);

        return ResponseReaderUtil.generateStringOfFile("responses/json/activeMessage.json");
    }
}
