package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonPage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    /**
     * BY selectors
     */
    // Loader
    private final String LOADER_ID = "loader-spinner";
    private final By LOADER = byId(LOADER_ID);

    // Header
    private final String HEADER_ID = "section-header";
    private final By HEADER = byId(HEADER_ID);

    // Alert prefix
    private final String ALERT_PREFIX = "alert-";

    // Close modal
    private final String CLOSE_XPATH = "//div[contains(@class, 'modal') and contains(@class, 'show')]//button[contains(@class, 'close')]";
    private final By CLOSE = byXpath(CLOSE_XPATH);

    public CommonPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }


    /**
     * Check if the page has loaded by ensuring the loader element is no longer visible
     *
     * @return True if loaded otherwise false.
     */
    public boolean pageHasLoaded() {
        return webDriverHelper.waitForElementToDisappear(LOADER);
    }

    /**
     * Get the page header
     *
     * @return page header
     */
    public String getPageHeader() {
        return webDriverHelper.getElementBy(HEADER).getText();
    }

    /**
     * Get the alert by class name
     *
     * @param alertType    Alert type
     * @param alertElement Alert element to query
     * @return the alert - it should be the same
     */
    public WebElement getAlertClass(String alertType, WebElement alertElement) {
        return alertElement.findElement(byCSS(ALERT_PREFIX + alertType));
    }

    /**
     * Try close the modal if it's open
     */
    public void closeModal() {
        try {
            webDriverHelper.getElementBy(CLOSE).click();
            // Wait a second for the modal to close
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Error waiting..");
            }
        } catch (TimeoutException | NoSuchElementException ex) {
            log.info("Modal closed");
        }
    }
}
