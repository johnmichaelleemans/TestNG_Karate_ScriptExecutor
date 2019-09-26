package app;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import functions.AppTest;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class Listener extends TestListenerAdapter  {
	
	
	@Override
    public void onTestFailure(ITestResult result) {
		Object currentClass = result.getInstance();
       
		ITestContext context = ((AppTest) currentClass).getContext();
		
		
        Report report = ((AppTest) currentClass).getReport();
        
        String exceptionDescription = result.getThrowable().toString();
        if(     exceptionDescription.contains("waiting for element") 
    			|| (exceptionDescription.contains("no") && exceptionDescription.contains("such") && exceptionDescription.contains("element"))
    			|| exceptionDescription.contains("waiting for visibility of element")
    			|| exceptionDescription.contains("would receive click")
    			|| exceptionDescription.contains("java.lang.NullPointerException")
    			|| exceptionDescription.contains("org.openqa.selenium.NoSuchFrameException")
    		  ){
        	report.updateTestLog(result.getName(), exceptionDescription, Status.FAIL);
       
        	((AppTest) currentClass).setFailed(true);
        	
        	
        }
        else if (exceptionDescription.contains("java.lang.AssertionError")){
        	report.updateTestLog(result.getName(), exceptionDescription, Status.FAIL);
        	((AppTest) currentClass).setFailed(true);
        }
        else {
        	
        	report.updateTestLogNoScreenshot(result.getName(), result.getThrowable(), Status.FAIL);
        	
        	//skip rest of test on fatal error such as unreachable browser
        	
        	((AppTest) currentClass).setFailed(true);
        	
        	
        }	
    }
	@Override
	public void onTestStart(ITestResult result){
		Object currentClass = result.getInstance();
     
        ITestContext context = ((AppTest) currentClass).getContext();
        Report report = ((AppTest) currentClass).getReport();
        
        
        report.updateTestLogMethodPassed(result.getName(),"Method " + result.getName() + " Started", Status.INFO);
		
	}
	
	@Override
	public void onTestSkipped(ITestResult result){
		Object currentClass = result.getInstance();
      
        ITestContext context = ((AppTest) currentClass).getContext();
        Report report = ((AppTest) currentClass).getReport();
        
        report.updateTestLogMethodPassed(result.getName(),"Method Skipped", Status.FAIL);
		
	}

}
