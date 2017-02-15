package auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Login {
	private WebDriver driver;
	WebDriverWait wait;

	public Login(String loginURL, String username, String password) {
		driver = setDriver("C:/Python27/Scripts/chromedriver.exe");
		driver.get(loginURL);
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
		driver.findElement(By.xpath("//button[contains(.,'Log in')]")).click();
	}

	private WebDriver setDriver(String path) {
		System.setProperty("webdriver.chrome.driver", path);
		return new ChromeDriver();
	}

	public WebDriver getDriver() {
		return driver;
	}
}
