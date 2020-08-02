package com.fut.desktop.app.steps;

import com.fut.desktop.app.operation.LoginOperations;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Login step.
 */
@Slf4j
@Component
public class LoginStep {

    @Autowired
    private final LoginOperations loginOperations;

    public LoginStep(LoginOperations loginOperations) {
        this.loginOperations = loginOperations;
    }

    @Then("I can see the buttons")
    @When("I can see the buttons")
    public void iCanSeeTheButtons() {
        loginOperations.verifyButtonAndFieldPresence();
    }

    @Then("the fields are empty")
    public void theFieldsAreEmpty() {
        loginOperations.verifyFieldsEmpty();
    }

    @Then("the login button is disabled")
    public void theLoginButtonIsDisabled() {
        Assert.assertTrue(loginOperations.isLoginButtonDisabled());
    }

    @Then("the license server $authoriseStatus to authorise")
    public void theLicenseServerToAuthorise(String authoriseStatus) {
        loginOperations.trainLicenseServerToAuthorise(!authoriseStatus.equals("fails"));
    }

    @Then("I click login")
    public void iClickLogin() {
        loginOperations.login();
    }

    @Then("I see error $errorMsg when logging in")
    public void iSeeErrorLoggingIn(String errorMsg) {
        Assert.assertTrue(loginOperations.verifyErrors(errorMsg));
    }

    @Then("I enter the login details: $loginDetails")
    public void iEnterTheLoginDetails(ExamplesTable loginDetails) {
        String email = loginDetails.getRow(0).get("email");
        String key = loginDetails.getRow(0).get("key");
        loginOperations.enterLoginDetails(email, key);
    }
}
