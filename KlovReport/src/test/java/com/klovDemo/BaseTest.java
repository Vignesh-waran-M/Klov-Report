package com.klovDemo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.mediastorage.MediaStorageManagerFactory;
import com.aventstack.extentreports.model.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

public class BaseTest{
    public static ExtentReports reports;
    public static ExtentHtmlReporter htmlReporter;
    public static ExtentTest testInfo;
    public static ExtentKlovReporter dashboardServer;
    public static Log log;

    private static final String DASHBOARD_SERVER_URL = "http://localhost:80/";
    private static final String MONGODB_HOST = "localhost";
    private static final int MONGODB_PORT = 27017;

    //private static ExtentTest parentTest;

    @BeforeSuite
    public void setupBeforeSuite() {
        dashboardServer = new ExtentKlovReporter();
        dashboardServer.initMongoDbConnection(MONGODB_HOST, MONGODB_PORT);
        dashboardServer.setProjectName("POC");
        dashboardServer.setReportName("POC_report");
        //dashboardServer.setKlovUrl(DASHBOARD_SERVER_URL);
        dashboardServer.initKlovServerConnection(DASHBOARD_SERVER_URL);


        htmlReporter = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + "/report.html"));
        //htmlReporter.loadXMLConfig(new File(System.getProperty("user.dir") + "/extent-config.xml"));
        //htmlReporter.setAppendExisting(true);
        htmlReporter.config().setTimeStampFormat("mm/dd/yyyy hh:mm:ss a");
        reports = new ExtentReports();

        reports.setAnalysisStrategy(AnalysisStrategy.SUITE);
        reports.attachReporter(htmlReporter);

        //dashboardServer.setStartTime(getTime(System.currentTimeMillis()));
        dashboardServer.start();
        
    }
    @Test
    public void test1() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://google.com");
        System.out.println(driver.getTitle());

    }


    @BeforeMethod
    public synchronized void setup(Method method) {
        testInfo = reports.createTest(method.getName());

        String testName = method.getName();
        //testInfo = reports.createTest(testName);
        testInfo.assignCategory("Regression");
        testInfo.assignAuthor("user1","user2");
        //testInfo.createNode("sampleNode");
        //Test test = testInfo.getModel();
        log=new Log(testInfo);
        log.setDetails(testName);
        dashboardServer.onTestStarted(testInfo.getModel());

    }
	/*
	 * @AfterMethod public synchronized void tearDown(ITestResult testResult) {
	 * 
	 * if (testResult.getStatus() == ITestResult.SUCCESS) {
	 * testInfo.pass(MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
	 * log.setMarkup(MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
	 * log.setStatus(Status.PASS); log.getParentModel().setStatus(Status.PASS); }
	 * else if (testResult.getStatus() == ITestResult.FAILURE) { //for attaching
	 * screenshot in the report try { // adding screenshots to log
	 * MediaEntityModelProvider mediaModel =
	 * MediaEntityBuilder.createScreenCaptureFromPath("test.png").build();
	 * Assert.fail("Screenshot :", mediaModel); // adding screenshots to test
	 * //testInfo.fail("Screenshot").addScreenCaptureFromPath("test.png");
	 * //screenshot will always be shown in down at test level }catch (Exception e){
	 * 
	 * } //for adding in local report
	 * Assert.fail(MarkupHelper.createLabel("Failed due below error :",
	 * ExtentColor.RED)); Assert.fail(testResult.getThrowable());
	 * 
	 * //for adding logs to dashboard
	 * log.setMarkup(MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
	 * log.setDetails(testResult.getThrowable().getMessage());
	 * log.setStatus(Status.FAIL); //for logging screenshot to the db Media media
	 * =new Media(); media.setReportObjectId(dashboardServer.getReportId());
	 * media.setObjectId(dashboardServer.getProjectId());
	 * media.setTestObjectId(dashboardServer.getProjectId());
	 * media.setMediaType(MediaType.IMG); media.setPath("test.png");
	 * 
	 * MediaStorageManagerFactory mediaStorage = new MediaStorageManagerFactory();
	 * try { mediaStorage.getManager("http-klov").init("http://localhost:1337/");
	 * mediaStorage.getManager("http-klov").storeMedia(media); }catch (IOException
	 * e){
	 * 
	 * } ScreenCapture screenCapture = new ScreenCapture();
	 * screenCapture.setReportObjectId(dashboardServer.getReportId());
	 * screenCapture.setMediaType(MediaType.IMG); screenCapture.getSource();
	 * log.setDetails(screenCapture.getSource());
	 * 
	 * } else if (testResult.getStatus() == ITestResult.SKIP) {
	 * testInfo.skip(testResult.getThrowable());
	 * log.setMarkup(MarkupHelper.createLabel("Test skipped ", ExtentColor.YELLOW));
	 * log.setDetails(testResult.getThrowable().getMessage());
	 * log.setStatus(Status.SKIP); log.getParentModel().setStatus(Status.SKIP); }
	 * dashboardServer.onCategoryAssigned(testInfo.getModel(), new
	 * Category("Regression")); dashboardServer.onLogAdded(testInfo.getModel(),log);
	 * 
	 * testInfo.getModel().end();
	 * testInfo.getModel().setStartTime(getTime(testResult.getStartMillis()));
	 * testInfo.getModel().setEndTime(getTime(testResult.getEndMillis())); // }
	 */


    @AfterSuite
    public void cleanUp() {
        //reports.flush();
        reports.flush();
        //dashboardServer.setEndTime(getTime(System.currentTimeMillis()));
        dashboardServer.stop();
        dashboardServer.flush(null);
    }

    private final Date getTime(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return  calendar.getTime();

    }

}
