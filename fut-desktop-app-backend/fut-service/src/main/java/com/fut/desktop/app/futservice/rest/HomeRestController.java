package com.fut.desktop.app.futservice.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.domain.PriceRange;
import com.fut.desktop.app.futservice.cache.CacheManager;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.futservice.service.base.StatusMessagingService;
import com.fut.desktop.app.restObjects.StatusMessage;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.EncryptUtil;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Home page, nothing should be displayed here
 */
@RequestMapping("")
@RestController
@Slf4j
public class HomeRestController {

    private final FileUtils fileUtils;

    private final SimpMessagingTemplate template;

    private final PlayerService playerService;

    private final AccountService accountService;

    private final StatusMessagingService statusMessagingService;

    private final RestTemplate rest;

    private boolean stop = false;

    private static String workingDir;

    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    @Value("${working.dir}")
    public void setWorkingDir(String dir) {
        workingDir = dir == null ? "" : dir;
    }

    private CacheManager cacheManager;

    /**
     * Constructor.
     *
     * @param fileUtils              Handle to {@link FileUtils}
     * @param template               Handle to {@link SimpMessagingTemplate}
     * @param playerService          Handle to {@link PlayerService}
     * @param accountService         Handle to {@link AccountService}
     * @param statusMessagingService Handle to {@link StatusMessagingService}
     * @param rest                   Handle to {@link RestTemplate}
     */
    @Autowired
    public HomeRestController(FileUtils fileUtils, SimpMessagingTemplate template, PlayerService playerService,
                              AccountService accountService, StatusMessagingService statusMessagingService, RestTemplate rest) {
        this.fileUtils = fileUtils;
        this.template = template;
        this.playerService = playerService;
        this.accountService = accountService;
        this.statusMessagingService = statusMessagingService;
        this.rest = rest;
        this.cacheManager = new CacheManager(20, TimeUnit.SECONDS);
    }

    @GetMapping
    public String index() throws Exception {
        log.info("working dir: " + workingDir);
        log.info("io: " + futServiceEndpoint);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        String itemjson = "{\"auctionInfo\":[{\"tradeId\":27682326106,\"itemData\":{\"id\":138173442036,\"timestamp\":1557864110,\"formation\":\"f4231\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":1,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":0,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":650,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":650,\"confidenceValue\":100,\"expires\":87,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682326106\"},{\"tradeId\":27682358208,\"itemData\":{\"id\":137962378985,\"timestamp\":1557420551,\"formation\":\"f352\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":1800,\"morale\":50,\"fitness\":77,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":4,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":4,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":3,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3000,\"confidenceValue\":100,\"expires\":191,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682358208\"},{\"tradeId\":27682370816,\"itemData\":{\"id\":129711035255,\"timestamp\":1540688715,\"formation\":\"f4231\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":1400,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":41,\"index\":0},{\"value\":2,\"index\":1},{\"value\":3,\"index\":2},{\"value\":1,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":41,\"index\":0},{\"value\":2,\"index\":1},{\"value\":3,\"index\":2},{\"value\":1,\"index\":3},{\"value\":1,\"index\":4}],\"training\":0,\"contract\":8,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":3,\"lifetimeAssists\":3,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":4000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3900,\"confidenceValue\":100,\"expires\":242,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682370816\"},{\"tradeId\":27682468083,\"itemData\":{\"id\":137350705580,\"timestamp\":1557699613,\"formation\":\"f3412\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":4,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":2100,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":23,\"index\":0},{\"value\":0,\"index\":1},{\"value\":2,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":14,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":268,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":4,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":4000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3500,\"confidenceValue\":100,\"expires\":460,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682468083\"},{\"tradeId\":27682477540,\"itemData\":{\"id\":137731930718,\"timestamp\":1557351013,\"formation\":\"f4222\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":800,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3200,\"confidenceValue\":100,\"expires\":512,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682477540\"},{\"tradeId\":27682460774,\"itemData\":{\"id\":137730511267,\"timestamp\":1557798694,\"formation\":\"f352\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":750,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3000,\"confidenceValue\":100,\"expires\":513,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682460774\"},{\"tradeId\":27682470286,\"itemData\":{\"id\":131177480052,\"timestamp\":1543048335,\"formation\":\"f41212\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":2000,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":6400,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":6300,\"confidenceValue\":100,\"expires\":516,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682470286\"},{\"tradeId\":27682499593,\"itemData\":{\"id\":131230457220,\"timestamp\":1557642321,\"formation\":\"f442\",\"untradeable\":false,\"assetId\":191687,\"rating\":82,\"itemType\":\"player\",\"resourceId\":50523335,\"owners\":3,\"discardValue\":656,\"itemState\":\"forSale\",\"cardsubtypeid\":1,\"lastSalePrice\":8000,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CB\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":8,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":33,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":56,\"index\":1},{\"value\":67,\"index\":2},{\"value\":56,\"index\":3},{\"value\":83,\"index\":4},{\"value\":85,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":265,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":9900,\"confidenceValue\":100,\"expires\":555,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682499593\"},{\"tradeId\":27682522011,\"itemData\":{\"id\":137740109766,\"timestamp\":1557411658,\"formation\":\"f352\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":2000,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":5,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":2,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":4900,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":4000,\"confidenceValue\":100,\"expires\":610,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682522011\"},{\"tradeId\":27682579318,\"itemData\":{\"id\":132071639810,\"timestamp\":1557692007,\"formation\":\"f4411\",\"untradeable\":false,\"assetId\":191687,\"rating\":82,\"itemType\":\"player\",\"resourceId\":50523335,\"owners\":2,\"discardValue\":656,\"itemState\":\"forSale\",\"cardsubtypeid\":1,\"lastSalePrice\":2500,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CB\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":56,\"index\":1},{\"value\":67,\"index\":2},{\"value\":56,\"index\":3},{\"value\":83,\"index\":4},{\"value\":85,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":9500,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":9400,\"confidenceValue\":100,\"expires\":776,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682579318\"},{\"tradeId\":27682607367,\"itemData\":{\"id\":137164701716,\"timestamp\":1555434885,\"formation\":\"f3421\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":1,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":0,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":4900,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":4000,\"confidenceValue\":100,\"expires\":926,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682607367\"},{\"tradeId\":27682611162,\"itemData\":{\"id\":137491299376,\"timestamp\":1556125871,\"formation\":\"f4222\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":1,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":0,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":650,\"confidenceValue\":100,\"expires\":948,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682611162\"},{\"tradeId\":27682629137,\"itemData\":{\"id\":137924528516,\"timestamp\":1557316318,\"formation\":\"f352\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":1,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":0,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":5000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":3500,\"confidenceValue\":100,\"expires\":992,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682629137\"},{\"tradeId\":27682647923,\"itemData\":{\"id\":131148535739,\"timestamp\":1557526482,\"formation\":\"f4222\",\"untradeable\":false,\"assetId\":191687,\"rating\":82,\"itemType\":\"player\",\"resourceId\":50523335,\"owners\":6,\"discardValue\":656,\"itemState\":\"forSale\",\"cardsubtypeid\":1,\"lastSalePrice\":9000,\"morale\":50,\"fitness\":98,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CB\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":19,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":17,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":56,\"index\":1},{\"value\":67,\"index\":2},{\"value\":56,\"index\":3},{\"value\":83,\"index\":4},{\"value\":85,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":264,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":9500,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":9400,\"confidenceValue\":100,\"expires\":1043,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682647923\"},{\"tradeId\":27682876566,\"itemData\":{\"id\":136951295118,\"timestamp\":1555767795,\"formation\":\"f4231\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":3,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":9900,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":650,\"confidenceValue\":100,\"expires\":1756,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682876566\"},{\"tradeId\":27682901806,\"itemData\":{\"id\":137928406819,\"timestamp\":1557508710,\"formation\":\"f352\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":5,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":1500,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":7500,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":2800,\"confidenceValue\":100,\"expires\":1837,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682901806\"},{\"tradeId\":27682919151,\"itemData\":{\"id\":137492164259,\"timestamp\":1556342167,\"formation\":\"f4222\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":2,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":1200,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":2800,\"confidenceValue\":100,\"expires\":1837,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682919151\"},{\"tradeId\":27682921433,\"itemData\":{\"id\":137919650327,\"timestamp\":1557275302,\"formation\":\"f4231\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":1,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":0,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":1,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":10000,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":850,\"confidenceValue\":100,\"expires\":1901,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682921433\"},{\"tradeId\":27682984986,\"itemData\":{\"id\":130630035290,\"timestamp\":1557519294,\"formation\":\"f532\",\"untradeable\":false,\"assetId\":191687,\"rating\":82,\"itemType\":\"player\",\"resourceId\":50523335,\"owners\":4,\"discardValue\":656,\"itemState\":\"forSale\",\"cardsubtypeid\":1,\"lastSalePrice\":8000,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CB\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":101,\"index\":0},{\"value\":3,\"index\":1},{\"value\":4,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":5,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":56,\"index\":1},{\"value\":67,\"index\":2},{\"value\":56,\"index\":3},{\"value\":83,\"index\":4},{\"value\":85,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":265,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":1,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":9500,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":9400,\"confidenceValue\":100,\"expires\":2097,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27682984986\"},{\"tradeId\":27683100013,\"itemData\":{\"id\":137912282324,\"timestamp\":1557866488,\"formation\":\"f4321\",\"untradeable\":false,\"assetId\":184134,\"rating\":80,\"itemType\":\"player\",\"resourceId\":50515782,\"owners\":4,\"discardValue\":640,\"itemState\":\"forSale\",\"cardsubtypeid\":2,\"lastSalePrice\":5200,\"morale\":50,\"fitness\":99,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CDM\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":7,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":62,\"index\":1},{\"value\":70,\"index\":2},{\"value\":71,\"index\":3},{\"value\":80,\"index\":4},{\"value\":84,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":250,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":4400,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":650,\"confidenceValue\":100,\"expires\":2452,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27683100013\"},{\"tradeId\":27683156126,\"itemData\":{\"id\":131856835600,\"timestamp\":1557672093,\"formation\":\"f5221\",\"untradeable\":false,\"assetId\":191687,\"rating\":82,\"itemType\":\"player\",\"resourceId\":50523335,\"owners\":4,\"discardValue\":656,\"itemState\":\"forSale\",\"cardsubtypeid\":1,\"lastSalePrice\":9000,\"morale\":50,\"fitness\":96,\"injuryType\":\"none\",\"injuryGames\":0,\"preferredPosition\":\"CB\",\"statsList\":[{\"value\":0,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"lifetimeStats\":[{\"value\":44,\"index\":0},{\"value\":0,\"index\":1},{\"value\":0,\"index\":2},{\"value\":0,\"index\":3},{\"value\":0,\"index\":4}],\"training\":0,\"contract\":4,\"suspension\":0,\"attributeList\":[{\"value\":63,\"index\":0},{\"value\":56,\"index\":1},{\"value\":67,\"index\":2},{\"value\":56,\"index\":3},{\"value\":83,\"index\":4},{\"value\":85,\"index\":5}],\"teamid\":325,\"rareflag\":48,\"playStyle\":268,\"leagueId\":68,\"assists\":0,\"lifetimeAssists\":0,\"loyaltyBonus\":0,\"pile\":5,\"nation\":54,\"resourceGameYear\":2019,\"groups\":[5]},\"tradeState\":\"active\",\"buyNowPrice\":9500,\"currentBid\":0,\"offers\":0,\"watched\":false,\"bidState\":\"none\",\"startingBid\":9400,\"confidenceValue\":100,\"expires\":2595,\"sellerName\":\"FIFA UT\",\"sellerEstablished\":0,\"sellerId\":0,\"tradeOwner\":false,\"tradeIdStr\":\"27683156126\"}],\"bidTokens\":{}}";

        AuctionResponse o = mapper.readValue(itemjson, AuctionResponse.class);

//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
        return "home page";
    }

    @GetMapping("/addCache/{defId}")
    public String addCache(@PathVariable("defId") Long defId) {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(defId);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);

        priceRange = cacheManager.addToCache(priceRange);
        log.info("new price range." + priceRange.getDefId());
        return String.format("Added to cache %s", defId);
    }

    @GetMapping("/startCache")
    public String startCache() {
        try {
            cacheManager.start();
            return "Starting cache";
        } catch (Exception e) {
            log.error("Error starting cache", e);
            return "error starting cache.";
        }
    }

    @GetMapping("/stopCache")
    public String stopCache() {
        try {
            cacheManager.stop();
            return "Stopped cache";
        } catch (Exception e) {
            log.error("Error stopping cache", e);
            return "error stopping cache.";
        }
    }

    @GetMapping("/fut-io")
    public String futIo() {
        log.info("IO Endpoint: " + futServiceEndpoint);
        ResponseEntity<String> resp = rest.getForEntity(URI.create(futServiceEndpoint), String.class);

        return "Got response: " + resp.getBody();
    }

    @GetMapping("/stop")
    public void stop() {
        stop = true;
        template.convertAndSend("/playerSearch/start", "stopping..");
    }


    @GetMapping("/status")
    public void status() {
        stop = true;
        template.convertAndSend("/playerSearch/status", new StatusMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + "test"));
    }

    @GetMapping("/testAction")
    public void testAction() {
        template.convertAndSend("/manual/actionNeeded", 429);
    }

    @GetMapping("/testWs")
    public void testWs() {

        for (int i = 0; i < 10; i++) {
            String str = RandomStringUtils.random(32, true, true);
            template.convertAndSend("/test/test", str);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Uh oh.. timer went bust");
            }
        }
//        return str;
    }

    /**
     * Setup initial folder and files. This needs to be called manually.
     *
     * @return error object if exists.
     */
    @GetMapping("setup")
    @ResponseBody
    public FutError setup() {
        String dataDir = FileUtils.dataDir;
        String listDir = dataDir + File.separator + "lists";
        Boolean createDir = fileUtils.createFolder(dataDir);
        Boolean createListDir = fileUtils.createFolder(listDir);
        Boolean createAccountsList = fileUtils.createDataFile("accounts");
        Boolean createAccountExpanded = fileUtils.createDataFile("accountDetails");

        FutError futError = null;

        if (createDir == null) {
            futError = new FutError();
            String err = "Something went wrong creating data folder!";
            futError.setMessage(err);
            log.error(err);
        }

        if (createListDir == null) {
            futError = new FutError();
            String err = "Something went wrong creating lists folder!";
            futError.setMessage(err);
            log.error(err);
        }

        if (createAccountsList == null) {
            futError = new FutError();
            String err = "Something went wrong creating account list file.";
            futError.setMessage(err);
            log.error(err);
        }

        if (createAccountExpanded == null) {
            futError = new FutError();
            String err = "Something went wrong creating account expanded file.";
            futError.setMessage(err);
            log.error(err);
        }

        return futError;
    }
}
