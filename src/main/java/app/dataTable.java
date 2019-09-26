package app;

import org.testng.ITestContext;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class dataTable {

	


	public dataTable() {
		

	}

	public static String getData(ITestContext context, String column_name)  {
		
		
		String value = context.getCurrentXmlTest().getParameter(column_name);
		
		return value;
		
	}
	

}
