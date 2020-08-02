package com.fut.desktop.app.widgets;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.page.AbstractPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatusModelWidget extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    /**
     * By selectors
     */
    private final String CLOSE_BTN_ID = "status-model-cancel-close";
    private final By CLOSE_BTN = byId(CLOSE_BTN_ID);

    public StatusModelWidget(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    /**
     * Wait for the close button to be enabled
     */
    public void waitForCloseToBeEnabled() {
        webDriverHelper.waitForElementToBeEnabled(CLOSE_BTN).click();
    }
}
