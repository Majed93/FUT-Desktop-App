package com.fut.desktop.app.widgets;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.page.AbstractPage;
import com.fut.desktop.app.page.NavigationPage;
import com.fut.desktop.app.utils.SectionEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PlayerCardWidget extends AbstractPage {
    @Autowired
    private final WebDriverHelper webDriverHelper;

    /**
     * By selectors
     */
    private final String STATUS_TEXT_ID = "[id$=-status]";

    public enum Status {
        ACTIVE("Active"),
        EXPIRED("Expired"),
        OUTBIDDED("Outbidded"),
        SOLD("Sold"),
        UNLISTED("Unlisted"),
        WINNING("Winning"),
        WON("Won");

        private String status;

        Status(String status) {
            this.status = status;
        }
    }

    public PlayerCardWidget(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    /**
     * Get all player cards
     *
     * @return all player card items on the page
     */
    public List<WebElement> getItems() {
        return webDriverHelper.getListOfElements(byTag(NavigationPage.APP_PREFIX + SectionEnum.PlayerCard.getSection()));
    }

    public List<WebElement> getTypeOfItems(Status status) {
        List<WebElement> items = getItems();
        List<WebElement> listedItems = new ArrayList<>();
        for (WebElement item : items) {
            WebElement element = item.findElement(By.cssSelector(STATUS_TEXT_ID));
            if (element.getText().contains(status.status)) {
                listedItems.add(item);
            }
        }

        return listedItems;
    }
}
