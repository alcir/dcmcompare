package dcmCompare;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.dcm4che2.data.Tag;
import org.dcm4che2.net.ConfigurationException;

public class typeOne {

    private String date;
    private String remote1;
    private String remote2;
    private String modality;
    private String strDate;
    private dateTime dateTime = new dateTime();
    private String [] risultato = new String [2];

    public StringBuilder message = new StringBuilder();
    public StringBuilder subject = new StringBuilder();

    public typeOne(String date, String remote1, String remote2, String modality) {
	this.date = date;
	this.remote1 = remote1;
	this.remote2 = remote2;
	this.modality = modality;
    }

    @SuppressWarnings("unchecked")
    public String [] go() throws InterruptedException, ParseException {

	varie varie1 = new varie(remote1, modality);
	varie varie2 = new varie(remote2, modality);
	
	strDate = dateTime.convertdateonly(date);

	subject.append("Compare " + varie1.getAETitle() + " "
		+ varie2.getAETitle() + " " + modality + " " + strDate);

	System.out.println("\nQuery to " + varie1.getAETitle() + "\n");
	query gogo1 = new query(date, remote1, modality);
	
	ArrayList<String> risultatoA = null;
	
	try {
	    risultatoA = gogo1.doit();
	} catch (IOException e) {
	    subject.insert(0, "ERROR ");
	    message.append("IOException " + e);
	    risultato [0] = subject.toString();
	    risultato [1] = message.toString();
	    return risultato;
	} catch (ConfigurationException e) {
	    subject.insert(0, "ERROR ");
	    message.append("ConfigurationException " + e);
	    risultato [0] = subject.toString();
	    risultato [1] = message.toString();
	    return risultato;
	}

	System.out.println("Query to " + varie2.getAETitle() + "\n");
	
	query gogo2 = new query(date, remote2, modality);
	
	ArrayList<String> risultatoB = null;
	
	try {
	    risultatoB = gogo2.doit();
	} catch (IOException e) {
	    subject.insert(0, "ERROR ");
	    message.append("IOException " + e);
	    risultato [0] = subject.toString();
	    risultato [1] = message.toString();
	    return risultato;
	} catch (ConfigurationException e) {
	    subject.insert(0, "ERROR ");
	    message.append("ConfigurationException " + e);
	    risultato [0] = subject.toString();
	    risultato [1] = message.toString();
	    return risultato;
	}

	// System.out.println(" ----" +risultatoA.size());

	// System.out.println("------"+risultatoB.size());

	Iterator<String> risultatoAiterator = risultatoA.iterator();
	
	Iterator<String> salcazzo = risultatoAiterator;
	
//	 while ( salcazzo.hasNext()) {
//	 System.out.println("-- " + salcazzo.next());
//	 }

	
	
	//Iterator<String> risultatoBiterator = risultatoB.iterator();
	// while ( salcazzo2.hasNext()) {
	// System.out.println(salcazzo2.next());
	// }

	Collection<?> diffAB = CollectionUtils.subtract(risultatoA, risultatoB);
	Collection<?> diffBA = CollectionUtils.subtract(risultatoB, risultatoA);

	int sizeA = risultatoA.size();
	int sizeB = risultatoB.size();

	//subject.append(varie1.getAETitle() + " " + varie2.getAETitle());

	String messagetoappend="";
	String subjecttoinsert="";
	
	if (sizeA > sizeB) {
	    messagetoappend="WARN " + varie1.getAETitle() + " > "
		    + varie2.getAETitle() + " (" + sizeA + " " + sizeB + ")\n";
	    //subject.insert(0, "WARN ");
	    subjecttoinsert="WARN ";
	} else if (sizeA < sizeB) {
	    messagetoappend="WARN " + varie1.getAETitle() + " < "
		    + varie2.getAETitle() + " (" + sizeA + " " + sizeB + ")\n";
	    subjecttoinsert="WARN ";
	} else if (sizeA == sizeB) {
	    messagetoappend="Same number of studies " + varie1.getAETitle() + " = "
		    + varie2.getAETitle() + " (" + sizeA + " " + sizeB + ")\n";
	    subjecttoinsert="OK ";
	}

	message.append(messagetoappend);
	
	String riga;
	
	if (diffAB.size() > 0) {
	    
	    subjecttoinsert="WARN ";
	    
	    message.append("\nDifference " + remote1 + " " + remote2 + " ("
		    + diffAB.size() + ")\n");

	    Iterator<String> diffABiterator = (Iterator<String>) diffAB.iterator();
	    while (diffABiterator.hasNext()) {
		riga = diffABiterator.next();
		System.out.println(riga);
		message.append(riga.replace("|", " ") + "\n");
	    }
	}

	if (diffBA.size() > 0) {

	    subjecttoinsert="WARN ";
	    
	    message.append("\nDifference " + remote2 + " " + remote1 + " ("
		    + diffBA.size() + ")\n");

	    Iterator<String> diffBAiterator = (Iterator<String>) diffBA.iterator();
	    while (diffBAiterator.hasNext()) {
		riga = diffBAiterator.next();
		message.append(riga.replace("|", " ") + "\n");
	    }
	}

	if (diffAB.size() == 0 & diffBA.size() == 0) {
	    
	    message.append("\nSame result " + remote1 + " " + remote2
		    + " (" + diffAB.size() + " " + diffBA.size() + ")\n\n");

	    String[] token;
	    String patname;
	    String patid;
	    String modality;
	    String series;
	    String instances;
	    String siuid;
	    
	    while (risultatoAiterator.hasNext()) {
		riga = risultatoAiterator.next();
		token = riga.split("\\|");
		patname = token[0];
		patid = token[1];
		modality = token[2];
		series = token[3];
		instances = token[4];
		siuid = token[5];
		
		//message.append(riga.replace("|", " ") + "\n");
		
		message.append(patname+"   "+patid+" "+modality+" "+series+" "+instances+"\n");
		
	    }
	    
	}

	subject.insert(0, subjecttoinsert);
	
	message.append("\nStudies on " + varie1.getAETitle() + " "
		+ risultatoA.size() + "\n" + "Studies on " + varie2.getAETitle()
		+ " " + risultatoB.size() + "\n" + "Diff "
		+ varie1.getAETitle() + " " + varie2.getAETitle() + " "
		+ diffAB.size() + "\n" + "Diff " + varie2.getAETitle() + " "
		+ varie1.getAETitle() + " " + diffBA.size() + "\n");
	
	
	//System.out.println("oooooo \n" + message.toString() + "cxzczxczxczx\n");
	//System.out.println("dasdasd \n" + subject.toString() + "cxzczxczxczx\n");
	risultato [0] = subject.toString();
	risultato [1] = message.toString();
	
	return risultato;
	
    }

}
