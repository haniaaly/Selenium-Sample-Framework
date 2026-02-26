package com.selenium.framework.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.time.Duration;

public class ValidatePortfolioValueTest {
	
	enum Browser { CHROME, FIREFOX, EDGE }

	  @ParameterizedTest(name = "validatePortfolioValueTest browser={0}, url={2}")
	  @MethodSource("scenarios")
	  void validatePortfolioValueTest(Browser browser, boolean headless, String url, String username, String password,
									  String expectedPorfolioValue, String expectedCurrency) {
	    WebDriver driver = newDriver(browser, headless);
	    try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			driver.get(url);
			if(driver.getTitle().contains("Just a moment"))
				throw new AssertionError("Blocked by Cloudflare challenge.");
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='Log in']"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys(username);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password"))).sendKeys(password);
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']"))).click();

			String actualPortfolioValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='portfolio-value'] > span:first-child"))).getText().trim();
			if (expectedCurrency != null && !expectedCurrency.trim().isEmpty()) {
				WebElement portfolioValueElement = driver.findElement(By.cssSelector("[data-testid='portfolio-value']"));
				String actualCurrency = portfolioValueElement.findElement(By.xpath("preceding-sibling::span[1]")).getText().trim();
				actualPortfolioValue = actualCurrency + actualPortfolioValue;
				expectedPorfolioValue = new String(expectedCurrency.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + expectedPorfolioValue;
			}

			Assertions.assertEquals(expectedPorfolioValue.trim(), actualPortfolioValue, "Portfolio value should be " + expectedPorfolioValue + ", but actual value is " + actualPortfolioValue);
	    } finally {
			driver.quit();
	    }
	  }

	  static Stream<Arguments> scenarios() {
	    List<Browser> browsers = parseBrowsers(System.getProperty("browsers", "CHROME"));
		boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

	    String url = required("url");
	    String username = required("username");
	    String password = required("password");
		String expectedPortfolioValue = required("portfolioValue");
		String expectedCurrency = System.getProperty("currency");

	    return browsers.stream().map(browser -> arguments(browser, headless, url, username, password,  expectedPortfolioValue, expectedCurrency));
	  }

	  static String required(String key) {
	    String value = System.getProperty(key);
	    if (value == null || value.trim().isEmpty()) {
	      throw new IllegalArgumentException("Missing required parameter" + key + ". " +
				  "Either add it in the config/test.properties file or add it to your run command via -D.");
	    }
	    return value.trim();
	  }

	  static List<Browser> parseBrowsers(String csv) {
	    return Arrays.stream(csv.split(","))
	        .map(String::trim).filter(s -> !s.isEmpty())
	        .map(s -> Browser.valueOf(s.toUpperCase(Locale.ROOT)))
	        .toList();
	  }

	static WebDriver newDriver(Browser browser, boolean headless) {
		return switch (browser) {
			case CHROME -> {
				WebDriverManager.chromedriver().setup();
				ChromeOptions o = new ChromeOptions();
				if (headless) {
					o.addArguments("--headless=new");
				}
				yield new ChromeDriver(o);
			}
			case FIREFOX -> {
				WebDriverManager.firefoxdriver().setup();
				FirefoxOptions o = new FirefoxOptions();
				if (headless)
					o.addArguments("-headless");
				yield new FirefoxDriver(o);
			}
			case EDGE -> {
				WebDriverManager.edgedriver().setup();
				EdgeOptions o = new EdgeOptions();
				if (headless)
					o.addArguments("--headless=new");
				yield new EdgeDriver(o);
			}
		};
	}

}
