package xmlGeneration.excel;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import app.App;
import app.ExcelDataAccess;
import app.FrameworkException;
import app.Util;
import app.iterationMode;
import xmlGeneration.GenerateXMLsuite;

public class xmlGenerationExcel extends GenerateXMLsuite {
	
	HashMap<List<String>,List<String>> testSetUpMap = new HashMap<List<String>,List<String>>();
		
	public xmlGenerationExcel(String[] cmdLine) {
		super(cmdLine);
	}
	
	@Override
	public  List<XmlSuite> getTests( String RunSuite, String dataSheetNum, String[] args) throws IOException, FrameworkException {
		 

		 List<XmlSuite> suite = new ArrayList<XmlSuite>();


		 List<List<String>> StringNames = StringNamesGen();

		 StringNames = processTestIteration(StringNames);
		 
		 for(List<String> setUpList : StringNames) {
			 List<String> keyString = new ArrayList<String>();
			 keyString.add(0, setUpList.get(0));
			 keyString.add(1, setUpList.get(6));
				 
			 testSetUpMap.put(keyString, setUpList);
		 }

		 suite = getSuitesFromList(StringNames);
		 

		 return suite;
	}

	@Override
	public String getParallelMode() throws IOException {
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();

		ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
		runManagerAccess.setDatasheetName("DriverSheet");


		String app= runManagerAccess.getValue(10, 7);
		if(app == "")
			app = "NONE";
		return app;

	}

	@Override
	public String getSuiteName() throws IOException {

		String suiteN = null;

		if(args.length > 0){
			suiteN = args[0];
		}
		else{
			String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();

			ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
			runManagerAccess.setDatasheetName("DriverSheet");
			//String test_suite;
			try{
				suiteN= runManagerAccess.getValue(8, 7);
			}
			catch(Exception ex){
				suiteN= "RunManagerError" ;
			}
			if(suiteN == "")
				suiteN ="RunManagerError" ;
			
		}
		return suiteN;

	}

	
	//get datasheet number
	@Override
	public String getDataSetNumber() throws IOException {
	
		String dataSheet;
		try{
		if(args.length > 3){
			dataSheet = args[3];
			Double.parseDouble(dataSheet);
		}
		else{
			String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();

			ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
			runManagerAccess.setDatasheetName("DriverSheet");


			dataSheet =runManagerAccess.getValue(9, 7);
			Double.parseDouble(dataSheet);
		}
	}
	catch(Exception ex){
		System.out.println(ex.toString());
		dataSheet = "0";
	}
		return dataSheet;
			
	}

	@Override
	public Boolean getEmailBool() throws IOException {
		Boolean sendEmail = false;
		try{
			if(args.length > 4){
				String emailArg = args[4];
				sendEmail = Boolean.parseBoolean(emailArg);

			}
			else{
				String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();

				ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
				runManagerAccess.setDatasheetName("DriverSheet");


				String app= runManagerAccess.getValue(11, 7);
				sendEmail = Boolean.parseBoolean(app);
			}
		}
		catch(Exception ex){
			System.out.println(ex.toString());
			sendEmail = false;
		}
		return sendEmail;
	}

	
	public List<List<String>> StringNamesGen() throws IOException, FrameworkException {

		int intRunCol;
		int intNameCol;
		//add columns for iteration mode, start iteration and end iteration
		int intIterationCol;
		int intStartCol;
		int intEndCol;
		//add test_scenario and application from run manager
		int tsColNum;
		int appColNum;
		
		String RunSuite = getRunSuiteName();


		ExcelDataAccess runManagerAccess;
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
		runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
		runManagerAccess.setDatasheetName(RunSuite);
		
			intRunCol = runManagerAccess.getColumnNum("Execute", 0);
			intNameCol = runManagerAccess.getColumnNum("Test_Case", 0);
			//get columns for iteration mode, start iteration, end iteration
			intIterationCol= runManagerAccess.getColumnNum("Iteration_Mode", 0);
			intStartCol = runManagerAccess.getColumnNum("Start_Iteration", 0);
			intEndCol = runManagerAccess.getColumnNum("End_Iteration", 0);
			tsColNum = runManagerAccess.getColumnNum("Test_Scenario", 0);
			appColNum = runManagerAccess.getColumnNum("Application", 0);



			List<Integer> intRowNums = runManagerAccess.getRunNums(intRunCol);
			//add iteration columns and return a list of a list of strings
			//list index 0- name 1- iteration mode 2- start iteration 3 end iteration

			List<List<String>> StringNames = runManagerAccess.getRunNames(intRowNums, intNameCol, intIterationCol, intStartCol, intEndCol, tsColNum, appColNum);
			return StringNames;
	}

	

	@Override
	public String getEnv() throws FrameworkException, IOException {
		
		String excelRunEnviroment;
		String dataSheetNum = dataSetNum;

		if(args.length > 1){
			excelRunEnviroment = getCmdLineEnviroment(args[1]);

		}
		else{
			excelRunEnviroment = getEnvironment();

		}

		if(dataSheetNum.equals("0")){

		}
		else{
			excelRunEnviroment = excelRunEnviroment + "_" + dataSheetNum;
		}
		return excelRunEnviroment;
	}
		
	public static String getCmdLineEnviroment(String Environment) throws FrameworkException {
		String General_Data = null;

		reportEnv = Environment;
	
		if (Environment.equalsIgnoreCase("DEVA"))
		{
			General_Data="DEVA_Data";
		}
		else if (Environment.equalsIgnoreCase("DEVB"))
		{
			General_Data="DEVB_Data";
		}
		else if (Environment.equalsIgnoreCase("DEVC"))
		{
			General_Data="DEVC_Data";
		}
		else if (Environment.equalsIgnoreCase("QAA"))
		{
			General_Data="QAA_Data";
		}
		else if (Environment.equalsIgnoreCase("QAB"))
		{
			General_Data="QAB_Data";
		}
		else if (Environment.equalsIgnoreCase("PROD"))
		{
			General_Data="PROD_Data";
		}
		else if (Environment.equalsIgnoreCase("LAB"))
		{
			General_Data="LAB_Data";
		}
		else {
			
			throw new FrameworkException(new Exception(), "EnvironmentErrorInDriverSheet " + Environment + " Not Found");
		}
		return General_Data;
	}
	public static String getEnvironment() throws IOException, FrameworkException {
		// TODO Auto-generated method stub
		String Environment = null;
		String General_Data = null;

		/*New code to call Datatable from outside folder */
		//		String currentDirectoryPath = frameworkParameters.getRelativePath();
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
	
		ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
		runManagerAccess.setDatasheetName("DriverSheet");
		try{
			Environment = runManagerAccess.getValue(7, 7);
			reportEnv = Environment;
		}
		catch(Exception ex){
		
			throw new FrameworkException(ex, "EnvironmentErrorInDriverSheet");
		}
		

		if (Environment.equalsIgnoreCase("DEVA"))
		{
			General_Data="DEVA_Data";
		}
		else if (Environment.equalsIgnoreCase("DEVB"))
		{
			General_Data="DEVB_Data";
		}
		else if (Environment.equalsIgnoreCase("DEVC"))
		{
			General_Data="DEVC_Data";
		}
		else if (Environment.equalsIgnoreCase("QAA"))
		{
			General_Data="QAA_Data";
		}
		else if (Environment.equalsIgnoreCase("QAB"))
		{
			General_Data="QAB_Data";
		}
		else if (Environment.equalsIgnoreCase("PROD"))
		{
			General_Data="PROD_Data";
		}
		else if (Environment.equalsIgnoreCase("LAB"))
		{
			General_Data="LAB_Data";
		}
		else{
			throw new FrameworkException(new Exception(), "EnvironmentErrorInDriverSheet " + Environment + "Not Found");
		
		}
		return General_Data;
	}

	
	private List<List<String>> processTestIteration(List<List<String>> StringNames) {
		List<List<String>> updateNames = new  ArrayList<List<String>>(); 
		 Iterator<List<String>> names     = StringNames.iterator();
		 int finIn = 0; 
		 while (names.hasNext()) {
			 List<String> name = names.next();
			 iterationMode mode = iterationMode.valueOf(name.get(1)) ;
			 //add preserve order
			 //testname.setPreserveOrder("true");
			 //end changes
			 //add switch to deal with iteration modes
			 int start = Integer.parseInt(name.get(2));
			 int end = Integer.parseInt(name.get(3));
			 if(start > end)
				 end = start;
			 switch (mode){
			 case RunAllIterations:

				 for(int start1 = 1; start1 <= end; start1++, finIn++){
					 //inserts element at 4 and shifts all after one position
					 if(start1==1){
						 name.add(6, String.valueOf(start1));
						 name.add(7, String.valueOf(finIn));
					 }
					 //change to insert to next position without overwriting
					 else{
						 List<String> copy = new ArrayList<String>();
						 copy.addAll(name);
						 copy.remove(7);
						 copy.remove(6);
						 copy.add(6,String.valueOf(start1));
						 copy.add(7, String.valueOf(finIn));
						 updateNames.add(copy);
					 }

				 }
				 break;	
			 case RunRangeOfIterations:
				 for(; start <= end; start++ ,finIn++){
					 //inserts element at 4 and shifts all after one position
					 if(start==Integer.parseInt(name.get(2))){
						 name.add(6, String.valueOf(start));
						 name.add(7, String.valueOf(finIn));
					 }
					 //change to insert to next position without overwriting
					 else{
						 List<String> copy = new ArrayList<String>();
						 copy.addAll(name);
						
						 copy.remove(7);
						 copy.remove(6);
						 copy.add(6,String.valueOf(start));
						 copy.add(7, String.valueOf(finIn));
						 updateNames.add(copy);
					 }

				 }
				 break;
			 case RunOneIterationOnly:
			 default:
				 name.add(6, "1");
				 name.add(7, String.valueOf(finIn));
				 finIn++;
				 break;


			 }


		 }
		 StringNames.addAll(updateNames);

		 //sort StringNames here if wanted by index 5
		 //now index 7

		 StringNames = sortTestOrder(StringNames);
		 return StringNames;
	}
	
	protected static List<List<String>> sortTestOrder(List<List<String>> stringNames) {
		Collections.sort(stringNames, new Comparator<List<String>> () {
			@Override
			public int compare(List<String> a, List<String> b) {

				return Integer.valueOf(a.get(7)).compareTo(Integer.valueOf(b.get(7)));
			}
		});
		return stringNames;
	}

	
	private List<XmlSuite> getSuitesFromList(List<List<String>> runTestList) throws IOException, FrameworkException {
		
		
		String appName = null;
		XmlSuite ExSuite = new XmlSuite();
		 ExSuite.addListener("app.Listener");
		 ExSuite.addListener("app.suiteListener");
		 ExSuite.addListener("app.jUnitReportListener");
		
		 List<XmlSuite> suite = new ArrayList<XmlSuite>();
		List<XmlTest> testList = new ArrayList<XmlTest>();
		Iterator<List<String>> names1     = runTestList.iterator();
		 while (names1.hasNext()) {
			 List<XmlClass> classes = new ArrayList<XmlClass>();
			
			 XmlClass funcComp2 = null;
			 try{
				 funcComp2 = new XmlClass("com.FunctionalComponents");
			 }
			 catch(Exception ex){
				 throw new FrameworkException(ex,"FunctionalComponents class not found");
			 }
		
			 List<XmlInclude> includedMethods = new ArrayList<XmlInclude>();

			 List<String> name = names1.next();
			 XmlTest testname = new XmlTest();
			 String nameXML = name.get(0);


		
			 if(!name.get(6).equals("1")){
				 nameXML = nameXML.concat("_Iteration_" + name.get(6) );
			 }
		

			 testname.setName(nameXML);
			 testname.setSuite(ExSuite);


			 //fix this add test parameters per iteration number
			 String runManSheetname = name.get(4);
		
			 appName = name.get(5);
			 List<List<String>>  array = getParameters(appName, SuiteName, excelRunEnv, name.get(0), name.get(6), runManSheetname); 
			 Iterator<List<String>> outsideList = array.iterator();
			 while(outsideList.hasNext()){
				 List<String> keypair = outsideList.next();
				 testname.addParameter(keypair.get(0), keypair.get(1));
			 }

		
			 includedMethods = getMethods(appName, runManSheetname, excelRunEnv, name.get(0));

			 funcComp2.setIncludedMethods(includedMethods);
			
			 classes.add(funcComp2);
			 testname.setXmlClasses(classes);
			 testList.add(testname);
		 }



		 ExSuite.setTests(testList);
	
		 ExSuite.setFileName("virtual.xml");
		 String parallelMode = "NONE";
		 
		 //do this during suite creation
		 try{
			 if(args.length>2){
				 parallelMode = args[2];
			 }
			 else{
				 parallelMode = getParallelMode();
			 }

		 }
		 catch(Exception e){
			 parallelMode = "NONE";
		 }
		 finally{
			 try{
				 parallelModeReport = parallelMode;
				 switch(parallelMode){
				 case "TESTS":
				 case "NONE":
				 default:
					 ExSuite.setParallel(XmlSuite.ParallelMode.valueOf(parallelMode));
					 ExSuite.setName(SuiteName);
				
					//add tests parallel thread count
					 if(parallelModeReport.equalsIgnoreCase("tests")) {
						 ExSuite.setThreadCount(App.getTestParallelThread());
					 }
					 suite.add(ExSuite);
					 break;
				 case "SUITES":
					 suite = editsuite(ExSuite);
					 break;
				 }
			 }
			 catch(Exception ex){
				 throw new FrameworkException(ex, parallelMode);
			 }

		 }
		 return suite;
	}
	

	protected static List<List<String>> getParameters(String appName, String runSuite, String runEnviroment, String name, String iteration, String runManSheetname) throws IOException, FrameworkException {
		
		List<List<String>>  array = new  ArrayList<List<String>>();
		//rewrite get datasheet functions for iterations
		array = getDataSheetParameters(array, appName, runManSheetname, runEnviroment, name, iteration);
		if(array.get(0).contains("Error")){
			List<String> descrOnError = new ArrayList<String>();
			List<String> AppOnError = new ArrayList<String>();
			descrOnError.add("Description");
			descrOnError.add("Failed to find Test Case in Data Sheet");
			array.add(descrOnError);
			AppOnError.add("Application");
			AppOnError.add("DataError");
			array.add(AppOnError);
		}
		else{
			array = getRunManagerParamenters(array, runSuite, name );
		}



		return(array);

	}

	private static List<List<String>> getRunManagerParamenters(List<List<String>> array, String runSuite, String name) throws IOException, FrameworkException {
	
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
		ExcelDataAccess dataParam = new ExcelDataAccess(currentDirectoryPath, "Run Manager.xls");
		dataParam.setDatasheetName(runSuite);

		//add get testcase column
		int tc_colNum = dataParam.getColumnNum("Test_Case", 0);

		int DataRowNum = dataParam.getRowNum(name, tc_colNum);
		List<String> key_list = new ArrayList<String>();
		key_list = dataParam.getAllKeys(0, 0);
		Iterator<String> key_iterator = key_list.iterator();
		while(key_iterator.hasNext()){
			String nextKey = key_iterator.next();
			List<String>  key_value = new ArrayList<String>();
			int columnInd = dataParam.getColumnNum(nextKey, 0);
			String value = dataParam.getValue(DataRowNum,columnInd );
			if(nextKey != "" && value != ""){
				key_value.add(nextKey);
				key_value.add(value);
				array.add(key_value);
			}
		}

		return(array);
	}

	private static List<List<String>> getDataSheetParameters(List<List<String>> array, String appName, String runSuite, String runEnviroment, String name, String iteration) throws IOException, FrameworkException {
	
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
		ExcelDataAccess dataParam = new ExcelDataAccess(currentDirectoryPath + Util.getFileSeparator() + appName, runSuite + ".xls");
		dataParam.setDatasheetName(runEnviroment);

		//add get testcase column
		int tc_colNum = dataParam.getColumnNum("TC_ID", 0);
		int it_colNum = dataParam.getColumnNum("Iteration", 0);

		int DataRowNum = dataParam.getRowNumIteration(name, tc_colNum, iteration, it_colNum);
		//if data sheet does not have iteration number - then datarow num should be -1
		if (DataRowNum != -1){

			List<String> key_list = new ArrayList<String>();
			key_list = dataParam.getAllKeys(0, 0);
			Iterator<String> key_iterator = key_list.iterator();
			while(key_iterator.hasNext()){
				String nextKey = key_iterator.next();
				List<String>  key_value = new ArrayList<String>();
				int columnInd = dataParam.getColumnNum(nextKey, 0);
				String value = dataParam.getValue(DataRowNum,columnInd );
				if(nextKey != "" && value != ""){
					key_value.add(nextKey);
					key_value.add(value);
					array.add(key_value);
				}
			}
		}
		else{
			List<String>  key_value_error = new ArrayList<String>();
			key_value_error.add("Error");
			key_value_error.add("Iteration Value or Test Not Found in Data Sheet");
			array.add(key_value_error);

		}
		return(array);
	}

	protected static List<XmlInclude> getMethods(String appName, String runSuite, String runEnviroment, String name) throws IOException, FrameworkException {
		
		List<XmlInclude> runFunctions = new ArrayList<XmlInclude>();
		String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();
		ExcelDataAccess flow = new ExcelDataAccess(currentDirectoryPath + Util.getFileSeparator() + appName, runSuite + ".xls");
		flow.setDatasheetName("Business_Flow");

		int tc_column = flow.getColumnNum("TC_ID", 0);
		int row_num = flow.getRowNum(name, tc_column);
		List<String> method_list = new ArrayList<String>();
		if(row_num == -1){

			method_list.add("BusinessFlowNotFound");	
		}
		else{
			method_list = flow.getFlow(tc_column, row_num);
		}

		Iterator<String> method_iterator = method_list.iterator();
		//12/27/2016 add index counter
		int index = 0;

		while(method_iterator.hasNext()){

			String nextFunc = method_iterator.next();
			//12/27/2016 add index value
			
			XmlInclude funcs = new XmlInclude(nextFunc, index);
			index++;
			runFunctions.add(funcs);
		}

		return runFunctions;
	}
	
	 //create suite list during curing 
	 protected static List<XmlSuite> editsuite(XmlSuite suite1) {
		
			List<XmlSuite> suite = new ArrayList<XmlSuite>();
			List<XmlTest> testList =  suite1.getTests();
			Iterator<XmlTest> testIterator = testList.iterator();
			boolean addListenerInterceptor = false;
			

			while(testIterator.hasNext()){
				XmlTest nextTest = testIterator.next();
				
			
				String suiteName = (nextTest.getParameter("Application"))+"_"+(nextTest.getParameter("Test_Scenario"));
				if(suiteName.equalsIgnoreCase("_")){
					suiteName = "ErrorSuite";
				}
				try{
					if(nextTest.getParameter("SingleBrowser").equalsIgnoreCase("debug")&&(!nextTest.getParameter("Driver").equalsIgnoreCase("make"))){
						addListenerInterceptor = true;
					}
				}
				catch(Exception e){
					
				}

				boolean addSuite = true;
				for(XmlSuite indSuite : suite ){
					if(indSuite.getName().equals(suiteName)){
						addSuite = false;
					}
				}
				if(addSuite){
					XmlSuite SuiteCur = new XmlSuite();
					SuiteCur.setName(suiteName);
					SuiteCur.addListener(suite1.getListeners().get(0));
					SuiteCur.addListener(suite1.getListeners().get(1));
					SuiteCur.addListener(suite1.getListeners().get(2));
					if(addListenerInterceptor){
						SuiteCur.addListener("app.interceptDebug");
					}
					suite.add(SuiteCur);
				}
				for(XmlSuite indSuite : suite ){
					if(indSuite.getName().equals(suiteName)){
						indSuite.addTest(nextTest);
					}
				}

			}

			return suite;
		}

	@Override
	protected List<XmlSuite> processRerunSuite() throws IOException, FrameworkException {

		List<XmlSuite> suiteRerunRebuilt = new ArrayList<XmlSuite>();
		List<List<String>> testListString = new ArrayList<List<String>>();

		List<String> testList = App.getFailedTestList();
		for(String test : testList) {
			List<String> testParameters = new ArrayList<String>();
			//testSetUpMap
			String testName = test;
			Pattern iterationPattern = Pattern.compile(".*_Iteration_\\d+");
			Matcher m = iterationPattern.matcher(testName);
			String iterationNumber = null;
			if(m.matches()) {
				Pattern iterationNumberPattern = Pattern.compile("(?:.*_Iteration_)(\\d+)");
				Matcher digit = iterationNumberPattern.matcher(testName);
				if(digit.matches())
					iterationNumber = digit.group(1);
				testName = testName.replaceAll("_Iteration_\\d+", "");
			}
			else {
				iterationNumber = "1";
			}
			
			
			testParameters.add(0, testName);
			//iteration - 1 
			testParameters.add(1, iterationNumber);
			testListString.add(testSetUpMap.get(testParameters));
		}

		

		suiteRerunRebuilt = getSuitesFromList(testListString);

		return suiteRerunRebuilt;
	}
	
}
