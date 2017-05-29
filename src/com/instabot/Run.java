package com.instabot;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class Run {
	private WebDriver driver;
	String loginURL = "https://www.instagram.com/accounts/login/?next=%2Foauth%2Fauthorize%2F%3Fclient_id%3D9d836570317f4c18bca0db6d2ac38e29%26redirect_uri%3Dhttp%3A%2F%2Fwebsta.me%2Fcallback%26state%3D1%26response_type%3Dcode%26scope%3Drelationships%2Blikes%2Bcomments%2Bbasic%2Bfollower_list%2Bpublic_content";
	String username = "username";
	String password = "password";

	String loadMore = "Load More";
	String[] comments = new String[] { "nice!", "cool...", "😃", "nice post", "good one", "love your feed", "perfect",
			"great feed...", "amazing", "love4food", "awesome", "oh", "greate content", "spectacular.." };
	String[] tags = new String[] { "love", "food", "restaurant", "delicious", "goodtimes", "foodporn", "instafood",
			"foodie", "foodlover", "foodbloger", "foodfashion", "tasty" };

	// per hour
	int likesLimit = 300;
	int commentsLimit = 60;

	static int likesSofar = 0;
	static int commentsSofar = 0;

	ExpectedCondition<Boolean> isPageLoaded = new ExpectedCondition<Boolean>() {
		@Override
		public Boolean apply(WebDriver d) {
			System.out.println("checking if page is loaded...");
			return ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete");
		}
	};

	public Run() {
		System.setProperty("webdriver.chrome.driver", "/Volumes/Data/Softwares/ChromeDriver/chromedriver");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		boolean isLoggedIn = Login(driver, loginURL, username, password); // loggin
																			// in
		if (!isLoggedIn) {
			System.out.println("cannot login..");
			System.exit(0);
		} else {
			process();
		}
	}

	private void process() {
		System.out.println("starting process...");
		while (true) {
			doLikesAndComments();
			thenDelay(1000 * 60 * 60);
		}
	}

	private void thenDelay(long time) {
		try {
			Thread.sleep(time); // wait for 1 hour
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void doLikesAndComments() {
		String tag = tags[(int) (Math.random() * tags.length)];
		System.out.println("like for tag:" + tag);
		driver.get("https://websta.me/tag/" + tag + "?vm=list");
		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
		int liked = 0;
		int commented = 0;
		while (liked < likesLimit) {
			try {
				List<WebElement> tobeLiked = driver.findElements(By.xpath(
						"//div[@class='media-action']/ul[@class='list-inline']/li/i[@class='fa fa-heart-o like-icon']"));
				System.out.println("found to be liked: " + tobeLiked.size());
				if (tobeLiked.size() == 0 && liked < likesLimit) {
					WebElement btnLoadMore = driver.findElement(By.xpath("//a[contains(.,'" + loadMore
							+ "') and contains(@class, 'btn btn-default text-uppercase')]"));
					btnLoadMore.click();
				}
				for (int i = 0; i < tobeLiked.size(); i++) {
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", tobeLiked.get(i));
					liked++;
					likesSofar++;
					Thread.sleep(2000);
				}
			} catch (Exception e) {
			}
		}

		while (commented < commentsLimit) {
			try {
				List<WebElement> tobeCommented = driver
						.findElements(By.xpath("//div[@class='media-comment-form form-inline']"));
				System.out.println("found to be commented: " + tobeCommented.size());
				if (tobeCommented.size() == 0 && commented < commentsLimit) {
					WebElement btnLoadMore = driver.findElement(By.xpath("//a[contains(.,'" + loadMore
							+ "') and contains(@class, 'btn btn-default text-uppercase')]"));
					btnLoadMore.click();
				}
				for (int i = 0; i < tobeCommented.size(); i++) {
					WebElement commentText = tobeCommented.get(i)
							.findElement(By.xpath("//div[@contenteditable='true']"));
					commentText.sendKeys(comments[(int) (Math.random() * comments.length)]);
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].setAttribute('contenteditable', 'false')", commentText);
					WebElement commentBtn = tobeCommented.get(i).findElement(
							By.xpath("//button[contains(.,'Post') and contains(@class,'btn btn-default')]"));
					js.executeScript("arguments[0].setAttribute('class', 'done')", commentBtn);
					commentBtn.click();
					commented++;
					commentsSofar++;
					Thread.sleep(15000);
				}
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		new Run();
	}

	public boolean Login(WebDriver driver, String loginURL, String username, String password) {
		try {
			driver.get(loginURL);
			driver.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
			driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
			driver.findElement(By.xpath("//button[contains(.,'Log in')]")).click();
			try {
				WebElement usernameVisibility = driver.findElement(By.xpath("//*[contains(.,'" + username + "')]"));
				System.out.println(usernameVisibility);
			} catch (Exception e) {
				System.err.println(e);
				// do nothing
				return false;
			}
			driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
			return true;
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

}
