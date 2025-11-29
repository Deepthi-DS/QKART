package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        driver.manage().window().maximize();
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
         @Test
         public void TestCase01() throws InterruptedException {
        Boolean status;
        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
         status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

         
        
    }
    @Test
    public void TestCase02() throws InterruptedException {
        Boolean status;
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        assertFalse(status, "Re-registration of existing user was successful");
    }
     @Test 
    public void TestCase03() throws InterruptedException {
         boolean status;

         // Visit the home page
         Home homePage = new Home(driver);
         homePage.navigateToHome();

         // Search for the "yonex" product
         status = homePage.searchForProduct("YONEX");

          assertTrue(status, " Failed to search for product");

         // Fetch the search results
         List<WebElement> searchResults = homePage.getSearchResults();
         // Verify the search results are available
            assertNotEquals(Integer.toString(searchResults.size()),"0", "Test case Failure. There was no results for the given search string");
         
        
         for (WebElement webElement : searchResults) {
             // Create a SearchResult object from the parent element
             SearchResult resultelement = new SearchResult(webElement);

             // Verify that all results contain the searched text
             String elementText = resultelement.getTitleofResult();
            assertTrue(elementText.toUpperCase().contains("YONEX"), " Test Case Failure. Test Results contains un-expected values: " + elementText); 
            
            
         }
         // Search for product
        status = homePage.searchForProduct("Gesundheit");
        assertTrue(status, "Test Case Failure. Invalid keyword returned results");

         // Verify no search results are found
         searchResults = homePage.getSearchResults();
         if (searchResults.size() == 0) {
            status = homePage.isNoResultFound();
            assertTrue(status, "No product found is not validated");
             }
     }
     @Test 
     public void TestCase04() throws InterruptedException {
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            assertTrue(result.verifySizeChartExists(), "Failed Validation of presence of Size Chart Link");
            assertTrue(result.verifyExistenceofSizeDropdown(driver), " Failed validaion of presence of size chart");
                // Open the size chart
                if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                    status = result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver);
                    assertTrue(status, "Failed validation of size chart contents");
                    // Close the size chart modal
                    status = result.closeSizeChart(driver);
                } 
        }
    }
    @Test
    public void TestCase05() throws InterruptedException {
        Boolean status;
        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Test Case Failure. Happy Flow Test Failed ");
        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Test case failure");
        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        assertTrue(status, "Test case failed for Happy flow");
        
    }
    @Test
    public void TestCase06() throws InterruptedException {
        Boolean status;
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "User registeration failed");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "User Login failed");

        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        assertTrue(status, "product not found Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct("Yarine");
        assertTrue(status, "product not found Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");

        // update watch quantity to 2
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status, "Test case failed : Place order unsuccesful");
        homePage.navigateToHome();
        homePage.PerformLogout();

       
    }

@Test 
public void TestCase07() throws InterruptedException {
    Boolean status;

    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
   assertTrue(status,"User Registration Failed");
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status, "Login Failed");

    Home homePage = new Home(driver);
    homePage.navigateToHome();
    status = homePage.searchForProduct("Stylecon");
    assertTrue(status, "Product not found");
    homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set ");

    homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set ", 10);

    homePage.clickCheckout();

    Checkout checkoutPage = new Checkout(driver);
    checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
    checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

    checkoutPage.placeOrder();
    Thread.sleep(3000);

    status = checkoutPage.verifyInsufficientBalanceMessage();
    assertTrue(status, "Insufficient balance not verified");
    
}
@Test
public void TestCase08() throws InterruptedException {
    Boolean status = false;

    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status, "Test case failed to verify added product in cart when new tab opened");
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status, "User Login failed");
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    status = homePage.searchForProduct("YONEX");
    assertTrue(status, "Product not found");
    homePage.addProductToCart("YONEX Smash Badminton Racquet");

    String currentURL = driver.getCurrentUrl();

    driver.findElement(By.linkText("Privacy policy")).click();
    Set<String> handles = driver.getWindowHandles();
    driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

    driver.get(currentURL);
    Thread.sleep(2000);

    List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
    status = homePage.verifyCartContents(expectedResult);
    assertTrue(status, " Verifiaction failed that product added to cart is available when a new tab is opened");

    driver.close();

    driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
}
// @Test
// public void TestCase09() throws InterruptedException {
//     Boolean status = false;
//     Register registration = new Register(driver);
//     registration.navigateToRegisterPage();
//     status = registration.registerUser("testUser", "abc@123", true);
//     assertTrue(status, "Test case failed validation of Privacy Policy");
//     lastGeneratedUserName = registration.lastGeneratedUsername;
//     SoftAssert softAssert = new SoftAssert();

//     Login login = new Login(driver);
//     login.navigateToLoginPage();
//     status = login.PerformLogin(lastGeneratedUserName, "abc@123");
//     assertTrue(status, "User Login Failed");
//     Home homePage = new Home(driver);
//     homePage.navigateToHome();

//     String basePageURL = driver.getCurrentUrl();

//     driver.findElement(By.linkText("Privacy policy")).click();
//     status = driver.getCurrentUrl().equals(basePageURL);
//     //softAssert.assertTrue(status, "Falied verification of parent page changing");
//     assertTrue(status, "Falied verification of parent page not changing");

//     Set<String> handles = driver.getWindowHandles();
//     driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
//     WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
//     status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
//     softAssert
//     if (!status) {
//         logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading failed", status ? "PASS" : "FAIL");
//         takeScreenshot(driver, "Failure", "TestCase9");
//         logStatus("End TestCase",
//                 "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
//                 status ? "PASS" : "FAIL");
//     }

//     driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
//     driver.findElement(By.linkText("Terms of Service")).click();

//     handles = driver.getWindowHandles();
//     driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
//     WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
//     status = TOSHeading.getText().equals("Terms of Service");
//     if (!status) {
//         logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading failed", status ? "PASS" : "FAIL");
//         takeScreenshot(driver, "Failure", "TestCase9");
//         logStatus("End TestCase",
//                 "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
//                 status ? "PASS" : "FAIL");
//     }

//     driver.close();
//     driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
//     driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

//     logStatus("End TestCase",
//     "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
//     "PASS");
//     takeScreenshot(driver, "EndTestCase", "TestCase9");

//     return status;
// }


    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

}

