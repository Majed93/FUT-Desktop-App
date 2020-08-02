package com.fut.desktop.app.services;

import com.fut.desktop.app.services.impl.PlayerServiceImpl;
import com.fut.desktop.app.utils.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayerServiceImplTest {

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    public void startUp() {

    }
}
