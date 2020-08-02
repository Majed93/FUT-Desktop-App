package com.fut.desktop.app.operation;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.page.MainPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Handle login FUT operations.
 */
@Slf4j
@Component
public class LoginFUTOperations {

    private final MainPage mainPage;

    private final AccountService accountService;

    @Autowired
    public LoginFUTOperations(MainPage mainPage,
                              AccountService accountService) {
        this.mainPage = mainPage;
        this.accountService = accountService;
    }

    /**
     * Select given account from drop down box.
     *
     * @param account Account to select
     */
    public void selectAccount(String account, String platform) {
        mainPage.selectAccount(account, platform);
        WebElement selectedAccount = mainPage.getSelectedAccount();
        Assert.assertNotNull("Selected account null!", selectedAccount);
        Assert.assertTrue("Not expected selected account..", selectedAccount.getText().contains(account));
        Assert.assertTrue("Not expected selected account..", selectedAccount.getText().contains(platform));
    }

    /**
     * Click login
     */
    public void login() {
        mainPage.clickLogin();
    }

    public void currentlyLoggedIn(String account) {
        WebElement currentlyLoggedIn = mainPage.getCurrentlyLoggedIn();
        Assert.assertNotNull("Currently logged in is not displayed", currentlyLoggedIn);
        Assert.assertTrue("Incorrect account logged in!", currentlyLoggedIn.getText().contains(account));
    }

    public void getCurrentCoins(String account, String platform) {
        WebElement currentCoins = mainPage.getCurrentCoins();
        Assert.assertNotNull("Current coins is null", currentCoins);
        Assert.assertEquals("Account coins don't match up",
                String.valueOf(accountService.findByEmailAndPlatform(account, Platform.getPlatform(platform)).getCoins()),
                currentCoins.getText().replaceAll(",", ""));
    }

    public void verifyTotalCoins() {
        WebElement currentCoins = mainPage.getCurrentCoins();
        WebElement totalCoins = mainPage.getTotalCoins();

        Long current = Long.valueOf(currentCoins.getText().replaceAll(",", ""));
        Long total = Long.valueOf(totalCoins.getText().replaceAll(",", ""));
        Assert.assertNotNull("Current coins is null", currentCoins);
        Assert.assertNotNull("Total coins is null", totalCoins);
        Assert.assertThat("Total coins is less than current coins", total, greaterThanOrEqualTo(current));

    }

}
