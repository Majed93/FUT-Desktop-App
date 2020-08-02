package com.fut.desktop.app.steps;

import com.fut.desktop.app.operation.LoginFUTOperations;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Step used for FUT login
 */
@Slf4j
@Component
public class LoginFUTStep {

    private final LoginFUTOperations loginFUTOperations;

    @Autowired
    public LoginFUTStep(LoginFUTOperations loginFUTOperations) {
        this.loginFUTOperations = loginFUTOperations;
    }

    @Then("I select account $account and platform $platform to login with")
    public void iSelectAccountToLoginWith(String account, String platform) {
        loginFUTOperations.selectAccount(account, platform);
    }

    @Then("I click login to login to FUT")
    public void iClickLoginToLoginToFUT() {
        loginFUTOperations.login();
    }

    @Then("I see currently logged in account is $account")
    public void currentlyLoggedIn(String account) {
        loginFUTOperations.currentlyLoggedIn(account);
    }

    @Then("I see the coins balance for the current account $account - $platform is as expected")
    public void currentCoins(String account, String platform) {
        loginFUTOperations.getCurrentCoins(account, platform);
    }

    @Then("I see the total coins")
    public void totalCoins() {
        loginFUTOperations.verifyTotalCoins();
    }

}
