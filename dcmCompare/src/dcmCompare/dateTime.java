package dcmCompare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dateTime {

    private String rigadate;
    private String rigatime;
    private String strDate;
    Date cdate = new Date();
    
    public String convertdatetime(String date, String time) throws ParseException {
	
	this.rigadate = date;
	this.rigatime = time;
	
	strDate = rigadate + " " + rigatime;
	SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd hhmmss");
	cdate = sdfSource.parse(strDate);
	SimpleDateFormat sdfDestination = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	strDate = sdfDestination.format(cdate);
	
	return strDate;
	
    }
    
    public String convertdateonly(String date) throws ParseException {
	
	this.rigadate = date;

	strDate = rigadate ;
	SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd");
	cdate = sdfSource.parse(strDate);
	SimpleDateFormat sdfDestination = new SimpleDateFormat("dd-MM-yyyy");
	strDate = sdfDestination.format(cdate);
	
	return strDate;
	
    }
    
}
