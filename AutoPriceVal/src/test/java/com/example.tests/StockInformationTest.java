package com.example.tests;

import com.example.StockPage;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Demonstrates searching for a primary stock (e.g. RCOM),
 * falling back to INFY if suspended, disclaimers, etc.
 */
public class StockInformationTest extends BaseTest {

    @Parameters({"primarySymbol", "fallbackSymbol"})
    @Test
    public void verifyStockInformation(@Optional("RCOM") String primarySymbol,
                                       @Optional("INFY") String fallbackSymbol) {
        try {
            // 1. Go to NSE
            StockPage stockPage = new StockPage(driver);
            stockPage.goToNseHomePage();

            // 2. Handle disclaimers/popups
            handleDisclaimerIfPresent();

            // 3. Search with fallback (auto-suggest approach in StockPage)
            stockPage.searchStockWithFallback(primarySymbol, fallbackSymbol);

            // 4. Fetch details
            String currentPrice = stockPage.getCurrentPrice();
            String highPrice = stockPage.get52WeekHigh();
            String lowPrice  = stockPage.get52WeekLow();

            // 5. Log them
            test.info("Current Price  : " + currentPrice);
            test.info("52-Week High   : " + highPrice);
            test.info("52-Week Low    : " + lowPrice);

            // 6. Basic validations
            Assert.assertFalse(currentPrice.isEmpty(), "[VALIDATION] Current price is not displayed!");
            Assert.assertFalse(highPrice.isEmpty(),    "[VALIDATION] 52-Week High is not displayed!");
            Assert.assertFalse(lowPrice.isEmpty(),     "[VALIDATION] 52-Week Low is not displayed!");

            test.pass("Stock info verified successfully for: " + primarySymbol
                    + " (fallback: " + fallbackSymbol + ")");

        } catch (Exception e) {
            test.fail("[ERROR] Exception in verifyStockInformation: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }
}
