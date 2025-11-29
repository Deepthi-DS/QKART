package QKART_TESTNG;

import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
public class ListnersTestNG  implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "onTestStart", result.getName());

    }
    @Override
    public void onTestSuccess(ITestResult result) {
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "onTestSuccess", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "onTestFailure", result.getName());
    }
    
}
