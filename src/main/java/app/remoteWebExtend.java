package app;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class remoteWebExtend extends RemoteWebDriver{

	public remoteWebExtend(String url, DesiredCapabilities caps, String opaqueKey) throws MalformedURLException{
		super(new URL(url),caps);
		this.setSessionId(opaqueKey);
	
	}
}

	
