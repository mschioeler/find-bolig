package dk.mschioeler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main {

    private static final By LOGIN = By.id("fm1_link_Login");
    private static final By USERNAME = By.xpath(".//input[contains(@id, 'UserName')]");
    private static final By PASSWORD = By.xpath(".//input[contains(@id, 'Password')]");
    private static final By LOGIN_BUTTON = By.xpath("//div[@class='formSectionSubmit']//a[text()='Log ind']");
    private static final String DEFAULT_SEARCH_QUERY = "https://www.findbolig.nu/ledigeboliger/liste.aspx?&adr=Myggen%C3%A6sgade&showrented=1&showyouth=1&showlimitedperiod=1&showunlimitedperiod=1&showOpenDay=0&focus=ctl00_placeholdersidebar_0_txt_Address";
    private static final By ANSOEG_BOLIG = By.xpath("//a[text()='Ansøg bolig']");

    public static void main(String[] args) {
        String driverType = args[0];
        String user = args[1];
        String password = args[2];
        final String query = args.length > 3? args[3]: DEFAULT_SEARCH_QUERY;
        final WebDriver driver = DriverFactory.getDriver(driverType);
        driver.get("https://www.findbolig.nu/");
        login(driver, user, password);

        driver.get(query);

        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .pollingEvery(Duration.ofSeconds(5))
                .withTimeout(Duration.ofDays(2));
        WebElement hit = wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(@Nullable WebDriver input) {
                driver.get(query);
                List<WebElement> results = driver.findElements(By.xpath("//*[@id='GridView_Results']//tr[@aid]"));
                if (results.size() == 0) return null;
                if (results.size() == 1) {
                    WebElement result = results.get(0);
                    if (isExistingBolig(result)) {
                        return null;
                    } else {
                        return result;
                    }
                }
                for (WebElement result : results) {
                    if (!isExistingBolig(result)) {
                        return result;
                    }
                }
                return null;
            }
        });

        hit.findElement(By.xpath(".//img")).click();
        driver.findElement(ANSOEG_BOLIG).click();
        System.out.println(String.format("Ansøgte om bolig: %s", driver.getCurrentUrl()));
    }

    private static boolean isExistingBolig(WebElement element) {
        return element.findElements(By.xpath(".//*[contains(text(),'Myggenæsgade  9, 5. lejl. 4')]")).size() > 0;
    }

    private static void login(WebDriver driver, String username, String password) {
        driver.findElement(LOGIN).click();
        new FluentWait<>(driver).until(ExpectedConditions.presenceOfElementLocated(USERNAME));
        driver.findElement(USERNAME).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();
    }
}
