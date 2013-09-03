package com.cloud.backend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Global 
{
	/*	initial setup	*/
	
	public static HashMap <Integer, Host> pmidToPM = new HashMap <Integer, Host> ();
	public static ArrayList <String> images = new ArrayList <String> ();
	public static HashMap <Integer, InstanceType > instanceType = new HashMap <Integer, InstanceType > ();
	
	// hypervior -> guestType
	public static HashMap <String, DomainConfig.Os.Type> guestType = new HashMap <String, DomainConfig.Os.Type> ();
	
	/*	runtime things */ 
	
	// vmid -> pmid
	public static HashMap <Integer, Integer> vmToPm = new HashMap <Integer, Integer> (); 
	public static HashMap <Integer, VM> vmidToVM = new HashMap <Integer, VM> ();
	public static HashMap <String, Integer> vmNameTovmid = new HashMap <String, Integer> (); 
	
	// vms on a pm
	public static HashMap <Integer, HashSet <Integer> > pmToVm = new HashMap <Integer, HashSet <Integer> > ();
	
	public static String pmPath;
	public static String imagePath;
	public static String typesPath;
	
	public static int vmid = 1;
	public static int pmid = 1;
	public static int imageid = 0;
	
	public static int currentPM = 1;
	
	public static void setDriverGuestTypeMapping ()
	{
		guestType.put ("qemu", new DomainConfig.Os.Type ("hvm"));
		guestType.put ("xen", new DomainConfig.Os.Type ("hvm"));
		guestType.put ("uml", new DomainConfig.Os.Type ("uml"));
		guestType.put ("lxc", new DomainConfig.Os.Type ("exe"));
		guestType.put ("vbox", new DomainConfig.Os.Type ("hvm"));
		guestType.put ("openvz", new DomainConfig.Os.Type ("exe"));
		guestType.put ("one", new DomainConfig.Os.Type ("hvm"));
	}
	
	public static DomainConfig.Os.Type getGuestType (String driver)
	{
		return guestType.get (driver);
	}
}