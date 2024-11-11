package org.example;

import logs.SignupData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ExcelUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class SignupTest {
    private static final Logger logger = LoggerFactory.getLogger(SignupTest.class);
    private WebDriver driver;
    private final String SRC = ExcelUtils.DATA_SRC + "SIGNUP_TEST.xlsx";
    private Set<SignupData> logs;
    private SignupData data;

    @BeforeClass
    public void init() throws IOException {
        logs = new LinkedHashSet<>();
    }

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.get("https://www.phptravels.net/signup");

        data = new SignupData();
    }

    private void processSignup(String firstName, String lastName, String country, String phone, String email, String password) {
        driver.findElement(By.name("first_name")).sendKeys(firstName);
        driver.findElement(By.name("last_name")).sendKeys(lastName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement countryDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title='Select Country']")));
        countryDropdown.click();
        String countryXPath = String.format("//ul[@class='dropdown-menu inner show']//a[contains(@class, 'dropdown-item') and contains(., '%s')]", country);
        WebElement countryOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(countryXPath)));
        countryOption.click();

        driver.findElement(By.name("phone")).sendKeys(phone);
        driver.findElement(By.name("user_email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);

        // Find the iframe (using a selector that matches the reCAPTCHA iframe)
//        WebElement recaptchaIframe = wait.until(ExpectedConditions.presenceOfElementLocated(
//                By.xpath("//iframe[contains(@src, 'recaptcha/api2/anchor')]")
//        ));
//        driver.switchTo().frame(recaptchaIframe);
//        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
//                By.id("recaptcha-anchor")
//        ));
//        checkbox.click();
//        driver.switchTo().defaultContent();
        // find form with id 'login' and submit
        driver.findElement(By.id("signup")).submit();
    }

    @Test(dataProvider = "signupData")
    private void multipleSignup(String firstName, String lastName, String country, String phone, String email, String password, String randomEmail, String expected) throws InterruptedException {
        if (randomEmail.toLowerCase(Locale.ROOT).equals("true")) {
            email = "random" + System.currentTimeMillis() + "@gmail.com";
        }
        processSignup(firstName, lastName, country, phone, email, password);
        String currentUrl = driver.getCurrentUrl();
        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setCountry(country);
        data.setPhone(phone);
        data.setEmail(email);
        data.setPassword(password);
        data.setAction("Test signup function");
        data.setLogTime(new Date());
        data.setExpected(expected);
        data.setActual(currentUrl);
        Thread.sleep(2000);
        assertEquals(currentUrl, expected);
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        data.setTestMethod(result.getName());
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                data.setStatus("SUCCESS");
                break;
            case ITestResult.FAILURE:
                data.setStatus("FAILURE");
                data.setException(result.getThrowable().getMessage());

                String path = ExcelUtils.IMAGE_SRC + "failure-" + System.currentTimeMillis() + ".png";
                ExcelUtils.takeScreenShot(driver, path);
                File file = new File(path);
                data.setImage(file.getAbsolutePath());
                break;
            case ITestResult.SKIP:
                data.setStatus("SKIP");
                break;
            default:
                data.setStatus("UNKNOWN");
                break;
        }
        logs.add(data);
        driver.quit();
    }

    @AfterClass
    public void destroy() throws IOException {
        ExcelUtils.writeLog(SRC, "RESULT_TEST", logs);
    }

    @DataProvider(name = "signupData")
    public Object[][] loginData() throws IOException {
        XSSFWorkbook workbook = ExcelUtils.getWorkbook(SRC);
        XSSFSheet sheet = ExcelUtils.getSheet(workbook, "SIGNUP_DATA");

        return ExcelUtils.readSheetData(sheet);
    }
}
