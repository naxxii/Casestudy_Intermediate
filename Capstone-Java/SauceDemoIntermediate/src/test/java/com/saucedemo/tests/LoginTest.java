package com.saucedemo.tests;

import com.aventstack.extentreports.Status;
import com.saucedemo.utils.ScreenshotUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(priority = 1)
    @Parameters({"validUsername", "validPassword"})
    public void testValidLogin(String username, String password) throws InterruptedException {
        test = extent.createTest("Valid Login Test");

        driver.get(baseUrl);
        test.log(Status.INFO, "Navigated to: " + baseUrl);
        System.out.println("\u001B[33m[STEP]\u001B[0m Navigated to: " + baseUrl);
        Thread.sleep(3000);

        // page title verification
        String pageTitle = driver.getTitle();
        System.out.println("\u001B[33m[STEP]\u001B[0m Title is: " + pageTitle);
        Assert.assertTrue(pageTitle.contains("Swag Labs"), "\u001B[31m[ERROR]\u001B[0m The page title does not contain 'Swag Labs'!");

        // user name validation
        driver.findElement(By.id("user-name")).sendKeys(username);
        test.log(Status.INFO, "Entered valid username: " + username);
        System.out.println("\u001B[33m[STEP]\u001B[0m Entered valid username.");
        Thread.sleep(3000);

        // password validation
        driver.findElement(By.id("password")).sendKeys(password);
        test.log(Status.INFO, "Entered valid password.");
        System.out.println("\u001B[33m[STEP]\u001B[0m Entered valid password.");
        Thread.sleep(3000);

        driver.findElement(By.id("login-button")).click();
        test.log(Status.INFO, "Clicked Login button.");
        System.out.println("\u001B[33m[STEP]\u001B[0m Clicked Login button.");
        Thread.sleep(3000);

        // Check if "Products" page is displayed with a capital P
        WebElement productsHeader = driver.findElement(By.className("title"));
        Assert.assertTrue(productsHeader.isDisplayed(), "\u001B[31m[ERROR]\u001B[0m 'Products' header not displayed!");
        Assert.assertEquals(productsHeader.getText(), "Products", "\u001B[31m[ERROR]\u001B[0m The header text is not 'Products'!");

        System.out.println("\u001B[32m[PASSED]\u001B[0m Valid login test passed.");
        test.log(Status.PASS, "Valid Login Test Passed Successfully!");

        // Screenshot on successful login
        String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "valid_login_success");
        test.addScreenCaptureFromPath(screenshotPath);

        // ************** LOG OUT **************
        System.out.println("\u001B[33m[STEP]\u001B[0m Logging out...");
        driver.findElement(By.id("react-burger-menu-btn")).click();
        System.out.println("\u001B[33m[STEP]\u001B[0m Clicked the hamburger menu.");
        Thread.sleep(3000);
        driver.findElement(By.id("logout_sidebar_link")).click();
        System.out.println("\u001B[33m[STEP]\u001B[0m Clicked the logout link. User should be logged out now.");
        test.log(Status.INFO, "Logged out after valid login test.");
    }

    @Test(priority = 2)
    @Parameters({"invalidUsername", "invalidPassword"})
    public void testInvalidLogin(String username, String password) throws InterruptedException {
        test = extent.createTest("Invalid Login Test");

        driver.get(baseUrl);
        test.log(Status.INFO, "Navigated to: " + baseUrl);
        System.out.println("\u001B[33m[STEP]\u001B[0m Navigated to: " + baseUrl);
        Thread.sleep(3000);

        String pageTitle = driver.getTitle();
        System.out.println("\u001B[33m[STEP]\u001B[0m Title is: " + pageTitle);
        Assert.assertTrue(pageTitle.contains("Swag Labs"), "\u001B[31m[ERROR]\u001B[0m The page title does not contain 'Swag Labs'!");

        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        test.log(Status.INFO, "Entered invalid credentials: " + username + " / " + password);
        System.out.println("\u001B[33m[STEP]\u001B[0m Entered invalid credentials.");
        Thread.sleep(3000);

        driver.findElement(By.id("login-button")).click();
        test.log(Status.INFO, "Clicked Login button.");
        System.out.println("\u001B[33m[STEP]\u001B[0m Clicked Login button.");
        Thread.sleep(3000);

        WebElement errorContainer = driver.findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue(errorContainer.isDisplayed(), "\u001B[31m[ERROR]\u001B[0m Error message not displayed for invalid login!");

        String errorText = errorContainer.getText();
        System.out.println("\u001B[33m[STEP]\u001B[0m Error text: " + errorText);
        Assert.assertTrue(errorText.contains("Epic sadface:"), "\u001B[31m[ERROR]\u001B[0m Error text doesn't contain 'Epic sadface:'!");

        System.out.println("\u001B[32m[PASSED]\u001B[0m Invalid login test passed (error displayed).");
        test.log(Status.PASS, "Invalid Login Test Passed (proper error shown)!");

        String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "invalid_login_error");
        test.addScreenCaptureFromPath(screenshotPath);
    }
}