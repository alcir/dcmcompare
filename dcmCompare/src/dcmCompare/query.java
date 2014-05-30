package dcmCompare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.tool.dcmqr.DcmQR;

public class query {

    private String date;
    // private String[] calledAETAddress;
    // private int hostPort;
    private String remoteHost;
    private int remotePort;
    private String calledAETitle;
    ArrayList<String> risultato = new ArrayList<String>();
    private String modality;
    private boolean verbose = DcmCompare.verbose;

    @SuppressWarnings("unused")
    private static String[] split(String s, char delim) {
	String[] s2 = { s, null };
	int pos = s.indexOf(delim);
	if (pos != -1) {
	    s2[0] = s.substring(0, pos);
	    s2[1] = s.substring(pos + 1);
	}
	return s2;
    }

    public query(String date, String remote, String modality) {
	
	this.date = date;
	this.modality = modality;

	varie varie1 = new varie(remote, modality);

	// calledAETAddress = split(remote, '@');
	// calledAETitle = calledAETAddress[0];
	// hostPort = split(calledAETAddress[1], ':');
	// remoteHost = hostPort[0];
	// remotePort = Integer.parseInt(hostPort[1]);

	this.calledAETitle = varie1.getAETitle();
	this.remotePort = varie1.getPort();
	this.remoteHost = varie1.getHost();
	
    }

    public ArrayList<String> doit() throws InterruptedException, IOException, ConfigurationException {

	//System.out.println(remotePort + " " + calledAETitle + " " + remoteHost);
	
	DcmQR q = new DcmQR("DCMQR");

	q.setCalledAET(calledAETitle, true);
	q.setRemoteHost(remoteHost);
	q.setRemotePort(remotePort);
	q.getKeys();

	q.setAcceptTimeout(100000);
	q.setCalling("DCMQR");

	q.setDateTimeMatching(true);

	/*
	 * q.setLocalHost("127.0.0.1"); q.setLocalPort(11112);
	 * q.setConnectTimeout(10000); q.setDimseRspTimeout(10000);
	 * q.setReleaseTimeout(10000); q.setCGet(false);
	 * q.setSocketCloseDelay(10000); q.setTlsNeedClientAuth(false);
	 */

	q.configureTransferCapability(false);

	q.setQueryLevel(DcmQR.QueryRetrieveLevel.STUDY);

	//System.out.println(DcmCompare.additionalquerytag + ".....................");
	

	
	String[] returnKeys = { "PatientName", "PatientID", "ModalitiesInStudy",
		"StudyTime", "Modality", "NumberOfStudyRelatedInstances",
		"NumberOfStudyRelatedSeries", "StudyInstanceUID", };

	for (int i = 0; i < returnKeys.length; i++)
	    q.addReturnKey(Tag.toTagPath(returnKeys[i]));

	String[] matchingKeys = { "ModalitiesInStudy", this.modality,
		"StudyDate", this.date };
	
	
	

	 //System.out.println("aaaa " + Tag.toTagPath(matchingKeys[0]) +
	 //" --- "+ matchingKeys[1].toString());

	for (int i = 1; i < matchingKeys.length; i++, i++) {
	    // int[] rrr = Tag.toTagPath(matchingKeys[i - 1]);
	    // System.out.println(matchingKeys.length + " " +i + "aaaa " +
	    // rrr.toString() + " "
	    // + Tag.toTagPath(matchingKeys[i - 1]) + " --- "
	    // + matchingKeys[i].toString());
	    	    	    
	    q.addMatchingKey(Tag.toTagPath(matchingKeys[i - 1]),
		    matchingKeys[i]);

	}

	if (DcmCompare.additionalquerytag != null) { 
	    for (int i = 1; i < DcmCompare.additionalquerytag.length; i++, i++)
	            q.addMatchingKey(Tag.toTagPath(DcmCompare.additionalquerytag[i - 1]), DcmCompare.additionalquerytag[i]);
	}
	
	
	// System.exit(1);
	if (verbose) {
	    System.out.println("Matching keys \n"+ q.getKeys()+"\n---\n");
	}


	// q.setCFind(false);

	q.open();

	
	//    risultato.add("Failed to establish association:" + e);
	//    return risultato;
	

	

	  //  long t1 = System.currentTimeMillis();

	    List<DicomObject> result = q.query();

	  //  long t2 = System.currentTimeMillis();

	    // System.out.println("Received " + Integer.valueOf(result.size())
	    // + "matching entries in " + Float.valueOf((t2 - t1) / 1000f)
	    // + "s");

	    Iterator<DicomObject> cazzo = result.iterator();
	    
	    iteratore cagnaccio = new iteratore(cazzo);
	    risultato = cagnaccio.itera();

	    q.close();
	
	    return risultato;

    }

}
