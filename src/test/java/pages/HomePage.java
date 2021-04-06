package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.sql.SQLException;

/***
 * Home page management
 */
public class HomePage extends BasePage{

    public HomePage() throws SQLException {
        super();
    }

    public String findPresent(int budget, int region, int category) throws NoSuchElementException {
        // Select an option from the amount dropdown list
        clickElement(By.linkText("סכום"));
        selectValueFromListElement(By.className("chosen-with-drop"), "li", budget);

        // Select an option from the area dropdown list
        clickElement(By.linkText("אזור"));
        selectValueFromListElement(By.className("chosen-with-drop"), "li", region);

        // Select an option from the category dropdown list
        clickElement(By.linkText("קטגוריה"));
        selectValueFromListElement(By.className("chosen-with-drop"), "li", category);

        String findPresentValues = getElementAttribute(By.cssSelector("a[rel='nofollow']"),"href");
        clickElement(By.cssSelector("a[rel='nofollow']"));

        return findPresentValues;
    }
}
