package pages;

import utils.DriverSingleton;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

/***
 * The class manages repeat operations on web pages.
 */
public class BasePage {
    WebDriver driver;

    public BasePage() throws SQLException {
        driver = DriverSingleton.getDriverInstance();
    }

    /***
     * Finds an element by locator.
     * @param locator The attributes and values of the element.
     * @return Returns the wanted element.
     */
    private WebElement getWebElement(By locator) throws NoSuchElementException {
        return driver.findElement(locator);
    }

    /***
     * Clicks on the element.
     * @param locator The attributes and values of the wanted element.
     */
    public void clickElement(By locator){
        getWebElement(locator).click();
    }

    /***
     * Sends text to the element using the Web element.
     * @param element The web element.
     * @param text The text to input in the element.
     */
    public void sendKeysToElement(WebElement element, String text){
        element.clear();
        element.sendKeys(text);
    }

    /***
     * Send text to the element using a locator.
     * @param locator The attributes and values of the wanted element.
     * @param text The text to input in the element.
     */
    public void sendKeysToElement(By locator, String text){
        sendKeysToElement(getWebElement(locator),text);
    }

    /***
     * Returns the text of an element.
     * @param locator The attributes and values of the element.
     * @return Returns the text of the element.
     */
    public String getElementAttribute(By locator, String attribute){
        return getWebElement(locator).getAttribute(attribute);
    }

    /***
     * Waits until the element is present.
     * @param locator The attributes and values of the wanted element.
     * @return Returns "true" if the element is found, "false" if not.
     */
    public boolean waitForElement(By locator){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return getWebElement(locator).isDisplayed();
    }

    /***
     * Gets the child elements from a parent element by tag name.
     * @param locator The attributes and values of the wanted element.
     * @param cssSelector the cssSelector of the child elements search for.
     * @return Returns a list of the children elements found.
     */
    public List<WebElement> getElementChildren(By locator, String cssSelector){
        WebElement listElement = getWebElement(locator);
        listElement.click();
        return listElement.findElements(By.cssSelector(cssSelector));
    }

    /***
     * Selects the wanted index from a list element.
     * @param element The list element.
     * @param tagName The options tag name.
     * @param index The index to select.
     */
    public void selectValueFromListElement(WebElement element,String tagName, int index){
        element.findElements(By.tagName(tagName)).get(index).click();
    }

    /***
     * Selects the wanted index from a list element, using a locator.
     * @param locator The attributes and values of the wanted element.
     * @param index the index of the wanted value in the list.
     */
    public void selectValueFromListElement(By locator,String tagName, int index){
        selectValueFromListElement(getWebElement(locator),tagName,index);
    }
}