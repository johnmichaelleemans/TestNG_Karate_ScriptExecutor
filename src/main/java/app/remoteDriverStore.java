package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;

import org.openqa.selenium.remote.HttpCommandExecutor;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */

public class remoteDriverStore {

	private static Map<String, WebDriver> drivers = new HashMap<String, WebDriver>();



	public static void saveDriver(String madeIn, WebDriver driverMade) {
		
		drivers.putIfAbsent(madeIn, driverMade);
	}

	public static WebDriver getMadeDriver(String madeIn) {
	
		return drivers.get(madeIn);
	}

	public static void writeDriverToFile(WebDriver driverCreated, ITestContext general_Data) throws IOException{
		
	
		CommandExecutor cmd = ((RemoteWebDriver)driverCreated).getCommandExecutor();
		String getSess = ((RemoteWebDriver)driverCreated).getSessionId().toString();
		String browserName = ((RemoteWebDriver)driverCreated).getCapabilities().getBrowserName();
		
		String url = ((HttpCommandExecutor)cmd).getAddressOfRemoteServer().toString();
		
		SaveBrowser save = new SaveBrowser(getSess, url);
		
		FileOutputStream fos;
		
		String currDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String debugOut = currDir + Util.getFileSeparator() + "debug";
		fos = new FileOutputStream(debugOut);

		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(save);
		oos.close();
		
	}
	
	public static WebDriver readDriverFromFile(WebDriver driver) throws IOException, ClassNotFoundException{
		
		String currDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String debugOut = currDir + Util.getFileSeparator() + "debug";
	
		
		
		 FileInputStream fis = new FileInputStream(debugOut);
	     ObjectInputStream ois = new ObjectInputStream(fis);
	    
	      SaveBrowser readSave = (SaveBrowser) ois.readObject();
	      
	      
	      ois.close();
	      
	      String url = readSave.url;
	
	      String stringTest = readSave.getSess;
	      
	
	      driver = new remoteWebExtend(url, DesiredCapabilities.chrome(), stringTest);
	      
		return driver;
		
	}
}


