package com.fut.desktop.app.operation;

import com.fut.desktop.app.futsimulator.constants.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.hamcrest.CoreMatchers.is;

/**
 * Handle simulator operations.
 */
@Slf4j
@Component
public class SimulatorOperations {

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Value("${fut.simulator.endpoint}")
    private String futSimEndpoint;

    public void trainToFailLogin(boolean isSuccess) {
        sendRequestToSim("/isLoginSuccess/" + isSuccess);
    }

    public void trainSecondaryAuthToken(boolean revoke) {
        sendRequestToSim("/setSecondaryAuthTokenReady/" + revoke);
    }

    /**
     * @param fail If true then set to fail
     */
    public void trainAutoListingToFail(Boolean fail) {
        sendRequestToSim(RestConstants.TRAIN + "/listing/trainToFail/" + fail);
    }

    /**
     * @param fail If true then set to fail
     */
    public void trainBiddingToFail(Boolean fail) {
        sendRequestToSim(RestConstants.TRAIN + "market/captcha/" + fail);
    }


    public void trainTradePileToHaveItems(Integer numberOfItems) {
        sendRequestToSim(RestConstants.TRAIN + RestConstants.PILE + "/" + RestConstants.TRADEPILE + "/" + numberOfItems);
    }

    /**
     * Train the trade pile to have a specific type of item.
     *
     * @param numberOfItems Number of items
     * @param type          Type - Usually from the RestConstants
     */
    public void trainTradePileToHaveTypeOfItems(Integer numberOfItems, String type, String listName) {
        sendRequestToSim(RestConstants.TRAIN + RestConstants.TRADEPILE + "/" + type + "/" + numberOfItems + "/" + listName);
    }

    /**
     * Train the simulator to have this amount of coins on the account
     *
     * @param coins coins to train with
     */
    public void trainAccountWithCoins(Long coins) {
        sendRequestToSim(RestConstants.TRAIN + RestConstants.COINS + "/" + coins);
    }

    private void sendRequestToSim(String endpoint) {
        ResponseEntity<Void> setSecondaryAuthToken = testRestTemplate.getForEntity(futSimEndpoint + endpoint, Void.class);
        Assert.assertThat(setSecondaryAuthToken.getStatusCode(), is(HttpStatus.OK));
    }
}
