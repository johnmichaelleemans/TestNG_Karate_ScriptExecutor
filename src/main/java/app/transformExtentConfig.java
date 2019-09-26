package app;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.CDATASection;

public class transformExtentConfig {

	public static File transform(File file, String nameOfReport)   {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(file);
		} catch (SAXException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		//change the xml config

		doc.getDocumentElement().getElementsByTagName("documentTitle").item(0).setTextContent(nameOfReport);

			
		
		CDATASection cdata = doc.createCDATASection(

						
				
						"  $(document).ready(function() {" 
								
						+"$('#test-view-charts').addClass('hide');"
						+"document.evaluate(\"//ul[@id='slide-out']/descendant::li[child::a[contains(@view,'dashboard')]]/a\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click();"
						
						+"divPercent = document.createElement(\"div\");\r\n" + 
						"divPercent.setAttribute(\"class\", \"col s2\");\r\n" + 
						"divPercent.setAttribute(\"id\",'divPercent');\r\n" + 
						"divPercentChild = document.createElement(\"div\");\r\n" + 
						"divPercentChild.setAttribute(\"class\",\"card-panel r\");\r\n" + 
						"divPercentChild.innerHTML = \"Pass Percentage\";\r\n" + 
						"divPercent.appendChild(divPercentChild);\r\n" + 
						"textNode = document.createElement(\"div\");\r\n" + 
						"textNode.setAttribute(\"class\",\"percentInserter panel-lead\");\r\n" + 
						"\r\n" + 
						"divPercentChild.appendChild(textNode);\r\n" + 
						"document.querySelector('body').appendChild(divPercent)\r\n" + 
						"$('#divPercent').insertAfter('#dashboard-view > div > div:nth-child(3) > div:nth-child(1)');"
						+"percentage = Math.round(((statusGroup.passParent  + statusGroup.warningParent)* 100) / (statusGroup.passParent + statusGroup.failParent + statusGroup.fatalParent + statusGroup.warningParent + statusGroup.errorParent + statusGroup.skipParent)) + '%';"  
						+"$('.percentInserter.panel-lead').text(percentage);\r\n"  
				
						+"document.querySelector('#dashboard-view > div > div:nth-child(3) > div:nth-child(3)').style.display = 'none';"
						+"document.querySelector('#charts-row > div:nth-child(2)').style.display = 'none';"
						+"$('#charts-row').insertAfter('#dashboard-view > div:nth-child(1)> div:nth-child(1)');"
						+"document.querySelector('#charts-row').setAttribute(\"class\",\"\");"
						
						+"document.evaluate(\"//ul[@id='slide-out']/descendant::li[child::a[contains(@view,'author-view')]]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style.display = 'none';"

						
						+"});"

				);
		doc.getDocumentElement().getElementsByTagName("js").item(0).appendChild(cdata);

		

		
		CDATASection cssData = doc.createCDATASection(".cyan, .light-blue, .deep-orange, .blue, .pink-accent, .green-accent {\r\n" + 
				"	color: #fff;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"#dashboard-view .progress {\r\n" + 
				"	margin-top: 100px;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				".panel-lead {\r\n" + 
				"	display: block;\r\n" + 
				"	font-size: 20px;\r\n" + 
				"	text-align: center;\r\n" + 
				"}"
		
				);
		
		doc.getDocumentElement().getElementsByTagName("css").item(0).appendChild(cssData);
		

		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e1) {
		
			e1.printStackTrace();
		}
		
		
		File transformedConfig = null;
		String pi = App.geTimeDir() + Util.getFileSeparator() + "xmlReportConfig.xml";
		
		Path outputXmlReportText = Paths.get(pi);
		
		transformedConfig = new File(outputXmlReportText.toUri());
		
		try {
			transformedConfig.createNewFile();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		Result output = new StreamResult(transformedConfig.getPath());
		Source input = new DOMSource(doc);

		try {
			transformer.transform(input, output);
		} catch (TransformerException e) {
		
			e.printStackTrace();
		}


		return transformedConfig ;

	}

}
