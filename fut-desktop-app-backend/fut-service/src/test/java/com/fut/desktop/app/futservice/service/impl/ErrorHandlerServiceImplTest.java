package com.fut.desktop.app.futservice.service.impl;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.futservice.service.base.StatusMessagingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests {@link ErrorHandlerServiceImpl}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class ErrorHandlerServiceImplTest {

    /**
     * Handle to {@link StatusMessagingService}
     */
    @Mock
    private StatusMessagingService statusMessagingService;

    /**
     * The class under test
     */
    @InjectMocks
    private ErrorHandlerServiceImpl errorHandlerService;

    /**
     * A test endpoint
     */
    private String TEST_ENDPOINT = "/test";

    /**
     * Before each test
     */
    @Before
    public void setUp() {
        Mockito.reset(statusMessagingService);
    }

    /**
     * Test {@link ErrorHandlerServiceImpl#isMajorError(FutError, String)}
     * <p>
     * - Verify captchaTriggered returns true
     * - Verify status sent
     * - Verify actionNeeded message is sent.
     */
    @Test
    public void isMajorErrorCaptchaTriggeredTest() {
        testIsMajorError(FutErrorCode.CaptchaTriggered);
    }

    /**
     * Test {@link ErrorHandlerServiceImpl#isMajorError(FutError, String)}
     * <p>
     * - Verify captchaTriggered returns true
     * - Verify status sent
     * - Verify actionNeeded message is sent.
     */
    @Test
    public void isMajorErrorUpgradeRequiredTest() {
        testIsMajorError(FutErrorCode.UpgradeRequired);
    }

    /**
     * Test {@link ErrorHandlerServiceImpl#isMajorError(FutError, String)}
     * <p>
     * - Verify captchaTriggered returns true
     * - Verify status sent
     * - Verify actionNeeded message is sent.
     */
    @Test
    public void isMajorErrorTooManyRequestsTest() {
        testIsMajorError(FutErrorCode.TooManyRequests);
    }

    @Test
    public void isMajorErrorFalseTest() {
        // Execute the test
        boolean result = errorHandlerService.isMajorError(createFutError(FutErrorCode.InternalServerError), TEST_ENDPOINT);

        // Verify the results
        Assert.assertFalse("Major error should be false.", result);
        Mockito.verifyZeroInteractions(statusMessagingService);
    }

    // Helper methods

    /**
     * Execute the test with different error codes
     *
     * @param futErrorCode The fut error code
     */
    private void testIsMajorError(FutErrorCode futErrorCode) {
        // Execute the test
        boolean result = errorHandlerService.isMajorError(createFutError(futErrorCode), TEST_ENDPOINT);

        // Verify the results
        Assert.assertTrue("Major error should be true.", result);
        Mockito.verify(statusMessagingService).sendStatus(anyString(), eq(TEST_ENDPOINT));
        Mockito.verify(statusMessagingService).sendActionNeeded(futErrorCode.getFutErrorCode());
    }

    /**
     * Create a fut error.
     *
     * @param code Code to create with.
     * @return The created fut error.
     */
    private FutError createFutError(FutErrorCode code) {
        return new FutError("test", code, "test", "test", "test");
    }

}
