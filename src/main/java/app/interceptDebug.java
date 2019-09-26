package app;

import java.util.ArrayList;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class interceptDebug implements IMethodInterceptor {

	
	public List<IMethodInstance> intercept(List<IMethodInstance> methodsList, ITestContext context) {
	
		if(context.getCurrentXmlTest().getParameter("SingleBrowser").equalsIgnoreCase("debug")
				&& (!context.getCurrentXmlTest().getParameter("Driver").equalsIgnoreCase("make")) ){
			List<IMethodInstance> newMethodList = new ArrayList<IMethodInstance>();
			String singleMethod = context.getCurrentXmlTest().getParameter("DriverCreated");
			for(IMethodInstance method : methodsList){
				if(method.getMethod().getMethodName().equals(singleMethod)){
					newMethodList.add(method);
				}
			}
			return  newMethodList;
		}
		else{
			return methodsList;
		}

	}

}
