package com.example;

import com.example.ScreenshotUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Encapsulates NSE site interactions, including:
 * 1. Navigating to homepage
 * 2. Handling auto-suggest search
 * 3. Fallback if "Temporarily Suspended"
 * 4. Retrieving current price, 52-week high, and 52-week low
 */
public class StockPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // === LOCATORS FOR NAVIGATION & AUTO-SUGGEST ===
    private By searchBox = By.id("header-search-input"); // e.g. #header-search-input
    private By autoSuggestListItems = By.xpath("//ul[@id='header-search-input_listbox']/li");
    private By searchResultsContainer = By.cssSelector(".symbol-container");
    // Update if your site uses different locators

    // === LOCATORS FOR STOCK DETAILS (From your SearchResultpage logic) ===
    // e.g., #quoteLtp for current price, or any new ID the site uses
    private By currentValue = By.cssSelector("#quoteLtp");

    // The row elements or placeholders for 52-week high & low
    private By highValueRow = By.xpath("//div[@class='col-md-3 card-spacing priceinfodiv']//tbody/tr[1]/td[1]");
    private By lowValueRow  = By.xpath("//div[@class='col-md-3 card-spacing priceinfodiv']//tbody/tr[2]/td[1]");
    private By week52HighVal = By.xpath("//span[@id='week52highVal']");
    private By week52LowVal  = By.xpath("//span[@id='week52lowVal']");

    // If the site uses a "Temporarily Suspended" .status or other locator
    private By suspendedStatusLocator = By.cssSelector(".status");

    // Wait 20 seconds for all key interactions
    public StockPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Navigates to NSE homepage.
     */
    public void goToNseHomePage() {
        try {
            driver.get("https://www.nseindia.com/");
            ScreenshotUtils.captureScreenshot(driver, "AfterNSEHomePage");
        } catch (TimeoutException te) {
            throw new RuntimeException("[ERROR] Timed out loading NSE homepage.", te);
        } catch (WebDriverException we) {
            throw new RuntimeException("[ERROR] WebDriver issue navigating to NSE homepage.", we);
        }
    }

    /**
     * Searches for a stock using auto-suggest:
     * 1. Type the symbol
     * 2. Wait for the <li> items
     * 3. Loop, find a match, click it
     * 4. Wait for results container
     */
    public void searchStock(String symbol) {
        try {
            // 1. Wait for search box
            WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
            ScreenshotUtils.captureScreenshot(driver, "BeforeTyping_" + symbol);

            box.clear();
            box.sendKeys(symbol);

            // 2. Wait for the <li> items
            List<WebElement> suggestions = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(autoSuggestListItems)
            );
            ScreenshotUtils.captureScreenshot(driver, "FoundSuggestionList_" + symbol);

            // 3. Loop each item, if text matches/contains the symbol, click it
            boolean clicked = false;
            for (WebElement item : suggestions) {
                String itemText = item.getText().trim().toLowerCase();
                if (itemText.contains(symbol.toLowerCase())) {
                    item.click();
                    ScreenshotUtils.captureScreenshot(driver, "ClickedSuggestion_" + symbol);
                    clicked = true;
                    break;
                }
            }

            if (!clicked) {
                throw new RuntimeException("[ERROR] No auto-suggest item matched the typed symbol: " + symbol);
            }

            // 4. Wait for results container
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsContainer));
            ScreenshotUtils.captureScreenshot(driver, "AfterResultsContainer_" + symbol);

        } catch (TimeoutException te) {
            throw new RuntimeException("[ERROR] Timed out waiting for search box, auto-suggest, or results container.", te);
        } catch (NoSuchElementException ne) {
            throw new RuntimeException("[ERROR] Auto-suggest elements not found on NSE page.", ne);
        } catch (WebDriverException we) {
            throw new RuntimeException("[ERROR] WebDriver issue during stock search (auto-suggest).", we);
        }
    }

    /**
     * If the primary stock is suspended, fallback to another.
     */
    public void searchStockWithFallback(String primarySymbol, String fallbackSymbol) {
        searchStock(primarySymbol);

        if (isStockSuspended()) {
            System.out.println("[INFO] " + primarySymbol + " is suspended. Falling back to " + fallbackSymbol);
            searchStock(fallbackSymbol);
        } else {
            System.out.println("[INFO] " + primarySymbol + " is active. Proceeding with validations.");
        }
    }

    /**
     * Checks if the stock is "Temporarily Suspended".
     */
    public boolean isStockSuspended() {
        try {
            WebElement statusEl = driver.findElement(suspendedStatusLocator);
            String statusText = statusEl.getText().trim().toLowerCase();
            return statusText.contains("temporarily suspended");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // === PRICE RETRIEVAL LOGIC (From SearchResultpage) ===

    /**
     * Retrieves the current price of the stock (#quoteLtp).
     */
    public String getCurrentPrice() {
        try {
            System.out.println("[INFO] Waiting for the current value (#quoteLtp) to be visible...");
            WebElement currentValueElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(currentValue)
            );
            String currentValueText = currentValueElement.getText();
            System.out.println("[INFO] Current stock value: " + currentValueText);
            return currentValueText;
        } catch (TimeoutException te) {
            throw new RuntimeException("[ERROR] Timed out waiting for current value (#quoteLtp).", te);
        } catch (WebDriverException we) {
            throw new RuntimeException("[ERROR] WebDriver issue fetching current value.", we);
        }
    }

    /**
     * Retrieves the 52-week high value from:
     * - row: //div[@class='col-md-3 card-spacing priceinfodiv']//tbody/tr[1]/td[1]
     * - then the span: //span[@id='week52highVal']
     */
    public String get52WeekHigh() {
        try {
            System.out.println("[INFO] Waiting for the 52-week high row to be visible...");
            WebElement highValueElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(highValueRow)
            );
            // Now find the actual <span> with id="week52highVal"
            WebElement highValSpan = highValueElement.findElement(week52HighVal);
            String fiftyTwoWeekHighValue = highValSpan.getText();
            System.out.println("[INFO] 52-week high value: " + fiftyTwoWeekHighValue);
            return fiftyTwoWeekHighValue;
        } catch (TimeoutException te) {
            throw new RuntimeException("[ERROR] Timed out waiting for 52-week high row or span.", te);
        } catch (NoSuchElementException ne) {
            throw new RuntimeException("[ERROR] 52-week high element not found.", ne);
        } catch (WebDriverException we) {
            throw new RuntimeException("[ERROR] WebDriver issue fetching 52-week high.", we);
        }
    }

    /**
     * Retrieves the 52-week low value from:
     * - row: //div[@class='col-md-3 card-spacing priceinfodiv']//tbody/tr[2]/td[1]
     * - then the span: //span[@id='week52lowVal']
     */
    public String get52WeekLow() {
        try {
            System.out.println("[INFO] Waiting for the 52-week low row to be visible...");
            WebElement lowValueElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(lowValueRow)
            );
            // Now find the actual <span> with id="week52lowVal"
            WebElement lowValSpan = lowValueElement.findElement(week52LowVal);
            String fiftyTwoWeekLowValue = lowValSpan.getText();
            System.out.println("[INFO] 52-week low value: " + fiftyTwoWeekLowValue);
            return fiftyTwoWeekLowValue;
        } catch (TimeoutException te) {
            throw new RuntimeException("[ERROR] Timed out waiting for 52-week low row or span.", te);
        } catch (NoSuchElementException ne) {
            throw new RuntimeException("[ERROR] 52-week low element not found.", ne);
        } catch (WebDriverException we) {
            throw new RuntimeException("[ERROR] WebDriver issue fetching 52-week low.", we);
        }
    }
}
