package com.saucedemo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports createInstance(String reportFilePath) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setReportName("SauceDemo Intermediate Case Study");
        sparkReporter.config().setDocumentTitle("SauceDemo Test Report");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // You can add system info if you want
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Author", "Your Name");

        return extent;
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance("reports/SauceDemoReport.html");
        }
        return extent;
    }
}