package com.cloud.initiator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;

import com.cloud.backend.Global;
import com.cloud.backend.Host;
import com.cloud.backend.InstanceType;

public class ResourceDiscovery
{	
	public ResourceDiscovery (String pm, String image, String type)
	{
		Global.pmPath = pm;
		Global.imagePath = image;
		Global.typesPath = type;
	}
		
	public void initialSetup () // run only once
	{
		// Driver -> Guest type
		// Arch -> host
		// Host -> Hypervisor
		// VM -> Host
		
		Global.setDriverGuestTypeMapping();
		setHostsInfo ();
		setImagesInfo ();
		setInstanceTypesInfo ();		
	}
	
	private void setImagesInfo() 
	{
		try
		{
			File imageFile = new File (Global.imagePath);
			BufferedReader br = new BufferedReader (new FileReader (imageFile));
			String line = new String ();
			while ((line = br.readLine ()) != null)
			{
				Global.images.add (line);
			}
			br.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setHostsInfo ()
	{
		File pmFile = new File (Global.pmPath);

		try
		{
			BufferedReader br = new BufferedReader (new FileReader (pmFile));
			String ip = new String ();
			while ((ip = br.readLine ()) != null)
			{
				int index = ip.indexOf ('@');
				if (index == -1)
					continue;
				ip = ip.substring (index + 1).trim();
				System.out.println ("IP: " + ip);
				String uri = "remote+ssh://" + ip + "/system";
				Connect connect = new Connect (uri, false);
				Host host = new Host ();
				host.connect = connect;
				
				if (connect != null)
				{
					host.setCapabilitiesInfo();
					host.pmid = Global.pmid++;
					Global.pmidToPM.put (host.pmid, host);
				}
			}
			br.close ();
		}
		catch (IOException e)
		{
			
		} 
		catch (LibvirtException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void setInstanceTypesInfo ()
	{
		JSONParser parser = new JSONParser();	 
		try 
		{
			JSONObject jsonObject = (JSONObject) parser.parse (new FileReader (Global.typesPath));;
			JSONArray msg = (JSONArray) jsonObject.get ("types");
			Iterator <?> it = msg.iterator();
			
			while (it.hasNext())
			{
				JSONObject obj = (JSONObject) it.next();
				
				int tid = Integer.parseInt (obj.get ("tid").toString());
				long cpu = Long.parseLong (obj.get ("cpu").toString());
				long ram = Long.parseLong (obj.get ("ram").toString());
				long disk = Long.parseLong (obj.get ("disk").toString());

				InstanceType instanceType = new InstanceType (tid, cpu, ram, disk);
				Global.instanceType.put (tid, instanceType);				
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}	
	}
}
