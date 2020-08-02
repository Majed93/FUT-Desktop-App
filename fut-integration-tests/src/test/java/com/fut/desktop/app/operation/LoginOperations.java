package com.fut.desktop.app.operation;

import com.fut.desktop.app.page.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

@Slf4j
@Component
public class LoginOperations {

    @Autowired
    private final LoginPage loginPage;

    @Autowired
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Value("${license.server.endpoint}")
    private String licenseServerEndpoint;

    public LoginOperations(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    /**
     * Verify fields and button exist
     */
    public void verifyButtonAndFieldPresence() {
        Assert.assertNotNull(loginPage.getEmailInput());
        Assert.assertNotNull(loginPage.getKeyInput());
        Assert.assertNotNull(loginPage.getLoginBtn());
    }

    /**
     * Verify the fields are empty.
     */
    public void verifyFieldsEmpty() {
        Assert.assertThat(loginPage.getEmailInputValue(), is(isEmptyString()));
        Assert.assertThat(loginPage.getKeyInputValue(), is(isEmptyString()));
    }

    /**
     * Check if login button is disabled.
     *
     * @return True if disabled otherwise false.
     */
    public boolean isLoginButtonDisabled() {
        return !loginPage.getLoginBtn().isEnabled();
    }

    /**
     * Train the simulated license server.
     *
     * @param authorise Value to train with.
     */
    public void trainLicenseServerToAuthorise(Boolean authorise) {
        String url = licenseServerEndpoint + "/" + authorise;
        String getAuth = licenseServerEndpoint + "/getAuth";
        HttpEntity<Void> httpEntity = restTemplate.getForEntity(url, Void.class);
        Assert.assertThat(((ResponseEntity<Void>) httpEntity).getStatusCode(), is(HttpStatus.OK));
        HttpEntity<Boolean> verifyNewAuth = restTemplate.getForEntity(getAuth, Boolean.class);
        Assert.assertEquals(authorise, verifyNewAuth.getBody());
    }

    /**
     * Click login
     */
    public void login() {
        loginPage.clickLogin();
    }

    /**
     * Verify errors exist
     *
     * @return true if errors exist, otherwise false.
     */
    public boolean verifyErrors(String expectedError) {
        WebElement errorMsgs = loginPage.getErrorMsgs();
        Assert.assertNotNull(errorMsgs);
        Assert.assertEquals(expectedError, errorMsgs.getText());
        return !errorMsgs.getText().isEmpty();
    }

    /**
     * Enter the login details
     *
     * @param email Email to use
     * @param key   Key to use
     */
    public void enterLoginDetails(String email, String key) {
        loginPage.enterLoginDetails(email, key);
    }
}
