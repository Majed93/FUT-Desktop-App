package com.fut.desktop.app.operation;

import com.fut.desktop.app.page.TradePilePage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@Slf4j
@Component
public class TradePileOperation {

    private final TradePilePage tradePilePage;

    public TradePileOperation(TradePilePage tradePilePage) {
        this.tradePilePage = tradePilePage;
    }


    public void verifyTradePileCount(Integer numberOfItems) {
        WebElement tradePileCount = tradePilePage.getTradePileCount();

        Assert.assertEquals("Trade pile count doesn't add up", numberOfItems + " Items", tradePileCount.getText());

        if (numberOfItems > 0) {
            List<WebElement> playerCards = tradePilePage.getPlayerCards();
            Assert.assertThat("Player card counts don't add up", numberOfItems, is(playerCards.size()));
        }
    }

    public void clickAutolistButton() {
        tradePilePage.clickAutolistBtn();
    }

    public void waitForListingToFinish() {
        tradePilePage.waitForCloseToBeEnabled();
    }

    public void verifyNumberOfListedItems(Integer numberOfItems) {
        List<WebElement> listedItems = tradePilePage.getListedItems();

        Assert.assertThat("Listed items don't add up!", numberOfItems, is(listedItems.size()));
    }

    public void verifyNumberOfSoldItems(Integer numberOfItems) {
        List<WebElement> soldItems = tradePilePage.getSoldItems();
        WebElement soldItemsCount = tradePilePage.getSoldItemsCount();

        Assert.assertEquals("Sold items count doesn't add up", "Sold " + numberOfItems, soldItemsCount.getText());

        if (numberOfItems > 0) {
            Assert.assertThat("Sold items don't add up!", numberOfItems, is(soldItems.size()));
        }
    }

    public void clickRemoveSold() {
        tradePilePage.clickRemoveSold();
    }

    public void verifyNoSoldItems() {
        verifyNumberOfSoldItems(0);
    }

    public void verifyTradePileUnlistedCount(Integer numberOfItems) {
        List<WebElement> unlistedItems = tradePilePage.getUnlistedItems();

        Assert.assertThat("Listed items don't add up!", numberOfItems, is(unlistedItems.size()));
    }
}
