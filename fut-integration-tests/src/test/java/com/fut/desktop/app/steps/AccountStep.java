package com.fut.desktop.app.steps;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.operation.AccountOperations;
import com.fut.desktop.app.restObjects.Account;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Account step.
 */
@Slf4j
@Component
public class AccountStep {

    @Autowired
    private final AccountOperations accountOperations;

    /**
     * Constructor
     *
     * @param accountOperations {@link AccountOperations}
     */
    public AccountStep(AccountOperations accountOperations) {
        this.accountOperations = accountOperations;
    }

    @When("I click the add account button")
    public void iClickTheAddAccountButton() {
        accountOperations.clickAddAccount();
    }

    @Then("the add account modal appears")
    public void theAddAccountModalAppears() {
        Assert.assertNotNull(accountOperations.getAddModal());
    }

    @Then("the update account modal appears")
    public void theUpdateAccountModalAppears() {
        Assert.assertNotNull(accountOperations.getUpdateModal());
    }

    @Then("I try $operation the following account: $accountTable")
    public void iTryAddTheFollowingAccount(String operation, ExamplesTable accountTable) {
        accountOperations.addAccount(accountTable, operation);
    }

    @Then("I click the add account button in the modal")
    public void iClickTheAddAccountButtonInTheModal() {
        accountOperations.clickAddAccountModal();
    }

    @Then("I click the update account button in the modal")
    public void iClickTheUpdateAccountButtonInTheModal() {
        accountOperations.clickUpdateAccountModal();

    }

    @Then("I can see $numberOfAccount {account|accounts} in the table")
    public void iCanSeeAccountsInTheTable(long numberOfAccounts) {
        if (numberOfAccounts == 0) {
            Assert.assertNotNull(accountOperations.getNoAccountsMsg());
        } else {
            List<Account> accountsList = this.accountOperations.getAccountsList();
            Assert.assertNotNull(accountsList);
            Assert.assertEquals(numberOfAccounts, accountsList.size());
        }
    }

    @Then("I can see account $email in the table with platform $platform")
    public void iCanSeeAccountInTheTable(String account, String platform) {
        accountOperations.findAccountInTable(account, platform);
    }

    @Then("I can see there was an error adding the account")
    public void iCanSeeThereWasAnErrorAddingTheAccount() {
        accountOperations.addAccountErrorMsg();
    }


    @Then("I can see there was an error updating the account")
    public void iCanSeeThereWasAnErrorUpdatingTheAccount() {
        accountOperations.updateAccountErrorMsg();
    }

    @Then("I ensure all the fields are cleared for the $operation modal")
    public void iEnsureAllTheFieldsAreClearedForTHeModal(String operation) {
        accountOperations.clearFields(operation);
    }

    @When("I click on account $email with platform $platform")
    public void iClickOnAccountWithPlatform(String email, String platform) {
        accountOperations.clickOnAccount(email, Platform.getPlatform(platform));
    }

    @Then("I click the delete button to delete the account")
    public void iClickTheDeleteButtonToDeleteTheAccount() {
        accountOperations.clickDeleteAccount();
    }

    @Then("I can no longer see account $email with platform $platform in the table")
    public void iCanNoLongerSeeAccountWithPlatformInHteTable(String email, String platform) {
        accountOperations.accountNotVisibleInTable(email, platform);
    }

    @Given("accounts exist")
    public void accountsExist() {
        accountOperations.addDefaultAccounts();

    }

    @Given("accounts are cleared")
    public void accountsAreCleared() {
        accountOperations.removeAllAccounts();
    }

    @When("account list is refreshed")
    public void accountListIsRefreshed() {
        accountOperations.refreshAccounts();
    }

}
