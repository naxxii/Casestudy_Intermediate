package com.saucedemo.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.saucedemo.utils.ExtentReportManager;
import com.saucedemo.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class BaseTest {

    protected WebDriver driver;
    protected static String baseUrl;
    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite
    @Parameters({"baseUrl"})
    public void beforeSuite(String baseUrlFromSuite) {
        // Capture suite-level URL from testng.xml
        baseUrl = baseUrlFromSuite;

        extent = ExtentReportManager.createInstance("reports/SauceDemoReport.html");
        System.out.println("\u001B[34m[INFO]\u001B[0m Suite started. Base URL set to: " + baseUrl);
    }

    @BeforeTest
    public void setUpDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setBinary("C:\\Chrome_Binary\\chrome.exe");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        System.out.println("\u001B[34m[INFO]\u001B[0m WebDriver initialized with Chromium binary and matching driver.");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
            test.fail("Test Failed. Screenshot saved at: " + screenshotPath);
            test.addScreenCaptureFromPath(screenshotPath);
            System.out.println("\u001B[31m[FAILED]\u001B[0m Test failed! Screenshot saved at: " + screenshotPath);
        } else {
            System.out.println("\u001B[32m[PASSED]\u001B[0m Test Passed Successfully!");
        }
    }

    @AfterTest
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
            System.out.println("\u001B[34m[INFO]\u001B[0m WebDriver closed.");
        }
    }

    @AfterSuite
    public void afterSuite() {
        if (extent != null) {
            extent.flush();
        }
        System.out.println("\u001B[34m[INFO]\u001B[0m Suite finished. Report generated.");
    }
}