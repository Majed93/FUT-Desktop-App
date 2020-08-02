package com.fut.api.fut.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.PinResponse;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.domain.pinEvents.*;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.StringEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Pin Service to handle pin events to EA.
 */
@Getter
@Setter
@Slf4j
@Scope(value = "singleton")
@Component
public class PinService extends FutRequestBase {

    public long pinEventNucleusPersonaId;
    public String pinEventPersonaId;
    public String pinEventSessionId;

    private PinEventId previousPinEventId = PinEventId.WebApp_Load;
    public PinEventId currentPinEventId;
    private int pinRequestCount;
    private String version;
    private String uuid;
    private String tid;
    private String dob;
    private List<Long> msgId = new ArrayList<>();
    private final static String CUSTOM_PARSE_THIS = "PARSETHIS";
    private Platform platform;
    public final static Long EMPTY_PID = 999999L;
    private String networkAccess;
    private String plat;
    ObjectMapper mapper;

    /**
     * Constructor.
     */
    public PinService() {
        this.mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        pinRequestCount = 0;
    }

    /**
     * This must be set each time before sending a pinEvent to ensure data is not stale.
     *
     * @param nucleusId         Nucleus Id
     * @param personaId         Persona Id
     * @param sessionId         Session Id
     * @param currentPinEventId Current Pin Event Id
     * @param dob               DOB
     * @param platform          Platform
     */
    public void setProperties(long nucleusId, String personaId, String sessionId,
                              PinEventId currentPinEventId, String dob, Platform platform) {
        this.pinEventNucleusPersonaId = nucleusId;
        this.pinEventPersonaId = personaId;
        this.pinEventSessionId = sessionId;
        this.currentPinEventId = currentPinEventId;
        this.dob = dob;
        this.platform = platform;
    }

    /**
     * increment the pin count.
     */
    private void incrementPinRequest() {
        pinRequestCount++;
    }

    /**
     * Reset the pin count.
     */
    public void resetPinCount() {
        pinRequestCount = 0;
    }

    /**
     * Send the pin event.
     *
     * @param eventId Current pin event.
     * @return pin response.
     */
    public Future<PinResponse> sendPinEvent(PinEventId eventId) {
        currentPinEventId = eventId;

        // Detect if web app or companion
        if (baseAppVersion != AppVersion.WebApp) {
            // Companion/Mobile App
            networkAccess = "W";
            addPinHeadersMobile();
            tid = "FUT" + Resources.FUT_YEAR + "AND";
            version = "19.0.3.179812";
            plat = "android";
        } else {
            // Web app
            networkAccess = "G";
            tid = Resources.FUT_WEB;
            version = "19.1.0";
            addPinHeaders();
            plat = "web";
        }

        try {
            PinEvent pinEvent = generatePinData();
            String json = mapper.writeValueAsString(pinEvent);
            json = json.replaceAll("\"" + CUSTOM_PARSE_THIS + "\"", "{}");
            // Pid is actually a number..
            json = json.replaceAll(String.valueOf(EMPTY_PID), "\"unknown\"");

            HttpMessage pinMsg = httpClient.PostAsync(Resources.PinRiver, new StringEntity(json)).get();

            incrementPinRequest();

            return new AsyncResult<>((PinResponse) deserializeAsync(pinMsg, PinResponse.class).get());

        } catch (Exception e) {
            log.error("Unable to get pinEvent! " + e.getMessage());
            return null;
        }
    }

    /**
     * Generate pin data.
     *
     * @return Pin event.
     */
    private PinEvent generatePinData() throws Exception {
        PinEvent pinEvent = new PinEvent();

        pinEvent.setEvents(new ArrayList<>());
        pinEvent.setTaxv(1.1);
        pinEvent.setTid(tid);
        pinEvent.setRel("prod");
        pinEvent.setV(version);
        pinEvent.setEt("client");

        pinEvent.setTidt("easku");
        pinEvent.setPlat(plat);
        pinEvent.setLoc("en_US");
        pinEvent.setSid(pinEventSessionId);
        pinEvent.setTs_post(DateTimeExtensions.ToISO8601Time(LocalDateTime.now()));
        pinEvent.setGid(0);

        Custom custom = new Custom();

        pinEvent.setCustom(custom);

        if (currentPinEventId == PinEventId.WebApp_Load) {
            pinEvent.setSid("");
            pinEvent = generateFirstPinEvent(pinEvent);
        } else if (currentPinEventId == PinEventId.WebApp_Login) {
            //Need to get message banner first.
            //getMessageBanners().get(); //TODO: not needed for now.
            //now set the login pinEvent.
            //login event
            pinEvent = generateLoginPinEvent(pinEvent);
        } else {
            // Normal nav between screens
            pinEvent = generateGenericPinEvent(pinEvent);
        }

        if (baseAppVersion == AppVersion.CompanionApp) {
            // Add the didm to each core.
            pinEvent.getEvents().forEach(event -> {
                if (event != null && event.getCore() != null) {
                    Didm didm = new Didm(Resources.ANDROID_ID);
                    event.getCore().setDidm(didm);
                }
            });
        }

        return pinEvent;
    }

    @Deprecated
    private Future<HttpMessage> getMessageBanners() throws Exception {
        throw new UnsupportedOperationException();
      /*  addCommonHeaders(HttpMethod.GET);

        HttpMessage message = httpClient.PostAsync(Resources.MessageBanners, new StringEntity(" ")).get();

        String[] regex = message.getContent().split(",\"startDateTime");

        for (String r : regex) {
            if (r.contains("\"screen\":\"futwebhome\"")) {
                String[] str = r.split(":");
                msgId.add(Long.valueOf(str[str.length - 1]));
            }
        }

        return new AsyncResult<>(message);*/
    }

    private String getServicePlatFromPlat() {
        switch (platform) {
            case Ps3:
                return "PS3";
            case Ps4:
                return "PS4";
            case Xbox360:
                return "XBX";
            case XboxOne:
                return "XBO";
            case Pc:
                return "PCC";
            default:
                return "XBO";
        }
    }

    /**
     * Generate a generic pin event, used when switching from one tab to another.
     *
     * @param pinEvent pinEvent to alter.
     * @return updated pinEvent
     */
    private PinEvent generateGenericPinEvent(PinEvent pinEvent) {
        pinEvent.set_sess(true);
        pinEvent.setCustom(genericCustom());

        Core core = generateCore(pinRequestCount, "persona", pinEventNucleusPersonaId,
                new Pidm(Long.valueOf(pinEventPersonaId)),
                null, DateTimeExtensions.ToISO8601Time(LocalDateTime.now()), "page_view", dob);

        Event event = generateEvent("menu", null, null, null, core,
                null, currentPinEventId, null, null);

        pinEvent.setEvents(Collections.singletonList(event));

        // If it's going to view items we need to send {@link PinEventId.App_ItemDetailView}
        // For now only if watchlist, tradepile & search results
        if (currentPinEventId == PinEventId.App_TransferList || currentPinEventId == PinEventId.App_TransferTargets || currentPinEventId == PinEventId.App_TransferMarketResults) {
            incrementPinRequest();
            Core core2 = generateCore(pinRequestCount, "persona", pinEventNucleusPersonaId,
                    new Pidm(Long.valueOf(pinEventPersonaId)),
                    null, DateTimeExtensions.ToISO8601Time(LocalDateTime.now()), "page_view", dob);

            // Explicitly add the event type
            Event additionalEvent = generateEvent("menu", null, null, null, core2,
                    null, PinEventId.App_ItemDetailView, null, null);

            pinEvent.setEvents(Arrays.asList(event, additionalEvent));
        }


        return pinEvent;
    }

    /**
     * This pinEvent contains the connection and boot_start pinEvent.
     *
     * @param pinEvent pinEvent to alter
     * @return return updated pinEvent.
     */
    private PinEvent generateFirstPinEvent(PinEvent pinEvent) {
        resetPinCount();
        pinEvent.set_sess(false);

        Custom custom = new Custom();
        custom.setNetworkAccess(networkAccess);
        custom.setService_plat("unknown");
        pinEvent.setCustom(custom);

        List<Event> events = new ArrayList<>();
        String iso8601Time = DateTimeExtensions.ToISO8601Time(LocalDateTime.now());

        //First event
      /*  Core firstEventCore = generateCore(pinRequestCount, "persona", EMPTY_PID,
                new Pidm(0L), new Didm("0"), iso8601Time, "connection", null);

        Event firstEvent = generateEvent(null, null, null, null, firstEventCore, null, null, null, null);
        events.add(firstEvent);

        incrementPinRequest();*/

        // Second event
        Core secondEventCore = generateCore(pinRequestCount, "persona", EMPTY_PID,
                null, null, iso8601Time, "boot_start", null);

        Event secondEvent = generateEvent(null, "success", "0-normal", null, secondEventCore, null, null, null, null);
        events.add(secondEvent);

        pinEvent.setEvents(events);

        return pinEvent;
    }

    /**
     * Create the pin event done after login. Pin count should be 2 here.
     *
     * @param pinEvent pinEvent to alter.
     * @return updated pin event.
     */
    private PinEvent generateLoginPinEvent(PinEvent pinEvent) {
        pinEvent.set_sess(true);
        pinEvent.setSid(null);
        pinEvent.setCustom(genericCustom());

        Core core = generateCore(pinRequestCount, "persona", pinEventNucleusPersonaId,
                new Pidm(Long.valueOf(pinEventPersonaId)), null,
                DateTimeExtensions.ToISO8601Time(LocalDateTime.now()), "login", dob);

        Event event = generateEvent("utas", "success", null, null, core,
                null, null, null, pinEventNucleusPersonaId);

        pinEvent.setEvents(Collections.singletonList(event));

        return pinEvent;
    }

    /**
     * Create a generic {@link Custom} field
     *
     * @return generic {@link Custom} object.
     */
    private Custom genericCustom() {
        Custom custom = new Custom();
        custom.setNetworkAccess(networkAccess);
        custom.setService_plat(getServicePlatFromPlat().toLowerCase());
        return custom;
    }

    /**
     * Generate {@link Core} object.
     * <p>
     * Examples of params are given with descriptions where possible
     *
     * @param s       0 - pin counts for session.
     * @param pidt    persona - usually persona, never seen anything different.
     * @param pid     190601680 -
     * @param pidm    {@link Pidm} object with nucleus id.
     * @param didm    {@link Didm} object with UUID.
     * @param tsEvent timestamp of event.
     * @param en      page type, e.g page_view
     * @param dob     user's DOB
     * @return {@link Core} object.
     */
    private Core generateCore(int s, String pidt, Long pid, Pidm pidm, Didm didm, String tsEvent,
                              String en, String dob) {
        Core core = new Core();

        core.setS(s);
        core.setPidt(pidt);
        core.setPid(pid);
        core.setPidm(pidm);
        core.setDidm(didm);
        core.setTs_event(tsEvent);
        core.setEn(en);
        core.setDob(dob);

        return core;
    }

    /**
     * Generate {@link Event} object.
     * <p>
     * Examples of params are given with descriptions where possible
     *
     * @param type   e.g menu - type of navigation
     * @param status usually success
     * @param source source of event
     * @param custom ?
     * @param core   {@link Core} object
     * @param msgId  possibly not used anymore.
     * @param pgid   id of page navigating to see {@link PinEventId}
     * @param toid   id of previous page (navigating from) see {@link PinEventId}
     * @param userId seems to be nucleus id.
     * @return {@link Event} object.
     */
    private Event generateEvent(String type, String status, String source, String custom, Core core,
                                Long msgId, PinEventId pgid, PinEventId toid, Long userId) {
        Event event = new Event();

        event.setType(type);
        event.setStatus(status);
        event.setSource(source);
        event.setCustom(custom);
        event.setCore(core);
        event.setMsg_Id(msgId);
        event.setPgid(pgid != null ? pgid.getPinEventId() : null);
        event.setToid(toid != null ? toid.getPinEventId() : null);
        event.setUserId(userId);

        return event;
    }
}
