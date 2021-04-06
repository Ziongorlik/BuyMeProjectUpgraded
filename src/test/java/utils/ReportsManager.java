package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/***
 * the class manages the extent reports
 */
public class ReportsManager {

    public ReportsManager(){
    }

    /***
     * Takes a screenshot and save it in a file
     * @param driver The web driver
     * @param imagePath The path of the saved image file.
     * @return Returns the image file path.
     */
    private String takeScreenshot(WebDriver driver, String imagePath){
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File screenShotFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File destinationFile = new File(imagePath + ".jpg");
        try{
            FileUtils.copyFile(screenShotFile,destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePath + ".jpg";
    }

    /***
     * Logs a screenshot when NoSuchElementException occurs
     * @param driver The web driver
     * @param e The NoSuchElementException
     * @param test The extent report test
     */
    public void logTheScreenshot(WebDriver driver, NoSuchElementException e, ExtentTest test){
        String timeNow = String.valueOf(System.currentTimeMillis());
        test.log(Status.FAIL, "Element Not Found : " + e.getMessage(),
                MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(driver,"Screenshots/" + timeNow)).build());
    }
}
