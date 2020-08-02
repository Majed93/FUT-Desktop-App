package com.fut.api.fut.requests;

import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class RemoveFromWatchlistRequestTest extends FutRequestBaseTest {

    @InjectMocks
    private RemoveFromWatchlistRequest removeFromWatchlistRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        removeFromWatchlistRequest.setBaseAppVersion(AppVersion.WebApp);
        removeFromWatchlistRequest.setBasePlatform(Platform.XboxOne);
        setupMocks();
    }

    @Test
    public void testPerformRequestAsync() throws Exception {
        performRequestAsyncDelete();

        Boolean result = removeFromWatchlistRequest.PerformRequestAsync().get();

        performRequestAsyncDeleteVerify();

        Assert.assertTrue(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncExceptionOnOption() throws Exception {
        performRequestAsyncExceptionOnOption();

        Boolean result = removeFromWatchlistRequest.PerformRequestAsync().get();

        performRequestAsyncExceptionOnOptionVerify();

        Assert.assertNull(result);
    }

    @Test(expected = Exception.class)
    public void testPerformRequestAsyncExceptionOnDelete() throws Exception {
        performRequestAsyncExceptionOnDelete();

        Boolean result = removeFromWatchlistRequest.PerformRequestAsync().get();

        performRequestAsyncExceptionOnDeleteVerify();

        Assert.assertNull(result);
    }

    @Test(expected = FutException.class)
    public void testPerformRequestAsyncNotSuccessCodeOnDelete() throws Exception {
        performRequestAsyncNotSuccessCodeOnDelete();

        Boolean result = removeFromWatchlistRequest.PerformRequestAsync().get();

        performRequestAsyncNotSuccessCodeOnDeleteVerify();

        Assert.assertNull(result);
    }
}
