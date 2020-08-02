package com.fut.api.fut.requests;

import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class CreditsRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private CreditsRequest creditsRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // For some reason base platform on this is null
        creditsRequest.setBaseAppVersion(AppVersion.WebApp);
        creditsRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPerformRequestAsync() throws Exception {
        creditsRequest.PerformRequestAsync().get();
    }
}
