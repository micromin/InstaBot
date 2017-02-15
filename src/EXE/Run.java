package EXE;

import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import auth.Login;

public class Run {

	public static void main(String[] args) {
		// your username and password will be only used for loggin in to websta.me
		String username = "your user name";
		String password = "your password";
		//////////////////////////////////////////////////////////////////////////
		String loginURL = "https://www.instagram.com/accounts/login/?next=%2Foauth%2Fauthorize%2F%3Fclient_id%3D9d836570317f4c18bca0db6d2ac38e29%26redirect_uri%3Dhttp%3A%2F%2Fwebsta.me%2Fcallback%26state%3D1%26response_type%3Dcode%26scope%3Drelationships%2Blikes%2Bcomments%2Bbasic%2Bfollower_list%2Bpublic_content";
		String loadMore = "Load More";
		String[] randomComments = new String[] { "nice!", "cool...", "😃", "nice post", "good one", "love your feed",
				"perfect", "great feed...", "amazing", "love4food", "awesome", "oh", "greate content",
				"spectacular.." };
		String[] tags = new String[] { "love", "food", "restaurant", "delicious", "goodtimes", "foodporn", "instafood",
				"foodie", "foodlover", "foodbloger", "foodfashion", "tasty" };

		// per hour
		int likesLimit = 300;
		int commentsLimit = 60;

		Login login = new Login(loginURL, username, password);
		WebDriver driver = login.getDriver();
		WebDriverWait wait = new WebDriverWait(driver, 1);
		// wait until the user is logged in
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(.,'" + username + "')]")));

		long begin = System.currentTimeMillis();
		int likes = 0;
		int comments = 0;
		int totalComments = 0;
		int totalLikes = 0;
		while (true) {
			if (likes >= likesLimit && comments >= commentsLimit) {
				begin = System.currentTimeMillis();
				likes = 0;
				comments = 0;
				long end = System.currentTimeMillis();
				System.out.println("liked=" + totalLikes + ", commented=" + totalComments);
				try {
					Thread.sleep(3600000 - (end - begin) + 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String tag = tags[(int) (Math.random() * tags.length)];
			// load the tag list
			driver.get("https://websta.me/tag/" + tag + "?vm=list");
			wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
					.executeScript("return document.readyState").equals("complete"));

			List<WebElement> medias = driver.findElements(By.xpath("//ul[@class='row media-list']/li"));

			for (Iterator iterator = medias.iterator(); iterator.hasNext();) {
				WebElement post = (WebElement) iterator.next();
				List<WebElement> like = post.findElements(By.xpath("//i[@class='fa fa-heart-o like-icon']"));
				Actions actions = new Actions(driver);
				try {
					for (Iterator iterator2 = like.iterator(); iterator2.hasNext();) {
						WebElement webElement = (WebElement) iterator2.next();
						if (likes < likesLimit) {
							actions.moveToElement(webElement).click().perform();
							likes++;
							totalLikes++;
							Thread.sleep(2000);
						}
					}
					
				} catch (Exception e) {

				}

				WebElement comment = post.findElement(By.xpath("//div[@class='media-comment-form form-inline']"));
				try {
					WebElement commentText = comment.findElement(By.xpath("//div[@contenteditable='" + true + "']"));
					commentText.sendKeys(randomComments[(int) (Math.random() * randomComments.length)]);
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].setAttribute('contenteditable', 'false')", commentText);
					WebElement commentButton = comment.findElement(
							By.xpath("//button[contains(.,'Post') and contains(@class, 'btn btn-default')]"));
					js.executeScript("arguments[0].setAttribute('class', 'done')", commentButton);
					if (comments < commentsLimit) {
						commentButton.click();
						comments++;
						Thread.sleep(58000);
						totalComments++;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {
					WebElement btnLoadMore = driver.findElement(By.xpath("//a[contains(.,'" + loadMore
							+ "') and contains(@class, 'btn btn-default text-uppercase')]"));
					btnLoadMore.click();
				} catch (Exception e) {
				}
				wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
						.executeScript("return document.readyState").equals("complete"));

			}
		}
	}

}
