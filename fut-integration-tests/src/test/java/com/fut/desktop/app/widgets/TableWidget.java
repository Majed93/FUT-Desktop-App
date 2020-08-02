package com.fut.desktop.app.widgets;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.page.AbstractPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TableWidget extends AbstractPage {

    private WebDriverHelper webDriverHelper;

    // Table
    private String TABLE_ID;

    public TableWidget(WebDriverHelper webDriverHelper, String tableId) {
        this.webDriverHelper = webDriverHelper;
        this.TABLE_ID = tableId;
    }

    /**
     * Get all rows from table without headers
     *
     * @return Table rows without headers
     */
    public List<WebElement> getRows() {
        try {
            return webDriverHelper.getElementBy(byId(TABLE_ID)).findElement(byTag("tbody")).findElements(byTag("tr"));
        }catch (TimeoutException | NoSuchElementException ex) {
            // 0 Rows / Empty table
            return new ArrayList<>();
        }
    }
}
