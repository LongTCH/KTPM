package org.example;
import logs.LoginData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ExcelUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
public class LoginTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);
    private WebDriver driver;
    private String SRC = ExcelUtils.DATA_SRC + "LOGIN_TEST.xlsx";
    private Set<LoginData> logs;
    private LoginData data;

    @BeforeClass
    public void init() throws IOException{
        logs = new LinkedHashSet<>();
    }

    @BeforeMethod
    public void setup(){
        driver = new ChromeDriver();
        driver.get("https://www.phptravels.net/login");

        data = new LoginData();
    }

    private void processLogin(String email, String password){
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        // find form with id 'login' and submit
        driver.findElement(By.id("login")).submit();
    }

    @Test(dataProvider = "loginData")
    private  void multipleLogin(String email, String password, String expected) throws InterruptedException {
        processLogin(email, password);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));  // Wait up to 10 seconds
//        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(expected)));  // Wait until URL changes
        String currentUrl = driver.getCurrentUrl();
        data.setEmail(email);
        data.setPassword(password);
        data.setAction("Test login function");
        data.setLogTime(new Date());
        data.setExpected(expected);
        data.setActual(currentUrl);
        Thread.sleep(2000);
        assertEquals(currentUrl, expected);
    }

    @AfterMethod
    public  void tearDown(ITestResult result) throws IOException {
        data.setTestMethod(result.getName());
        switch (result.getStatus()){
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

    @DataProvider(name = "loginData")
    public Object[][] loginData() throws IOException {
        XSSFWorkbook workbook = ExcelUtils.getWorkbook(SRC);
        XSSFSheet sheet = ExcelUtils.getSheet(workbook, "LOGIN_DATA");

        return ExcelUtils.readSheetData(sheet);
    }
}
