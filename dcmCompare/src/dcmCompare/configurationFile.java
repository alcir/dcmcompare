package dcmCompare;

import java.io.FileInputStream;

import java.net.URL;
import java.util.Properties;

public class configurationFile {

	String file;
	
	Properties properties = new Properties();
	
	public configurationFile(String arg) throws Exception {
		
		//file=new File(args);
		
	    	this.file=arg;
	    
		readProperties();
	
	}
	
	@SuppressWarnings("unused")
	private boolean esiste() {
		
		//boolean exists = 
		
		System.out.println("Config file: " + file.toString() );
		/*
		if (exists) {
			System.out.println("The specified configuration file exist.");
		} else {
			// It returns true if File or directory exists
			System.out.println("The specified configuration file does not exist.");
			System.exit(100);
		}
		
		return exists;
		
		*/
		return false;
		
	}
	
	 private void readProperties() throws Exception {
	  	     	
	     	//String filename = ClassLoader.getSystemClassLoader().getResource("/etc/mail.conf").getFile();
	     	
	     	//InputStream filename = Class.getResourceAsStream("mail.conf");
	     	
	     	URL myConfigFile = this.getClass().getClassLoader().getResource(file); 
	     	
	     	//System.out.println("Mail conf file = " + file);
	     	
	     	//System.out.println("Mail conf file = " + myConfigFile.getPath());

	     	
	     	try {
	     	    
			FileInputStream in = new FileInputStream(myConfigFile.getPath());
	
			properties.load(in);
		
			//System.out.println("Configuration file is read successfully.");
			//System.out.println(fileName.toString() + " .... ");
		    }
		    catch (Exception e) {
			
		      System.out.println("\n - " + file + " Configuration file reading failed.\n");
		     
		   //   System.out.println(e);
		      throw new Exception(e);      
		    }
	 }
	 
	 public String getProperty(String propName) {
	     
	    return properties.getProperty(propName);
	    
	 }
	
}