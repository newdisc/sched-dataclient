package nd.data.stream;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmlSink implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(EmlSink.class);
	private MimeMessage message;
	public boolean load(final String emlName) {
	    try {
		    final Properties props = System.getProperties();
		    final Session session = Session.getInstance(props, null);	        
		    final InputStream inputStream = new FileInputStream(emlName);
		    message = new MimeMessage(session, inputStream);
		    logger.info("From: {}", Arrays.toString(message.getFrom()));//setFrom(new InternetAddress(emailID));
		    logger.info("Rcpt: {}", Arrays.toString(message.getAllRecipients()));//.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		    logger.info("Subject: {}", message.getSubject());//setSubject(subject);

		    final Multipart multipart = (Multipart) message.getContent();
		    for (int i = 0;i < multipart.getCount(); i++) {
		    	final MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
		    	logger.info("Body Part: {} : {} : {}", part.getContentID(), part.getContentType(), part.getFileName());
		    }
		} catch (IOException | MessagingException e) {
            logger.error("Problem saving message", e);
			return false;
		}
	    return true;
	}
	public boolean save(final String fname) {
        try {
			message.writeTo(new FileOutputStream(new File(fname)));
		} catch (IOException | MessagingException e) {
            logger.error("Problem saving message", e);
			return false;
		}
		return true;
	}
	@Override
	public void close() throws IOException {
	}
}
/*
 * 
	        // create the message part 
	        MimeBodyPart content = new MimeBodyPart();
	        // fill message
	        content.setText("BODY TBD");
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(content);
	        // add attachments
	        for(File file : attachments) {
	            MimeBodyPart attachment = new MimeBodyPart();
	            DataSource source = new FileDataSource(file);
	            attachment.setDataHandler(new DataHandler(source));
	            attachment.setFileName(file.getName());
	            multipart.addBodyPart(attachment);
	        }
	        // integration
	        message.setContent(multipart);
	        // store file
 * */
 