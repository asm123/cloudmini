package com.cloud.backend;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Host
{
	public class Resources 
	{
		public long cpu;
		public long ram; // MB -> KB
		public long disk; // GB -> KB
		
		public Resources ()
		{
			cpu = 1;
			ram = 512 * 1024;
			disk = 0;
		}
	}
	
	public Resources resources = new Resources ();
	public Connect connect;
	public String hostName; 
	protected Host.Cpu cpu;
	protected Host.MigrationFeatures migrationFeatures;
	protected Host.Topology topology;
	protected Capabilities capabilities;
	public int pmid;
	public boolean is64;
	public HashSet <Integer> vm = new HashSet <Integer> ();
	
	public Host ()
	{
		hostName = new String ();
		cpu = new Host.Cpu();
		migrationFeatures = new Host.MigrationFeatures();
		topology = new Host.Topology();
		capabilities = new Capabilities ();
	}
	
	public void setCapabilitiesInfo ()
	{	
		try 
		{
			String capabilitiesStr = connect.getCapabilities();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse (new InputSource (new StringReader (capabilitiesStr)));
			
			doc.getDocumentElement().normalize();
			
			NodeList hostList = doc.getElementsByTagName ("host");
			for (int host = 0; host < hostList.getLength(); host++) // all hosts
			{
				NodeList hostNodeList = hostList.item (host).getChildNodes();
				for (int hostNode = 0; hostNode < hostNodeList.getLength(); hostNode++) // under host
				{
					Node hostChild = hostNodeList.item (hostNode);
					setHostProperties (hostChild);
				}
			}
			
			NodeList guestList = doc.getElementsByTagName ("guest");
			this.capabilities.guest = new ArrayList <Capabilities.Guest> ();
			for (int guestNo = 0; guestNo < guestList.getLength(); guestNo++) // all guests
			{
				Capabilities.Guest guest = new Capabilities.Guest();
				Node guestNode = guestList.item (guestNo);
				setGuestProperties (guest, guestNode);
				this.capabilities.guest.add (guest);
				if (guest.arch.name.contains ("64"))
					this.is64 = true;
			}
			
			this.resources.cpu = this.connect.getMaxVcpus (this.connect.getType());
			System.out.println ("CPU: " + this.resources.cpu);
			this.resources.ram = 2 * this.connect.nodeInfo().memory;
			
			/*for (int guestNo = 0; guestNo < guestList.getLength(); guestNo++) // all guests
			{
				Capabilities.Guest guest = this.capabilities.guest.get (guestNo);
				System.out.println ("os type: " + guest.osType);
				System.out.println ("arch: " + guest.arch.name);
			}*/
		} 
		catch (LibvirtException e) 
		{
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Host.Cpu getCpu() 
	{	
		return cpu;	
	}
        
	public void setCpu (Host.Cpu value)	
	{	
		this.cpu = value;	
	}
	
	public Host.MigrationFeatures getMigrationFeatures() 
    {
		return migrationFeatures;
	}
	
	public void setMigrationFeatures (Host.MigrationFeatures value)
	{
		this.migrationFeatures = value;
	}

    public Host.Topology getTopology()
    {
    	return topology;
    }

    public void setTopology (Host.Topology value)
    {
            this.topology = value;
    }

    public static class Cpu 
    {
    	protected String arch;
    	
    	public Cpu ()
    	{
    		arch = new String ("i686");
    	}
    	
    	public Cpu (String arch)
    	{
    		this.arch = arch;
    	}
    	
    	public String getArch()
    	{
    		return arch;
    	}

    	public void setArch (String value)
    	{
                this.arch = value;
        }
    }

    public static class MigrationFeatures
    {
    	protected boolean live;
    	protected Host.MigrationFeatures.UriTransports uriTransports;
    	
    	public MigrationFeatures() 
    	{
    		live = false;
    		uriTransports = new Host.MigrationFeatures.UriTransports();
		}

		public boolean getLive()
    	{
    		return live;
    	}

    	public void setLive (boolean value) 
        {
    		this.live = value;
    	}

    	public Host.MigrationFeatures.UriTransports getUriTransports()
    	{
    		return uriTransports;
    	}

    	public void setUriTransports (Host.MigrationFeatures.UriTransports value)
    	{
    		this.uriTransports = value;
    	}
    	
    	public static class UriTransports
    	{
    		protected String uriTransport;
    		
    		public UriTransports ()
    		{
    			uriTransport = new String ("tls");
    		}
    		
    		public String getUriTransport() 
            {
                return uriTransport;
            }

            public void setUriTransport (String value) 
            {
            	this.uriTransport = value;
            }
        }
    }
    
    public static class Topology
    {
    	protected Host.Topology.Cells cells;
    	
    	public Topology ()
    	{
    		cells = new Host.Topology.Cells();
    	}
    	
    	public Host.Topology.Cells getCells()
    	{
    		return cells;
    	}
    	
    	public void setCells (Host.Topology.Cells value)
    	{
    		this.cells = value;
    	}
    	
    	public static class Cells
    	{
    		protected Host.Topology.Cells.Cell cell;
    		protected int num;
    		
    		public Cells ()
    		{
    			cell = new Host.Topology.Cells.Cell();
    			num = 1;
    		}
    		
    		public Host.Topology.Cells.Cell getCell()
    		{
    			return cell;
    		}

    		public void setCell (Host.Topology.Cells.Cell value)
    		{
    			this.cell = value;
    		}

    		public int getNum() 
            {
    			return num;
    		}

    		public void setNum (int value)
    		{
    			this.num = value;
    		}

    		public static class Cell
    		{
    			protected Host.Topology.Cells.Cell.Cpus cpus;
    			protected int id;
    			
    			public Cell ()
    			{
    				cpus = new Host.Topology.Cells.Cell.Cpus();
    				id = 0;
    			}

    			public Host.Topology.Cells.Cell.Cpus getCpus()
    			{
    				return cpus;
    			}

    			public void setCpus (Host.Topology.Cells.Cell.Cpus value)
    			{
    				this.cpus = value;
    			}

    			public int getId()
    			{
    				return id;
    			}

    			public void setId (int value)
    			{
    				this.id = value;
    			}

    			public static class Cpus
    			{
    				protected List <Host.Topology.Cells.Cell.Cpus.Cpu> cpu;
    				protected int num;
    				             
    				public Cpus ()
    				{
    					cpu = new ArrayList <Host.Topology.Cells.Cell.Cpus.Cpu> ();
    					num = 1;
    				}
    				
    				public List <Host.Topology.Cells.Cell.Cpus.Cpu> getCpu()
    				{
    					if (cpu == null)
    						cpu = new ArrayList <Host.Topology.Cells.Cell.Cpus.Cpu> ();
    					return this.cpu;
    				}

    				public int getNum()
    				{
    					return num;
    				}

    				public void setNum (Byte value)
    				{
    					this.num = value;
    				}
                    
    				public static class Cpu
    				{
    					protected String value;
    					protected int id;
    					
    					public Cpu ()
    					{
    						value = new String ();
    						id = 0;
    					}
    					
    					public String getValue()
    					{
    						return value;
    					}

    					public void setValue (String value)
    					{
    						this.value = value;
    					}
    					
    					public int getId()
    					{
    						return id;
    					}

    					public void setId (Byte value)
    					{
    						this.id = value;
    					}
    				}
    			}
    		}
    	}
    }
     
    private void setHostProperties (Node hostChild)
	{
		String hostChildName = hostChild.getNodeName();
		
		if (hostChildName.equalsIgnoreCase ("cpu"))
		{
			setCpu (hostChild);
		}
		else if (hostChildName.equalsIgnoreCase ("migration_features"))
		{
			setMigrationFeatures (hostChild);
		}
	}
	
	private void setGuestProperties (Capabilities.Guest g, Node guest)
	{
		NodeList guestChildList = guest.getChildNodes();
		for (int guestChildNo = 0; guestChildNo < guestChildList.getLength(); guestChildNo++)
		{
			Node guestChild = guestChildList.item (guestChildNo);
			String guestChildName = guestChild.getNodeName();
			if (guestChildName.equalsIgnoreCase ("os_type"))
			{
				String osType = guestChild.getTextContent();
				if (osType.length() > 0)
				{
					g.setOsType (osType);
				}
			}
			else if (guestChildName.equalsIgnoreCase ("arch"))
			{
				Element arch = (Element) guestChild;
				String archName;
				if (arch.hasAttribute ("name"))
					archName = arch.getAttribute ("name");
				else
					archName = arch.getTextContent();
				g.arch.setName (archName);
				
				NodeList archChildList = guestChild.getChildNodes();
				for (int archChildNo = 0; archChildNo < archChildList.getLength(); archChildNo++)
				{
					Node archChild = archChildList.item (archChildNo);
					String archChildName = archChild.getNodeName();
					
					if (archChildName.equalsIgnoreCase ("wordsize"))
					{
						byte wordSize = 32;
						String value = archChild.getTextContent();
						if (value.length() > 0)
							wordSize = Byte.parseByte (value);
						g.arch.setWordSize (wordSize);
						if (wordSize == 64)
							this.is64 = true;
					}
					else if (archChildName.equalsIgnoreCase ("emulator"))
					{
						String emulator = archChild.getTextContent();
						if (emulator.length() > 0)
							g.arch.setEmulator (emulator);
					}
					else if (archChildName.equalsIgnoreCase ("machine"))
					{
						String machine = archChild.getTextContent();
						if (machine.length() > 0)
							g.arch.machine.add (machine);
					}
					else if (archChildName.equalsIgnoreCase ("domain"))
					{
						Capabilities.Guest.Arch.Domain dom = new Capabilities.Guest.Arch.Domain (); 
						Element domain = (Element) archChild;
						String domainType = domain.getAttribute ("type");
						if (domainType.length() > 0)
							dom.setType (domainType);
						g.arch.domain.add (dom);
					}
				}
			}
/*			else if (guestChildName.equalsIgnoreCase ("features"))
			{
				NodeList featureList = guestChild.getChildNodes();
				for (int featureNo = 0; featureNo < featureList.getLength(); featureNo++)
				{
					Node feature = featureList.item (featureNo);
					String featureName = feature.getNodeName();
					if (featureName.equalsIgnoreCase ("pae"))
						g.features.pae = true;
					else if (featureName.equalsIgnoreCase ("nonpae"))
						g.features.nonpae = true;
					else if (featureName.equalsIgnoreCase ("acpi"))
					{
						Element ele = (Element) feature;
						String _default = ele.getAttribute ("default"); 
						g.features.acpi.setDefault (_default);
						String toggle = ele.getAttribute ("toggle"); 
						g.features.acpi.setDefault (toggle);
					}
				}
			}
*/		}
	}
	
	private void setCpu (Node cpuNode)
	{		
		NodeList cpuChildList = cpuNode.getChildNodes();
		for (int cpuChildNo = 0; cpuChildNo < cpuChildList.getLength(); cpuChildNo++)
		{
			Node cpuChild = cpuChildList.item (cpuChildNo);
			String cpuChildName = cpuChild.getNodeName();
			if (cpuChildName.equalsIgnoreCase ("arch"))
			{
				String value = cpuChild.getTextContent();
				if (value.length() > 0)
				{
					this.cpu.setArch (value);
					if (this.cpu.arch.contains ("64"))
						this.is64 = true;
				}
			}
		}
	}
	
	private void setMigrationFeatures (Node mfNode)
	{
		NodeList mfChildList = mfNode.getChildNodes();
		for (int mfChildNo = 0; mfChildNo < mfChildList.getLength(); mfChildNo++)
		{
			Node mfChild = mfChildList.item (mfChildNo);
			String mfChildName = mfChild.getNodeName();
			if (mfChildName.equalsIgnoreCase ("live"))
				this.migrationFeatures.setLive (true);
			else if (mfChildName.equalsIgnoreCase ("uri_transports"))
			{
				NodeList uriChildList = mfChild.getChildNodes();
				for (int uriChildNo = 0; uriChildNo < uriChildList.getLength(); uriChildNo++)
				{
					Node uriChild = uriChildList.item (uriChildNo);
					String uriChildName = uriChild.getNodeName();
					if (uriChildName.equalsIgnoreCase ("uri_transport"))
					{
						String value = uriChild.getTextContent ();
						if (value.length() > 0)
							this.migrationFeatures.uriTransports.setUriTransport (value);
					}
				}
			}
		}
	}
}