package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.widgets.PlayerCardWidget;
import com.fut.desktop.app.widgets.StatusModelWidget;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TradePilePage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    private final PlayerCardWidget playerCardWidget;

    private final StatusModelWidget statusModelWidget;

    /**
     * By selectors
     */
    private final String TRADE_PILE_COUNT_ID = "trade-pile-count";
    private final By TRADE_PILE_COUNT = byId(TRADE_PILE_COUNT_ID);

    private final String SOLD_ITEMS_COUNT_ID = TRADE_PILE_COUNT_ID + "-sold";
    private final By SOLD_ITEMS_COUNT = byId(SOLD_ITEMS_COUNT_ID);

    private final String AUTOLIST_BTN_ID = "autolist-unlisted-btn";
    private final By AUTOLIST_BTN = byId(AUTOLIST_BTN_ID);

    private final String REMOVE_SOLD_ID = "remove-sold-btn";
    private final By REMOVE_SOLD = byId(REMOVE_SOLD_ID);

    public TradePilePage(WebDriverHelper webDriverHelper, PlayerCardWidget playerCardWidget,
                         StatusModelWidget statusModelWidget) {
        this.webDriverHelper = webDriverHelper;
        this.playerCardWidget = playerCardWidget;
        this.statusModelWidget = statusModelWidget;
    }

    /**
     * Get the trade pile count
     */
    public WebElement getTradePileCount() {
        return webDriverHelper.getElementBy(TRADE_PILE_COUNT);
    }

    /**
     * Get number of sold items
     */
    public WebElement getSoldItemsCount() {
        return webDriverHelper.getElementBy(SOLD_ITEMS_COUNT);
    }

    public void clickAutolistBtn() {
        autolistBtn().click();
    }

    private WebElement autolistBtn() {
        return webDriverHelper.getElementBy(AUTOLIST_BTN);
    }

    public List<WebElement> getPlayerCards() {
        return playerCardWidget.getItems();
    }

    public List<WebElement> getListedItems() {
        return playerCardWidget.getTypeOfItems(PlayerCardWidget.Status.ACTIVE);
    }

    public List<WebElement> getUnlistedItems() {
        return playerCardWidget.getTypeOfItems(PlayerCardWidget.Status.UNLISTED);
    }

    public void waitForCloseToBeEnabled() {
        statusModelWidget.waitForCloseToBeEnabled();
    }

    public List<WebElement> getSoldItems() {
        return playerCardWidget.getTypeOfItems(PlayerCardWidget.Status.SOLD);
    }

    public void clickRemoveSold() {
        webDriverHelper.getElementBy(REMOVE_SOLD).click();
    }
}
