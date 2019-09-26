package xmlGeneration.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;

import app.App;
import app.ExcelDataAccess;
import app.FrameworkException;
import app.iterationMode;
import xmlGeneration.GenerateXMLsuite;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class xmlGenerationDatabase extends GenerateXMLsuite{

	static Set<String> functionSet = new HashSet<String>();
	static Set<String> objectsPageSet = new HashSet<String>();
	static ArrayDeque<HashMap<String,String>> StringNames = new ArrayDeque<HashMap<String, String>>(400);




	public xmlGenerationDatabase(String[] cmdLine) {
		
		super(cmdLine);
	}

	@Override
	public String getParallelMode() throws IOException {
	
		return args[2];
	}

	@Override
	public String getSuiteName() throws IOException {

		return args[0];
	}

	//dataSheetnumber
	@Override
	public String getDataSetNumber() throws IOException {

		String dataSheetNum;

		try{

			dataSheetNum = args[3];
			Double.parseDouble(dataSheetNum);

		}
		catch(Exception ex){
			System.out.println(ex.toString());
			dataSheetNum = "1";
		}

		return dataSheetNum;
	}

	@Override
	public Boolean getEmailBool() throws IOException {
		
		return Boolean.parseBoolean(args[4]);

	}


	public ArrayDeque<HashMap<String, String>> StringNamesGen() throws IOException, FrameworkException {
		//output name, mode, start end, test_scenario, application
		
		//set size to 400
		ArrayDeque<HashMap<String, String>> test = new ArrayDeque<HashMap<String, String>>(400);

		
		if(!getDatabaseRunMixed()) {
			Connection conn = ConnectionDataBase.getConnection();
			ResultSet rs = null;
			String ececStoredProcRunList = "EXEC sp_getTestExecList ?, ?";
			SQLServerPreparedStatement pStmt = null;
			try {
				pStmt = (SQLServerPreparedStatement)conn.prepareStatement(ececStoredProcRunList);
				pStmt.setString(1, getReportEnv());
				pStmt.setString(2,  getRunSuiteName());
				pStmt.execute();


				rs = pStmt.getResultSet();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}


			


			try {
				while(rs.next()){
					HashMap<String,String> list = new HashMap<String,String>();
					String TestName = rs.getString("TestName");
					list.put("TestName", TestName);
					
					String Iteration_Mode = rs.getString("Iteration_Mode");
					list.put("Iteration_Mode", Iteration_Mode);
					
					String Start_Iteration = rs.getString("Start_Iteration"); 
					list.put("Start_Iteration", Start_Iteration);
				
					String End_Iteration = rs.getString("End_Iteration");
					list.put("End_Iteration", End_Iteration);
					
					String TestID = rs.getString("TestID");
					list.put("TestID", TestID);
					
					String Application = rs.getString("Application");
					list.put("Application", Application);
				
					String TestDescription = rs.getString("TestDescription");
					list.put("TestDescription", TestDescription);
					
					String Locale_MT = rs.getString("Locale_MT"); 
					list.put("Locale_MT", Locale_MT);
					
					String TestEnv = rs.getString("TestEnv");
					list.put("TestEnv", TestEnv);
					
					String SingleBrowser = rs.getString("Single_Browser");
					list.put("Single_Browser", SingleBrowser);
					
					String Driver = rs.getString("Driver");
					list.put("Driver", Driver);
				
					String DriverCreated = rs.getString("DriverCreated");
					list.put("DriverCreated", DriverCreated);
					

					String JiraTestKey = rs.getString("JiraTestKey");
					list.put("JiraTestKey",JiraTestKey);
					

					test.add(list);
				}
			} catch (SQLException e) {
		
				e.printStackTrace();
			}

			try {
				rs.close();
				pStmt.close();
				conn.close();		
			} catch (SQLException e) {
			
				e.printStackTrace();
			}

		}
		else {
			String reportEnv = getReportEnv();
			String runSuiteName = getRunSuiteName();
			
			String currentDirectoryPath = new File(System.getProperty("user.dir")).getAbsolutePath();

			ExcelDataAccess runManagerAccess = new ExcelDataAccess(currentDirectoryPath, "tcRunManDB.xls");
			runManagerAccess.setDatasheetName("Query");
			List<Integer> runList = runManagerAccess.getRunNumsMixed(reportEnv, runSuiteName );
			test = runManagerAccess.getRunTestsMixed(runList);
		}

		return test;
	}

	@Override
	public String getEnv() throws FrameworkException, IOException {
	
		reportEnv = args[1];
		return reportEnv;
	}


	public ArrayDeque<HashMap<String, String>> processTestIteration(ArrayDeque<HashMap<String, String>> stringNames) {
		

		//set size to 400
		ArrayDeque<HashMap<String, String>> updateNames = new  ArrayDeque<HashMap<String, String>>(400); 
		Iterator<HashMap<String, String>> names     = stringNames.iterator();
		int finIn = 0; 
		while (names.hasNext()) {

			HashMap<String, String> name = names.next();
			iterationMode mode = iterationMode.valueOf(name.get("Iteration_Mode")) ;

			//id and env
			List<String> businessFlowKey = new ArrayList<String>();
			List<XmlInclude> functionList = new ArrayList<XmlInclude>();
			businessFlowKey.add(name.get("TestID"));
			businessFlowKey.add(name.get("TestEnv"));
			Connection busConn = ConnectionDataBase.getConnection();
			ResultSet busFlow = null;
			String ececStoredBusProc = "EXEC sp_getBusinessFlow ?, ?";
			SQLServerPreparedStatement pStmtBus = null;
		
			try {
				pStmtBus = (SQLServerPreparedStatement)busConn.prepareStatement(ececStoredBusProc);
				pStmtBus.setString(1, reportEnv);
				pStmtBus.setInt(2, Integer.valueOf(name.get("TestID")));
				pStmtBus.execute();
				busFlow = pStmtBus.getResultSet();
				functionList = getMethods(busFlow);
				businessFlowMap.put(businessFlowKey, functionList);
			} catch (SQLException e2) {
		
				e2.printStackTrace();
			}


			try {
				busFlow.close();
				pStmtBus.close();
				busConn.close();
			} catch (SQLException e2) {
		
				e2.printStackTrace();
			}



			int start = Integer.parseInt(name.get("Start_Iteration"));
			int end = Integer.parseInt(name.get("End_Iteration"));
			if(start > end)
				end = start;
			switch (mode){
			case RunAllIterations:

				Connection iterationConnection = ConnectionDataBase.getConnection();
				ResultSet rs = null;
				String ececStoredAllIter = "EXEC sp_allIterationQuery ?, ?, ?";
				SQLServerPreparedStatement pStmtIter = null;
				

				try {
					pStmtIter = (SQLServerPreparedStatement)iterationConnection.prepareStatement(ececStoredAllIter);
					pStmtIter.setString(1, reportEnv);
					pStmtIter.setString(2,  dataSetNum);
					pStmtIter.setInt(3, Integer.valueOf(name.get("TestID")));
					pStmtIter.execute();


					rs = pStmtIter.getResultSet();
				} catch (SQLException e1) {
					
					e1.printStackTrace();
				}

				try {
					while(rs.next()){
						HashMap<String, String> nameNew = new HashMap<String, String>();
						String testName = name.get("TestName");

						String iterationNum = rs.getString(("Iteration")); 

						nameNew.putAll(name);
						nameNew.put("Iteration", String.valueOf(iterationNum));

						String Iteration = "";
						if(Integer.valueOf(nameNew.get("Iteration"))>1)
							Iteration = "_Iteration_"+nameNew.get("Iteration");


						//remove environment
						nameNew.put("TestName",nameNew.get("TestID")+"_"+testName+Iteration);
						updateNames.add(nameNew);
					}
				} catch (SQLException e) {
					
					e.printStackTrace();
				}

				try {
					rs.close();

					pStmtIter.close();
					iterationConnection.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				break;	
			case RunRangeOfIterations:
				for(; start <= end; start++ ,finIn++){
					HashMap<String, String> nameNew = new HashMap<String, String>();
					String testName = name.get("TestName");
					nameNew.putAll(name);


					nameNew.put("Iteration", String.valueOf(start));
					String Iteration = "";
					if(Integer.valueOf(nameNew.get("Iteration"))>1)
						Iteration = "_Iteration_"+nameNew.get("Iteration");

					//remove environment
					nameNew.put("TestName",nameNew.get("TestID")+"_"+testName+Iteration);
					updateNames.add(nameNew);

					

				}
				break;
			case RunOneIterationOnly:
			default:
				HashMap<String, String> nameNew = new HashMap<String, String>();
				String testName = name.get("TestName");
				nameNew.putAll(name);


				nameNew.put("Iteration", String.valueOf(1));
				String Iteration = "";
				if(Integer.valueOf(nameNew.get("Iteration"))>1)
					Iteration = "_Iteration_"+nameNew.get("Iteration");

				//remove env
				nameNew.put("TestName",nameNew.get("TestID")+"_"+testName+Iteration);

				updateNames.add(nameNew);
				finIn++;
				break;


			}

		}
	
		return(updateNames);
	}




	public List<XmlSuite> getSuitesFromList(ArrayDeque<HashMap<String, String>> stringNames) throws IOException, FrameworkException {
		
		List<XmlTest> testList = new ArrayList<XmlTest>();
		List<XmlSuite> suite = new ArrayList<XmlSuite>();

		Iterator<HashMap<String, String>> names1     = stringNames.iterator();
		while (names1.hasNext()) {

			HashMap<String, String> name = names1.next();
			XmlTest testname = new XmlTest();
			testname.setName(name.get("TestName"));

			testname = setRunManagerParams(testname,name);
			testname = setTestDataParams(testname,name);

			List<XmlClass> classes = new ArrayList<XmlClass>();
			XmlClass funcComp2 = null;
			try{
				funcComp2 = new XmlClass("com.FunctionalComponents");
			}
			catch(Exception ex){
				throw new FrameworkException(ex,"FunctionalComponents class not found");
			}
			List<String> businessFlowKey = new ArrayList<String>();
			List<XmlInclude> functionList = new ArrayList<XmlInclude>();
			businessFlowKey.add(name.get("TestID"));
			businessFlowKey.add(name.get("TestEnv"));
			functionList = businessFlowMap.get(businessFlowKey);


			funcComp2.setIncludedMethods(functionList);
			classes.add(funcComp2);
			
			testname.setXmlClasses(classes);
			


			testList.add(testname);
		}


		switch(parallelModeReport){
		case "TESTS":
		case "NONE":
		default:
			XmlSuite ExSuite = new XmlSuite();

			ExSuite.addListener("app.Listener");
			ExSuite.addListener("app.suiteListener");
			ExSuite.addListener("app.jUnitReportListener");

			ExSuite.setFileName("virtual.xml");

			ExSuite.setName(SuiteName);

			ExSuite.setParallel(XmlSuite.ParallelMode.valueOf(parallelModeReport));
			
			//add tests parallel thread count
			if(parallelModeReport.equalsIgnoreCase("tests")) {
				ExSuite.setThreadCount(App.getTestParallelThread());
			}
			Boolean addListenerInterceptor = false;

			Iterator<XmlTest> testListIt     = testList.iterator();
			while(testListIt.hasNext()){
				XmlTest testCase = testListIt.next();
				testCase.setSuite(ExSuite);
				

			}


			if(testList.size()==1){
				Iterator<XmlTest> testListIt2     = testList.iterator();
				while(testListIt2.hasNext()){
					XmlTest testCase = testListIt2.next();
					try{
						if((testCase.getParameter("SingleBrowser")!=null && testCase.getParameter("Driver")!= null)&&testCase.getParameter("SingleBrowser").equalsIgnoreCase("debug")&&(!testCase.getParameter("Driver").equalsIgnoreCase("make"))){
							addListenerInterceptor = true;
						}
					}
					catch(Exception e){
						
					}
				}
			}
			if(addListenerInterceptor){
				ExSuite.addListener("app.interceptDebug");
			}



			ExSuite.setTests(testList);

			suite.add(ExSuite);
			break;
		case "SUITES":
			suite = editsuite(testList);
			break;
		}

		

		long startTime = 0;
		long endTime = 0 ;

		if(objectsTable.isEmpty()) {
			startTime =  System.nanoTime();
			generateObjectsTable();
			endTime =  System.nanoTime();
			long duration = (endTime - startTime); 
			System.out.println("duration of Objects Table Creation in nanoseconds : " + duration);
		}
		
		return suite;
	}

	private void generateObjectsTable() {
		

		
		try {
			SQLServerDataTable functionList = new SQLServerDataTable();
			functionList.addColumnMetadata("functionListNames", java.sql.Types.NVARCHAR);
			for(String functionNames : functionSet) {
				functionList.addRow(functionNames);
			}

			Connection conn = ConnectionDataBase.getConnection();
			String ececStoredProc = "EXEC sp_getPageClassNames ?";
			SQLServerPreparedStatement pStmt = (SQLServerPreparedStatement)conn.prepareStatement(ececStoredProc);
			pStmt.setStructured(1, "functionListTableTypeCase", functionList);
			pStmt.execute();
			ResultSet parentPageClasses = pStmt.getResultSet();
			while (parentPageClasses.next()) {
			
				String uniquePageClassName = parentPageClasses.getString("PageClassName");
				objectsPageSet.add(uniquePageClassName);
			}
			parentPageClasses.close();
			pStmt.close();
			conn.close();

		} catch (SQLException e1) {
		
			e1.printStackTrace();
		}

		for(String pageClassName : objectsPageSet ) {
			addToObjectsTable(pageClassName);
		}

	}

	public static void addToObjectsTable(String pageClassName) {
		
		Connection connObjects = ConnectionDataBase.getConnection();
		ResultSet ObjectsTableRS = null;
		String ececStoredProObjects = "EXEC sp_objectsParamQuery ?, ?";
		SQLServerPreparedStatement pStmtObj = null;

		try {
			pStmtObj = (SQLServerPreparedStatement)connObjects.prepareStatement(ececStoredProObjects);
			pStmtObj.setString(1, getReportEnv());
			pStmtObj.setString(2,  pageClassName);
			pStmtObj.execute();



			ObjectsTableRS = pStmtObj.getResultSet();
		} catch (SQLException e1) {
	
			e1.printStackTrace();
		}


		HashMap<String,List< String>> objectsValue = new HashMap<String,List< String>>();
		try {
			while(ObjectsTableRS.next()) {
				List<String> tableValuesList = new ArrayList<String>();
				String objectName = ObjectsTableRS.getString("PageObjectName");
				String objectValue = ObjectsTableRS.getString("PropertyValue");
				String objectMethod = ObjectsTableRS.getString("MethodUsed");
				tableValuesList.add(0,objectValue);
				tableValuesList.add(1,objectMethod);
				objectsValue.put(objectName, tableValuesList);

			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		objectsTable.put(pageClassName, objectsValue);
		try {
			ObjectsTableRS.close();
			pStmtObj.close();
			connObjects.close();
		} catch (SQLException e) {
		
			e.printStackTrace();
		}

	}

	private XmlTest setTestDataParams(XmlTest testname, HashMap<String, String> name) {
		
		Connection testDataConn = ConnectionDataBase.getConnection();

		ResultSet testData =null;
		String ececStoredTestData = "EXEC sp_QueryTestData ?, ?, ?, ?";
		SQLServerPreparedStatement pStmtTD = null;
		try {
			pStmtTD = (SQLServerPreparedStatement)testDataConn.prepareStatement(ececStoredTestData);
			pStmtTD.setInt(1, Integer.valueOf(name.get("TestID")));
			pStmtTD.setInt(2, Integer.valueOf(name.get("Iteration")));
			pStmtTD.setString(3, reportEnv);
			pStmtTD.setString(4,  dataSetNum);
			pStmtTD.execute();



			testData = pStmtTD.getResultSet();
		} catch (SQLException e1) {
		
			e1.printStackTrace();
		}


		Boolean emptyDataSet = true;
		try {
			while(testData.next()){
				testname.addParameter( testData.getString("Test_Attribute"), testData.getString("Test_Value"));
				emptyDataSet = false;
			}
		} catch (SQLException e) {
	
			e.printStackTrace();
		}
		if(emptyDataSet) {
			testname.addParameter( "Application","DataError");
			testname.addParameter("Description", "Failed to find Test Case  and Iteration in Data Table");
		}
		try {
			testData.close();
			pStmtTD.close();

			testDataConn.close();
		} catch (SQLException e2) {
			
			e2.printStackTrace();
		}

		
		
		Connection testDataConfigCon = ConnectionDataBase.getConnection();

		ResultSet testConfig =null;
		String ececStoredConfigProc = "EXEC sp_queryTestConfig ?, ?, ?, ?";
		SQLServerPreparedStatement pStmtConf = null;
		try {
			pStmtConf = (SQLServerPreparedStatement)testDataConfigCon.prepareStatement(ececStoredConfigProc);
			pStmtConf.setInt(1, Integer.valueOf(name.get("TestID")));
			pStmtConf.setInt(2, Integer.valueOf(name.get("Iteration")));
			pStmtConf.setString(3, reportEnv);
			pStmtConf.setString(4,  dataSetNum);
			pStmtConf.execute();



			testConfig = pStmtConf.getResultSet();
		} catch (SQLException e1) {
		
			e1.printStackTrace();
		}

		try {
			while(testConfig.next()){
				if(testConfig.getString("Browser")!=null)
					testname.addParameter("Browser", testConfig.getString("Browser"));
				if(testConfig.getString("Device")!=null)
					testname.addParameter("Device", testConfig.getString("Device"));
				if(testConfig.getString("Rotation")!=null)
					testname.addParameter("Rotation", testConfig.getString("Rotation"));
				if(testConfig.getString("headless")!=null)
					testname.addParameter("headless", testConfig.getString("headless"));
				if(testConfig.getString("res")!=null)
					testname.addParameter("res", testConfig.getString("res"));
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}

		try {
			testConfig.close();
			pStmtConf.close();

			testDataConfigCon.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return testname;
	}

	private XmlTest setRunManagerParams(XmlTest testname, HashMap<String, String> name) {

		//possibly add checks against an empty string for mixed mode runs
		if(name.get("Application")!=null && (!name.get("Application").isEmpty()) )
			testname.addParameter("Application", name.get("Application"));
		
		if(name.get("TestDescription")!=null && (!name.get("TestDescription").isEmpty()))
			testname.addParameter("Description", name.get("TestDescription"));
		
		if(name.get("Driver")!=null && (!name.get("Driver").isEmpty()))
			testname.addParameter("Driver", name.get("Driver"));
		
		if(name.get("DriverCreated")!=null && (!name.get("DriverCreated").isEmpty()) )
			testname.addParameter("DriverCreated", name.get("DriverCreated"));
		
		if(name.get("Single_Browser")!=null && (!name.get("Single_Browser").isEmpty()))
			testname.addParameter("SingleBrowser", name.get("Single_Browser"));
		
		if(name.get("Iteration")!=null && (!name.get("Iteration").isEmpty()))
			testname.addParameter("Iteration", name.get("Iteration"));
		
		if(name.get("Locale_MT")!=null && (!name.get("Locale_MT").isEmpty()))
			testname.addParameter("Locale_MT", name.get("Locale_MT"));
		
		if(name.get("TestEnv")!=null && (!name.get("TestEnv").isEmpty()))
			testname.addParameter("TestEnv", name.get("TestEnv"));
		
		if(name.get("JiraTestKey")!=null && (!name.get("JiraTestKey").isEmpty()))
			testname.addParameter("JiraTestKey", name.get("JiraTestKey"));

		return testname;
	}

	protected static List<XmlInclude> getMethods(ResultSet busFlow){
		List<XmlInclude> functionList = new ArrayList<XmlInclude>();
		try {
			Integer flowNumber = 0;
			while(busFlow.next()){
				String FunctionName = busFlow.getString("Test_Function");
				XmlInclude testMethod = new XmlInclude(FunctionName, flowNumber);
				functionSet.add(FunctionName);
				functionList.add(testMethod);
				flowNumber++;
			}
		} catch (SQLException e1) {
			
			System.out.println(e1.toString());
		}
		return functionList;

	}

	protected static List<XmlSuite> editsuite(List<XmlTest> testList) {
		
		List<XmlSuite> suite = new ArrayList<XmlSuite>();
	
		Iterator<XmlTest> testIterator = testList.iterator();
		boolean addListenerInterceptor = false;
		


		while(testIterator.hasNext()){
			XmlTest nextTest = testIterator.next();

			String suiteName = (nextTest.getParameter("Application")==null? "NullApplication" :nextTest.getParameter("Application"));
	
			try{
				if((nextTest.getParameter("SingleBrowser")!=null && nextTest.getParameter("Driver")!= null)&&nextTest.getParameter("SingleBrowser").equalsIgnoreCase("debug")&&(!nextTest.getParameter("Driver").equalsIgnoreCase("make"))){
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
				SuiteCur.addListener("app.Listener");
				SuiteCur.addListener("app.suiteListener");
				SuiteCur.addListener("app.jUnitReportListener");
				if(addListenerInterceptor){
					SuiteCur.addListener("app.interceptDebug");
				}
				suite.add(SuiteCur);
			}
			for(XmlSuite indSuite : suite ){
				if(indSuite.getName().equals(suiteName)){
					nextTest.setXmlSuite(indSuite);
					indSuite.addTest(nextTest);
				}
			}

		}


		return suite;
	}

	@Override
	protected List<XmlSuite> getTests(String RunSuite, String dataSheetNum, String[] args)
			throws IOException, FrameworkException {

		List<XmlSuite> suite = new ArrayList<XmlSuite>();
	
		StringNames = StringNamesGen();

		StringNames = processTestIteration(StringNames);

		suite = getSuitesFromList(StringNames);


		return suite;
	}

	@Override
	protected List<XmlSuite> processRerunSuite() throws IOException, FrameworkException {
		
		List<XmlSuite> suite = new ArrayList<XmlSuite>();
		List<String> testList = App.getFailedTestList();
		ArrayDeque<HashMap<String,String>> failTestDeque = new ArrayDeque<HashMap<String, String>>(400);

	
		failTestDeque = StringNames.stream().filter(mapTest ->
			testList.contains(mapTest.get("TestName"))
			).collect(Collectors.toCollection(ArrayDeque::new));
		
		suite = getSuitesFromList(failTestDeque);
	
		return suite;
	}

}
