package functions;

import app.*;

import app.ExcelDataAccess;
//import app.FrameworkException;
import app.Status;
import app.Util;
import app.dataTable;
import app.deviceEnum;
import app.Report;
import pomUtility.*;
import pomUtility.deviceView.deviceViewEnum;
import xmlGeneration.database.xmlGenerationDatabase;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;

import com.intuit.karate.CallContext;
import com.intuit.karate.ScriptEnv;
import com.intuit.karate.cucumber.CallType;
import com.intuit.karate.cucumber.CucumberUtils;
import com.intuit.karate.cucumber.FeatureSection;
import com.intuit.karate.cucumber.FeatureWrapper;
import com.intuit.karate.cucumber.KarateBackend;
import com.intuit.karate.cucumber.ScenarioOutlineWrapper;
import com.intuit.karate.cucumber.ScenarioWrapper;

import app.App;
import app.ExtentCucumberFormatter;
import functions.AppTest;
import java.util.Map;
import functions.AppTest;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class AppTest 
{
	//String testName parameter
	public AppTest(  )
	{
	}

	public AppTest(Object object) {
		General_Data = ((AppTest) object).getContext();
		driver = ((AppTest) object).getDriver();
		report = ((AppTest) object).getReport();
		isfailed = ((AppTest) object).getFailed();
		deviceView = ((AppTest) object).getDeviceView();
	}

	protected WebDriver driver;
	protected ITestContext General_Data;
	protected app.Report report;

	public deviceView.deviceViewEnum deviceView= null;

	private boolean isfailed = false;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @return 
	 */

	public boolean getFailed() {
		return this.isfailed;
	}

	public void setFailed(boolean set){
		isfailed = set;
	}

	public AppTest getInstance(){
		return this;
	}

	public WebDriver getDriver(){
		return this.driver;
	}

	public ITestContext getContext(){
		return this.General_Data;
	}

	public Report getReport(){
		return this.report; 
	}

	public void screenshotFailure(ITestResult result) {

		report.updateTestLog("test",  "Already Logged in", Status.FAIL);

	}

	@BeforeMethod
	public void beforeMethod(ITestResult result, ITestContext context)  {
		
		if(isfailed){
			throw new SkipException("Testing skip.");
			}
		

	}



	public void makeDriver(){
		try{
			driver = null;
			browserEnum browserName = browserEnum.valueOf(dataTable.getData(General_Data,"Browser"));
			String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
			Pattern resolutionPattern = Pattern.compile("\\d+x\\d+");

			switch(browserName){ 

			case Firefox:
				System.setProperty("webdriver.gecko.driver", currentDirectoryPath + Util.getFileSeparator() + "WebDrivers" + Util.getFileSeparator() + "geckodriver.exe");
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				if(getData("headless")!=null && getData("headless").equalsIgnoreCase("true")) {
					firefoxOptions.setHeadless(true);
					if(getData("res")!=null) {
						Matcher resMatcher = resolutionPattern.matcher(getData("res"));
						if(resMatcher.find()) {
							firefoxOptions.addArguments("window-size="+getData("res"));
						}
						else {
							firefoxOptions.addArguments("window-size=1920x1080");
						}
					}
					else
						firefoxOptions.addArguments("window-size=1920x1080");
				}
				driver = new FirefoxDriver(firefoxOptions);
				break;	
			case InternetExplorer:
				
				System.setProperty("webdriver.ie.driver", currentDirectoryPath + Util.getFileSeparator() + "WebDrivers" + Util.getFileSeparator() + "IEDriverServer.exe");
				InternetExplorerOptions ieOptions = new InternetExplorerOptions();
				ieOptions.introduceFlakinessByIgnoringSecurityDomains();
				
				driver = new InternetExplorerDriver(ieOptions);
				break;
			case Safari:
				driver = new SafariDriver();
				break;
			default:
			case Chrome:
				System.setProperty("webdriver.chrome.driver", currentDirectoryPath + Util.getFileSeparator() + "WebDrivers" + Util.getFileSeparator() + "chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();			
				options.addArguments("disable-infobars");
				HashMap<String, Object> chromePreferences = new HashMap<String, Object>();
				chromePreferences.put("credentials_enable_service", false);
				chromePreferences.put("profile.password_manager_enabled", false);
				options.setExperimentalOption("prefs", chromePreferences);
				if(getData("headless")!=null && getData("headless").equalsIgnoreCase("true")) {
					options.setHeadless(true);
					if(getData("res")!=null) {
						Matcher resMatcher = resolutionPattern.matcher(getData("res"));
						if(resMatcher.find()) {
							options.addArguments("window-size="+getData("res"));
						}
						else
							options.addArguments("window-size=1920x1080");
					}
					else
						options.addArguments("window-size=1920x1080");

				}
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				
				driver = new ChromeDriver(options);
				break;
			}
		}
		catch(Exception ex){
			report.updateTestLogNoScreenshot("Make driver Failed",ex, Status.FAIL);
			throw ex;
		}

	}

	@Test
	public void BusinessFlowNotFound(){
		report.updateTestLogNoScreenshot("BusinessFlow", "Test Case not found in Business Flow" , Status.FAIL);
		Assert.fail(" Test Case not found in Business Flow");
	}

	@Test
	public void DataSheetMissing(){
		report.updateTestLogNoScreenshot("Data Sheet", "Data Sheet Error or Missing" , Status.FAIL);
		Assert.fail(" Test Case not found in Business Flow");
	}

	@BeforeTest 
	public void testSetUp( ITestContext context) throws UnsupportedEncodingException{
		try{
			General_Data = context;
			driver = null;
			
			report = new app.Report(General_Data);
			
			if(context.getCurrentXmlTest().getParameter("Error") != null){
				report.updateTestLogNoScreenshot("Test Set Up", new SkipException(context.getCurrentXmlTest().getParameter("Error")), Status.FAIL);

				throw new SkipException("Error making xml");
			}


			String singleBrowser = dataTable.getData(General_Data, "SingleBrowser");
		

			if(singleBrowser == null){

				singleBrowser = "false";
			}

			if(singleBrowser.equalsIgnoreCase("true")){
				if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("make")){
					deviceEnum mobileDevice = deviceEnum.valueOf(dataTable.getData(General_Data,"Device"));
					switch(mobileDevice){

					case S8:

					case S5:

					case ipad:

					case iphone:
						makePerfectoDriver(mobileDevice);
						break;
					case Services:
						break;
					default:
					case Local:
						makeDriver();
						break;

					}

					report.setDriver(driver);
					
				}
				else{
					
					driver = remoteDriverStore.getMadeDriver(dataTable.getData(General_Data, "DriverCreated"));
					report.setDriver(driver);
					if(driver.equals(null)){
						report.updateTestLogNoScreenshot("Get driver Failed","Driver Null", Status.FAIL);
					}
				}

			}
			else if(singleBrowser.equalsIgnoreCase("debug")){
				if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("make")){
					deviceEnum mobileDevice = deviceEnum.valueOf(dataTable.getData(General_Data,"Device"));
					switch(mobileDevice){

					case S8:

					case S5:

					case ipad:

					case iphone:
						makePerfectoDriver(mobileDevice);
						break;
					case Services:
						break;

					default:
					case Local:
						makeDriver();
						break;

					}

					report.setDriver(driver);
					try {
						remoteDriverStore.writeDriverToFile(driver, General_Data);
					} catch (IOException e) {
				
						e.printStackTrace();
					}

				
				}
				else{
					try {
					
						driver = remoteDriverStore.readDriverFromFile(driver);
						report.setDriver(driver);
					} catch (ClassNotFoundException e) {
						
						e.printStackTrace();
						report.updateTestLogNoScreenshot("Get driver Failed",e, Status.FAIL);
					} catch (IOException e) {
						
						e.printStackTrace();
						report.updateTestLogNoScreenshot("Get driver Failed",e, Status.FAIL);
					}
				}
				
			}
			else{
				deviceEnum mobileDevice = deviceEnum.valueOf(dataTable.getData(General_Data,"Device"));
				switch(mobileDevice){

				case S8:

				case S5:

				case ipad:

				case iphone:
					makePerfectoDriver(mobileDevice);
					break;
				case Services:
						break;
				default:
				case Local:
					makeDriver();
					break;

				}

				report.setDriver(driver);
				

			}	
			System.out.println(General_Data.getCurrentXmlTest().getName());
		}
		catch(Exception e){
			report.updateTestLogNoScreenshot("Make Driver_Test-SetUp", e, Status.FAIL);
			throw e;
		}

	}

	public void makePerfectoDriver(deviceEnum mobileDevice) throws UnsupportedEncodingException{
		driver = null;
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		String user = URLEncoder.encode("", "UTF-8");
		String password =URLEncoder.encode("", "UTF-8");
		String PerfectoHost = "";			

		switch (mobileDevice){
		case ipad:
			
			//new iPad device 10/23/17
			desiredCapabilities.setCapability("platformName", "iOS");
			desiredCapabilities.setCapability("platformVersion", "10.3");
			desiredCapabilities.setCapability("manufacturer", "Apple");
			desiredCapabilities.setCapability("model", "iPad 9.7");
			desiredCapabilities.setCapability("location", "NA-US-BOS");
			desiredCapabilities.setCapability("resolution", "2048 x 1536");
			desiredCapabilities.setCapability("network", "Verizon-United States of America");
			break;

		case iphone:
			//commenting to set capability as per  Perfecto 
		
			desiredCapabilities.setCapability("platformName", "iOS");
			desiredCapabilities.setCapability("platformVersion", "10.2.1");
			desiredCapabilities.setCapability("manufacturer", "Apple");
			desiredCapabilities.setCapability("model", "iPhone-6");
			desiredCapabilities.setCapability("location", "NA-US-BOS");
			desiredCapabilities.setCapability("resolution", "750 x 1334");
			desiredCapabilities.setCapability("network", "AT&T-United States of America");
			break;
		case S5:
	
			//			//commenting to set capability as per  Perfecto 
	
			desiredCapabilities.setCapability("platformName", "Android");
			desiredCapabilities.setCapability("platformVersion", "6.0.1");
			desiredCapabilities.setCapability("manufacturer", "Samsung");
			desiredCapabilities.setCapability("model", "Galaxy S5");
			desiredCapabilities.setCapability("location", "NA-US-BOS");
			desiredCapabilities.setCapability("resolution", "1080 x 1920");
			desiredCapabilities.setCapability("network", "Verizon-United States of America");
			break;
		case S8:
			desiredCapabilities.setCapability("platformName", "Android");
			desiredCapabilities.setCapability("platformVersion", "7.0");
			desiredCapabilities.setCapability("manufacturer", "Samsung");
			desiredCapabilities.setCapability("model", "Galaxy S8");
			desiredCapabilities.setCapability("location", "NA-US-BOS");
			desiredCapabilities.setCapability("resolution", "1440 x 2960");
			desiredCapabilities.setCapability("network", "Verizon-United States of America"); 
			break;
		case Local:
			break;
		default:
			break;


		}


		try {
			
			//new code for handling 404 exception
			
			// disable video and reporting for speed improvement
			desiredCapabilities.setCapability("outputReport", false);
			desiredCapabilities.setCapability("outputVideo", false);
			URL url = new URL("https://" + user + ':' + password + '@' + PerfectoHost +  "/nexperience/wd/hub");
			
			driver = new RemoteWebDriver(url, desiredCapabilities);
			

		} catch (Exception e) {
			report.updateTestLogNoScreenshot("Make driver Failed",e, Status.FAIL);
			
			e.printStackTrace();

		}
	}



	@AfterTest(alwaysRun=true) 
	public void CloseTest()
	{
		try{
			report.endExtentTest();
			if(dataTable.getData(General_Data,"Device").equalsIgnoreCase("Services")) {

			}
			else{
			if(dataTable.getData(General_Data, "SingleBrowser").equalsIgnoreCase("true")){
				if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("make")){
				
					remoteDriverStore.saveDriver(dataTable.getData(General_Data, "DriverCreated"),driver);

				}
				else if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("kill")){
				
					driver.quit();
				}
				else{
					
				}
			}
			else if(dataTable.getData(General_Data, "SingleBrowser").equalsIgnoreCase("debug")){
				if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("make")){
				

				}
				else if(dataTable.getData(General_Data, "Driver").equalsIgnoreCase("kill")){
				
					driver.quit();
				}
				else{
					

				}
			}
			else{
				
				driver.quit();
			}
		}
	}
		catch(Exception e){
		
			driver.quit();
		}
	
	}


	protected String Objects(String strObjName, String strSheetName ){
		
		if(!App.getDatabaseRun()) {
			String relativePath = new File(System.getProperty("user.dir")).getAbsolutePath();
			ExcelDataAccess objRepository;

			deviceEnum mobileDevice = deviceEnum.valueOf(dataTable.getData(General_Data,"Device"));
			switch(mobileDevice){

			case S8:
			case S5:
			case ipad:
			case iphone:

				objRepository = new ExcelDataAccess(relativePath , "ObjectsTable_Mobile.xls");
				if(objRepository.checkFileExists()){

				}
				else{
					objRepository = new ExcelDataAccess(relativePath , "ObjectsTable.xls");
				}
				break;
			case Local:
			case Services:
			default:
				objRepository = new ExcelDataAccess(relativePath , "ObjectsTable.xls");
				break;}

			objRepository.setDatasheetName(strSheetName);

			int intObjCol;
			int intPropCol;
			String strProperty = "";
			try {
				intObjCol = objRepository.getColumnNum("ObjectName", 0);



				intPropCol = objRepository.getColumnNum("Properties", 0);

				int intRowNum = objRepository.getRowNum(strObjName, intObjCol);
				strProperty = objRepository.getValue(intRowNum, intPropCol);
			}

			catch (Exception e) {
			
				report.updateTestLogNoScreenshot("ObjectTable",strObjName +" not found in "+strSheetName + " or sheet doesn't exist", Status.FAIL);
				e.printStackTrace();
			}
			return strProperty;	

		}
		else {
			String value = null;
			try {
				if(!App.objectsTable.containsKey(strSheetName)) {
					System.out.println(strSheetName + " : Not Loaded to Objects Table before Run");
					
					Long startAdd = System.nanoTime();
					xmlGenerationDatabase.addToObjectsTable(strSheetName);
					Long endAdd = System.nanoTime();
					Long addDur = (endAdd - startAdd);
					System.out.println("RunTime ObjectsTable Query in nanoseconds : " + addDur);
				}
				HashMap<String, List<String>> table = App.objectsTable.get(strSheetName);
				List<String> tableValues = table.get(strObjName);
				value = tableValues.get(0);
			}
			catch(Exception ex) {
				
				report.updateTestLogNoScreenshot("ObjectsTable", ex, Status.FAIL);
				report.updateTestLogNoScreenshot("ObjectTable",strObjName +" not found in "+strSheetName + " or sheet doesn't exist", Status.FAIL);

			}
			return value;
		}

	}

	protected void waiting(long i) throws InterruptedException {
		
		Thread.sleep(i);

	}



	protected String Objects(String ObjectName){
		return Objects(ObjectName ,getClass().getSimpleName());
	}

	protected String getData(String ColumnName){
		return dataTable.getData(General_Data, ColumnName);
	}


	public deviceViewEnum getDeviceView(){
		return deviceView;
	}

	public Map<String,Object> karateFeatureScenarioRunner(String appName,String featureName,String scenarioName, Map<String, Object> addObj){
		
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.putAll(General_Data.getCurrentXmlTest().getAllParameters());
		if(addObj != null) {
			vars.putAll(addObj);
		}
		File dir =  new File(System.getProperty("user.dir"));
		String currentDirectoryPath = dir.getAbsolutePath();

		File appFolder = new File(currentDirectoryPath + Util.getFileSeparator() +appName);
		
        File file = new File(currentDirectoryPath + Util.getFileSeparator() +appName + Util.getFileSeparator() +  featureName);
		ExtentCucumberFormatter testClassForm = new app.ExtentCucumberFormatter(this);	
        ScriptEnv scriptEnv = new ScriptEnv(null, appFolder, featureName, null, testClassForm);
        FeatureWrapper featureWrapper = FeatureWrapper.fromFile(file,scriptEnv );

        CallContext callContext = new CallContext(null, 0, vars, -1, false, false, null);

        ScriptEnv env = featureWrapper.getEnv();
        KarateBackend backend = CucumberUtils.getBackendWithGlue(env, callContext);
 
        for (FeatureSection section : featureWrapper.getSections()) {
            if (section.isOutline()) {
                ScenarioOutlineWrapper outline = section.getScenarioOutline();
                for (ScenarioWrapper scenario : outline.getScenarios()) {

                	CucumberUtils.call(scenario, backend, CallType.DEFAULT);
                }
            } else {
            	ScenarioWrapper scenario = section.getScenario();
            	if(scenario.getScenario().getVisualName().matches("(?i).*" +scenarioName))
            		CucumberUtils.call(section.getScenario(), backend, CallType.DEFAULT);
            	else
            		continue;
            	
            }
        }

        return backend.getStepDefs().getContext().getVars().toPrimitiveMap();
	}
	
	public Map<String,Object> karateFeatureScenarioRunner(String appName,String featureName,String scenarioName){
		Map<String, Object> vars = null;
		return karateFeatureScenarioRunner( appName, featureName, scenarioName, vars);
		
	}
	

}


















