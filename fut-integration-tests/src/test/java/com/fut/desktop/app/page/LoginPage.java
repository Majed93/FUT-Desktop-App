package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginPage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    // Email field
    private final String EMAIL_INPUT_ID = "email-input";
    private final By EMAIL_INPUT = byId(EMAIL_INPUT_ID);

    // Key field
    private final String KEY_INPUT_ID = "key-input";
    private final By KEY_INPUT = byId(KEY_INPUT_ID);

    // Login button
    private final String LOGIN_BUTTON_ID = "main-login-btn";
    private final By LOGIN_BUTTON = byId(LOGIN_BUTTON_ID);

    // Error messages
    private final String ERROR_MSG_ID = "login-errors";
    private final By ERROR_MSG = byId(ERROR_MSG_ID);

    public LoginPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    /**
     * Get email input
     *
     * @return Email input element
     */
    public WebElement getEmailInput() {
        return webDriverHelper.getElementBy(EMAIL_INPUT);
    }

    /**
     * Get key input
     *
     * @return Key input element
     */
    public WebElement getKeyInput() {
        return webDriverHelper.getElementBy(KEY_INPUT);
    }

    /**
     * Get the login button
     *
     * @return Login button element
     */
    public WebElement getLoginBtn() {
        return webDriverHelper.getElementBy(LOGIN_BUTTON);
    }

    /**
     * Get error messages
     *
     * @return Error messages element
     */
    public WebElement getErrorMsgs() {
        return webDriverHelper.getElementBy(ERROR_MSG);
    }

    /**
     * Get email input value
     *
     * @return emai input value
     */
    public String getEmailInputValue() {
        return getInputValue(getEmailInput());
    }

    /**
     * Get input value of key
     *
     * @return the value of the key input field
     */
    public String getKeyInputValue() {
        return getInputValue(getKeyInput());
    }

    /**
     * Click the login button
     */
    public void clickLogin() {
        clickElement(webDriverHelper.getElementBy(LOGIN_BUTTON));
    }

    /**
     * Enter the login details
     *
     * @param email Email to enter
     * @param key   Key to enter
     */
    public void enterLoginDetails(String email, String key) {
        setInputValue(getEmailInput(), email);
        setInputValue(getKeyInput(), key);
    }
}
