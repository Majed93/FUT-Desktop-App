package com.fut.api.fut.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.api.fut.httpWrappers.HttpMessage;
import com.fut.api.fut.utils.HttpClientTestUtils;
import com.fut.api.fut.utils.MockResponseUtils;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.constants.PinEventId;
import com.fut.desktop.app.domain.PinResponse;
import com.fut.desktop.app.domain.Platform;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class PinServiceTest extends FutRequestBaseTest {

    @InjectMocks
    private PinService pinService;

    @Mock
    private Future<HttpMessage> message;

    @Mock
    private ObjectMapper objectMapper;

    private int startingPinCount = 0;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // For some reason base platform on this is null
        pinService.setBaseAppVersion(AppVersion.WebApp);
        pinService.setBasePlatform(Platform.XboxOne);
        setupMocks();
        testSetProperties();
        pinService.resetPinCount();
        startingPinCount = pinService.getPinRequestCount();
    }

    @Test
    public void testSetProperties() {
        long expectedNucleus = 123L;
        String expectedPersonaId = "12345";
        String expectedSessionId = "ABC";
        PinEventId expectedPinId = PinEventId.WebApp_Load;
        String expectedDob = "01/01/1970";
        Platform expectedPlatform = Platform.Ps4;

        pinService.setProperties(expectedNucleus, expectedPersonaId, expectedSessionId,
                expectedPinId, expectedDob, expectedPlatform);

        Assert.assertEquals(expectedNucleus, pinService.getPinEventNucleusPersonaId());
        Assert.assertEquals(expectedPersonaId, pinService.getPinEventPersonaId());
        Assert.assertEquals(expectedSessionId, pinService.getPinEventSessionId());
        Assert.assertEquals(expectedPinId, pinService.getCurrentPinEventId());
        Assert.assertEquals(expectedDob, pinService.getDob());
        Assert.assertEquals(expectedPlatform, pinService.getPlatform());
    }

    @Test
    public void testResetPinCount() {
        pinService.resetPinCount();
        Assert.assertEquals(0, pinService.getPinRequestCount());
    }

    @Test
    public void testSendPinEventWebApp_Login() throws Exception {
        pinService.setPlatform(Platform.Ps3);
        pinService.setBaseAppVersion(AppVersion.WebApp);
        HttpMessage mockMessage = mock(HttpMessage.class);
        PinResponse mockResponse = new PinResponse("ok");
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock put request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), PinResponse.class)).thenReturn(mockResponse);

        PinResponse result = pinService.sendPinEvent(PinEventId.WebApp_Login).get();

        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));

        // Assert it's been incremented.
        Assert.assertEquals(startingPinCount + 1, pinService.getPinRequestCount());
        Assert.assertNotNull(result);
        Assert.assertEquals(PinEventId.WebApp_Login, pinService.getCurrentPinEventId());
    }

    @Test
    public void testSendPinEventCompanionApp_Connect() throws Exception {
        pinService.setBaseAppVersion(AppVersion.CompanionApp);
        HttpMessage mockMessage = mock(HttpMessage.class);
        PinResponse mockResponse = new PinResponse("ok");
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock put request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), PinResponse.class)).thenReturn(mockResponse);

        PinResponse result = pinService.sendPinEvent(PinEventId.CompanionApp_Connect).get();

        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));

        // Assert it's been incremented.
        // 2 because we need to send 2 pin events by the end of it.
        Assert.assertEquals(startingPinCount + 1, pinService.getPinRequestCount());
        Assert.assertNotNull(result);
        Assert.assertEquals(PinEventId.CompanionApp_Connect, pinService.getCurrentPinEventId());
    }


    @Test
    public void testSendPinEventTransfer_hub() throws Exception {
        pinService.setBaseAppVersion(AppVersion.WebApp);
        pinService.setPlatform(Platform.Xbox360);

        HttpMessage mockMessage = mock(HttpMessage.class);
        PinResponse mockResponse = new PinResponse("ok");
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock put request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), PinResponse.class)).thenReturn(mockResponse);

        PinResponse result = pinService.sendPinEvent(PinEventId.App_HubTransfers).get();

        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));

        // Assert it's been incremented.
        // 2 because we need to send 2 pin events by the end of it.
        Assert.assertEquals(startingPinCount + 1, pinService.getPinRequestCount());
        Assert.assertNotNull(result);
        Assert.assertEquals(PinEventId.App_HubTransfers, pinService.getCurrentPinEventId());
    }

    @Test
    public void testSendPinEventWebApp_load() throws Exception {
        pinService.setBaseAppVersion(AppVersion.WebApp);
        pinService.setPlatform(Platform.Pc);

        HttpMessage mockMessage = mock(HttpMessage.class);
        PinResponse mockResponse = new PinResponse("ok");
        String content = MockResponseUtils.objectToString(mockResponse);

        // Mock successful response.
        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        // Mock put request
        when(httpClient.PostAsync(anyString(), any(StringEntity.class))).thenReturn(message);
        when(objectMapper.readValue(mockMessage.getContent(), PinResponse.class)).thenReturn(mockResponse);

        PinResponse result = pinService.sendPinEvent(PinEventId.WebApp_Load).get();

        verify(httpClient).PostAsync(anyString(), any(StringEntity.class));

        // Assert it's been incremented.
        Assert.assertEquals(1, pinService.getPinRequestCount());
        Assert.assertNotNull(result);
        Assert.assertEquals(PinEventId.WebApp_Load, pinService.getCurrentPinEventId());
    }

    @Test(expected = Exception.class)
    public void testSendPinEventOnExceptionOnPost() throws Exception {
        HttpMessage mockMessage = mock(HttpMessage.class);
        PinResponse mockResponse = new PinResponse("ok");
        String content = MockResponseUtils.objectToString(mockResponse);

        HttpClientTestUtils.mockSuccessfulResponse(message, mockMessage, content);

        doThrow(Exception.class).when(httpClient).PostAsync(anyString(), any(StringEntity.class));

        PinResponse result = pinService.sendPinEvent(PinEventId.WebApp_Load).get();

        // Verify calls
        Assert.assertEquals(startingPinCount, pinService.getPinRequestCount());
        verifyNoMoreInteractions(httpClient);
        verifyZeroInteractions(objectMapper);
        Assert.assertNull(result);
    }

    @Test
    public void testGetPinEventNucleusPersonaId() {
        long expected = 1234567890L;
        pinService.setPinEventNucleusPersonaId(expected);
        Assert.assertEquals(expected, pinService.getPinEventNucleusPersonaId());
    }

    @Test
    public void testGetPinEventPersonaId() {
        String expected = "test";
        pinService.setPinEventPersonaId(expected);
        Assert.assertEquals(expected, pinService.getPinEventPersonaId());
    }

    @Test
    public void testGetPinEventSessionId() {
        String expected = "sessionId";
        pinService.setPinEventSessionId(expected);
        Assert.assertEquals(expected, pinService.getPinEventSessionId());
    }

    @Test
    public void testGetPreviousPinEventId() {
        PinEventId expected = PinEventId.WebApp_Load;
        pinService.setPreviousPinEventId(expected);
        Assert.assertEquals(expected, pinService.getPreviousPinEventId());
    }

    @Test
    public void testGetCurrentPinEventId() {
        PinEventId expected = PinEventId.WebApp_Load;
        pinService.setCurrentPinEventId(expected);
        Assert.assertEquals(expected, pinService.getCurrentPinEventId());
    }

    @Test
    public void testGetPinRequestCount() {
        int expected = 1;
        pinService.setPinRequestCount(expected);
        Assert.assertEquals(expected, pinService.getPinRequestCount());
    }

    @Test
    public void testGetUuid() {
        String expected = "uuid";
        pinService.setUuid(expected);
        Assert.assertEquals(expected, pinService.getUuid());
    }

    @Test
    public void testGetDob() {
        String expected = "01/01/1970";
        pinService.setDob(expected);
        Assert.assertEquals(expected, pinService.getDob());
    }

    @Test
    public void testGetMsgId() {
        List<Long> expected = new ArrayList<>();
        expected.add(1L);
        pinService.setMsgId(expected);

        Assert.assertEquals(expected, pinService.getMsgId());
    }

    @Test
    public void testGetPlatform() {
        pinService.setPlatform(Platform.Xbox360);
        Assert.assertEquals(Platform.Xbox360, pinService.getPlatform());
    }
}
