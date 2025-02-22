package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.example.DriverFactory;
import com.example.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Base class for all tests. Manages WebDriver, ExtentReports, screenshots on failure, etc.
 * Also includes a placeholder for disclaimers/cookie pop-ups.
 */
public class BaseTest {

    protected WebDriver driver;
    protected ExtentReports extent;
    protected ExtentTest test;

    @Parameters("browser")
    @BeforeTest
    public void setUp(String browser) {
        // 1. Initialize ExtentReports
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // 2. Create driver using DriverFactory
        try {
            driver = DriverFactory.getDriver(browser);
            driver.manage().window().maximize();
            System.out.println("[INFO] WebDriver initialized for browser: " + browser);
        } catch (RuntimeException re) {
            re.printStackTrace();
            System.err.println("[ERROR] Could not initialize WebDriver. Aborting tests.");
            throw re; // rethrow to fail the test suite
        }
    }

    @BeforeMethod
    public void beforeMethodSetup(Method method) {
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String path = ScreenshotUtils.captureScreenshot(driver, result.getName());
            if (path != null) {
                test.addScreenCaptureFromPath(path);
            }
            test.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test passed successfully.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test skipped: " + result.getThrowable());
        }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("[INFO] WebDriver quit successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("[ERROR] Failed to quit WebDriver: " + e.getMessage());
            }
        }
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Example method to handle disclaimers/popups if needed.
     * Replace with real locators for cookie disclaimers, etc.
     * We also do a debug screenshot if found or not found.
     */
    protected void handleDisclaimerIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Suppose the site has a cookie or disclaimer button with id="acceptCookie"
            WebElement cookieBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(org.openqa.selenium.By.id("acceptCookie"))
            );
            cookieBtn.click();
            test.info("Cookie/disclaimer pop-up accepted.");
            // Debug screenshot
            ScreenshotUtils.captureScreenshot(driver, "AfterDisclaimerAccepted");
        } catch (org.openqa.selenium.TimeoutException e) {
            test.info("No cookie/disclaimer pop-up found.");
            ScreenshotUtils.captureScreenshot(driver, "NoDisclaimer");
        } catch (Exception ex) {
            test.info("Exception while handling disclaimer: " + ex.getMessage());
        }
    }
}
