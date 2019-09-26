package xmlGeneration.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class ConnectionDataBase {

	private static Connection conn = null;
	
	private ConnectionDataBase() {
		
	}


	
	static Connection getConnection() {
		
	

		try {
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
	
			e.printStackTrace();
		}
		String connectionString =  
				"jdbc:sqlserver://;"  
				+ "databaseName=TestAutomationData;"  
				+ "integratedSecurity=true;";

		try {
			conn=DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		return conn;

	}
}
