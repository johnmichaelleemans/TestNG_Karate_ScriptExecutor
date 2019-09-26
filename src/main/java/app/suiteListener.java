package app;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.testng.IExecutionListener;
import com.aventstack.extentreports.ExtentReports;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class suiteListener implements IExecutionListener {

	ExtentReports extent;
	static String reportPathString;
	static String nameOfReport;

	@Override
	public void onExecutionFinish() {
		

		extent.flush();
		if(!App.getDatabaseRun()) {
			String currentOutDirectoryPath = App.geTimeDir() + Util.getFileSeparator() ;
			String nameOfNewRunMan = testStartTime.getTime() + "_Run Manager.xls";
		
			try{
				ReportsExcelModification.generateUpdatedRunManager(currentOutDirectoryPath,nameOfNewRunMan);
			}
			catch(Exception ex){
				System.out.println(ex);
				System.out.println("Excel Not Modified");
			}
		}

	
		
		System.out.println(ExtentReportInitializer.getPassPercentage() + "% Passed");
	}


	@Override
	public void onExecutionStart() {
		
		String currentDirectoryPath = App.geTimeDir() + Util.getFileSeparator() ;
		nameOfReport = App.getReportEnv() + "__"+ testStartTime.getTime() + "__Summary.html";
		reportPathString = currentDirectoryPath + nameOfReport;
		
		File currentDirFolder = new File(currentDirectoryPath);
		currentDirFolder.mkdir();
		
		extent = ExtentReportInitializer.getExtentReport(reportPathString);

		extent.setSystemInfo("DataSheet Number", App.getDataSheetNum());

	}
	
	public static void openReport() {
		File extentReport = new File(reportPathString);
		
		try {
			Desktop.getDesktop().browse(extentReport.toURI());
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static String getNameOfReport() {
		return nameOfReport;
	}

}
