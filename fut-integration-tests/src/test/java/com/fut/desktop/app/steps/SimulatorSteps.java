package com.fut.desktop.app.steps;

import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.operation.SimulatorOperations;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

/**
 * Step used to train the simulator.
 */
@Slf4j
@Component
public class SimulatorSteps {

    private final SimulatorOperations simulatorOperations;

    public SimulatorSteps(SimulatorOperations simulatorOperations) {
        this.simulatorOperations = simulatorOperations;
    }

    @When("the simulator is trained to $isSuccess login")
    public void trainToFailLogin(String isSuccess) {
        simulatorOperations.trainToFailLogin(!isSuccess.equals("fail"));
    }

    @When("the simulator is trained so the secondary auth token is $created")
    public void trainSecondaryAuthToken(String created) {
        boolean revoke = true;
        if (created.equals("created")) {
            revoke = false;
        }

        simulatorOperations.trainSecondaryAuthToken(revoke);
    }

    @When("the simulator is trained to set captcha verified to $failed for auto listing")
    public void trainAutoListingCaptchaError(Boolean failed) {
        simulatorOperations.trainAutoListingToFail(failed);
    }

    @When("the simulator is trained to set captcha verified to $failed for bidding")
    public void trainBiddingCaptchaError(Boolean failed) {
        simulatorOperations.trainBiddingToFail(failed);
    }

    // ******************* ACCOUNT *********************** //


    @When("the simulator is trained to have $coins coins")
    public void trainSumForCoins(Long coins) {
        simulatorOperations.trainAccountWithCoins(coins);
    }

    // ******************* TRADE PILE *********************** //

    @Given("the trade pile is trained to have $numberOfItems items")
    public void theTradePileHasXItems(Integer numberOfItems) {
        simulatorOperations.trainTradePileToHaveItems(numberOfItems);
    }

    @When("the trade pile is trained to have $numberOfItems unlisted items from list $listName")
    public void theTradePileHasXUnlistedItems(Integer numberOfItems, String listName) {
        simulatorOperations.trainTradePileToHaveTypeOfItems(numberOfItems, RestConstants.UNLISTED, listName);
    }

    @When("the trade pile is trained to have $numberOfItems sold items")
    public void theTradePileIsTrainedToHaveNumberSoldItems(Integer numberOfItems) {
        simulatorOperations.trainTradePileToHaveTypeOfItems(numberOfItems, RestConstants.SOLD, null);
    }

}
