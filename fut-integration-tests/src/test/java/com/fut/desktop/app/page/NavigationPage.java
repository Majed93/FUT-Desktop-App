package com.fut.desktop.app.page;

import com.fut.desktop.app.driver.WebDriverHelper;
import com.fut.desktop.app.futservice.service.impl.ErrorHandlerServiceImpl;
import com.fut.desktop.app.utils.SectionEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class NavigationPage extends AbstractPage {

    @Autowired
    private final WebDriverHelper webDriverHelper;

    /**
     * The app prefix
     */
    public final static String APP_PREFIX = "app-";
    private final String SIDEBAR_PREFIX = "sidebar-btn-";
    private final String CLICKABLE_ID = "-clickable";
    private final String TOGGLE_SIDEBAR_ID = "toggle-sidebar-btn";
    private final By TOGGLE_SIDEBAR = byId(TOGGLE_SIDEBAR_ID);

    /**
     * Action needed modal
     */
    private final String ACTION_NEEDED_MODAL_ID = "actionNeededModal";
    private final String ACTION_NEEDED_MODAL_TITLE_ID = "actionNeededModalLabel";
    private final String ACTION_NEEDED_MODAL_BODY_ID = "action-needed-modal-body";
    private final String ACTION_NEEDED_MODAL_CLOSE_ID = "action-needed-modal-close";

    private final By ACTION_NEEDED_MODAL = byId(ACTION_NEEDED_MODAL_ID);
    private final By ACTION_NEEDED_MODAL_TITLE = byId(ACTION_NEEDED_MODAL_TITLE_ID);
    private final By ACTION_NEEDED_MODAL_BODY = byId(ACTION_NEEDED_MODAL_BODY_ID);
    private final By ACTION_NEEDED_MODAL_CLOSE = byId(ACTION_NEEDED_MODAL_CLOSE_ID);

    public NavigationPage(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    public WebElement navigateToSection(String section) {
        return webDriverHelper.getElementBy(byTag(APP_PREFIX + section));
    }

    public void clickSidebarButton(String sectionName) {
        WebElement sideBarElement = webDriverHelper.getElementBy(byId(SIDEBAR_PREFIX + sectionName));

        if (!sectionName.contains("account")) {
            sideBarElement = webDriverHelper.getElementBy(byId(SIDEBAR_PREFIX + sectionName + CLICKABLE_ID));
            //Because this might be off the screen, need to actually scroll up to it.
        }
        Actions actions = new Actions(webDriverHelper.getDriver());
        actions.moveToElement(sideBarElement).click().build().perform();
    }

    public void closeAlert() {
        webDriverHelper.getElementBy(byTag(APP_PREFIX + SectionEnum.Alert.getSection())).findElement(byTag("button")).click();
    }

    public void takeScreenshot() {
        try {
            webDriverHelper.takeScreenshot();
        } catch (IOException e) {
            log.error("Unable to take screenshot! {}", e.getMessage());
        }
    }


    public WebElement verifySection(String sectionName) {
        return webDriverHelper.getElementBy(byTag(APP_PREFIX + sectionName));
    }

    public void verifyActionNeededModal(String msg, String account) {
        WebElement actionNeededModal = this.webDriverHelper.getElementBy(ACTION_NEEDED_MODAL);

        String attribute = actionNeededModal.getAttribute("aria-modal");

        Assert.assertEquals("Aria modal should be true", true, Boolean.valueOf(attribute));

        // Get the title
        WebElement modalTitle = this.webDriverHelper.getElementBy(ACTION_NEEDED_MODAL_TITLE);
        Assert.assertTrue("Modal title does not contain the account", modalTitle.getText().contains(account));

        // Get the
        String expectedMsg = "Looks like something went wrong with your FUT account, please check the web app to ensure everything is ok.";

        switch (msg) {
            case "Captcha Verify":
                expectedMsg = ErrorHandlerServiceImpl.CAPTCHA_NEEDED_MSG;
                break;
            case "Soft Ban":
                expectedMsg = ErrorHandlerServiceImpl.SOFT_BAN_MSG;
                break;
            case "Token Expired":
                expectedMsg = ErrorHandlerServiceImpl.TOKEN_EXPIRED_MSG;
                break;
            default:
                break;
        }

        WebElement modalBody = this.webDriverHelper.getElementBy(ACTION_NEEDED_MODAL_BODY);
        Assert.assertTrue("Modal title does not contain the message", modalBody.getText().contains(expectedMsg));
    }

    public void dismissActionNeededModal() {
        webDriverHelper.getElementBy(ACTION_NEEDED_MODAL_CLOSE).click();
    }
}
