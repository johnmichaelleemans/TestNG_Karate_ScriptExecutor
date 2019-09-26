package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */

import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;

public class ExtentReportInitializer  {

	private static ExtentReportInitializer instance = null;
	private static ExtentReports extent;
	private static ExtentHtmlReporter htmlReporter;
	
	private ExtentReportInitializer() {
		
	}

	public  static ExtentReports getExtentReport(String filePath) {
		if(instance == null){
			instance = new ExtentReportInitializer();
			htmlReporter = new ExtentHtmlReporter(filePath);



			extent = new ExtentReports();

			File extentConfig = new File(System.getProperty("user.dir")+ Util.getFileSeparator() + "WebDrivers" + Util.getFileSeparator() 
			+"extent-config.xml");

			htmlReporter.loadXMLConfig(app.transformExtentConfig.transform(extentConfig, suiteListener.getNameOfReport()));

			htmlReporter.setAnalysisStrategy(AnalysisStrategy.TEST);

	


			extent.attachReporter(htmlReporter);
			extent.setSystemInfo("ParallelMode",App.getParralelText() );
			extent.setSystemInfo("Environment", App.getReportEnv());
			try
			{
				InetAddress addr;
				addr = InetAddress.getLocalHost();
				extent.setSystemInfo("Host Name",addr.getHostName());

			}
			catch (UnknownHostException ex)
			{

			}


		}
		return extent;
			
	}
	
	public static ExtentReports getExtentReport() {
		return extent;
	}
	
	public static int getPassPercentage() {

		Integer passed = htmlReporter.getStatusCount().getParentCountPass()+ htmlReporter.getStatusCount().getParentCountWarning();
		Integer total = htmlReporter.getStatusCount().getParentCount();
		float percent = total > 0 ? (float)passed/(float)total : 0; 
        return (int)percent*100;
	}
	
	public static ExtentHtmlReporter getExtentHtmlReporter() {
		return htmlReporter;
	}
}

	