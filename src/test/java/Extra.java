import pages.BasePage;
import pages.HomePage;
import pages.IntroPage;
import utils.DriverSingleton;
import utils.DataManager;
import utils.ReportsManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

/***
 * The class test the extras in the project's specification document
 */
public class Extra {
    WebDriver driver;

    ReportsManager reportsManager;
    ExtentReports extent;
    ExtentTest test;

    IntroPage introPage;
    HomePage homePage;

    private int extraTestID = 1;

    @BeforeClass
    public void setup() {
        try {
            extent = ExtentReporterNG.getExtentReportInstance("BuyMe Site - Extra Automation Results", "extra_extent.html");
            test = extent.createTest("Initialize web driver");
            DataManager.logTest(extraTestID++);
            test.log(Status.INFO, "Getting driver instance");
            driver = DriverSingleton.getDriverInstance();
            driver.manage().window().maximize();
            test.log(Status.PASS, "Driver Established Successfully");
            reportsManager = new ReportsManager();

            test.log(Status.INFO, "Navigate to BuyMe Site");
            driver.get(DataManager.getData("URL"));
            test.log(Status.PASS, "Navigation was successful");
        } catch (Exception e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 1)
    public void assertLoginErrors() {
        try {
            introPage = new IntroPage();
            test = extent.createTest("Asserting Login Errors");
            DataManager.logTest(extraTestID++);
            introPage.emptyLogin();
            test.log(Status.PASS, "Assertion Was Successful.");
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (SQLException e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 2)
    public void scrollToBottom() {
        try {
            test = extent.createTest("Scroll to the bottom of the screen");
            DataManager.logTest(extraTestID++);
            homePage = new HomePage();
            homePage.selectValueFromListElement(By.cssSelector("div[class='card-items']"), "div", 2);

            WebElement element = driver.findElement(By.cssSelector("img[class='buyme-footer-logo']"));
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
            javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            test.log(Status.PASS, "Scrolling was successful");
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (SQLException e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 3)
    public void getElementTextColor() {
        try {
            test = extent.createTest("Print the step name text color");
            DataManager.logTest(extraTestID++);
            driver.navigate().to("https://buyme.co.il/money/9069918?price=2");
            WebElement stepName = driver.findElement(By.cssSelector("div[class='label bottom-xs']"));
            System.out.println("Step name text color : " + Color.fromString(stepName.getCssValue("color")).asHex());
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        }
    }

    @Test(priority = 4)
    public void uploadPictureFromTheWeb() {
        String fileName = "220px-The_Flash_Family.jpg";
        String webSite = "https://upload.wikimedia.org/wikipedia/en/thumb/e/ed/The_Flash_Family.jpg/" + fileName;

        try {
            test = extent.createTest("Upload a picture from the web");
            DataManager.logTest(extraTestID++);
            driver.navigate().to("https://buyme.co.il/money/15287647?price=3");

            String imageFile = DataManager.downloadPictureFromWeb(fileName, webSite);
            new BasePage().sendKeysToElement(By.cssSelector("input[type='file']"), imageFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 5)
    public void testJSON() {
        try {
            String apiURL = "https://my-json-server.typicode.com/Dgotlieb/JSFakeServer/config";
            System.out.println(DataManager.getJsonData(apiURL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void closeAll() {
        try {
            DataManager.saveExtraTestsLogs();

            if (driver != null) {
                driver.quit();
            }

            DataManager.closeConnection();
            extent.flush();
        } catch (SQLException | IOException e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }
}