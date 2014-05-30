package dcmCompare;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.tool.dcmqr.DcmQR;

public class typeTwo {

    private String date;
    private String remote1;
    private String remote2;
    private String modality;
    private int stato;
    private int diffamount = 0;
    private dateTime dateTime = new dateTime();
    private StringBuilder strDate = new StringBuilder();
    private boolean verbose = DcmCompare.verbose;
    private boolean machine = DcmCompare.machine;
    private boolean goodalso = DcmCompare.goodalso;
    
    public StringBuilder message = new StringBuilder();
    public StringBuilder subject = new StringBuilder();
    private String [] risultato = new String [2];
    private int laststatus;
    private int nInstancesA;
    private int nSeriesA;

    private String isgreat( int a, int b) {
	if (a > b) {
	    return "more";
	} else {
	    return "less";
	}
    }
    
    public typeTwo(String date, String remote1, String remote2, String modality) {
	this.date = date;
	this.remote1 = remote1;
	this.remote2 = remote2;
	this.modality = modality;
    }

    public String [] go() throws ParseException {

	varie varie1 = new varie(remote1, modality);
	varie varie2 = new varie(remote2, modality);

	StringTokenizer stokDate = new StringTokenizer(date, "-");
	while (stokDate.hasMoreTokens()) {
	    strDate.append(" "
		    + dateTime.convertdateonly(stokDate.nextElement()
			    .toString()));
	}

	subject.append("Compare " + varie1.getAETitle() + " "
		+ varie2.getAETitle() + " " + varie1.getModality() + " "
		+ strDate);
	
	if (DcmCompare.additionalquerytag != null) { 
	
	    for (int i = 1; i < DcmCompare.additionalquerytag.length; i++, i++)
		subject.append(" " + DcmCompare.additionalquerytag[i - 1] + "=" + DcmCompare.additionalquerytag[i]);
	}
	
	if (verbose) {
	    System.out.println("\nQuery to " + varie1.getAETitle());
	}

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
	} catch (InterruptedException e) {
	    subject.insert(0, "ERROR ");
	    message.append("InterruptedException " + e);
	    risultato [0] = subject.toString();
	    risultato [1] = message.toString();
	    return risultato;
	} 
	
	    
	    int sizeA = risultatoA.size();

	    	if (verbose) {
	    	    System.out.println(sizeA + " Studies on " + varie1.getAETitle() + " matching criteria\n");
	    	}
		
	    	if (!machine) {
	    	    message.append(sizeA + " Studies on " + varie1.getAETitle() + " matching criteria\n");
	    	}
	    	
		DcmQR q = new DcmQR("DCMQR");

		String calledAETitle = varie2.getAETitle();
		String remoteHost = varie2.getHost();
		int remotePort = varie2.getPort();

		q.setCalledAET(calledAETitle, true);
		q.setRemoteHost(remoteHost);
		q.setRemotePort(remotePort);
		// q.getKeys();
		q.setAcceptTimeout(100000);
		// q.setCalling("DCMQR");public class unicodeString

		q.setDateTimeMatching(true);
		q.configureTransferCapability(false);
		q.setQueryLevel(DcmQR.QueryRetrieveLevel.STUDY);
		
		if (verbose) {
		    System.out.println("\nStart of the comparison " + calledAETitle);
		}
		
		// + " " + remoteHost + " " + remotePort);

		String[] returnKeys = { "PatientName", "ModalitiesInStudy",
			"StudyTime", "Modality", "NumberOfStudyRelatedInstances",
			"NumberOfStudyRelatedSeries", "StudyInstanceUID", };

		for (int i = 0; i < returnKeys.length; i++)
		    q.addReturnKey(Tag.toTagPath(returnKeys[i]));

		String riga;

		String patientName = null;
		String patientID = null;
		String modInstudy = null;
		String rigadatetime = null;
		String rigadate = null;
		String rigatime = null;
		String siuid = null;
		int nSeries;
		int nInstances;

		StringBuilder allafine = new StringBuilder();

		// q.setCFind(false);

		try {
		    q.open();
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
		} catch (InterruptedException e) {
		    subject.insert(0, "ERROR ");
		    message.append("InterruptedException " + e);
		    risultato [0] = subject.toString();
		    risultato [1] = message.toString();
		    return risultato;
		}

		Iterator<String> salcazzo = risultatoA.iterator();

		configurationFile conf = null;
		
		try {
		    conf = new configurationFile("var.conf");
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		   
		String charset = conf.getProperty("charSet");
		
		while (salcazzo.hasNext()) {
		   
		    riga = salcazzo.next();
		    
		    if (verbose) {
			System.out.print(".");
			System.out.println("..--" + riga + "\n");
		    }
		    
		    StringTokenizer st = new StringTokenizer(riga, "|");
		    patientName = st.nextElement().toString();
		    patientID = st.nextElement().toString();		    
		    modInstudy = st.nextElement().toString();
		    
		    nSeries = Integer.parseInt(st.nextElement().toString());
		   
		    nInstances = Integer.parseInt(st.nextElement().toString());
		   
		    rigadatetime = st.nextElement().toString();
		    StringTokenizer dt = new StringTokenizer(rigadatetime, " ");
		    rigadate = dt.nextElement().toString().replace(".", "");
		    rigatime = dt.nextElement().toString().replace(".", "");
		    siuid = st.nextElement().toString();
		    
		    
		//    System.out.println("...............--" + patientName +  "--..............."+modInstudy);
		    
		    
		    
		    StringBuilder sb = new StringBuilder();
		    
		    if (machine) {
			sb.append(patientName);
			sb.append("," + patientID);
			sb.append("," + modInstudy);
			sb.append("," + nSeries);
			sb.append("," + nInstances);
			sb.append("," + rigadate);
			sb.append("," + rigatime);
			sb.append("," + siuid);
			
			
		    } else {
			sb.append(" " + patientName);
			sb.append("," + patientID);
			sb.append(" " + modInstudy);
			sb.append(" " + nSeries);
			sb.append(" " + nInstances);
			sb.append(" " + rigadate);
			sb.append(" " + rigatime);
			sb.append(" " + siuid);
		    }

		   
		  //  System.out.println(" --- " +sb.toString());

		  //  System.out.println("sbsbsb " + charset);

		    
		    String[] matchingKeys = { "SpecificCharacterSet", charset, "ModalitiesInStudy", modInstudy,
			    "StudyDate", rigadate, "StudyTime", rigatime,
			    "PatientName", patientName, "StudyInstanceUID", siuid };

		    for (int i = 1; i < matchingKeys.length; i++, i++) {
			q.addMatchingKey(Tag.toTagPath(matchingKeys[i - 1]),
				matchingKeys[i]);
		    }

		    List<DicomObject> result = null;

		   // System.out.println("AAAAAAAAAAAAA " + q.getKeys());
		    
		    try {
			result = q.query();
		    } catch (IOException e) {
			    subject.insert(0, "ERROR ");
			    message.append("IOException e " + e);
			    risultato [0] = subject.toString();
			    risultato [1] = message.toString();
			    return risultato;
		    } catch (InterruptedException e) {
			    subject.insert(0, "ERROR ");
			    message.append("InterruptedException " + e);
			    risultato [0] = subject.toString();
			    risultato [1] = message.toString();
			    return risultato;
		    }
		   

		 //   System.out.println(result.size() + "--------------------------" +
		 //   result.size());
		    
		    //System.out.println("--- " +siuid);
		    
		    laststatus = 0;
		    
		    if (result.size() == 0) {
	       		strDate.append(dateTime.convertdatetime(rigadate, rigatime));
	       		//System.out.println(" rs " + result.size());
	       		if (machine) {
	       		        //System.out.println(" --- " +sb.toString());
        	    	       	allafine.append(patientName);
        	    	       	allafine.append("," + patientID);
        		       	allafine.append("," + siuid);
            	       		allafine.append("," + modInstudy 
        	       			+ "," + nInstances
        	       			+ "," + nSeries
        	       			//+ strDate.toString());
        	       			+ "," + dateTime.convertdatetime(rigadate, rigatime));
        	       		allafine.append("," + "not found on " + varie2.getAETitle()
        	       			+ "\n");
    	       		} else {
        	       		allafine.append("\nPatientName: " + patientName);
        	       		allafine.append("\nPatientID" + patientID);
        	       		allafine.append("\n\tSIUID " + siuid);
        	       		allafine.append("\n\t" + modInstudy 
        	       			+ " Inst " + nInstances
        	       			+ " Series " + nSeries
        	       			//+ strDate.toString());
        	       			+ " Datetime " + dateTime.convertdatetime(rigadate, rigatime));
        	       		allafine.append(" " + " not found on " + varie2.getAETitle()
        	       			+ "\n");    	       		    
    	       		}
	       		stato = 1;
	       		diffamount += 1;
	       		laststatus = 1;
	       		if (verbose) { 
	       		    System.out.println(allafine.toString());
	       		}
	       		
		    } else if (result.size() == 1) {
	       		if (nInstances != result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedInstances"))) {
		       		strDate.append(dateTime.convertdatetime(rigadate, rigatime));
		       		if (machine) {
			    	       	allafine.append(patientName);
			    	        allafine.append("," + patientID);
				       	allafine.append("," + siuid);
		    	       		allafine.append("," + modInstudy 
			       			+ "," + nInstances
			       			+ "," + nSeries
			       			//+ strDate.toString());
			       			+ "," + dateTime.convertdatetime(rigadate, rigatime));
		    	       		nInstancesA=result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedInstances"));
			       		allafine.append(",("+ nInstancesA + "," + nInstances
			       			+ ")," + isgreat(nInstancesA, nInstances)
			       			+ "\n");
		    	       	} else {
		    	       	    allafine.append("\nPatientName: " + patientName);
		    	       	    allafine.append("\nPatientID " + patientID);
		    	       	    allafine.append("\n\tSIUID " + siuid);
		    	       	    allafine.append("\n\t" + modInstudy + " date "
		    	       		    //+ strDate.toString());
		    	       		    + dateTime.convertdatetime(rigadate, rigatime));
		    	       	
		    	       	    nInstancesA=result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedInstances"));
		    	       	    
		    	       	    allafine.append(" On "
        	       			    + varie2.getAETitle()
        	       			    + " n° instances not equal to "
        	       			    + varie1.getAETitle()
        	        		    + " ("
        	        		    + nInstancesA
        	        		    + " vs "
        	        		    + nInstances
        	        		    + ") "
        	        		    + isgreat(nInstancesA, nInstances)
        	        		    +"\n");
		    	       	}
		       		
	       		    stato = 2;
	       		    diffamount += 1;
	       		    laststatus = 1;

	        	}
	        	if (nSeries != result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedSeries"))) {
	        	    strDate.append(dateTime.convertdatetime(rigadate, rigatime));
	        	    if (machine) {
		    	       	allafine.append(patientName);
		    	       	allafine.append("," + patientID);
			       	allafine.append("," + siuid);
	    	       		allafine.append("," + modInstudy 
		       			+ "," + nInstances
		       			+ "," + nSeries
		       			//+ strDate.toString());
		       			+ "," + dateTime.convertdatetime(rigadate, rigatime));
	    	       		nSeriesA=result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedSeries"));
	    	       		allafine.append(",("+ nSeriesA + "," + nSeries
	    	       			+ ")," + isgreat(nInstancesA, nInstances)
	    	       			+ "\n");
	    	       	    } else {
        	        	    allafine.append("\nPatientName: " + patientName);
        	        	    allafine.append("\nPatientID " + patientID);
        		       	    allafine.append("\n\tSIUID " + siuid);
        		       	    allafine.append("\n\t" + modInstudy + " date "
        		       			//+ strDate.toString());
        		       			+ dateTime.convertdatetime(rigadate, rigatime));
        	       		    
        		       	    //allafine.append(" " + patientName + " ");
        		       	    //	allafine.append("\n\tSIUID " + siuid);
	    	       	    
        		       	    nSeriesA=result.get(0).getInt(Tag.toTagPath("NumberOfStudyRelatedSeries"));
        		       	    
        		       	    allafine.append(" On "
	        			    + varie2.getAETitle()
	        			    + " n° series not equal to "
	        			    + varie1.getAETitle()
	        			    + " ("
	        			    + nSeriesA
	        			    + " vs "
	        			    + nSeries
	        			    + ") "
	        			    + isgreat(nSeriesA, nSeries)
	        			    + "\n");
	        		    
	    	       	    }
	        	    stato = 3;
	        	    diffamount += 1;
	        	    laststatus = 1;
	        	}
		    } else {
			stato = 4;
			laststatus = 1;
			allafine.append("Something wrong on " + patientName + "\n");
		    }
		    
		    
		   
		    if (laststatus == 0 && goodalso) {
	    	       	allafine.append(patientName);
	    	       	allafine.append("," + patientID);
		       	allafine.append("," + siuid);
    	       		allafine.append("," + modInstudy 
	       			+ "," + nInstances
	       			+ "," + nSeries
	       			//+ strDate.toString());
	       			+ "," + dateTime.convertdatetime(rigadate, rigatime));
	       		allafine.append("," + "OK\n");
		    }
		    
		}

		if (verbose) {
		    System.out.println("\nCLOSE\n");
		}
			 
		try {
		    q.close();
		} catch (InterruptedException e) {
		    subject.insert(0, "ERROR ");
		    message.append("InterruptedException " + e);
		    risultato [0] = subject.toString();
		    risultato [1] = message.toString();
		    return risultato;
		}
		    
		if (stato == 0) {
		    
		    subject.insert(0, "OK ");
		    
		    if (!machine) {
			message.append("\nComparison OK\n\n");
		    }
		    
		    Iterator<String> risultatoAiterator = risultatoA.iterator();

		    if (risultatoA.size() == 0) {
			message.append("Note: 0 studies.");
		    }
		
		    if (!machine) {
			while (risultatoAiterator.hasNext()) {
			    riga = risultatoAiterator.next();
			    message.append(riga.replace("|", " ") + "\n");
			}
		    } else {
			    
			    if (goodalso) {
				message.append("" + allafine.toString());
			    } else {
				message.append("No differences");
			    }
		    }
		    
		} else {
		    subject.insert(0, "WARN ");
		    if (!machine) {
        		    message.append("\nComparison Warn code: " + stato);
        		    message.append("\nDifferences: " + diffamount);
		    }
        	    message.append("" + allafine.toString());
		   
		}
		
		risultato [0] = subject.toString();
		risultato [1] = message.toString();
			
		return risultato;
		
    }
}
