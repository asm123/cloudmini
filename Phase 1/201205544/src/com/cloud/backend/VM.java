package com.cloud.backend;
import java.util.HashSet;
import java.util.Map.Entry;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

public class VM 
{
	DomainConfig dcfg;
	Domain domain;
	int vmid;
	public int pmid;
	String vmName;
	int instanceType;
	String imageName;
	
	public VM ()
	{
		vmid = 0;
		pmid = -1;
	}
	
	public int create (String vmName, int instanceType, int imageID)
	{
		this.vmName = vmName;
		this.instanceType = instanceType;
		
		if (imageID >= Global.images.size())
			return 0;
		this.imageName = Global.images.get (imageID);
		
		getHost (instanceType, imageName);
		if (pmid == 0)
		{
			System.out.println ("Could not get a host.");
			return 0;
		}
		try 
		{
			System.out.println ("host: " + pmid + " name: " + Global.pmidToPM.get (pmid).connect.getURI());
		} 
		catch (LibvirtException e) 
		{
			return 0;
		}		
		
		Global.currentPM = this.pmid;
		
		boolean status = configureDomain () && createDomain ();
		if (status)
			return vmid;
		return 0;			
	}
	
	public boolean configureDomain ()
	{		
		// configure domain
		
		long vcpu = Global.instanceType.get (instanceType).cpu;
		Long ram = Global.instanceType.get (instanceType).ram;
		
		Host host = Global.pmidToPM.get (pmid);
		dcfg = new DomainConfig ();
		boolean status = dcfg.defineDomainConfig (host, vmName, vcpu, ram, imageName);
		return status;
	}
	
	public boolean createDomain ()
	{
		try 
		{
			String xmlString = dcfg.toXMLString ();
			Connect connect = Global.pmidToPM.get (pmid).connect;
//			System.out.println ("XML: " + xmlString);
			domain = connect.domainDefineXML (xmlString);
			
			int creationStatus = domain.create ();
			
			if (creationStatus == -1)
			{
				domain.undefine();
				vmid = 0;
				return false;
			}
			
			vmid = Global.vmid++;
			Global.vmidToVM.put (vmid, this);
			Global.vmNameTovmid.put (dcfg.name, vmid);
			Global.vmToPm.put (vmid, pmid);
			Host host = Global.pmidToPM.get (pmid);
			host.resources.ram = host.resources.ram - dcfg.memory;
			host.resources.cpu = host.resources.cpu - dcfg.vcpu;
			
			HashSet <Integer> vms = Global.pmToVm.get (pmid);
			if (vms == null)
				vms = new HashSet <Integer> ();
			vms.add (vmid);
			Global.pmToVm.put (pmid, vms);
		} 
		catch (LibvirtException e) 
		{
//			e.printStackTrace();
	
			vmid = 0;
			return false;
		}
		return true;
	}
	
	public int destroy ()
	{
		try 
		{
			domain.destroy();
			domain.undefine();
			Global.vmToPm.remove (vmid);
			Global.vmNameTovmid.remove (vmName);
			Global.vmidToVM.remove (vmid);
			Global.pmToVm.get (pmid).remove (vmid);
			System.out.println ("vmid: " + vmid + " :" + Global.vmidToVM.get (vmid));
			Host host = Global.pmidToPM.get (pmid);
			host.resources.ram = host.resources.ram + dcfg.memory;
			host.resources.cpu = host.resources.cpu + dcfg.vcpu;
		} 
		catch (LibvirtException e) 
		{
			return 0;
		}
		return 1;
	}
	
	private void getHost (int instanceType, String imageName)
	{
		long vcpu = Global.instanceType.get (instanceType).cpu;
		Long ram = Global.instanceType.get (instanceType).ram;
//		Long disk = Global.instanceType.get (instanceType).disk;
		
		boolean is64 = imageName.contains ("64");
		
		for (Entry <Integer, Host> entry : Global.pmidToPM.entrySet())
		{
			Host host = entry.getValue();
			if (host.connect == null)
				continue;
			if (host.resources.cpu >= vcpu && host.resources.ram >= ram)
			{
				if (is64)
				{
					System.out.println ("here!");
					if (host.is64)
					{
						this.pmid = host.pmid;
						break;
					}
				}
				else
				{
					System.out.println ("here 32!");
					this.pmid = host.pmid;
					break;
				}
			}
		}
		System.out.println ("pmid: " + this.pmid);
	}
}
