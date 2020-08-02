package com.fut.desktop.app.steps;

import com.fut.desktop.app.operation.TradePileOperation;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradePileStep {

    private final TradePileOperation tradePileOperation;

    public TradePileStep(TradePileOperation tradePileOperation) {
        this.tradePileOperation = tradePileOperation;
    }

    @Then("I can see the trade pile has $numberOfItems items")
    public void iCanSeeTradePileHasXItems(Integer numberOfItems) {
        tradePileOperation.verifyTradePileCount(numberOfItems);
    }

    @Then("I can $numberOfItems unlisted items")
    public void iCanSeeTradePileHasXUnlistedItems(Integer numberOfItems) {
        tradePileOperation.verifyTradePileUnlistedCount(numberOfItems);
    }

    @Then("I click the autolist unlisted button")
    public void iClickTheAutolistUnListedButton() {
        tradePileOperation.clickAutolistButton();
    }

    @Then("I wait for the listing to finish")
    public void iWaitForTheListingToFinish() {
        tradePileOperation.waitForListingToFinish();
    }

    @Then("I can see $numberOfItems items listed")
    public void iCanSeeItemsListed(Integer numberOfItems) {
        tradePileOperation.verifyNumberOfListedItems(numberOfItems);
    }

    @Then("I can see $numberOfItems items sold")
    public void iCanSeeItemsSold(Integer numberOfItems){
        tradePileOperation.verifyNumberOfSoldItems(numberOfItems);
    }

    @When("I click remove sold")
    public void clickRemoveSold(){
      tradePileOperation.clickRemoveSold();
    }

    @Then("I can see no sold items")
    public void iCanSeeNoSoldItems(){
        tradePileOperation.verifyNoSoldItems();
    }
}
