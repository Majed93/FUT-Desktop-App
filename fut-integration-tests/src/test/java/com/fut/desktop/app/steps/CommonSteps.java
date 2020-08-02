package com.fut.desktop.app.steps;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.page.CommonPage;
import com.fut.desktop.app.page.NavigationPage;
import com.fut.desktop.app.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CommonSteps {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    @Value("${ci.server}")
    private Boolean ciServer;

    @Autowired
    private Environment environment;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final LoginStep loginStep;

    private final NavigationPage navigationPage;

    @Autowired
    private final CommonPage commonPage;

    public CommonSteps(WebDriverHelper webDriverHelper, LoginStep loginStep,
                       NavigationPage navigationPage, CommonPage commonPage) {
        this.webDriverHelper = webDriverHelper;
        this.loginStep = loginStep;
        this.navigationPage = navigationPage;
        this.commonPage = commonPage;
    }

    @Given("the app is loaded")
    public void givenTheAppIsLoaded() {
        webDriverHelper.createDriver(ciServer);
        ResponseEntity<String> restResp = restTemplate.getForEntity(UrlUtils.getBaseUrl(environment), String.class);
        Assert.assertNotNull(restResp.getBody());
    }

    @Given("the loader has disappeared")
    @Then("the loader has disappeared")
    @When("the loader has disappeared")
    public void theLoaderHasDisappeared() {
        Assert.assertTrue(commonPage.pageHasLoaded());
    }

    @Given("the app is closed")
    @Then("the app is closed")
    @When("the app is closed")
    public void theAppIsClosed() {
        webDriverHelper.clean();
    }

    @Then("I can see the $heading heading")
    public void iCanSeeTheHeading(String heading) {
        Assert.assertEquals(commonPage.getPageHeader(), heading);
    }

    @Then("the user waits $timeout seconds")
    @When("the user waits $timeout seconds")
    @Given("the user waits $timeout seconds")
    public void theUserWaitsXSeconds(Integer timeout) {
        try {
            log.debug("Sleeping for {}", timeout);
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            log.error("Unable to sleep!");
            e.getStackTrace();
        }
    }

    @Then("verify the app has fully loaded")
    @When("verify the app has fully loaded")
    public void verifyTheAppHasFullyLoaded() {
        String sectionName = "accounts";
        if (isLoginPage()) {
            // Not logged in so need to log in.
            loginStep.theLicenseServerToAuthorise("true");
            ExamplesTable loginDetails = new ExamplesTable("|email|key\r\n|test@test.com|ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
            loginStep.iEnterTheLoginDetails(loginDetails);
            loginStep.iClickLogin();
            iSeeTheAlertWithMessage("success", "Successfully logged in.");
        }
        Assert.assertTrue(navigationPage.navigateToSection(sectionName).getTagName().contains(sectionName));
        iCanSeeTheHeading("Account List");
    }

    @Then("I see the $alertType alert with message $message")
    public void iSeeTheAlertWithMessage(String alertType, String message) {
        WebElement alertElement = navigationPage.navigateToSection("alert");
        Assert.assertNotNull(alertElement);
        Assert.assertNotNull(commonPage.getAlertClass(alertType, alertElement));
        Assert.assertTrue(alertElement.getText().contains(message));
    }

    @Then("I ensure the modal is closed")
    public void iEnsureTheModalIsClosed() {
        commonPage.closeModal();
    }

    /**
     * Check if login page
     *
     * @return True if login page otherwise false.
     */
    private boolean isLoginPage() {
        try {
            navigationPage.navigateToSection("login");
            log.info("On login page");
            return true;
        } catch (TimeoutException | NoSuchElementException nseEx) {
            log.info("Not on login page");
            return false;
        }
    }

    @When("I close the alert")
    public void closeAlert() {
        navigationPage.closeAlert();
    }

    @Then("I verify i can see the header $header")
    public void verifyHeader(String header) {
        String getHeader = commonPage.getPageHeader();
    }

    @Then("I can see the action needed modal with $msg message for account $account")
    public void iCanSeeActionNeededModal(String msg, String account) {
        navigationPage.verifyActionNeededModal(msg, account);
    }

    @Then("I dismiss the action needed modal")
    public void iDismissTheActionNeededModal(){
        navigationPage.dismissActionNeededModal();
    }
}
