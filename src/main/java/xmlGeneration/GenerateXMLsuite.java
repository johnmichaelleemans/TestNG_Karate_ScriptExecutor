package xmlGeneration;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.xml.XmlSuite;
import app.FrameworkException;

public abstract class GenerateXMLsuite extends app.App{
	protected String[] args;
	
	public GenerateXMLsuite(String[] cmdLineArg){
		this.args = cmdLineArg;
	}
	
	public List<XmlSuite> generateSuites() throws IOException, FrameworkException{

		List<XmlSuite> suite = new ArrayList<XmlSuite>();
		

		//should set all Config values at start


		SuiteName = getSuiteName();


		dataSetNum = getDataSetNumber();

		shouldSendEmail = getEmailBool();

		//sets report environment and return excel envi 
		//eg QAA_DATA_1
		excelRunEnv = getEnv();
		
		parallelModeReport = getParallelMode();

		suite = getTests( SuiteName, dataSetNum, args);

	

		return suite;

	}
	
	 public abstract String getParallelMode()  throws IOException;
	 
	 public abstract String getSuiteName() throws IOException;
	 
	 public abstract String getDataSetNumber() throws IOException;
	 
	 public abstract Boolean getEmailBool() throws IOException;
	 



	 protected  abstract List<XmlSuite> getTests( String RunSuite, String dataSheetNum, String[] args) throws IOException, FrameworkException;


	
	 public abstract String getEnv() throws FrameworkException, IOException ;
	
	
	 public List<XmlSuite> getRerunSuites() throws IOException, FrameworkException{
		 List<XmlSuite> suiteRerun = new ArrayList<XmlSuite>();
	
		 //reset the environment for excel
		 excelRunEnv = getEnv();
		 suiteRerun = processRerunSuite();
		 
		 return suiteRerun;

	 }

	protected abstract List<XmlSuite> processRerunSuite() throws IOException, FrameworkException;
}
