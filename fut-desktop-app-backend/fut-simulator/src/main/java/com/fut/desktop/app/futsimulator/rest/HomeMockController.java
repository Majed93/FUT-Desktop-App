package com.fut.desktop.app.futsimulator.rest;

import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.ResponseReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("Duplicates")
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class HomeMockController {

    @GetMapping("/homeHub")
    public Boolean homeHub() {
        return true;    // TODO:
    }

    /**
     * For web app
     */
    @GetMapping(value = "/www.easports.com/fifa/ultimate-team/web-app/content/{assetId}/{year}/fut/config/companion/remoteConfig.json")
    public String remoteConfigOptions(@RequestHeader HttpHeaders requestHeaders, @PathVariable("assetId") String assetId,
                                      @PathVariable("year") Integer year) throws Exception {
        // Assertions
        org.springframework.util.Assert.hasText(assetId, "Asset id is null");
        Assert.assertEquals("Asset ids do not match", Resources.Asset_Id, assetId);
        Assert.assertEquals("Year is too old: " + year, 20 + Resources.FUT_YEAR, year.toString());

        // Assert headers
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        return ResponseReaderUtil.generateStringOfFile("responses/json/remoteConfig.json");
    }

    /**
     * For mobile
     */
    @GetMapping(value = "/fifa19.content.easports.com/fifa/fltOnlineAssets/{assetId}/{year}/fut/config/companion/remoteConfig.json")
    public String remoteConfigOptionsMobile(@RequestHeader HttpHeaders requestHeaders, @PathVariable("assetId") String assetId,
                                      @PathVariable("year") Integer year) throws Exception {
        // Assertions
        org.springframework.util.Assert.hasText(assetId, "Asset id is null");
        Assert.assertEquals("Asset ids do not match", Resources.Asset_Id, assetId);
        Assert.assertEquals("Year is too old: " + year, 20 + Resources.FUT_YEAR, year.toString());

        // Assert headers
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        return ResponseReaderUtil.generateStringOfFile("responses/json/remoteConfig.json");
    }

    @GetMapping("/www.easports.com/fifa/ultimate-team/web-app/")
    public String futHomePage(@RequestHeader HttpHeaders requestHeaders) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        return ResponseReaderUtil.generateStringOfFile("responses/html/homePage.html");
    }
}
