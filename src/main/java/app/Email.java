package app;
import java.io.File;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.text.StringEscapeUtils;

import com.aventstack.extentreports.model.Test;

/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */

public class Email {

	private static String from = "TestRunReport@noreply.com";

	private static Email serverEmailinstance = null;
	private static Properties properties = new Properties();
	private static ArrayList<String> toList = new ArrayList<String>();

	private Email(){

		FileInputStream fileInStream = null;
		try {
			File file = new File(System.getProperty("user.dir")+ Util.getFileSeparator() + "configProperties" + Util.getFileSeparator() + "emailServerProperties");
			fileInStream = new FileInputStream(file);
			properties.load(fileInStream);
		} catch (Exception e ) {
			e.printStackTrace();
			String host = "";
			properties.put("mail.smtp.host", host);
			properties.put("mail.transport.protocol", "smtp");
			properties.put("mail.smtp.port", "25");
			properties.put("mail.smtp.auth", "false");
			properties.put("mail.debug", "true");
		}

		try {
			File recipList = new File(System.getProperty("user.dir")+ Util.getFileSeparator() + "configProperties" + Util.getFileSeparator() + "emailRecipientList");
			Files.lines(recipList.toPath()).forEach(line -> {
				if(!line.isEmpty()){
					toList.add(line);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	public static synchronized void sendEmail(Test iTest) {
		
		//make singleton
		if(serverEmailinstance == null){
			serverEmailinstance = new Email();
		}

		// Get the default Session object.
		Session session = Session.getInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			for(String to : toList ){
				try{
					to = StringEscapeUtils.unescapeJava(to);
				
					InternetAddress[] list = InternetAddress.parse(to, false);
					for (InternetAddress address : list) { 
						message.addRecipient(Message.RecipientType.TO,address);
					}
					
				}
				catch(AddressException adEx){
					System.out.println(adEx);
				}

			}

			// Set Subject: header field
			message.setSubject("QA-Automation E-Mail Reporting: " + iTest.getName());

			

			// Create the message part 
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			StringBuffer mailContent = new StringBuffer("Test Name: " + iTest.getName() + "\n");
			mailContent.append("Application: " );
			iTest.getCategoryContext().getAll().forEach(item -> {mailContent.append(item.getName() + "\t");});
			mailContent.append("\nEnvironment: " + App.getReportEnv());
			mailContent.append("\nDataSheet Number: " + App.getDataSheetNum());

			mailContent.append("\n\nFailing Test Steps:");


			
			iTest.getLogContext().getAll().forEach(steplog -> {
				if(steplog.getStatus()==com.aventstack.extentreports.Status.FAIL){
					mailContent.append("\n\nStep Details: "+ steplog.getDetails() 
					+"\nStep Status: " +steplog.getStatus());}
			}
					);

			messageBodyPart.setText(mailContent.toString());

			// Create a multi-part message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart );

			// Send message
			Transport.send(message);
		
		}catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

}
