package app;

import org.testng.collections.Lists;

import org.testng.internal.Utils;

import org.testng.reporters.XMLConstants;
import org.testng.reporters.XMLStringBuffer;


import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;



import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.List;

import java.util.Properties;

public class htmlToJunit {

	/*******************************TestNG Testing Framework Tool ************************************
	 *******************************      Author:    ***********************************
	 *******************************John-Michael Leemans********************************
	 */


	public static void generateJunitFromHtml() {

		String defaultOutputDirectory = App.geTimeDir() + Util.getFileSeparator() ;
		ExtentHtmlReporter htmlReport = ExtentReportInitializer.getExtentHtmlReporter();
		List<Test> testList = htmlReport.getTestList();
		
		Float totalTestTime;
		try {
			totalTestTime= Float.valueOf(htmlReport.getRunDuration());
		}
		catch(NumberFormatException ex) {
			totalTestTime = new Float(0.00);
		}

		Integer totalTestCount = htmlReport.getStatusCount().getParentCount();
		Integer totalSkip =  htmlReport.getStatusCount().getParentCountSkip(); 
		Integer totalFail = htmlReport.getStatusCount().getParentCountFail(); 
		totalFail += htmlReport.getStatusCount().getParentCountFatal(); 
		totalFail += htmlReport.getStatusCount().getParentCountError(); 
		Integer totalError =  htmlReport.getStatusCount().getParentCountError(); 

		List<TestTag> testCases = Lists.newArrayList();

		for(Test test : testList) {

			Float testTime;
			try {
				testTime = Float.valueOf(test.getRunDurationMillis());
			}
			catch(NumberFormatException ex) {
				testTime = new Float(0.00);
			}


			String testApplication = test.getCategory(0).getName();
			String testClass = test.getAuthor(0).getName();

			TestTag testTag = createTestTagFor(test, testClass,testApplication );
			if(testTag == null)
				continue;
			//milliseconds to seconds
			testTag.properties.setProperty(XMLConstants.ATTR_TIME, "" + formatTime(testTime));
			testCases.add(testTag);
		}



		Properties p1 = new Properties();

		p1.setProperty(XMLConstants.ATTR_FAILURES, "" + totalFail);
		p1.setProperty(XMLConstants.ATTR_ERRORS, "" + totalError);
		p1.setProperty(XMLConstants.SKIPPED, "" + (totalSkip ));
		p1.setProperty(XMLConstants.ATTR_NAME,"AutomationTesting");
		p1.setProperty(XMLConstants.ATTR_TESTS, "" + (totalTestCount));
		//format time takes milliseconds
		p1.setProperty(XMLConstants.ATTR_TIME, "" + formatTime(totalTestTime));


		Properties p2 = new Properties();

		try {
			p2.setProperty("name", "host.name");
			p2.setProperty("value", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// ignore
		}

		//
		// Now that we have all the information we need, generate the file
		//
		XMLStringBuffer xsb = new XMLStringBuffer();
	

		xsb.push(XMLConstants.TESTSUITE, p1);
		xsb.push("properties");
		xsb.addEmptyElement("property", p2);
		
		xsb.pop("properties");
		for (TestTag testTag : testCases) {
			if (putElement(xsb, XMLConstants.TESTCASE, testTag.properties, testTag.childTag != null)) {
				Properties p = new Properties();
				safeSetProperty(p, XMLConstants.ATTR_MESSAGE, testTag.message);
				safeSetProperty(p, XMLConstants.ATTR_TYPE, testTag.type);

				if (putElement(xsb, testTag.childTag, p, testTag.stackTrace != null)) {
					xsb.addCDATA(testTag.stackTrace);
					xsb.pop(testTag.childTag);
				}
				xsb.pop(XMLConstants.TESTCASE);
			}
		}
		xsb.pop(XMLConstants.TESTSUITE);

		String outputDirectory = defaultOutputDirectory + File.separator + "junitreports";

		Utils.writeUtf8File(outputDirectory, "htmlJunitReport.xml", xsb.toXML());


	}

	private static String formatTime(float time) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		// JUnitReports requires points here, regardless of the locale
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#.###", symbols);
		format.setMinimumFractionDigits(3);
		return format.format(time / 1000.0f);
	}

	private static class TestTag {
		public Properties properties;
		public String message;
		public String type;
		public String stackTrace;
		String childTag;
	}



	private static TestTag createTestTagFor(Test tr,String cls, String testApplication) {
		TestTag testTag = new TestTag();

		Properties p2 = new Properties();
		p2.setProperty(XMLConstants.ATTR_CLASSNAME, cls);
	
		p2.setProperty(XMLConstants.ATTR_NAME,tr.getName());
		p2.setProperty("Application",testApplication);
		Status status = tr.getStatus();

		if (status == Status.FAIL || status == Status.ERROR || status == Status.SKIP || status == Status.FATAL) {
			if(tr.hasException()) {
				handleFailure(testTag, tr.getExceptionInfoList().get(0).getException());
			}

			else {
				testTag.childTag = XMLConstants.FAILURE;
				testTag.message = "Test Failed Without a Throwable Error";
				testTag.type = "report.UpdateTestLog.FAIL";
				StringWriter logWriter = new StringWriter();
				for(Log log : tr.getLogContext().getAll()) {
					if(log.getStatus() == Status.FAIL) {
						logWriter.append("Failing Step Description : " + log.getDetails() +"\n");
					}
				}
				testTag.stackTrace = logWriter.toString();
			}

		}

		testTag.properties = p2;
		return testTag;
	}

	private static void handleFailure(TestTag testTag, Throwable t) {
		testTag.childTag = t instanceof AssertionError ? XMLConstants.FAILURE : XMLConstants.FAILURE;
		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			testTag.message = t.getMessage();
			testTag.type = t.getClass().getName();
			testTag.stackTrace = sw.toString();
		}
	}

	/** Put a XML start or empty tag to the XMLStringBuffer depending on hasChildElements parameter */
	private static boolean putElement(XMLStringBuffer xsb, String tagName, Properties attributes, boolean hasChildElements) {
		if (hasChildElements) {
			xsb.push(tagName, attributes);
		}
		else {
			xsb.addEmptyElement(tagName, attributes);
		}
		return hasChildElements;
	}

	/** Set property if value is non-null */
	private static void safeSetProperty(Properties p, String key, String value) {
		if (value != null) {
			p.setProperty(key, value);
		}
	}


}



