package com.fut.desktop.app.driver;

import com.fut.desktop.app.extensions.DateTimeExtensions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
@Component
@Scope("singleton")
public class WebDriverHelper {

    @Autowired
    private Environment environment;

    private WebDriver driver;

    private Integer TIMEOUT = 30; // Seconds

    Wait<WebDriver> wait;

    @Value("${drive.letter:M}")
    private String driverLetter;

    public void createDriver(Boolean ciServer) {
        // If chromediver executable is not in your project directory,
        //  point to it with this system variable
        File driverFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("driver/chromedriver.exe")).getFile());

        try {
            System.setProperty("webdriver.chrome.driver", URLDecoder.decode(driverFile.getAbsolutePath(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            log.error("Unable to get driver path {}", ex.getMessage());
            log.error("Path of: " + driverFile.getAbsolutePath());
            ex.printStackTrace();
        }

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.setBinary(driverLetter + ":\\Jenkins\\workspace\\FUT Desktop App Frontend\\app-builds\\FUT Desktop App 1.0.0.exe");
        if (ciServer) {
            chromeOptions.addArguments("ci_server");
            chromeOptions.addArguments("--window-size=1920,1080");
        } else {
            chromeOptions.addArguments("--start-maximized"); // open Browser in maximized mode
        }
        chromeOptions.addArguments("int_test");
        chromeOptions.addArguments("int_test_port=" + environment.getProperty("local.server.port"));
        chromeOptions.addArguments("int_test_location=\"" + driverLetter + ":/FUT Desktop App\"");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("chromeOptions", chromeOptions);
        capabilities.setBrowserName("chrome");

        driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.MILLISECONDS);
        createWait();
    }

    private void createWait() {
        wait = new FluentWait<>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);
    }

    public void takeScreenshot() throws IOException {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcFile, new File("screenshot\\" + DateTimeExtensions.ToUnixTime() + ".jpg"));
    }

    /**
     * Clean up after all the tests have ran.
     * <p>
     * Close electron windows
     * Quit chromewebdriver
     * </p>
     */
    public void clean() {
        log.info("Trying to close now.");

        if (driver != null) {
            driver.close();
            driver.quit();
        }
        log.info("Driver closed.");
    }

    /**
     * Wait for given query to be invisible
     *
     * @param by selector to query
     * @return true if disappeared within timeout otherwise false.
     */
    public boolean waitForElementToDisappear(By by) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    /**
     * Wait for element to be visible
     *
     * @param by selector to query
     * @return true if visible otherwise false.
     */
    public boolean waitForElementToBeVisible(By by) {
        return getElementBy(by) != null;
    }

    /**
     * Wait for given element to be enabled.
     * elementToBeClickable checks if the element is enabled.
     *
     * @param by selector to query
     * @return Element once enabled
     */
    public WebElement waitForElementToBeEnabled(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * A list of web elements
     *
     * @param by By selector to query
     * @return List of web elements
     */
    public List<WebElement> getListOfElements(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    /**
     * Get web element
     *
     * @param by selector to query
     * @return found element
     */
    public WebElement getElementBy(By by) {
        int counter = 0;
        int counterLimit = 5;
        while (counter < counterLimit) {
            try {
                if (wait == null) {
                    createWait();
                }
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } catch (StaleElementReferenceException ex) {
                log.info("Stale element or timeoutException, trying again");
                counter++;
            }
        }
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
}
