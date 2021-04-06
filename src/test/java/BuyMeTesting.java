import pages.HomePage;
import pages.IntroPage;
import pages.MoneyPage;
import pages.SupplierPage;
import utils.DriverSingleton;
import utils.DataManager;
import utils.MoneyPageEnums.PresentReceiver;
import utils.MoneyPageEnums.SendingTime;
import utils.MoneyPageEnums.SendingMethod;
import utils.ReportsManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

/***
 * The testing class
 */
public class BuyMeTesting {
    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest test;
    private ReportsManager reportsManager;

    private IntroPage introPage;
    private HomePage homePage;

    private final String firstName = "Zion";
    private String presentParametersURL;

    private int testID = 1;

    @BeforeClass
    public void initializeSite() {
        try {
            extent = ExtentReporterNG.getExtentReportInstance("BuyMe Site - Automation Results", "extent.html");
            test = extent.createTest("Initialize web driver");
            DataManager.logTest(testID++);
            test.log(Status.INFO, "Getting driver instance");
            driver = DriverSingleton.getDriverInstance();
            driver.manage().window().maximize();
            test.log(Status.PASS, "Driver Established Successfully");
            reportsManager = new ReportsManager();
            DataManager.createTables();
        } catch (Exception e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 1)
    public void navigateToSite() {
        try {
            test = extent.createTest("Intro and navigation screen");
            DataManager.logTest(testID++);
            test.log(Status.INFO, "Navigate to BuyMe Site");
            driver.get(DataManager.getData("URL"));
            test.log(Status.PASS, "Navigation was successful");
        } catch (NullPointerException | SQLException e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 2)
    public void registerOrLogin() {
        test.log(Status.INFO, "Registering / Login");
        String email = "ziontest@test.co.il";
        String password = "Password1";

        try {
            introPage = new IntroPage();
            if (introPage.register(firstName, email, password)) {
                test.log(Status.PASS, "Inputting Credentials Was Successful");
                test.log(Status.PASS, "Register Was Successful");
            } else {
                test.log(Status.WARNING, "Register Was Unsuccessful. Trying To Login...");
                introPage.login();
                test.log(Status.PASS, "Login Was Successful");
            }
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (SQLException e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 3)
    public void findPresent() {
        try {
            test = extent.createTest("Home screen");
            DataManager.logTest(testID++);
            test.log(Status.INFO, "Selecting present parameters");
            homePage = new HomePage();
            presentParametersURL = homePage.findPresent(1, 2, 3);
            test.log(Status.PASS, "Selecting present parameters was Successful");
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (Exception e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 4)
    public void pickBusiness() {
        try {
            test.log(Status.INFO, "Asserting the URL");
            Assert.assertEquals(driver.getCurrentUrl(), presentParametersURL);
            test.log(Status.PASS, "Assertion Was Successful.");
            test.log(Status.INFO, "Choosing a supplier");
            homePage.waitForElement(By.className("bm-product-cards"));
            homePage.selectValueFromListElement(By.className("bm-product-cards"), "li", 2);
            test.log(Status.PASS, "Supplier chosen");

            test = extent.createTest("Supplier screen");
            DataManager.logTest(testID++);
            test.log(Status.INFO, "Inputting money Amount");
            SupplierPage supplierPage = new SupplierPage();
            supplierPage.inputMoneyAmount(5);
            test.log(Status.PASS, "Amount Of Money Submitted");
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (AssertionError e) {
            test.log(Status.FAIL, "Assertion Failed: " + e.getMessage());
        } catch (Exception e) {
            test.log(Status.FAIL, e.getMessage());
        }
    }

    @Test(priority = 5)
    public void payAndSendGift() {
        try {
            String recipientEmail = "test@test.il";
            test = extent.createTest("Money screen");
            DataManager.logTest(testID++);
            MoneyPage moneyPage = new MoneyPage();
            test.log(Status.INFO, "Filling 'Send to who' form");
            moneyPage.fillForm_SendToWho(PresentReceiver.OTHER, "Benzy", 5, "Happy Birth Day, Dude!", "src/main/resources/Elmo.jpg");
            test.log(Status.PASS, "'Send to who' form - Filling was successful.");
            test.log(Status.INFO, "Filling 'How to send' form");
            moneyPage.fillForm_HowToSend(firstName, SendingTime.NOW, SendingMethod.EMAIL, recipientEmail);
            test.log(Status.PASS, "'How to send' form - Filling was successful.");
        } catch (NoSuchElementException e) {
            reportsManager.logTheScreenshot(driver, e, test);
        } catch (SQLException e) {
            test.log(Status.FAIL, e.getMessage());
        } catch (AssertionError e) {
            test.log(Status.FAIL, "Assertion Failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void closeAll() {
        try {
            DataManager.saveTestsLogs(testID - 1);

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