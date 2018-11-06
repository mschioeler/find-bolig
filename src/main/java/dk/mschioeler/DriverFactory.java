package dk.mschioeler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class DriverFactory {
    public static WebDriver getDriver(String driverType) {
        switch (driverType) {
            case "chrome": return new ChromeDriver();
            case "ie": return new InternetExplorerDriver();
            case "firefox": return new FirefoxDriver();
            default: throw new IllegalArgumentException();
        }
    }
}
