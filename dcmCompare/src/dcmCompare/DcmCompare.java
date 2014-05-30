package dcmCompare;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che2.data.Tag;

/**
 * @author alessio
 * @version 
 * @since Apr, 2012
 */
public class DcmCompare {

    private static boolean sendmail;
    private static String modality;
    private static String type;
    private static String subject;
    private static String message;
    private static String version="0.4.10";
    public static boolean verbose = false;
    public static boolean machine = false;
    public static boolean mirth = false;
    public static boolean goodalso = false;
    private static String mailconffile;
    public static String[] additionalquerytag = null;
    
    private static final String USAGE = "DcmCompare <date|fromdate-todate> "
	    + "<aet1>@<host1>:<port1> <aet2>@<host2>:<port2> [Options]\n";

    private static final String DESCRIPTION = "This program compares studies between two DICOM servers.\n"
	    + "Type 1: it queries each server, then it compares the studies doing a \"diff\" between "
	    + "results from server A and server B, and vice versa.\n"
	    + "Type 2: it queries the server A, then for each study it queries server B looking for the "
	    + "presence of the same study.\n"
	    + "\nOptions:\n\n";

    private static final String EXAMPLE = "\n"
	    + "Examples:\n"
	    + "DcmCompare 20120101-20120131 QRSCP@localhost:11112 QRSCP2@otherhost:11112 -m MR -type 1 -mail\n"
	    + "DcmCompare 20120101 QRSCP@localhost:11112 QRSCP2@otherhost:11112 -m MR -type 1 -mail mail.conf\n"
    	    + "DcmCompare YESTERDAY QRSCP@localhost:11112 QRSCP2@otherhost:11112 -type 2\n"
    	    + "DcmCompare TODAY QRSCP@localhost:11112 QRSCP2@otherhost:11112 -type 2 -m DR\n";

    private static void exit(String msg) {
	System.err.println(msg);
	System.err.println("Try 'dcmcompare -h' for more information.");
	System.exit(1);
    }

    private static CommandLine parse(String[] args) {

	Options opts = new Options();

	OptionBuilder.withArgName("config file");
	OptionBuilder.hasArg(true);
	OptionBuilder.withDescription("Send a mail containing the results of the comparison, look at etc/mail.conf for the configuration. "
		+"Please note: the file must be in the etc directory and you must not specify the path (i.e. -mail mymail.conf).");
	opts.addOption(OptionBuilder.create("mail"));

	OptionBuilder.withArgName("m");
	OptionBuilder.hasArg(true);
	OptionBuilder.withDescription("limit modality in study to query");
	opts.addOption(OptionBuilder.create("m"));

	OptionBuilder.withArgName("type");
	OptionBuilder.hasArg(true);
	OptionBuilder.withDescription("type of comparison");
	opts.addOption(OptionBuilder.create("type"));

	OptionBuilder.withArgName("v");
	OptionBuilder.hasArg(false);
	OptionBuilder.withDescription("verbose");
	opts.addOption(OptionBuilder.create("v"));
	
	OptionBuilder.withArgName("machine");
	OptionBuilder.hasArg(false);
	OptionBuilder.withDescription("Output in machine readable format");
	opts.addOption(OptionBuilder.create("machine"));

	OptionBuilder.withArgName("mirth");
	OptionBuilder.hasArg(false);
	OptionBuilder.withDescription("It is mandatory to use -machine. As first line it is printed a result string.");
	opts.addOption(OptionBuilder.create("mirth"));
	
	OptionBuilder.withArgName("goodalso");
	OptionBuilder.hasArg(false);
	OptionBuilder.withDescription("Display not different entries as well");
	opts.addOption(OptionBuilder.create("goodalso"));
	
        OptionBuilder.withArgName("[seq/]attr=value");
        OptionBuilder.hasArgs();
        OptionBuilder.withValueSeparator('=');
        OptionBuilder.withDescription("specify additional matching key. attr can be " +
                "specified by name or tag value (in hex), e.g. PatientName\n" +
                "or 00100010. Attributes in nested Datasets can\n" +
                "be specified by including the name/tag value of\n" +
                "                            the sequence attribute, e.g. 00400275/00400009\n" +
                "for Scheduled Procedure Step ID in the Request\n" +
                "Attributes Sequence");
        opts.addOption(OptionBuilder.create("q"));
	
	opts.addOption("h", "help", false, "print this message");
	opts.addOption("V", "version", false,
		"print the version information and exit");

	CommandLine cl = null;
	
	try {
	    cl = new GnuParser().parse(opts, args);
	} catch (ParseException e) {
	    exit("dcmqr: " + e.getMessage());
	    throw new RuntimeException("unreachable");
	}

	if (cl.hasOption('V')) {
	    // Package p = DcmCompare.class.getPackage();
	    System.out.println("DcmCompare v " + version );
	    System.exit(0);
	}

	if (cl.hasOption('h') || cl.getArgList().size() != 3) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.setWidth(80);
	    formatter.printHelp(USAGE, DESCRIPTION, opts, EXAMPLE);
	    System.exit(0);
	}
	
	if ( cl.getOptionValue("type").equals("1") ) {

	    if ( cl.hasOption("m") ) {
		if ( cl.getOptionValue("m").equals("") || cl.getOptionValue("m").isEmpty() ) {
		    HelpFormatter formatter = new HelpFormatter();
		    formatter.setWidth(80);
		    formatter.printHelp("Using type 1 you must specify a modality\n" + USAGE, DESCRIPTION, opts, EXAMPLE);
		    System.exit(0);
		}
	    } else {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		formatter.printHelp("Using type 1 you must specify a modality\n" + USAGE, DESCRIPTION, opts, EXAMPLE);
		System.exit(0);
	    }
	    
	    
	}
	
	return cl;

    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException, java.text.ParseException, UnsupportedEncodingException {

	CommandLine cl = parse(args);

	if (cl.hasOption("mail")) {
	    sendmail = true;
	    mailconffile = cl.getOptionValue("mail");
	}

	if (cl.hasOption("m")) {
	    modality = cl.getOptionValue("m");
	}
	
	if (cl.hasOption("v")) {
	    verbose  = true;
	}

	if (cl.hasOption("machine")) {
	    machine  = true;
	}

	if (cl.hasOption("mirth")) {
	    mirth  = true;
	}
	
	if (cl.hasOption("goodalso")) {
	    goodalso  = true;
	}
	
        if (cl.hasOption("q")) {
            additionalquerytag = cl.getOptionValues("q");
        }

	
	if (cl.hasOption("type")) {
	    type = cl.getOptionValue("type");
	} else {
	    type = "2";
	}

	// String date = args[0];

	final List<String> argList = cl.getArgList();

	String date = argList.get(0);
	
	if (date.equals("YESTERDAY")) {
	    Calendar cal = Calendar.getInstance();
	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    cal.add(Calendar.DATE, -1);
	    date = dateFormat.format(cal.getTime()).toString();
	}
	
	if (date.equals("TODAY")) {
	    Calendar cal = Calendar.getInstance();
	    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    cal.get(Calendar.DATE);
	    date = dateFormat.format(cal.getTime()).toString();
	}

	String remote1 = argList.get(1);
	String remote2 = argList.get(2);

        if (verbose) {
            System.out.println("arg " + type + " " + modality + " " + sendmail + " " + date
        		+ " " + remote1 + " " + remote2);
        }
	
	// type = "2";

	// String date = "20120101-20120401";
	//
	// String remote1 = "DCM4SCOLL1@192.168.56.103:11112";
	// String remote2 = "PACSBK@192.168.56.104:11112";
	// String modality = "";

	// remote1 = "DCM4CHEE@192.168.56.103:11112";
	// remote2 = "DCM4CHEE@192.168.56.103:11112";

	if (type.equals("1")) {
	    System.out.println("Running type 1");
	    typeOne todo = new typeOne(date, remote1, remote2, modality);
	    String [] result = todo.go();
	    subject = result[0];
	    message = result[1];
	  //  System.out.println("-----\n");
	    System.out.println(subject+"\n");
	    System.out.println(message);
	  //  System.out.println("-----\n");
	    System.out.println(subject);

	} else if (type.equals("2")) {
            if (verbose) { 
                System.out.println("Running type 2");
            }
            //System.out.println(cl.hasOption("machine"));
	    typeTwo todo = new typeTwo(date, remote1, remote2, modality);
	    String [] result = todo.go();
	    subject = result[0];
	    message = result[1];
	    if (machine) {
		if (mirth) {
		    System.out.println(subject.toString());
		}
		System.out.println(message);
	    } else {
        	    System.out.println(subject);
        	    System.out.println(message);
        	    System.out.println(subject);
	    }
	} else {
	    System.out.println("Please specify a valid running method (type)");
	}
	
	//System.out.println("-" + type + "-");
	
	if ( cl.hasOption("mail") ) {
	    
	    // configurationFile mailconf = new configurationFile("mail.conf");
	    
	    configurationFile mailconf;
	    
	    try {
		
		mailconf = new configurationFile(mailconffile);
		    
		System.out.println("---------------" + subject.toString() + "--------------\n");
		  
		new mailer(mailconf, subject.toString(), message.toString());
		    
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    
	}


	
	
    }

}
