package com.fut.desktop.app.constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resources {
    public String Validate;

    public String Question;

    public String Auth;

    public String AuthCode;

    public String Home;

    public String NucleusId;

    public String Shards;

    public String AccountInfo;

    public String Item;

    public String PlayerImage;

    public String FlagsImage;

    public String ClubImage;

    public String TradePile = "tradepile";

    public String RemoteConfig;

    // Year of FUT
    public final static String FUT_YEAR = "19";

    // FUT_<YEAR>_WEB constant
    public final static String FUT_WEB = "FUT" + FUT_YEAR + "WEB";
    public final static String FUT_ANDROID = "FUT" + FUT_YEAR + "AND";

    public static final String TIME_EXT = "?_=";

    // This is changed once logged in to s3 or s2 or appropriate.
    public String FutHome = "https://utas.external.s2.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/";

    //TODO: THESE NEED TO BE DYNAMIC IN TERMS OF s2/s3
    public final static String FutHomeXbox = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/";

    public final static String ValidateXbox = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/phishing/validate";

    public final static String AuthXbox = "https://utas.external.s3.fut.ea.com/ut/auth?timestamp={0}";

    public final static String AccountInfoXbox =
            "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/user/accountinfo?sku=FUT" + FUT_YEAR + "AND&_={0}";

    public final static String POWAuth = "https://pas.mob.v2.easfc.ea.com:8095/pow/auth?timestamp={0}";

    public final static String CaptchaImage = "https://www.easports.com/iframe/fut" + FUT_YEAR + "/p/ut/captcha/img?token=AAAA&_={0}";

    public final static String CaptchaValidate = "https://www.easports.com/iframe/fut" + FUT_YEAR + "/p/ut/captcha/validate";

    public final static String Token = "https://accounts.ea.com/connect/token";

    public final static String Pid = "https://gateway.ea.com/proxy/identity/pids/me";

    /**
     * Used below to be replaced by nucleus id
     */
    public final static String GATEWAY_SUBSCRIPTION_REPLACE = "GW_REPLACE";

    /**
     * Use {@link String#replaceAll(String, String)} to insert the nucleus ID.
     */
    public final static String GatewaySubscriptionPid = "https://gateway.ea.com/proxy/subscription/pids/" + GATEWAY_SUBSCRIPTION_REPLACE + "/subscriptionsv2/groups/Origin%20Membership?state=ENABLED,PENDINGEXPIRED";
    public final static String LoggedIn = "https://www.easports.com/fifa/api/isUserLoggedIn";

    /**
     * URL to get secondary authorization bearer token
     */
    public final static String SecondaryAuthBearerToken = "https://accounts.ea.com/connect/auth?response_type=token&redirect_uri=nucleus%3Arest&prompt=none&client_id=ORIGIN_JS_SDK";

    // This has a static client_secret - use String format to put the auth code in :)
    public final static String MobileAuthBearerToken = "https://accounts.ea.com/connect/token?grant_type=authorization_code&code=%s&client_id=FIFA-"
            + FUT_YEAR + "-MOBILE-COMPANION&client_secret=88dOrWFDtq7CCIQSiYUd9LbWgLW1UWAjijGLmt3rNSam5cCpCb68sAAkbGyOSFiMfDmwLxb&release_type=prod";

    public final static String MobileAuthBearerTokenRefresh = "https://accounts.ea.com/connect/token?client_id=FIFA-" + FUT_YEAR +
            "-MOBILE-COMPANION&refresh_token=" +
            "%s" + // The refresh token
            "&client_secret=88dOrWFDtq7CCIQSiYUd9LbWgLW1UWAjijGLmt3rNSam5cCpCb68sAAkbGyOSFiMfDmwLxb&grant_type=refresh_token&release_type=prod";

    public final static String CheckLoggedIn = "https://accounts.ea.com/connect/auth?client_id=FIFA-" + FUT_YEAR + "-WEBCLIENT&response_type=token&display=web2/login&locale=en_US&redirect_uri=nucleus:rest&prompt=none&scope=basic.identity+offline+signin";

    public final static String TryGetCodeMobile = "https://accounts.ea.com/connect/auth?client_id=FIFA-" + FUT_YEAR + "-MOBILE-COMPANION&response_type=code&display=web2/login&locale=en_US&redirect_uri=nucleus:rest&prompt=none&release_type=prod&scope=basic.identity+offline+signin";

    public final static String BaseShowoff =
            "http://www.easports.com/iframe/fut" + FUT_YEAR + "/?baseShowoffUrl=https%3A%2F%2Fwww.easports.com%2Fuk%2Ffifa%2Fultimate-team%2Fweb-app%2Fshow-off&guest_app_uri=http%3A%2F%2Fwww.easports.com%2Fuk%2Ffifa%2Fultimate-team%2Fweb-app&locale=en_GB";

    public final static String PriceRange = "marketdata/item/pricelimits?itemIdList="; // + item id and timestamp

    public final static String MyClub = "club?level=10";

    public final static String TransferMarket = "transfermarket";

    public final static String Bid = "trade/%s/bid";

    public final static String TradeStatus = "trade/status?tradeIds=";

    public final static String Credits = "user/credits";

    public final static String Auctionhouse = "auctionhouse";

    public final static String Watchlist = "watchlist?_="; // + timestamp

    public final static String WatchlistRemoveItem = "watchlist?tradeId="; // + timestamp

    public final static String PurchasedItems = "purchased/items";

    public final static String ListItem = "item";

    public final static String QuickSell = "item?itemIds={0}";

    public final static String RemoveFromTradePile = "trade/";

    public final static String PileSize = "clientdata/pileSize";

    public final static String Consumables = "club/stats/consumables";

    public final static String ConsumablesDetails = "club/consumables/development";

    public final static String SquadList = "squad/list";

    public final static String SquadDetails = "squad/{0}";

    public final static String ReList = "/relist";

    public final static String Definition = "defid?type=player&count=35&start=0&defId={0}";

    public final static String Store = "store/purchaseGroup/cardpack?ppInfo=true";

    public final static String PinRiver = "https://pin-river.data.ea.com/pinEvents";

    public final static String MessageBanners =
            "https://utas.external.s3.fut.ea.com/ut/mm/game/fifa" + FUT_YEAR + "/message/list?offset=0&screen=futwebhome&count=6";

    public final static String AccountsAuthCode = "https://accounts.ea.com/connect/auth?client_id=FOS-SERVER&redirect_uri=nucleus:rest&response_type=code&access_token=";

    public final static String AccountsAuthCodePrefix = "&release_type=prod";

    public final static String Asset_Id = "7D49A6B1-760B-4491-B10C-167FBC81D58A";

    public final static String SKU = "?sku_c=fut" + FUT_YEAR;

    public final static String AMP_SKU = "&sku_c=fut" + FUT_YEAR;

    public final static String CLIENT_COMP_AMPS = "&client=webcomp";
    public final static String CLIENT_COMP_QUESTION = "?client=webcomp";

    public final static String TOTW_URL = "totw?start=0&count=90&sku_c=fut" + FUT_YEAR + "&_="; // append timestamp

    // Contains pile sizes.
    public final static String userInfo = "usermassinfo";

    public final static String UTAS = "https://utas.external.fut.ea.com";
    public final static String UTAS_S2 = "https://utas.external.s2.fut.ea.com";
    public final static String UTAS_S3 = "https://utas.external.s3.fut.ea.com";

    // Settings
    public final static String SETTINGS = "settings?_="; // + timestamp

    // Live message
    public final static String LIVE_MESSAGE = "livemessage/template?screen=futweblivemsg&_="; // utas + {} + timestamp

    // Referer header sometimes.
    public final static String Referer = "https://www.easports.com/fifa/ultimate-team/web-app/";

    public final static String NUCLEUS_ID_PLACEHOLDER = "NUCLEUS_ID_PLACEHOLDER";

    // Message list
    public final static String MESSAGE_LIST = "message/list/template?nucPersId=" + NUCLEUS_ID_PLACEHOLDER + "&screen=webfuthub&_="; // + timestamp

    // Champions Hub
    public final static String CHAMPIONS_HUB = "champion/user/hub?scope=nano";

    // Club stats staff
    public final static String CLUB_STATS_STAFF = "club/stats/staff?_=";

    //Squads
    public final static String SQUADS = "squad/0/user/";

    // User objectives
    public final static String USER_OBJECTIVES = "user/dynamicobjectives?scope=all";

    // rivals/weekendleague
    public final static String RIVAL_WEEKEND_LEAGUE = "rivals/weekendleague/status";

    // Active message
    public final static String ACTIVE_MESSAGE = "activeMessage?_=";

    // sqbt
    public final static String SQBT = "sqbt/user/hub?scope=mini";

    // Prize details
    public final static String PRIZE_DETAILS = "rivals/user/prizeDetails";

    // Unassigned placeholder
    public final static String SEND_TO_TRADE_UNASSIGNED = "99999";

    public final static String ANDROID_ID = "e0d19aa1115b1e34"; // androidid - right now this is hard coded because we don't know how it's generated.

    public final static String Default_Home = "https://www.easports.com/fifa/ultimate-team/web-app/";

    public Resources(AppVersion appVersion) {
        switch (appVersion) {
            case WebApp:

                Validate = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/phishing/validate?answer=";

                Question = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/phishing/question?_=";

                Auth = "https://utas.external.s3.fut.ea.com/ut/auth?sku_c=fut" + FUT_YEAR;

                AuthCode = "https://accounts.ea.com/connect/auth?prompt=login&accessToken=null&client_id=FIFA-" + FUT_YEAR + "-WEBCLIENT&response_type=token&display=web2/login&locale=en_US&redirect_uri=https://www.easports.com/uk/fifa/ultimate-team/web-app/auth.html&scope=basic.identity+offline+signin";

                Home = Default_Home;

                NucleusId =
                        "https://www.easports.com/iframe/fut" + FUT_YEAR + "/?locale=en_US&baseShowoffUrl=https%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app%2Fshow-off&guest_app_uri=http%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app";

                Shards = "https://utas.mob.v1.fut.ea.com/ut/shards/v2";

                // Need to add https://utas.external.fut.ea.com in front of it. and appended with a timestamp.
                AccountInfo =
                        "/ut/game/fifa" + FUT_YEAR + "/user/accountinfo?filterConsoleLogin=true&sku=" + FUT_WEB + "&returningUserGameYear=2018";

                Item =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/various/web/{0}.json";

                PlayerImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/players/web/{0}.png";

                FlagsImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/flags/web/low/{0}.png";

                ClubImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/clubbadges/web/normal/s{0}.png";

                RemoteConfig = "https://www.easports.com/fifa/ultimate-team/web-app/content/" + Asset_Id + "/20" + FUT_YEAR + "/fut/config/companion/remoteConfig.json";

                // TradePile = "tradepile?brokeringSku=FFA" + FUT_YEAR + "WEB";
                break;
            case CompanionApp:

                Validate = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/phishing/validate?answer=";

                Question = "https://utas.external.s3.fut.ea.com/ut/game/fifa" + FUT_YEAR + "/phishing/question?_=";

                Auth = "https://utas.external.s3.fut.ea.com/ut/auth?sku_c=fut" + FUT_YEAR;

                AuthCode = "https://accounts.ea.com/connect/auth?prompt=login&accessToken=&client_id=FIFA-" + FUT_YEAR +
                        "-MOBILE-COMPANION&response_type=code&display=web2/login&locale=en_US&machineProfileKey=" +
                        ANDROID_ID + "&release_type=prod&scope=basic.identity+offline+signin";

                Home = Default_Home;

                NucleusId =
                        "https://www.easports.com/iframe/fut" + FUT_YEAR + "/?locale=en_US&baseShowoffUrl=https%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app%2Fshow-off&guest_app_uri=http%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app";

                Shards = "https://utas.mob.v1.fut.ea.com/ut/shards/v2";

                // Need to add https://utas.external.fut.ea.com in front of it. and appended with a timestamp.
                AccountInfo =
                        "/ut/game/fifa" + FUT_YEAR + "/user/accountinfo?filterConsoleLogin=true&sku=" + FUT_ANDROID + "&returningUserGameYear=2018";

                Item =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/web/{0}.json";

                PlayerImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/players/web/{0}.png";

                FlagsImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/cardflagssmall/web/{0}.png";

                ClubImage =
                        "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/items/images/clubbadges/web/dark/s{0}.png";

                RemoteConfig = "https://fifa" + FUT_YEAR + ".content.easports.com/fifa/fltOnlineAssets/" + Asset_Id + "/20" + FUT_YEAR + "/fut/config/companion/remoteConfig.json";

                break;
            default:
                throw new IndexOutOfBoundsException("Argument out of bounds: " + appVersion.name());
        }
    }
}
