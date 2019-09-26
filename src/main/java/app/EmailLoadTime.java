/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */
package app;
import java.io.File;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.text.StringEscapeUtils;


public class EmailLoadTime {

	private static String from = "TestRunReport@noreply.com";

	private static EmailLoadTime serverEmailinstance = null;
	private static Properties properties = new Properties();
	private static ArrayList<String> toList = new ArrayList<String>();
	StringBuffer mailContent = new StringBuffer("Time Required for the following steps: " + "\n");

	public EmailLoadTime(){

		if (serverEmailinstance == null) {
		
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
			File recipList = new File(System.getProperty("user.dir")+ Util.getFileSeparator() + "configProperties" + Util.getFileSeparator() + "timeStampEmailList");
			Files.lines(recipList.toPath()).forEach(line -> {
				if(!line.isEmpty()){
					toList.add(line);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		serverEmailinstance = this;
		
		}
		
		
	}

	public synchronized void sendTimeEmail(String Application) {
		
		

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
			message.setSubject(App.getReportEnv()+"-Automation E-Mail Timestamp Reporting: "+Application);
			
			// Create the message part 
			BodyPart messageBodyPart = new MimeBodyPart();


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

	public void timeReporter(String loadTime, String loadStep) {
			
			mailContent.append(loadStep+": "+loadTime+" seconds" );
			mailContent.append("\n" );
			
	
	}
	
	
}
