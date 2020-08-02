package com.fut.desktop.app.futsimulator.rest;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.dto.MarketSearchRequest;
import com.fut.desktop.app.futsimulator.service.TransferSimService;
import com.fut.desktop.app.futsimulator.utils.AuctionResponseGenerator;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.parameters.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Handle transfer market operations
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class TransferMockMarketRestController {

    private final AuctionResponseGenerator auctionResponseGenerator;

    private boolean trainForCaptcha;

    private final TransferSimService transferSimService;

    @Autowired
    public TransferMockMarketRestController(AuctionResponseGenerator auctionResponseGenerator,
                                            TransferSimService transferSimService) {
        this.auctionResponseGenerator = auctionResponseGenerator;
        this.transferSimService = transferSimService;
        this.trainForCaptcha = false;
    }

    /**
     * https://utas.external.s3.fut.ea.com/ut/game/fifa19/transfermarket?start=0&num=21&type=player&maskedDefId=176635&macr=17000&_=1547312221838
     * https://utas.external.s3.fut.ea.com/ut/game/fifa19/transfermarket?start=0&num=21&type=player&zone=defense&lev=gold&nat=18&leag=13&team=1&playStyle=265&micr=5900&macr=17000&minb=6000&maxb=60000&_=1547312221843
     */
    @GetMapping(RestConstants.UTAS + Resources.TransferMarket + "{start}{num}{type}{player}{maskedDefId}{zone}{lev}{nat}{leag}{team}{playStyle}{micr}{macr}{minb}{maxb}{_}")
    @ResponseBody
    public AuctionResponse search(@RequestHeader HttpHeaders requestHeaders,
                                  @RequestParam("start") Integer start,
                                  @RequestParam("num") Integer num,
                                  @RequestParam("type") String type,
                                  @RequestParam(value = "maskedDefId", required = false) Integer maskedDefId,
                                  @RequestParam(value = "zone", required = false) String zone,
                                  @RequestParam(value = "lev", required = false) String lev,
                                  @RequestParam(value = "nat", required = false) Integer nat,
                                  @RequestParam(value = "leag", required = false) Integer leag,
                                  @RequestParam(value = "team", required = false) Integer team,
                                  @RequestParam(value = "playStyle", required = false) Integer playStyle,
                                  @RequestParam(value = "micr", required = false) Integer micr,
                                  @RequestParam(value = "macr", required = false) Integer macr,
                                  @RequestParam(value = "minb", required = false) Integer minb,
                                  @RequestParam(value = "maxb", required = false) Integer maxb,
                                  @RequestParam("_") String timestamp) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        // Validate correct parameters have been passed in.
        Assert.assertNotNull("Timestamp is null", timestamp);
        Assert.assertNotNull("Start is null!", start);
        Assert.assertNotNull("Num is null", num);
        Assert.assertNotNull("Type is null", type);

        if (lev != null) {
            Assert.assertNotNull("Invalid level", Level.valueOf(lev));
        }

        if (nat != null) {
            Assert.assertNotNull("Invalid Nation", Nation.getAll().stream().filter(n -> n.getValue() == nat.longValue()).findFirst().orElse(null));

        }

        if (leag != null) {
            Assert.assertNotNull("Invalid League", League.getAll().stream().filter(l -> l.getValue() == leag.longValue()).findFirst().orElse(null));
        }

        if (team != null) {
            Assert.assertNotNull("Invalid Team", Team.getAll().stream().filter(t -> t.getValue() == team.longValue()).findFirst().orElse(null));
        }

        if (playStyle != null) {
            if (playStyle != ChemistryStyle.Basic.getChemistryStyle()) {
                Assert.assertNotNull("Invalid Chemistry", ChemistryStyle.fromValue(playStyle));
            }
        }

        if (micr != null && macr != null) {
            Assert.assertTrue("Min is greater than the max", micr < macr);
        }

        if (minb != null && maxb != null) {
            Assert.assertTrue("Min is greater than the max", minb < maxb);
        }

        if (macr != null && maxb != null) {
            Assert.assertTrue("Max bid is greater than max BIN", macr < maxb);
        }

        if (micr != null && minb != null) {
            Assert.assertTrue("Min bid is greater than min BIN", micr < minb);
        }

        return this.transferSimService.setAssetIdAndReturnSearchResults(maskedDefId);
    }

    @PutMapping(RestConstants.UTAS + "trade/{tradeId}/bid{client}")
    @ResponseBody
    public AuctionResponse bidItem(@RequestHeader HttpHeaders requestHeaders,
                                   @RequestBody String bidInfo, @PathVariable("tradeId") Long tradeId,
                                   @RequestParam("client") String client, HttpServletResponse response) throws Exception {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);
        Assert.assertNotNull("client is null", client);
        Assert.assertTrue("Trade id should be greated than 0", tradeId > 0);
        if (trainForCaptcha) {
            response.setStatus(FutErrorCode.CaptchaTriggered.getFutErrorCode());
            return null;
        }

        JsonObject jsonObject = new JsonParser().parse(bidInfo).getAsJsonObject();
        Long bidAmount = jsonObject.get("bid").getAsLong();

        return this.transferSimService.generateBiddedItem(tradeId, bidAmount);
    }

    @GetMapping(RestConstants.TRAIN + RestConstants.MARKET + "/captcha/{trainForCaptcha}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setTrainForCaptcha(@PathVariable Boolean trainForCaptcha) {
        this.trainForCaptcha = trainForCaptcha;
        log.info("Train for captcha is now {}", this.trainForCaptcha);
    }

    /******************************************************************************************************************
     *********************************************** Trainer REST Calls ***********************************************
     ******************************************************************************************************************
     */

    @PostMapping(RestConstants.TRAIN + RestConstants.MARKET + RestConstants.SEARCH)
    @ResponseBody
    public AuctionResponse trainMarketSearch(@RequestBody List<MarketSearchRequest> request) {
        log.info("Generating search result for {} items", request.size());
        this.transferSimService.generateSearchResults(request);

        return this.transferSimService.getSearchResults();
    }

}
