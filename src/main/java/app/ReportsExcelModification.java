package app;


/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;


public class ReportsExcelModification {
	//using hashtable instead of hashmap because hashtable is synchronized
	private static Hashtable<String, ArrayList<String>> testResultsMap = new Hashtable<String, ArrayList<String>>();

	static void addResult(String name, ArrayList<String> result){
		testResultsMap.put(name, result);
	}

	static void printTable(){

		testResultsMap.forEach((key, value) -> {
			System.out.println(key + " : " + value );
		});
	}

	static void generateUpdatedRunManager(String currentOutDirectoryPath, String nameOfNewRunMan){
		String runManager =  new File(System.getProperty("user.dir")).getAbsolutePath() + Util.getFileSeparator() + "Run Manager.xls";
		String updatedRunManger = currentOutDirectoryPath + nameOfNewRunMan;
		String runManagerSheetName = App.getRunSuiteName();

		try {
			FileUtils.copyFile(new File(runManager), new File(updatedRunManger));
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		ExcelDataAccess runMan = new ExcelDataAccess(currentOutDirectoryPath,nameOfNewRunMan);
		runMan.setDatasheetName(runManagerSheetName);
		int executeCol = -1;
		try {
			executeCol = runMan.getColumnNum("Execute", 0);
			runMan.deleteColumn(executeCol,true);
		} catch (IOException | FrameworkException e) {
			
			e.printStackTrace();
		}
		int LastRunStatusColNum =-1;
		try {
			LastRunStatusColNum = runMan.getColumnNum("LastRunStatus", 0);
			if(LastRunStatusColNum == -1){
				
				LastRunStatusColNum = runMan.AddColumn(0, "LastRunStatus");
			}
			else{
				runMan.deleteColumn(LastRunStatusColNum,false);
				runMan.AddColumnNum(0,LastRunStatusColNum, "LastRunStatus");
			}
		} catch (IOException |FrameworkException e) {
		
			e.printStackTrace();
		}

		int testCaseColumn = -1;
		try {
			testCaseColumn = runMan.getColumnNum("Test_Case",0);
		} catch (IOException | FrameworkException e) {
			
			e.printStackTrace();
		}
		final int tcColumnNum = testCaseColumn;
		final int lrsColumnNum = LastRunStatusColNum;
		final int eColumnNum = executeCol;

		final Pattern testnameWithIteration = Pattern.compile(".+(?=_Iteration_\\d+)");
		final Pattern testIterationNumber = Pattern.compile("\\d+(?!_Iteration_)");

		testResultsMap.forEach((key, value) -> {
			Matcher matcher = testnameWithIteration.matcher(key);
			Matcher matcherNum = testIterationNumber.matcher(key);
			String iterationNum = null;

			if(matcher.find()){				
				key = matcher.group(0);
				matcherNum.find();
				iterationNum = matcherNum.group(0);
			
			}	
			if(iterationNum == null){
				iterationNum = "1";
			}
			String logStatusTest = value.get(0);
			int testRowNum = -1;
			//parse test name to check iteration number
			try {
				testRowNum = runMan.getRowNum(key, tcColumnNum);
			} catch (IOException e) {
		
				e.printStackTrace();
			}


			try {
				runMan.setValueCommentStatus(testRowNum, lrsColumnNum, logStatusTest,iterationNum);
				//should be yes for failed Tests
				runMan.setValueCommentExecute(testRowNum, eColumnNum, logStatusTest, iterationNum);
			} catch (IOException e) {
			
				e.printStackTrace();
			}

		});
	}

}
