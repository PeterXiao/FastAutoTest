package yph.base;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import yph.utils.SleepUtil;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.NO_SIGN;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.RESET_KEYBOARD;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.UNICODE_KEYBOARD;

public class BaseTest {

    @Parameters({"node","appiumMainJs","port", "bootstrap_port", "chromedriver_port", "udid"})
    @BeforeSuite
    public void startServer(String node,String appiumMainJs,String port,String bootstrapPort, String chromeDriverPort,String udid) {
        if(!appiumMainJs.equals(""))
            AppiumServer.start(node,appiumMainJs,port,bootstrapPort, chromeDriverPort, udid);
    }

    @Parameters({"port", "platformName", "platformVersion", "deviceName", "appPackage", "appActivity","app"})
    @BeforeTest
    public void setUp(String appiumPort, String platformName, String platformVersion, String deviceName, String appPackage,
                      String appActivity,String app) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("noReset", true);
        caps.setCapability("app", app);
        caps.setCapability(APP_PACKAGE, appPackage);
        caps.setCapability(APP_ACTIVITY, appActivity);
        caps.setCapability(UNICODE_KEYBOARD, true);
        caps.setCapability(RESET_KEYBOARD, true);
        caps.setCapability(NO_SIGN, true);//表示不重签名app在设置为true的情况下
        if(Float.valueOf(platformVersion) >= 4.4)
            caps.setCapability (MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        addCap(caps);
        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:" + appiumPort + "/wd/hub"), caps);
        mThreadLocal.set(driver);
    }

    protected void addCap(DesiredCapabilities caps){}

    @AfterTest
    public void tearDown() throws Exception {
        mThreadLocal.get().quit();
    }

    @Parameters("port")
    @AfterSuite
    public void stopServer(String port) {
        AppiumServer.stop(port);
    }

    public static ThreadLocal<AndroidDriver> mThreadLocal = new ThreadLocal<>();
    protected AndroidDriver driver;

    @Parameters("udid")
    @BeforeClass
    public void findPage(String udid) {
        driver = mThreadLocal.get();
        SleepUtil.s(2000);
        PageFactory.initElements(driver, this);
    }
}
