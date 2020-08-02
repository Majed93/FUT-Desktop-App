package com.fut.desktop.app.page;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@Slf4j
public abstract class AbstractPage {

    /**
     * Return selector by id.
     *
     * @param id Id to use
     * @return By selector.
     */
    protected By byId(String id) {
        return By.id(id);
    }

    /**
     * Return selector by tag.
     *
     * @param tag Tag to use.
     * @return By selector.
     */
    protected By byTag(String tag) {
        return By.tagName(tag);
    }

    /**
     * Return selector by css.
     *
     * @param css CSS to query
     * @return By selector.
     */
    By byCSS(String css) {
        return By.className(css);
    }

    /**
     * Return selector by xpayj
     *
     * @param xpath xpath to query
     * @return By xpath.
     */
    By byXpath(String xpath) {
        return By.xpath(xpath);
    }

    /**
     * Return string value of input field
     *
     * @param element Element to query
     * @return value string
     */
    String getInputValue(WebElement element) {
        return element.getAttribute("value");
    }

    void clickElement(WebElement element) {
        element.click();
    }

    /**
     * Set input value of input element.
     *
     * @param inputElement Element to set value.
     * @param input        input to set.
     */
    void setInputValue(WebElement inputElement, String input) {
        inputElement.clear();
        inputElement.sendKeys(input);
    }

    /**
     * Select a drop down by it's text.
     *
     * @param selectElement Select element to select
     * @param selectBy      text to select by.
     */
    void selectDropDownByText(WebElement selectElement, String selectBy) {
        Select select = new Select(selectElement);
        select.selectByVisibleText(selectBy);
    }

    /**
     * Select a dorp down by it's index.
     *
     * @param selectElement Select element to select
     * @param index         index to select by
     */
    void selectDropDownByIndex(WebElement selectElement, Integer index) {
        Select select = new Select(selectElement);
        select.selectByIndex(index);
    }
}
