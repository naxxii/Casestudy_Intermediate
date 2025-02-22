package com.example;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class for capturing screenshots with error handling.
 */
public class ScreenshotUtils {

    /**
     * Takes a screenshot and saves it to 'test-output/screenshots'.
     * Returns the file path, or null if an error occurred.
     */
    public static String captureScreenshot(WebDriver driver, String fileName) {
        if (driver == null) {
            System.err.println("[ERROR] WebDriver is null, cannot capture screenshot.");
            return null;
        }
        try {
            File screenshotDir = new File("test-output/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String filePath = screenshotDir.getAbsolutePath() + "/"
                    + fileName + "_" + System.currentTimeMillis() + ".png";
            Files.copy(srcFile.toPath(), new File(filePath).toPath());
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Failed to save screenshot: " + e.getMessage());
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("[ERROR] Unexpected exception capturing screenshot: " + ex.getMessage());
            return null;
        }
    }
}
