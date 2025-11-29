package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import net.bytebuddy.build.Plugin.Factory.UsingReflection.Priority;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.experimental.theories.ParametersSuppliedBy;
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
import org.testng.annotations.*;
import org.testng.annotations.Test;

public class QKART_Tests {

     static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite(alwaysRun = true)
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
    @Test(priority = 1, description="Verify registration happens correctly", groups = {"Sanity_Test"})
    @Parameters({"TC1_userName","TC1_password"})
    public void TestCase01(String userName, String password) throws InterruptedException {
   Boolean status;
   // Visit the Registration page and register a new user
   Register registration = new Register(driver);
   registration.navigateToRegisterPage();
    status = registration.registerUser(userName,password, true);
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
@Test(priority = 2, description = "Verify re-registering an already registered user fails",groups = {"Sanity_Test"})
@Parameters({"TC1_userName","TC1_password"})
public void TestCase02(String userName, String password) throws InterruptedException {
   Boolean status;
   Register registration = new Register(driver);
   registration.navigateToRegisterPage();
   status = registration.registerUser(userName, password, true);

   // Save the last generated username
   lastGeneratedUserName = registration.lastGeneratedUsername;

   // Visit the Registration page and try to register using the previously
   // registered user's credentials
   registration.navigateToRegisterPage();
   status = registration.registerUser(lastGeneratedUserName, password, false);

   // If status is true, then registration succeeded, else registration has
   // failed. In this case registration failure means Success
   assertFalse(status, "Re-registration of existing user was successful");
}
@Test(priority = 3,description = "Verify the functionality of search text box",groups = {"Sanity_Test"})
@Parameters("TC3_ProductSearchFor")
public void TestCase03(String ProductSearchFor) throws InterruptedException {
    boolean status;

    // Visit the home page
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    // Search for the "yonex" product
    status = homePage.searchForProduct(ProductSearchFor);

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
       assertTrue(elementText.toUpperCase().contains(ProductSearchFor), " Test Case Failure. Test Results contains un-expected values: " + elementText); 
       
       
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
@Test(priority = 4, description = "Verify the existence of size chart for certain items and validate contents of size chart", groups ={"Regression _Test"})
@Parameters({"TC4_productSearchFor"})
public void TestCase04(String productSearchFor) throws InterruptedException {
   boolean status = false;

   // Visit home page
   Home homePage = new Home(driver);
   homePage.navigateToHome();

   // Search for product and get card content element of search results
   status = homePage.searchForProduct(productSearchFor);
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
@Test(priority = 5,description = "Verify that a new user can add multiple products in to the cart and Checkout",groups = {"Sanity_Test"})
@Parameters({"TC5_productSerachFor1", "TC5_productSearchFor2", "address"})
public void TestCase05(String productSearchFor1, String productSearchFor2, String address) throws InterruptedException {
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
   status = homePage.searchForProduct(productSearchFor1);
   homePage.addProductToCart(productSearchFor1);
   status = homePage.searchForProduct(productSearchFor2);
   homePage.addProductToCart(productSearchFor2);

   // Click on the checkout button
   homePage.clickCheckout();

   // Add a new address on the Checkout page and select it
   Checkout checkoutPage = new Checkout(driver);
   checkoutPage.addNewAddress(address);
   checkoutPage.selectAddress(address);

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
@Test(priority = 6, description="Verify that the contents of the cart can be edited", groups ={"Regression_Test"})
@Parameters({"TC1_userName","TC1_password","TC6_productSearchFor1","TC6_productSearchFor2","address"})
public void TestCase06(String userName, String password,String productSearchFor1, String productSearchFor2, String address) throws InterruptedException {
   Boolean status;
   Home homePage = new Home(driver);
   Register registration = new Register(driver);
   Login login = new Login(driver);

   registration.navigateToRegisterPage();
   status = registration.registerUser(userName ,password, true);
   assertTrue(status, "User registeration failed");
   lastGeneratedUserName = registration.lastGeneratedUsername;

   login.navigateToLoginPage();
   status = login.PerformLogin(lastGeneratedUserName, password);
   assertTrue(status, "User Login failed");

   homePage.navigateToHome();
   status = homePage.searchForProduct(productSearchFor1);
   assertTrue(status, "product not found Xtend");
   homePage.addProductToCart(productSearchFor1);

   status = homePage.searchForProduct(productSearchFor2);
   assertTrue(status, "product not found Yarine");
   homePage.addProductToCart(productSearchFor2);

   // update watch quantity to 2
   homePage.changeProductQuantityinCart(productSearchFor1, 2);

   // update table lamp quantity to 0
   homePage.changeProductQuantityinCart(productSearchFor2, 0);

   // update watch quantity again to 1
   homePage.changeProductQuantityinCart(productSearchFor1, 1);

   homePage.clickCheckout();

   Checkout checkoutPage = new Checkout(driver);
   checkoutPage.addNewAddress(address);
   checkoutPage.selectAddress(address);

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

@Test(priority = 7, description = "Verify that insufficient balance error is thrown when the wallet balance is not enough",groups = {"Sanity_Test"})
 @Parameters({"TC7_productSearchFor" ,"TC7_Qty"})
 public void TestCase07(String productSearchFor, String qty) throws InterruptedException {
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
status = homePage.searchForProduct(productSearchFor);
assertTrue(status, "Product not found");
homePage.addProductToCart(productSearchFor);
Integer qty_i = Integer.valueOf(qty);
homePage.changeProductQuantityinCart(productSearchFor,qty_i);

homePage.clickCheckout();

Checkout checkoutPage = new Checkout(driver);
checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

checkoutPage.placeOrder();
Thread.sleep(3000);

status = checkoutPage.verifyInsufficientBalanceMessage();
assertTrue(status, "Insufficient balance not verified");

}
@Test(priority = 8,description = "Verify that a product added to a cart is available when a new tab is added", groups ={"Regression_Test"})
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

@Test(priority = 9, description = "Verify that privacy policy and about us links are working fine", groups ={"Regression_Test"})
public static void TestCase09() throws InterruptedException {
    Boolean status = false;
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    String basePageURL = driver.getCurrentUrl();

    driver.findElement(By.linkText("Privacy policy")).click();
    status = driver.getCurrentUrl().equals(basePageURL);
    assertTrue(status, "On clicking on Privacy policy page moved to a new URL");

    Set<String> handles = driver.getWindowHandles();
    driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
    WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
    status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
    assertTrue(status, "Failed in verifing that the heading had Privacy Policy");

    driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
    driver.findElement(By.linkText("Terms of Service")).click();

    handles = driver.getWindowHandles();
    driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
    WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
    status = TOSHeading.getText().equals("Terms of Service");
    assertTrue(status, "Failed in verifying that the heading had About Us");
    

    driver.close();
    driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
    driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

}
@Test(priority = 10, description = "Verify that the contact us dialog works fine", groups ={"Regression_Test"})
@Parameters({"TC10_Name","TC10_Email","TC10_Message"})
public static void TestCase10(String Name , String Email, String Message) throws InterruptedException {
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    driver.findElement(By.xpath("//*[text()='Contact us']")).click();

    WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
    name.sendKeys(Name);
    WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
    email.sendKeys(Email);
    WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
    message.sendKeys(Message);

    WebElement contactUs = driver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

    contactUs.click();

    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(ExpectedConditions.invisibilityOf(contactUs));
}

@Test(priority = 11, description = "Ensure that the Advertisement Links on the QKART page are clickable",groups = {"Sanity_Test"})
@Parameters({"TC5_productSerachFor1", "address"})
public static void TestCase11(String productSearchFor1, String address) throws InterruptedException {
    Boolean status = false;

    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser("testUser", "abc@123", true);
    assertTrue(status, "User Registration failed");
    
    lastGeneratedUserName = registration.lastGeneratedUsername;

    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, "abc@123");
    assertTrue(status, "Login successful");

    Home homePage = new Home(driver);
    homePage.navigateToHome();

    status = homePage.searchForProduct(productSearchFor1);
    homePage.addProductToCart(productSearchFor1);
    homePage.changeProductQuantityinCart(productSearchFor1, 1);
    homePage.clickCheckout();

    Checkout checkoutPage = new Checkout(driver);
    checkoutPage.addNewAddress(address);
    checkoutPage.selectAddress(address);
    checkoutPage.placeOrder();
    Thread.sleep(3000);

    String currentURL = driver.getCurrentUrl();

    List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

    //status = Advertisements.size() == 3;
    assertEquals(3, Advertisements.size(), "Failed to locate the advertisments");
    

    WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
    driver.switchTo().frame(Advertisement1);
    driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
    driver.switchTo().parentFrame();

    //status = !driver.getCurrentUrl().equals(currentURL);
    assertNotEquals(driver.getCurrentUrl(),currentURL,"Advertisement 1 link was not clickable" );

    driver.get(currentURL);
    Thread.sleep(3000);

    WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
    driver.switchTo().frame(Advertisement2);
    driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
    driver.switchTo().parentFrame();

    assertNotEquals(driver.getCurrentUrl(),currentURL,"Advertisement 2 link was not clickable" );
    driver.get(currentURL);

}



    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    // public static void logStatus(String type, String message, String status) {

    //     System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
    //             message, status));
    // }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

