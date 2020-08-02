package com.fut.desktop.app.operation;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.page.AccountPage;
import com.fut.desktop.app.restObjects.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Accounts operation.
 */
@Slf4j
@Component
public class AccountOperations {

    private final AccountPage accountPage;

    private final AccountService accountService;

    @Autowired
    public AccountOperations(AccountPage accountPage, AccountService accountService) {
        this.accountPage = accountPage;
        this.accountService = accountService;
    }

    public List<Account> getAccountsList() {
        List<Account> accountsList = accountPage.getAccountsList();

        accountsList.forEach(acc -> {
            Account foundAccount = accountService.findByEmailAndPlatform(acc.getEmail(), acc.getPlatform());
            Assert.assertNotNull(foundAccount);
            Assert.assertEquals(acc.getEmail(), foundAccount.getEmail());
            Assert.assertEquals(acc.getPlatform(), foundAccount.getPlatform());
        });

        return accountsList;
    }

    public String getNoAccountsMsg() {
        return accountPage.getNoAccountsMsg().getText();
    }

    public void clickAddAccount() {
        accountPage.clickAddAccount();
    }

    public WebElement getAddModal() {
        return accountPage.getAddAccountModal();
    }

    public WebElement getUpdateModal() {
        return accountPage.getUpdateAccountModal();
    }

    public void addAccount(ExamplesTable accountTable, String operation) {
        Map<String, String> accountDetails = accountTable.getRow(0);

        accountPage.enterAccountDetails(accountDetails.get("email"),
                accountDetails.get("password"),
                accountDetails.get("secretAnswer"),
                accountDetails.get("2FA"),
                accountDetails.get("platform"),
                operation);
    }

    public void clickAddAccountModal() {
        accountPage.clickAddAccountModal();
    }

    public void clickUpdateAccountModal() {
        accountPage.clickUpdateAccountModal();
    }

    public void findAccountInTable(String account, String platform) {
        Assert.assertTrue(foundAccount(account, platform));
    }

    public void addAccountErrorMsg() {
        WebElement errorMsg = accountPage.getAddAccountErrorMsg();
        Assert.assertNotNull(errorMsg);
        Assert.assertTrue(errorMsg.getText().contains("Error adding"));
        Assert.assertTrue(errorMsg.getText().contains("exists"));
    }

    public void updateAccountErrorMsg() {
        WebElement errorMsg = accountPage.getUpdateAccountErrorMsg();
        Assert.assertNotNull(errorMsg);
        Assert.assertTrue(errorMsg.getText().contains("Error updating"));
        Assert.assertTrue(errorMsg.getText().contains("exists"));
    }

    public void clearFields(String operation) {
        accountPage.clearFields(operation);
    }

    public void clickOnAccount(String email, Platform platform) {
        accountPage.clickOnAccount(email, platform);
    }

    public void clickDeleteAccount() {
        accountPage.clickDeleteAccount();
    }

    public void accountNotVisibleInTable(String email, String platform) {
        List<Account> accountsList = accountPage.getAccountsList();
        List<Account> collectedAccounts = accountsList.stream().
                filter(acc -> acc.getEmail().equals(email)). // Check email
                filter(acc -> acc.getPlatform().getPlatform().equals(platform)). // Check platform
                collect(Collectors.toList());
        Assert.assertTrue(collectedAccounts.isEmpty());
    }

    /**
     * Find given account in the table
     *
     * @param account  Account to check
     * @param platform Platform to check
     * @return True if found otherwise false
     */
    private boolean foundAccount(String account, String platform) {
        List<Account> accountsList = accountPage.getAccountsList();
        return accountsList.stream().
                filter(acc -> acc.getEmail().equals(account)). // Check email
                filter(acc -> acc.getPlatform().getPlatform().equals(platform)). // Check platform
                collect(Collectors.toList()).get(0) != null;
    }

    /**
     * Add default accounts
     */
    public void addDefaultAccounts() {
        for (int i = 0; i < 6; i++) {
            try {
                Account account = new Account();
                account.setEmail("testAccount" + i + "@test.com");
                account.setPassword(RandomStringUtils.random(30, true, true));
                account.setAnswer(RandomStringUtils.random(20, true, true));
                account.setSecretKey(RandomStringUtils.random(16, true, true).toUpperCase());
                account.setPlatform(Platform.XboxOne);
                accountService.add(account);
            } catch (FutErrorException ex) {
                log.error("Error adding account: {}", ex.getMessage());
            }
        }
        tryClickRefresh();
    }

    /**
     * Remove all accounts
     */
    public void removeAllAccounts() {
        accountService.findAll().forEach(a -> {
            try {
                accountService.delete(a.getId());
            } catch (FutErrorException e) {
                log.error("Error deleting: {}" + e.getMessage());
            }
        });
        tryClickRefresh();
    }

    /**
     * Refresh the account list.
     */
    public void refreshAccounts() {
        accountPage.refreshAccountList();
    }


    /**
     * Attempt to click the refresh button. It might not be visible but that's ok for this method.
     */
    private void tryClickRefresh() {
        try {
            refreshAccounts();
        } catch (WebDriverException ex) {
            log.error("Refresh button not visible here");
        }
    }
}
