package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestContext;
import org.testng.Reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class Report {

	private WebDriver driver;
	private ITestContext context;
	private ExtentReports extent;
	private ExtentTest test;
	
	

	public Report( ITestContext general_Data) {
		
		this.context = general_Data;
		
		this.extent = ExtentReportInitializer.getExtentReport(Util.getParentdir(general_Data.getOutputDirectory()) + Util.getFileSeparator() + "ReportExtent.html");
		
		this.test = extent.createTest(general_Data.getCurrentXmlTest().getName(), general_Data.getCurrentXmlTest().getParameter("Description"));
		//add categories
		this.test.assignCategory(general_Data.getCurrentXmlTest().getParameter("Application"));
		String JiraId = general_Data.getCurrentXmlTest().getParameter("JiraTestKey")==null ?"JIRA-000":general_Data.getCurrentXmlTest().getParameter("JiraTestKey");
		this.test.assignAuthor(JiraId);
	
		if(App.getSuiteRerun()) {
			HashMap<String, ExtentTest> testMap = App.getTestMap();
			if(testMap.containsKey(general_Data.getCurrentXmlTest().getName())) {
				ExtentTest testFirstRun = testMap.get(general_Data.getCurrentXmlTest().getName());
				this.extent.removeTest(testFirstRun);
				App.addTestToMap(general_Data.getCurrentXmlTest().getName(),test);
			}
			else{
				App.addTestToMap(general_Data.getCurrentXmlTest().getName(),test);
			}
		}
		
	}

	public void setDriver(WebDriver driver2){
		this.driver = driver2;
	}
	
	public ExtentReports getExtent(){
		return extent;
	}
	public ExtentTest getExtentTest(){
		return test;
	}
	
	public void setExtentTest(ExtentTest childtest){
		this.test = childtest;
		
	}
	public  void updateTestLog(String stepName, String description, Status stepStatus) {
		
		String device = context.getCurrentXmlTest().getParameter("Device");
		String singleBrowser = context.getCurrentXmlTest().getParameter("SingleBrowser");
		List<String> paths= new ArrayList<String>();
		paths = getScreenshotPath(stepName);
		String ssPath = paths.get(0);
		String relPath = paths.get(1);
		com.aventstack.extentreports.Status extentStatus;

		switch(stepStatus){
		case DONE:
		case FAIL:
		case FATAL:
		case PASS:

		case SCREENSHOT:
		case WARNING:
			if(stepStatus.toString().equals("SCREENSHOT")||stepStatus.toString().equals("DONE")){
				extentStatus = com.aventstack.extentreports.Status.PASS;}
			
			else{
				extentStatus = com.aventstack.extentreports.Status.valueOf(stepStatus.toString());}
		
			if(device.equalsIgnoreCase("Local")){
				takeScreenshotLocal(ssPath);}
			else{
				takeScreenshotMobile(ssPath);}
			Reporter.log(description + "\t\t" +"<a href='" + relPath + "'>" + stepStatus.toString() + "</a>");
			
			
			
			MediaEntityModelProvider screenshotBuilder = null;
			try {
				screenshotBuilder = MediaEntityBuilder.createScreenCaptureFromPath(relPath).build();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			

			
			test.log(extentStatus, stepName + " : " + description,  screenshotBuilder );
			
			

			break;

		case DEBUG:
		default:
			Reporter.log(description + "\t\t" + stepStatus.toString() );
	
			test.log(com.aventstack.extentreports.Status.INFO, description);
		
			break;

		}

	}
	public void updateTestLogNoScreenshot(String stepName, Throwable throwable, Status stepStatus) {
		
		com.aventstack.extentreports.Status extentStatus = com.aventstack.extentreports.Status.valueOf(stepStatus.toString());
		
		Reporter.log(stepStatus.toString() + throwable );
		
		test.log(extentStatus, stepName );
		test.log(extentStatus, throwable);
		
		
		
		
		}
	
	public void updateTestLogMethodPassed(String stepName, String description, Status stepStatus) {
	
		com.aventstack.extentreports.Status extentStatus = com.aventstack.extentreports.Status.valueOf(stepStatus.toString());
		
		Reporter.log(stepStatus.toString() + description );
	
		test.log(extentStatus, stepName +" : "+ description );
	
	}

	private void takeScreenshotLocal(String ssPath) {
		driver.manage().window().maximize();
		try{
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(ssPath), true);
		}catch (Exception e) {
			e.printStackTrace();
			
			
		}

	}


	private List<String> getScreenshotPath(String stepName) {
		List<String> paths= new ArrayList<String>();
		String path;
		String screenshottime = (new SimpleDateFormat("mm_ss_SSS").format(new Date()));
		
		new  File(context.getOutputDirectory() + Util.getFileSeparator() + "Screenshots" + Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")).mkdirs();

		path = context.getOutputDirectory() + Util.getFileSeparator() + "Screenshots" + Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")
		+ Util.getFileSeparator() + screenshottime
		+  "_" +stepName.replaceAll("\\W+", "") + ".jpg";

		String relativePath = context.getSuite().getName() + Util.getFileSeparator()  +"Screenshots"+ Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")
		+ Util.getFileSeparator() + screenshottime
		+  "_" +stepName.replaceAll("\\W+", "") + ".jpg";

		paths.add(path);
		paths.add(relativePath);
		return paths;
	}


	protected void takeScreenshotMobile(String screenshotPath)
	{	
		File scrFile = null;

		try{
			
			WebDriver augmentedDriver = new Augmenter().augment(driver);
			scrFile = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			FileUtils.copyFile(scrFile, new File(screenshotPath), true);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	public void endExtentTest() {

		ArrayList<String> testDetails = new ArrayList<String>();

		com.aventstack.extentreports.Status testStat = test.getModel().getStatus();
		testDetails.add(testStat.toString());

		String runDur = test.getModel().getRunDuration();
		testDetails.add(runDur);


		String testApp = test.getModel().getCategoryContext().get(0).getName();
		testDetails.add(testApp);

		String testName = test.getModel().getName();

		ReportsExcelModification.addResult(testName,testDetails );
		if(test.getModel().getStatus()==com.aventstack.extentreports.Status.FAIL && App.getEmailBoolean()){
			Email.sendEmail(test.getModel());
		}
		if(test.getModel().getStatus()==com.aventstack.extentreports.Status.FAIL)
			App.addFailedTest(context.getCurrentXmlTest().getName());
	}

	public void updateTestLogNoScreenshot(String stepName, String string, Status stepStatus) {
	
		com.aventstack.extentreports.Status extentStatus = com.aventstack.extentreports.Status.valueOf(stepStatus.toString());

		Reporter.log(stepStatus.toString() + string );
	
		test.log(extentStatus, stepName +":" + string );
		


	}

	
public void addFileToLog(String stepName, String stringWrittenToFile , String description, Status stepStatus) {

		
		List<String> paths= new ArrayList<String>();
		paths = getResponsePath(stepName);
		String ssPath = paths.get(0);
		String relPath = paths.get(1);
		com.aventstack.extentreports.Status extentStatus;

		switch(stepStatus){
		case DONE:
		case FAIL:
		case FATAL:
		case PASS:

		case SCREENSHOT:
			if(stepStatus.toString().equals("SCREENSHOT")||stepStatus.toString().equals("DONE")){
				extentStatus = com.aventstack.extentreports.Status.PASS;}
			
			else{
				extentStatus = com.aventstack.extentreports.Status.valueOf(stepStatus.toString());
				}
			
			Reporter.log(description + "\t\t" +"<a href='" + relPath + "'>" + stepStatus.toString() + "</a>");
					
			
			saveResponseLocal(ssPath, stringWrittenToFile);
			

			
			test.log(extentStatus, description + "<br><a href=" + relPath +" data-featherlight=\"iframe\" data-featherlight-iframe-width = 85vw data-featherlight-iframe-height = 85vh >"
			+ stepName+"</a></br>");
			
			

			break;

		case DEBUG:
		default:
			Reporter.log(description + "\t\t" + stepStatus.toString() );
		
			test.log(com.aventstack.extentreports.Status.INFO,stepName + " : " + description);
			
			break;

		}

	
	}


	private List<String> getResponsePath(String stepName) {
		List<String> paths= new ArrayList<String>();
		String path;
		String screenshottime = (new SimpleDateFormat("mm_ss_SSS").format(new Date()));

			
		new  File(context.getOutputDirectory() + Util.getFileSeparator() + "XMLresponse" + Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")).mkdirs();

		path = context.getOutputDirectory() + Util.getFileSeparator() + "XMLresponse" + Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")
		+ Util.getFileSeparator() + screenshottime
		+  "_" +stepName.replaceAll("\\W+", "");

		String relativePath = context.getSuite().getName() + Util.getFileSeparator()  +"XMLresponse"+ Util.getFileSeparator() 
		+ context.getCurrentXmlTest().getName().replaceAll("\\W+", "_")
		+ Util.getFileSeparator() + screenshottime
		+  "_" +stepName.replaceAll("\\W+", "") ;

		paths.add(path);
		paths.add(relativePath);
		return paths;
	}

	private void saveResponseLocal(String ssPath, String response) {
		
		try{
			FileWriter writer = new FileWriter(ssPath,false);
			writer.write( response);	    
			writer.close();


		}catch (Exception e) {
			e.printStackTrace();

			
		}

	}

		
	}




