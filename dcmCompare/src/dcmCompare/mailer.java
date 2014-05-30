package dcmCompare;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

public class mailer {

    private static String smtpServer;
    private static String from;
    private static String to;
    private static String ccn;
    private static String message;
    private static String subject;
    
    private ArrayList<String> addressTO = new ArrayList<String>();
    

    public mailer(configurationFile mailconf, String subj, String msg) {

//	System.out.println(mailconf.getProperty("mailTo"));
//	System.out.println(mailconf.getProperty("smtpServer"));
	
	mailer.smtpServer = mailconf.getProperty("smtpServer");
	mailer.from = mailconf.getProperty("mailFrom");
	
	mailer.message = msg;
	mailer.subject = subj;
	
	StringTokenizer st = new StringTokenizer(mailconf.getProperty("mailTo"), ",");
	    while (st.hasMoreTokens()) {
		addressTO.add(st.nextToken());
	    }
	
	try {
	    mailer.send(addressTO, null, null, null, mailer.subject, mailer.message, false);
	    
	} catch (Exception e) {
	    
	    e.printStackTrace();
	    
	}

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void send(	ArrayList addressTO, 
	    			ArrayList addressCC,
	    			ArrayList addressCCN, 
	    			ArrayList attachFiles, 
	    			String subject,
	    			String msgText, 
	    			boolean html) throws Exception {
	// create some properties and get the default Session
	Properties props = System.getProperties();
	props.put("mail.smtp.host", smtpServer);

	Session session = Session.getDefaultInstance(props, null);
	// session.setDebug(true);

	// InetAddress ia = InetAddress.getLocalHost();
	// String from = ia.getHostName();
	// create a message
	MimeMessage msg = new MimeMessage(session);
	msg.setFrom(new InternetAddress(from));
	// inserisco gli indirizzi da inserire in TO
	if (!StringUtils.isEmpty(to)) {
	    if (addressTO == null) {
		addressTO = new ArrayList();
	    }
	    StringTokenizer st = new StringTokenizer(to, ";");
	    while (st.hasMoreTokens()) {
		addressTO.add(st.nextToken());
	    }
	}
	if (!(addressTO == null)) {
	    InternetAddress[] iAddressTO = new InternetAddress[addressTO.size()];
	    for (int w = 0; w < addressTO.size(); w++) {
		iAddressTO[w] = new InternetAddress((String) addressTO.get(w));
	    }

	    msg.setRecipients(Message.RecipientType.TO, iAddressTO);
	}
	if (!StringUtils.isEmpty(ccn)) {
	    if (addressCC == null) {
		addressCC = new ArrayList();
	    }
	    StringTokenizer st = new StringTokenizer(ccn, ";");
	    while (st.hasMoreTokens()) {
		addressCC.add(st.nextToken());
	    }
	}

	// inserisco gli indirizzi da inserire in CC
	if (!(addressCC == null)) {
	    InternetAddress[] iAddressCC = new InternetAddress[addressCC.size()];
	    for (int w = 0; w < addressCC.size(); w++) {
		iAddressCC[w] = new InternetAddress((String) addressCC.get(w));
	    }
	    msg.setRecipients(Message.RecipientType.CC, iAddressCC);
	}
	// inserisco gli indirizzi da inserire in CCN
	if (!(addressCCN == null)) {
	    InternetAddress[] iAddressCCN = new InternetAddress[addressCCN
		    .size()];
	    for (int w = 0; w < addressCCN.size(); w++) {
		iAddressCCN[w] = new InternetAddress((String) addressCCN.get(w));
	    }
	    msg.setRecipients(Message.RecipientType.BCC, iAddressCCN);
	}
	// inserisco l'oggetto
	msg.setSubject(subject);

	// create and fill the first message part
	MimeBodyPart mbp1 = new MimeBodyPart();
	if (html) {
	    mbp1.setHeader("Content-Type", "text/html");
	    mbp1.setContent(msgText, "text/html");
	} else {
	    mbp1.setText(msgText);
	}

	// create the Multipart and its parts to it
	Multipart mp = new MimeMultipart();
	mp.addBodyPart(mbp1);

	// se ci sono allegati li inserisco
	if (!(attachFiles == null)) {
	    for (int w = 0; w < attachFiles.size(); w++) {
		File tmp = new File((String) attachFiles.get(w));
		if (tmp.exists()) { // se il file esiste lo allego altrimenti
				    // segnalo l'errore
		// create the second message part
		    MimeBodyPart mbp2 = new MimeBodyPart();
		    // attach the file to the message
		    FileDataSource fds = new FileDataSource(
			    tmp.getAbsolutePath());
		    mbp2.setDataHandler(new DataHandler(fds));
		    mbp2.setFileName(fds.getName());
		    mp.addBodyPart(mbp2);
		} else {
		    System.out.println("File " + (String) attachFiles.get(w)
			    + " non trovato !!");
		}
	    }
	} // end if
	  // add the Multipart to the message
	msg.setContent(mp);
	// set the Date: header
	msg.setSentDate(new Date());
	// send the message
	Transport.send(msg);
    }

}
