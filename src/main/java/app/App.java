package app;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import com.aventstack.extentreports.ExtentTest;

import org.testng.xml.XmlTest;

import xmlGeneration.GenerateXMLsuite;
import xmlGeneration.database.xmlGenerationDatabase;
import xmlGeneration.excel.xmlGenerationExcel;




/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */
public class App 
{
	private static String timeDir;
	protected static String reportEnv;
	protected static String excelRunEnv;
	protected static String parallelModeReport;
	protected static String dataSetNum;

	protected static Boolean shouldSendEmail;
	public static String SuiteName;
	public static HashMap<List<String>, List<XmlInclude>> businessFlowMap = new HashMap<List<String>, List<XmlInclude>>();
	protected static Boolean databaseRun = false;
	protected static Boolean databaseRunMixed = false;
	public static HashMap<String,HashMap<String, List<String>>> objectsTable = new HashMap<String,HashMap<String,List<String>>>();

	protected static Boolean suiteRerun = false;

	protected static HashMap<String,ExtentTest> testMap = new HashMap<String,ExtentTest>();
	protected static List<String> failedTestList = new ArrayList<String>();
	
	private static Properties threadProperties = new Properties();
	private static Integer suiteThreadPoolSize = 3;
	private static Integer testParallelThread = 5;
	private static Integer passPercentageRurun = 75;

	public static void main( String[] args ) throws IOException, FrameworkException
	{

		
		//add setting of thread count with a property file
		FileInputStream fileInStream = null;
		try {
					File file = new File(System.getProperty("user.dir")+ Util.getFileSeparator() + "configProperties" + Util.getFileSeparator() + "miscSettings");
					fileInStream = new FileInputStream(file);
					threadProperties.load(fileInStream);
		} catch (Exception e ) {
				
					threadProperties.put("suiteThreadPoolSize", "3");
					threadProperties.put("testParallelThread", "5");
					threadProperties.put("passPercentageRurun", "75");
		}
		
		try {
			suiteThreadPoolSize = Integer.valueOf(threadProperties.getProperty("suiteThreadPoolSize","3"));
		}
		catch(NumberFormatException ex) {

			suiteThreadPoolSize = 3;
		}
		try {
			testParallelThread = Integer.valueOf(threadProperties.getProperty("testParallelThread","5"));
		}
		catch(NumberFormatException ex) {
			testParallelThread = 5;
		}
		try {
			passPercentageRurun = Integer.valueOf(threadProperties.getProperty("passPercentageRurun","75"));
		}
		catch(NumberFormatException ex) {	
			passPercentageRurun = 75;
		}
		
		
		if(args.length>6 && args[6].equalsIgnoreCase("true")) {
			suiteRerun = true;
		}

		List<XmlSuite> suite = new ArrayList<XmlSuite>();
		
		//pass args to generateXML
		//args[0] = Test Suite in DriverSheet
		//args[1] = Environment in DriverSheet
		//args[2] = Parallel in driversheet
		//args[3] = Datasheet Number
		//args[4] = send email
		//args[5] = database true false
		//args[6] = rerun Failed tests
		GenerateXMLsuite suiteGen;
		if(args.length>5 && (args[5].equalsIgnoreCase("true")||args[5].equalsIgnoreCase("mixed"))){
			databaseRun = true;
			if(args[5].equalsIgnoreCase("mixed")) {
				databaseRunMixed = true;
			}
			suiteGen = ((GenerateXMLsuite) new xmlGenerationDatabase(args));
		}
		else{
			suiteGen = ((GenerateXMLsuite) new xmlGenerationExcel(args));
		}

	
		long startTime = 0;
		long endTime = 0 ;
		try{

			startTime = System.nanoTime();
			suite = suiteGen.generateSuites();
			endTime = System.nanoTime();	
		}
		catch(Exception ex){
			suite = exceptionSuite(ex);

		}
		long duration = (endTime - startTime); 
		System.out.println("XML GEN COMPLETE");
		System.out.println("Duration of XML + ObjectsTable(if database) generation in nanoseconds  : " + duration);


	
		TestNG testng = new TestNG();

	
		String currentDirPath = new File(System.getProperty("user.dir")).getAbsolutePath();
		String StartTime = testStartTime.getTime();
		
		String outputdir = currentDirPath + Util.getFileSeparator() + "History" + Util.getFileSeparator() + StartTime ;
		String outputdirPar = currentDirPath + Util.getFileSeparator() + "LastRun_test-output";
		String historyDir = currentDirPath + Util.getFileSeparator() + "History";
		timeDir = outputdir;
		new File(historyDir).mkdirs();
		new File(outputdirPar).mkdirs();

		

		try{
			
			FileUtils.cleanDirectory(new File(outputdirPar));
		}
		catch(Exception Ex){
			System.out.println("Last Run Directory Open and Not Deleted");
		}

	
		testng.setOutputDirectory(outputdir);


		testng.setXmlSuites(suite);
				
		
		testng.setSuiteThreadPoolSize(suiteThreadPoolSize);

		testng.run();


		if(suiteRerun && (ExtentReportInitializer.getPassPercentage() < passPercentageRurun)){

			int dataSetInt = Integer.parseInt(dataSetNum);
			dataSetInt++;
			dataSetNum = String.valueOf(dataSetInt);
		
			TestNG testngRerun = new TestNG();
			List<XmlSuite> suiteRerun = new ArrayList<XmlSuite>();
			try {
				suiteRerun = suiteGen.getRerunSuites();
			}
			catch(Exception ex){
				suite = exceptionSuite(ex);

			}
			testngRerun.setOutputDirectory(outputdir);
			testngRerun.setXmlSuites(suiteRerun);
			testngRerun.setSuiteThreadPoolSize(3);
			testngRerun.run();
		
			
		}
		//generate junit Report
		try {
			htmlToJunit.generateJunitFromHtml();
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
			System.out.println("HTML Junit Report Not Generated");
		}
		
		
		suiteListener.openReport();
		FileUtils.copyDirectoryToDirectory(new File(outputdir), new File(outputdirPar));

		System.out.println("duration of XML + ObjectsTable(if database) generation in nanoseconds  : " + duration);

	}










	public static String geTimeDir() {

		return timeDir;
	}

	private static List<XmlSuite> exceptionSuite(Exception e) {

	
		reportEnv = reportEnv == null? "Env Not Set" : reportEnv;
		parallelModeReport = parallelModeReport == null? "Parallel Not Set" : parallelModeReport;
		dataSetNum = dataSetNum == null? "DataSet Not Set" : dataSetNum;
	
		List<XmlSuite> suite = new ArrayList<XmlSuite>();
		List<XmlTest> testList = new ArrayList<XmlTest>();
		List<XmlClass> classes = new ArrayList<XmlClass>();
		XmlClass funcComp = new XmlClass("functions.AppTest");	
		XmlTest testname = new XmlTest();
		String nameXML;
		if( e.getMessage() !=null){

			nameXML =  e.getMessage();
		}
		else{
			if(e instanceof FrameworkException)
				nameXML =  ((FrameworkException)e).getValue();
			else{
				if(e.getCause()!= null)
					nameXML = e.getCause().toString();
				else
					nameXML = "UnknownError";
			}
		}

		List<XmlInclude> includedMethods = new ArrayList<XmlInclude>();
		XmlInclude element = new XmlInclude("DataSheetMissing", 0);
		includedMethods.add(0, element);
		funcComp.setIncludedMethods(includedMethods);
		String nameClean = nameXML.replaceAll("[^a-zA-Z0-9]", "_");
		testname.setName(nameClean);
		testname.addParameter("Description", "Data configuration Error");
		testname.addParameter("Application", "ConfigError");
		testname.addParameter("Error", "Config error while making XML");
		testname.addParameter("Iteration", "1");
		testname.addParameter("Device", "Local");
		testname.addParameter("Test_Case", nameClean);


		XmlSuite ExSuite = new XmlSuite();
		ExSuite.addListener("app.Listener");
		ExSuite.addListener("app.suiteListener");
		ExSuite.addListener("app.jUnitReportListener");
		testname.setSuite(ExSuite );
		classes.add(funcComp);
		testname.setXmlClasses(classes);

		testList.add(testname);

		ExSuite.setTests(testList);

		ExSuite.setFileName("ErrorSuite.xml");
		e.printStackTrace();
		suite.add(ExSuite);
		return suite;
	}



	public static String getReportEnv() {
	
		return reportEnv;
	}



	public static String getParralelText() {
	
		return parallelModeReport;
	}

	public static String getDataSheetNum(){
		return dataSetNum;
	}

	public static Boolean getEmailBoolean(){
		return shouldSendEmail;
	}

	public static String getRunSuiteName(){
		return SuiteName;
	}
	public static Boolean getDatabaseRun() {
		return databaseRun;
	}
	
	public static Boolean getDatabaseRunMixed() {
		return databaseRunMixed;
	}
	
	public static Boolean getSuiteRerun() {
		return suiteRerun;
	}

	public static void addTestToMap(String name , ExtentTest test) {
		testMap.put(name, test);
	}

	public static HashMap<String, ExtentTest> getTestMap() {
		return testMap;
	}
	
	public static void addFailedTest(String testName) {
		failedTestList.add(testName);
	}
	
	public static  List<String> getFailedTestList() {
		return failedTestList;
	}
	
	public static Integer getTestParallelThread() {
		return testParallelThread;
	}
	
}
