package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Main page actions here
 */
@Slf4j
@Component
public class MainPage extends AbstractPage {

    /**
     * By selectors.
     */
    private final String ACCOUNT_LIST_ID = "accounts-select";
    private final By ACCOUNT_LIST = byId(ACCOUNT_LIST_ID);

    private final String LOGIN_BTN_ID = "login-btn";
    private final By LOGIN_BTN = byId(LOGIN_BTN_ID);

    private final String CURRENTLY_LOGGED_IN_ID = "current-logged-in";
    private final By CURRENTLY_LOGGED_IN = byId(CURRENTLY_LOGGED_IN_ID);

    private final String CURRENT_COINS_ID = "current-coin-balance";
    private final By CURRENT_COINS = byId(CURRENT_COINS_ID);

    private final String TOTAL_COINS_ID = "total-coin-balance";
    private final By TOTAL_COINS = byId(TOTAL_COINS_ID);
    /**
     * Web driver
     */
    private final WebDriverHelper webDriverHelper;

    @Autowired
    public MainPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    /**
     * Select given account from drop down
     *
     * @param account Account to select
     */
    public void selectAccount(String account, String platform) {
        selectDropDownByText(webDriverHelper.getElementBy(ACCOUNT_LIST), account + " - " + platform);
    }

    /**
     * Get the selected account
     */
    public WebElement getSelectedAccount() {
        Select selected = new Select(webDriverHelper.getElementBy(ACCOUNT_LIST));
        return selected.getFirstSelectedOption();
    }

    /**
     * Click login button
     */
    public void clickLogin() {
        webDriverHelper.getElementBy(LOGIN_BTN).click();
    }

    /**
     * Get the currently logged in text
     */
    public WebElement getCurrentlyLoggedIn() {
        return webDriverHelper.getElementBy(CURRENTLY_LOGGED_IN);
    }

    /**
     * Get current coins of account
     */
    public WebElement getCurrentCoins() {
        return webDriverHelper.getElementBy(CURRENT_COINS);
    }

    public WebElement getTotalCoins() {
        return webDriverHelper.getElementBy(TOTAL_COINS);
    }
}
