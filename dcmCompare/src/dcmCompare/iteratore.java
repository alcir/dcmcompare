package dcmCompare;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.iod.module.composite.PatientModule;


public class iteratore {
	
	ArrayList<String> risultato = new ArrayList<String>();

	private Iterator<DicomObject> cazzo;

	public iteratore(Iterator<DicomObject> cazzo) {
		this.cazzo = cazzo;
	}

	public ArrayList<String> itera() {
		
		while ( cazzo.hasNext()) {
		
		DicomObject i = cazzo.next();
		
		int daTag = Tag.toTag("StudyDate");
		int tmTag = Tag.toTag("StudyTime");					
		
		//GeneralStudyModule study = new GeneralStudyModule(i);
		PatientModule patient = new PatientModule(i);
		//GeneralSeriesModule series = new GeneralSeriesModule(i);
		
		Date date = new Date();
		
		date = i.getDate(daTag, tmTag, date);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
		String s = formatter.format(date);
		
		//System.out.println(s+"----------"+patient.getPatientName()+"--------"+ i.getString(Tag.toTagPath("ModalitiesInStudy")));
		
		String paziente = patient.getPatientName();
		if (paziente.isEmpty() || paziente == "") {
		    paziente = "-";
		}
		
		String riga = paziente + "|"
				+ i.getString(Tag.PatientID) + "|"
				//+ i.getString(Tag.StudyDate) + " " 
				//+ i.getString(Tag.StudyTime) + " "
				//+ study.getStudyDateTime() + " "
				+ i.getString(Tag.toTagPath("ModalitiesInStudy")) + "|"
				+ i.getString(Tag.NumberOfStudyRelatedSeries) + "|" 
				+ i.getString(Tag.NumberOfStudyRelatedInstances) + "|" 
				+ s + "|"
				+ i.getString(Tag.StudyInstanceUID) 
				;
		
		risultato.add(riga);
		
		//System.out.println(riga); 
	}

	return risultato;
		
}
}
