package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.widgets.TableWidget;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlayerListPage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    private final TableWidget playerTable;

    private final String PLAYER_LIST_TABLE_ID = "player-list-table";
    private final By PLAYER_LIST_TABLE = byId(PLAYER_LIST_TABLE_ID);

    public PlayerListPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
        this.playerTable = new TableWidget(webDriverHelper, PLAYER_LIST_TABLE_ID);
    }

    public WebElement getPlayerTable() {
        return webDriverHelper.getElementBy(PLAYER_LIST_TABLE);
    }

    public List<WebElement> getPlayerRows() {
        return playerTable.getRows();
    }
}
