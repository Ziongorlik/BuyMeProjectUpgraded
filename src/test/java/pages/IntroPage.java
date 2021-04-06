package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.sql.SQLException;
import java.util.List;

/***
 * Intro page management
 */
public class IntroPage extends BasePage{

    public IntroPage() throws SQLException {
        super();
    }

    /***
     * Enters the Registration page.
     * @param firstName The user's first name.
     * @param email The user's email.
     * @param password The user's password.
     * @return Returns "true" or "false" whether the registration was successful
     */
    public boolean register(String firstName, String email, String password){
        clickElement(By.className("seperator-link"));
        clickElement(By.cssSelector("span[class='text-link theme']"));
        inputCredentials(firstName,email,password);
        clickElement(By.cssSelector("button[type='submit']"));
        return !waitForElement(By.className("login-error"));
    }

    /***
     * Input the correct credentials and asserts them.
     * @param firstName The user's first name.
     * @param email The user's email.
     * @param password The user's password.
     */
    private void inputCredentials(String firstName, String email, String password) {
        List<WebElement> credentialsTxtBoxes = getElementChildren(By.cssSelector("form[action='register']"),"input");

        sendKeysToElement(credentialsTxtBoxes.get(0), firstName);
        sendKeysToElement(credentialsTxtBoxes.get(1), email);
        sendKeysToElement(credentialsTxtBoxes.get(2), password);
        sendKeysToElement(credentialsTxtBoxes.get(3), password);

        Assert.assertEquals(credentialsTxtBoxes.get(0).getAttribute("value"),firstName);
        Assert.assertEquals(credentialsTxtBoxes.get(1).getAttribute("value"),email);
        Assert.assertEquals(credentialsTxtBoxes.get(2).getAttribute("value"),password);
        Assert.assertEquals(credentialsTxtBoxes.get(3).getAttribute("value"),password);
    }

    /***
     *  Login if the registration already done (email exist).
     */
    public void login() {
        clickElement(By.className("text-link"));
        clickElement(By.cssSelector("button[type='submit']"));
    }

    /***
     * Performs an empty login and asserts the error messages.
     */
    public void emptyLogin(){
        clickElement(By.className("seperator-link"));
        clickElement(By.cssSelector("button[type='submit']"));
        String emailErrorMsg = getElementAttribute(By.cssSelector("input[type=email]"),"data-parsley-required-message");
        String passwordErrorMsg = getElementAttribute(By.cssSelector("input[type=password]"),"data-parsley-required-message");

        String loginPageError = "כל המתנות מחכות לך! אבל קודם צריך מייל וסיסמה";
        Assert.assertEquals(emailErrorMsg, loginPageError);
        Assert.assertEquals(passwordErrorMsg, loginPageError);

        clickElement(By.id("times"));
    }
}
