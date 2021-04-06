package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.sql.SQLException;

/***
 * The Class manage the web driver instance
 */
public class DriverSingleton {
    private static WebDriver driver;

    /***
     * Creates a single instance of a web driver.
     * @return the instance of the web driver.
     */
    public static WebDriver getDriverInstance() throws SQLException {
        if (driver == null){

            switch (DataManager.getData("browser").toLowerCase()){
                case "chrome" : {
                    System.setProperty("webdriver.chrome.driver","drivers/chromedriver.exe");
                    driver = new ChromeDriver();
                    break;
                }

                case "firefox" : {
                    System.setProperty("webdriver.firefox.driver","drivers/geckodriver.exe");
                    driver = new FirefoxDriver();
                    break;
                }
            }
        }
        return driver;
    }
}
