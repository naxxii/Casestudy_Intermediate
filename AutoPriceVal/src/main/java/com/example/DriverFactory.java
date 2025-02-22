package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Provides a configured WebDriver instance for the given browser.
 * Main place to specify custom Chromium path & matching ChromeDriver.
 */
public class DriverFactory {

    public static WebDriver getDriver(String browser) {
        if (browser == null || browser.isEmpty()) {
            browser = "chrome"; // default
        }

        WebDriver driver;
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    // 1) Ensure you have a matching ChromeDriver for your custom Chromium build
                    // E.g. "C:\\WebDriver\\chromedriver.exe" that matches version 133.x if you use a dev build
                    System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\chromedriver.exe");

                    // 2) If you want to override user agent or disable devtools netty, do it here
                    // For example:
                    // System.setProperty("webdriver.http.factory", "apache"); // only if Selenium 4.8.0+ supports it

                    // 3) Point ChromeOptions to your custom Chromium
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setBinary("C:\\Chrome_Binary\\chrome.exe");

                    // Additional arguments if needed
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");

                    driver = new ChromeDriver(chromeOptions);
                    break;

                case "firefox":
                    // Not used currently, left here for future
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    driver = new FirefoxDriver(ffOptions);
                    break;

                case "edge":
                    // Not used currently, left here for future
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    driver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException("Browser not supported: " + browser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[ERROR] Failed to create WebDriver for browser: " + browser, e);
        }
        return driver;
    }
}
