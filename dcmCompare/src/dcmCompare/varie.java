package dcmCompare;

public class varie {

    private static String[] split(String s, char delim) {
        String[] s2 = { s, null };
        int pos = s.indexOf(delim);
        if (pos != -1) {
            s2[0] = s.substring(0, pos);
            s2[1] = s.substring(pos + 1);
        }
        return s2;
    }

	private String[] calledAETAddress;
	private String calledAETitle;
	private String[] hostPort;
	private String remoteHost;
	private int remotePort;
	private String modality;
	
	public varie(String remote, String modality) {
		
		this.calledAETAddress = split(remote, '@');
		this.calledAETitle = calledAETAddress[0];
		this.hostPort = split(calledAETAddress[1], ':');
		this.remoteHost = hostPort[0];
		this.remotePort = Integer.parseInt(hostPort[1]);
		this.modality = modality;
		
        
	}
	
	public String getAETitle () {
		return this.calledAETitle;
	}

	public String getHost () {
		return this.remoteHost;
	}
	
	public int getPort () {
		return this.remotePort;
	}
	
	public String getModality () {
	    
	    	if ( this.modality == null ) {
	    	    return "ANY";
	    	} else {
	    	    return this.modality;
	    	}
	}
    
}
