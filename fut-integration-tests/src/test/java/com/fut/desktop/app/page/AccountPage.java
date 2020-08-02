package com.fut.desktop.app.page;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.restObjects.Account;
import com.fut.desktop.app.widgets.TableWidget;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Account page object.
 */
@Slf4j
@Component
public class AccountPage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    /**
     * BY selectors
     */
    // No accounts msg
    private final String NO_MSGS_ID = "no-accounts-msg";
    private final By NO_MSGS = byId(NO_MSGS_ID);

    // Add button
    private final String ADD_BTN_ID = "add-account-modal-btn";
    private final By ADD_BTN = byId(ADD_BTN_ID);

    // Add modal
    private final String ADD_MODAL_ID = "add-account-modal";
    private final By ADD_MODAL = byId(ADD_MODAL_ID);

    // View/Update modal
    private final String VIEW_UPDATE_MODAL_ID = "view-account-modal";
    private final By VIEW_UPDATE_MODAL = byId(VIEW_UPDATE_MODAL_ID);

    // Delete button
    private final String DELETE_BTN_ID = "delete-account-form-btn";
    private final By DELETE_BTN = byId(DELETE_BTN_ID);

    // Delete modal
    private final String DELETE_MODAL_ID = "delete-account-modal";
    private final By DELETE_MODAL = byId(DELETE_MODAL_ID);

    // Confirm delete button
    private final String CONFIRM_DELETE_BTN_ID = "confirm-delete-btn";
    private final By CONFIRM_DELETE_BTN = byId(CONFIRM_DELETE_BTN_ID);

    // Add account <b>modal</b> button
    private final String ADD_MODAL_BTN_ID = "add-account-form-btn";
    private final By ADD_MODAL_BTN = byId(ADD_MODAL_BTN_ID);

    // Update account <b>modal</b> button
    private final String UPDATE_MODAL_BTN_ID = "update-account-form-btn";
    private final By UPDATE_MODAL_BTN = byId(UPDATE_MODAL_BTN_ID);

    // Account table
    private final String ACCOUNT_TABLE_ID = "account-table";

    // Refresh button
    private final String REFRESH_BTN_ID = "refresh-accounts-btn";
    private final By REFRESH_BTN = byId(REFRESH_BTN_ID);

    // Add form error message
    private final String ADD_FORM_ERROR_MSG_ID = "add-form-error-msg";
    private final By ADD_FORM_ERROR_MSG = byId(ADD_FORM_ERROR_MSG_ID);

    // Update form error message
    private final String UPDATE_FORM_ERROR_MSG_ID = "update-form-error-msg";
    private final By UPDATE_FORM_ERROR_MSG = byId(UPDATE_FORM_ERROR_MSG_ID);
    // Table rows id
    private final String EMAIL_ROW_ID = "row-email-";
    private final String COINS_ROW_ID = "row-coins-";
    private final String TIME_FINISH_ROW_ID = "row-time-finish-";
    private final String TOTAL_SESSION_ROW_ID = "row-total-session-";
    private final String WATCH_LIST_COUNT_ROW_ID = "row-watch-list-count-";
    private final String TRADE_PILE_COUNT_ROW_ID = "row-trade-pile-count-";
    private final String PLATFORM_ROW_ID = "row-platform-";
    private final String KEY_ROW_ID = "row-key-";

    private TableWidget accountTable;

    // Common modal fields
    class AccountForm {
        // Email
        private final String EMAIL_INPUT_ID = "-account-email-input";

        // Password
        private final String PASSWORD_INPUT_ID = "-account-password-input";

        // Secret Answer
        private final String SECRECT_ANSWER_INPUT_ID = "-account-answer-input";

        // 2FA
        private final String TWOFA_INPUT_ID = "-account-key-input";

        // Platform
        private final String PLATFORM_INPUT_ID = "-account-platform-input";

        // add or update
        private String operation;

        AccountForm(String operation) {
            this.operation = operation;
        }

        /**
         * FOrmat the id with the operation prepended
         *
         * @param id Id to format
         * @return formatted Id
         */
        String formattedId(String id) {
            return operation + id;
        }
    }


    public AccountPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
        accountTable = new TableWidget(webDriverHelper, ACCOUNT_TABLE_ID);
    }

    /**
     * Get the list of accounts
     *
     * @return list of accounts displayed
     */
    public List<Account> getAccountsList() {
        List<WebElement> rows = accountTable.getRows();
        if (rows.isEmpty()) {
            return new ArrayList<>();
        }

        List<Account> accountList = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Account account = new Account();
            By ACCOUNT_ROW_ID = byId("account-row-" + i);

            WebElement row = webDriverHelper.getElementBy(ACCOUNT_ROW_ID);

            account.setEmail(row.findElement(byId(EMAIL_ROW_ID + i)).getText());
            account.setCoins(Long.parseLong(row.findElement(byId(COINS_ROW_ID + i)).getText().replace(",", "")));
            account.setTotalSession(Integer.parseInt(row.findElement(byId(TOTAL_SESSION_ROW_ID + i)).getText()));
            account.setWatchListCount(Integer.parseInt(row.findElement(byId(WATCH_LIST_COUNT_ROW_ID + i)).getText()));
            account.setTradePileCount(Integer.parseInt(row.findElement(byId(TRADE_PILE_COUNT_ROW_ID + i)).getText()));
            account.setPlatform(Platform.getPlatform(row.findElement(byId(PLATFORM_ROW_ID + i)).getText()));
            account.setSecretKey(row.findElement(byId(KEY_ROW_ID + i)).getText());

            accountList.add(account);
        }

        return accountList;
    }

    /**
     * Get the no account messages element
     *
     * @return No account message element
     */
    public WebElement getNoAccountsMsg() {
        return webDriverHelper.getElementBy(NO_MSGS);
    }

    /**
     * Click on the add account button above the table
     */
    public void clickAddAccount() {
        clickElement(webDriverHelper.getElementBy(ADD_BTN));
    }

    /**
     * Get the add account modal
     *
     * @return Add account modal
     */
    public WebElement getAddAccountModal() {
        return webDriverHelper.getElementBy(ADD_MODAL);
    }

    /**
     * Get the update modal
     *
     * @return Update account modal
     */
    public WebElement getUpdateAccountModal() {
        return webDriverHelper.getElementBy(VIEW_UPDATE_MODAL);
    }

    /**
     * Click the add button in the modal.
     */
    public void clickAddAccountModal() {
        clickElement(webDriverHelper.getElementBy(ADD_MODAL_BTN));
    }

    /**
     * Click the update button in the modal.
     */
    public void clickUpdateAccountModal() {
        clickElement(webDriverHelper.getElementBy(UPDATE_MODAL_BTN));
    }

    /**
     * Enter email
     *
     * @param email Email to enter
     */
    public void enterAccountDetails(String email, String password, String answer, String key, String platform, String operation) {
        AccountForm accountForm = new AccountForm(operation);

        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.EMAIL_INPUT_ID))), email);
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.PASSWORD_INPUT_ID))), password);
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.SECRECT_ANSWER_INPUT_ID))), answer);
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.TWOFA_INPUT_ID))), key);

        selectDropDownByText(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.PLATFORM_INPUT_ID))),
                platform);
    }

    /**
     * Get the error msg from the add account modal.
     *
     * @return Error message from add account modal
     */
    public WebElement getAddAccountErrorMsg() {
        return webDriverHelper.getElementBy(ADD_FORM_ERROR_MSG);
    }

    /**
     * Get the error msg from the update account modal.
     *
     * @return Error message from update account modal.
     */
    public WebElement getUpdateAccountErrorMsg() {
        return webDriverHelper.getElementBy(UPDATE_FORM_ERROR_MSG);
    }

    /**
     * Clear the input fields
     *
     * @param operation Add or update
     */
    public void clearFields(String operation) {
        AccountForm accountForm = new AccountForm(operation);

        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.EMAIL_INPUT_ID))), "");
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.PASSWORD_INPUT_ID))), "");
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.SECRECT_ANSWER_INPUT_ID))), "");
        setInputValue(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.TWOFA_INPUT_ID))), "");

        selectDropDownByIndex(webDriverHelper.getElementBy(byId(accountForm.formattedId(accountForm.PLATFORM_INPUT_ID))),
                0);
    }


    /**
     * Click on given account
     *
     * @param email    Email of account to click
     * @param platform Platform of account to click
     */
    public void clickOnAccount(String email, Platform platform) {
        List<WebElement> rows = accountTable.getRows();

        rows.stream().
                filter(r -> r.getText().contains(email) && r.getText().contains(platform.getPlatform()))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Cannot find row.")).click();
    }

    /**
     * Click on the delete account button in modal.
     */
    public void clickDeleteAccount() {
        webDriverHelper.getElementBy(DELETE_BTN).click();
        // Assert the delete modal popped up
        Assert.assertNotNull(webDriverHelper.getElementBy(DELETE_MODAL));

        // Confirm delete.
        webDriverHelper.getElementBy(CONFIRM_DELETE_BTN).click();
    }

    /**
     * Refresh account list
     */
    public void refreshAccountList() {
        webDriverHelper.getElementBy(REFRESH_BTN).click();
    }
}
