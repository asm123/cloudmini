package com.cloud.initiator;

import com.cloud.rest.RESTServer;

public class Initiator 
{	
	public static void main (String [] args) 
	{
		ResourceDiscovery rd = new ResourceDiscovery (args[0], args[1], args[2]);
		rd.initialSetup();
		
		RESTServer rs = new RESTServer ();
		rs.run ();
	}
}
