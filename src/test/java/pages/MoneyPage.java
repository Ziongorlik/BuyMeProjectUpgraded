package pages;

import utils.MoneyPageEnums.PresentReceiver;
import utils.MoneyPageEnums.SendingTime;
import utils.MoneyPageEnums.SendingMethod;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;


import java.sql.SQLException;
import java.time.Duration;

/***
 * Money page management
 */
public class MoneyPage extends BasePage{

    public MoneyPage() throws SQLException {
        super();
    }

    /***
     * Fills the first step of the Money page - "Send To Who" form.
     * @param receiver The receiver of the present - the user or someone else.
     * @param receiverName The name of the receiver, if it someone else.
     * @param eventType The type of the event, if the receiver is someone else.
     * @param blessing The blessing message, if the receiver is someone else.
     */
    public void fillForm_SendToWho(PresentReceiver receiver, String receiverName, int eventType, String blessing, String picturePath){
        switch (receiver){
            case ME:{
                clickElement(By.className("button-forMyself"));
                break;
            }
            case OTHER: {
                clickElement(By.className("button-forSomeone"));
                break;
            }
        }
        // Inputting the receiver name in the textbox
        sendKeysToElement(By.cssSelector("input[type='text']"),receiverName);

        // Choosing the event type
        clickElement(By.cssSelector("div[class='selected-name']"));

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[gtm='eventType']"))).click();
        selectValueFromListElement(By.cssSelector("label[gtm='eventType']"),"li",eventType);

        // Filling the blessing textarea
        sendKeysToElement(By.tagName("textarea"),blessing);

        // Upload a picture
        sendKeysToElement(By.cssSelector("input[type='file']"),picturePath);

        Assert.assertEquals(getElementAttribute(By.cssSelector("input[type='text']"),"value"),receiverName);
        clickElement(By.cssSelector("button[gtm='המשך']"));
    }

    /***
     * Fills the second step of the Money page - "How To Send" form.
     * @param sendingTime When to send the present - Now or Later.
     * @param sendingMethod Send the present by Sms or Email.
     * @param methodText The method text: SMS - Phone number / Email = Email address.
     */
    public void fillForm_HowToSend(String senderName, SendingTime sendingTime, SendingMethod sendingMethod, String methodText){
        switch (sendingTime){
            case NOW: {
                clickElement(By.className("button-now"));
                break;
            }

            case LATER:{
                clickElement(By.className("button-later"));
                break;
            }
        }

        switch (sendingMethod){
            case SMS: {
                clickElement(By.cssSelector("svg[gtm='method-sms']"));
                sendKeysToElement(By.id("sms"),methodText);
                break;
            }

            case EMAIL:{
                clickElement(By.cssSelector("svg[gtm='method-email']"));
                sendKeysToElement(By.id("email"),methodText);
                break;
            }
        }
        Assert.assertEquals(getElementAttribute(By.cssSelector("input[type='text']"),"value"),senderName);
        clickElement(By.cssSelector("button[gtm='המשך לתשלום']"));
    }
}