package com.cloud.backend;

import java.io.StringWriter;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.libvirt.LibvirtException;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "uuid",
    "memory",
    "currentMemory",
    "vcpu",
    "os",
    "clock",
    "onPoweroff",
    "onReboot",
    "onCrash",
    "devices"
})
@XmlRootElement(name = "domain")
public class DomainConfig {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected UUID uuid;
    protected Long memory;
    protected Long currentMemory;
    protected long vcpu;
    @XmlElement(required = true)
    protected DomainConfig.Os os;
    @XmlElement(required = true)
    protected DomainConfig.Clock clock;
    @XmlElement(name = "on_poweroff", nillable = true)
    protected String onPoweroff;
    @XmlElement(name = "on_reboot", nillable = true)
    protected String onReboot;
    @XmlElement(name = "on_crash", nillable = true)
    protected String onCrash;
    @XmlElement(required = true)
    protected DomainConfig.Devices devices;
    @XmlAttribute(name = "type")
    protected String type;

    public DomainConfig ()
    {
    	name = new String ();    	
    	os = new DomainConfig.Os();
    	clock = new DomainConfig.Clock();
    	onPoweroff = new String ("destroy");
    	onReboot = new String ("restart");
    	onCrash = new String ("destroy");
    	devices = new DomainConfig.Devices();
    	type = new String ();
    }
    
    public boolean defineDomainConfig (Host host, String domainName, long vcpu, Long ram, String imageName)
    {
    	boolean img = true;
    	boolean is64 = false;
    	
    	if (imageName.contains ("iso"))
    		img = false;
    	if (imageName.contains ("64"))
    		is64 = true;
    	
    	uuid = UUID.randomUUID();
    	this.setName (domainName);
		this.setUuid (uuid);
		this.setMemory (ram * 1024);
		this.setCurrentMemory (this.memory);
		this.setVcpu (vcpu);
		
		String driver = "qemu";
		try 
		{
			driver = host.connect.getType().toLowerCase();
		} 
		catch (LibvirtException e) 
		{
			return false;
//			e.printStackTrace();
		}
		
		DomainConfig.Os.Type osType = Global.getGuestType (driver); 
		
		this.setType (driver);
		this.os.setType (osType);
		this.os.type.setMachine ("pc-1.0");
		this.os.type.setValue (osType.value.toString());
	
		this.setOnPoweroff ("destroy");
		this.setOnReboot ("restart");
		this.setOnCrash ("destroy");
		
		if (is64)
		{
			boolean found64 = false;
			for (Capabilities.Guest guest : host.capabilities.guest)
			{
				if (guest.arch.name.contains ("64"))
				{
					this.os.type.setArch (guest.arch.name);
					this.devices.setEmulator (guest.arch.emulator);
					found64 = true;
					break;
				}
			}
			if (!found64)
			{
				Capabilities.Guest guest = host.capabilities.guest.get (0);
				this.os.type.setArch (guest.arch.name);
				this.devices.setEmulator (guest.arch.emulator);
			}
		}
		else
		{
			Capabilities.Guest guest = host.capabilities.guest.get (0);
			this.os.type.setArch (guest.arch.name);
			this.devices.setEmulator (guest.arch.emulator);
		}
		
//		this.devices.setEmulator ("/usr/bin/qemu-system-x86_64");
				
		DomainConfig.Devices.Disk d = new DomainConfig.Devices.Disk ();
		
		d.setType ("file");
		
		if (img)
		{
			this.os.boot.setDev ("hd");
			d.setDevice ("disk");
		}
		else
		{
			this.os.boot.setDev ("cdrom");
			d.setDevice ("cdrom");
		}
		d.driver.setName (driver);
		d.driver.setType ("raw");
		d.source.setFile ("/home/" + imageName);
		d.target.setDev ("hda");
		d.target.setBus ("ide");

/*		d.address.setType ("drive");
		d.address.setController (0);
		d.address.setBus (1);
		d.address.setUnit (0);
*/			
		this.devices.setDisk (d);
		
		this.devices.input.setType ("mouse");
		this.devices.input.setBus ("ps2");
		
/*		this.devices._interface.setType ("network");
		this.devices._interface.source.setNetwork ("default");
*/		
		this.devices.graphics.setType ("vnc");
		this.devices.graphics.setPort (-1);
		this.devices.graphics.setAutoport ("yes");
					
		this.devices.memballoon.setModel ("virtio");
		
		return true;
    }
    
    public String toXMLString ()
    {
    	String xml = null;
    	try 
    	{
    		StringWriter sw = new StringWriter ();
    		JAXBContext jaxbContext = JAXBContext.newInstance (DomainConfig.class);
    		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
     
    		jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
    		jaxbMarshaller.marshal (this, sw);
    		
    		xml = sw.toString();
    	}
    	catch (JAXBException e) 
    	{
    		e.printStackTrace();
    	}
    	return xml;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String value) {
    	if (this.name == null)
    		this.name = new String ();
        this.name = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID value) {
        this.uuid = value;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long ram) {
    	this.memory = ram;
    }

    public Long getCurrentMemory() {
        return currentMemory;
    }

    public void setCurrentMemory(Long ram) {
        this.currentMemory = ram;
    }

    public long getVcpu() {
        return vcpu;
    }

    public void setVcpu (long vcpu2) {
        this.vcpu = vcpu2;
    }

    public DomainConfig.Os getOs() {
        return os;
    }

    public void setOs(DomainConfig.Os value) {
        if (this.os == null)
        	this.os = new DomainConfig.Os();
    	this.os = value;
    }

    public DomainConfig.Clock getClock() {
        return clock;
    }

    public void setClock (DomainConfig.Clock value) {
    	if (this.clock == null)
        	this.clock = new DomainConfig.Clock();
        this.clock = value;
    }

    public String getOnPoweroff() {
        return onPoweroff;
    }

    public void setOnPoweroff(String value) {
    	if (this.onPoweroff == null)
        	this.onPoweroff = new String();
        this.onPoweroff = value;
    }

    public String getOnReboot() {
        return onReboot;
    }

    public void setOnReboot(String value) {
    	if (this.onReboot == null)
        	this.onReboot = new String();
        this.onReboot = value;
    }

    public String getOnCrash() {
        return onCrash;
    }

    public void setOnCrash(String value) {
    	if (this.onCrash == null)
        	this.onCrash = new String();
    	this.onCrash = value;
    }

    public DomainConfig.Devices getDevices() {
        return devices;
    }

    public void setDevices(DomainConfig.Devices value) {
    	if (this.devices == null)
    		this.devices = new DomainConfig.Devices();
        this.devices = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Clock {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "offset")
        protected String offset;

        public Clock ()
        {
        	value = new String ("utc");
        	offset = new String ();
        }
        
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String value) {
            this.offset = value;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "emulator",
        "disk",
//        "serial",
//        "console",
        "input",
        "graphics",
        "memballoon"
    })
    public static class Devices {

        @XmlElement(required = true)
        protected String emulator;
        @XmlElement(required = true)
        protected DomainConfig.Devices.Disk disk;
/*        @XmlElement(required = true)
        protected DomainConfig.Devices.Serial serial;*/
/*        @XmlElement(required = true)
        protected DomainConfig.Devices.Console console;*/
        @XmlElement(required = true)
        protected DomainConfig.Devices.Input input;
        @XmlElement(required = true)
        protected DomainConfig.Devices.Graphics graphics;
        @XmlElement(required = true)
        protected DomainConfig.Devices.Memballoon memballoon;

        public Devices ()
        {
        	emulator = new String ();
        	disk = new DomainConfig.Devices.Disk ();
//        	serial = new DomainConfig.Devices.Serial ();
//        	console = new DomainConfig.Devices.Console ();
        	input = new DomainConfig.Devices.Input ();
        	graphics = new DomainConfig.Devices.Graphics ();
        	memballoon = new DomainConfig.Devices.Memballoon ();
        }
        
        public String getEmulator() {
            return emulator;
        }

        public void setEmulator(String value) {
            this.emulator = value;
        }

        public DomainConfig.Devices.Disk getDisk() {
            return disk;
        }

        public void setDisk(DomainConfig.Devices.Disk value) {
        	this.disk = new DomainConfig.Devices.Disk();
            this.disk = value;
        }

        /*public DomainConfig.Devices.Serial getSerial() {
            return serial;
        }*/

        /*public void setSerial(DomainConfig.Devices.Serial value) {
            this.serial = value;
        }*/

/*        public DomainConfig.Devices.Console getConsole() {
            return console;
        }*/

        /*public void setConsole(DomainConfig.Devices.Console value) {
            this.console = value;
        }*/

        public DomainConfig.Devices.Input getInput() {
            return input;
        }

        public void setInput(DomainConfig.Devices.Input value) {
        	this.input = new DomainConfig.Devices.Input();
            this.input = value;
        }

        public DomainConfig.Devices.Graphics getGraphics() {
            return graphics;
        }

        public void setGraphics(DomainConfig.Devices.Graphics value) {
        	this.graphics = new DomainConfig.Devices.Graphics();
            this.graphics = value;
        }

        public DomainConfig.Devices.Memballoon getMemballoon() {
            return memballoon;
        }

        public void setMemballoon(DomainConfig.Devices.Memballoon value) {
        	this.memballoon = new DomainConfig.Devices.Memballoon();
            this.memballoon = value;
        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "target"
        })
        public static class Console {

            @XmlElement(required = true)
            protected DomainConfig.Devices.Console.Target target;
            @XmlAttribute(name = "type")
            protected String type;

            public Console ()
            {
            	target = new DomainConfig.Devices.Console.Target ();
            	type = new String ();
            }
            
            public DomainConfig.Devices.Console.Target getTarget() {
                return target;
            }

            public void setTarget(DomainConfig.Devices.Console.Target value) {
            	this.target = new DomainConfig.Devices.Console.Target();
                this.target = value;
            }

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Target {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "type")
                protected String type;
                @XmlAttribute(name = "port")
                protected int port;

                public Target ()
                {
                	value = new String ();
                	type = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getType() {
                    return type;
                }

                public void setType(String value) {
                    this.type = value;
                }

                public int getPort() {
                    return port;
                }

                public void setPort(int value) {
                    this.port = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "driver",
            "source",
            "target"
//            "address"
        })
        public static class Disk {

            @XmlElement(required = true)
            protected DomainConfig.Devices.Disk.Driver driver;
            @XmlElement(required = true)
            protected DomainConfig.Devices.Disk.Source source;
            @XmlElement(required = true)
            protected DomainConfig.Devices.Disk.Target target;
/*            @XmlElement(required = true)
            protected DomainConfig.Devices.Disk.Address address;
 */
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "device")
            protected String device;

            public Disk ()
            {
            	driver = new DomainConfig.Devices.Disk.Driver ();
            	source = new DomainConfig.Devices.Disk.Source ();
            	target = new DomainConfig.Devices.Disk.Target ();
 /*           	address = new DomainConfig.Devices.Disk.Address ();
            	type = new String ();
            	device = new String ();
   */
            }
            
            public DomainConfig.Devices.Disk.Driver getDriver() {
                return driver;
            }

            public void setDriver(DomainConfig.Devices.Disk.Driver value) {
            	this.driver = new DomainConfig.Devices.Disk.Driver();
                this.driver = value;
            }

            public DomainConfig.Devices.Disk.Source getSource() {
                return source;
            }

            public void setSource(DomainConfig.Devices.Disk.Source value) {
            	this.source = new DomainConfig.Devices.Disk.Source();
                this.source = value;
            }

            public DomainConfig.Devices.Disk.Target getTarget() {
                return target;
            }

            public void setTarget(DomainConfig.Devices.Disk.Target value) {
            	this.target = new DomainConfig.Devices.Disk.Target();
                this.target = value;
            }

            /*public DomainConfig.Devices.Disk.Address getAddress() {
                return address;
            }*/

            /*public void setAddress(DomainConfig.Devices.Disk.Address value) {
                this.address = value;
            }*/

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }

            public String getDevice() {
                return device;
            }

            public void setDevice(String value) {
                this.device = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Address {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "type")
                protected String type;
                @XmlAttribute(name = "controller")
                protected int controller;
                @XmlAttribute(name = "bus")
                protected int bus;
                @XmlAttribute(name = "unit")
                protected int unit;

                public Address ()
                {
                	value = new String ();
                	type = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getType() {
                    return type;
                }

                public void setType(String value) {
                    this.type = value;
                }

                public int getController() {
                    return controller;
                }

                public void setController(int i) {
                    this.controller = i;
                }

                public int getBus() {
                    return bus;
                }

                public void setBus(int i) {
                    this.bus = i;
                }

                public int getUnit() {
                    return unit;
                }

                public void setUnit(int i) {
                    this.unit = i;
                }
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Driver {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "name")
                protected String name;
                @XmlAttribute(name = "type")
                protected String type;

                public Driver ()
                {
                	value = new String ();
                	name = new String ();
                	type = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getName() {
                    return name;
                }

                public void setName(String value) {
                    this.name = value;
                }

                public String getType() {
                    return type;
                }

                public void setType(String value) {
                    this.type = value;
                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Source {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "file")
                protected String file;

                public Source ()
                {
                	value = new String ();
                	file = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getFile() {
                    return file;
                }

                public void setFile(String value) {
                    this.file = value;
                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Target {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "dev")
                protected String dev;
                @XmlAttribute(name = "bus")
                protected String bus;

                public Target ()
                {
                	value = new String ();
                	dev = new String ();
                	bus = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getDev() {
                    return dev;
                }

                public void setDev(String value) {
                    this.dev = value;
                }

                public String getBus() {
                    return bus;
                }

                public void setBus(String value) {
                    this.bus = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Graphics {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "port")
            protected int port;
            @XmlAttribute(name = "autoport")
            protected String autoport;

            public Graphics ()
            {
            	value = new String ();
            	type = new String ();
            	autoport = new String ();
            }
            
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int i) {
                this.port = i;
            }

            public String getAutoport() {
                return autoport;
            }

            public void setAutoport(String value) {
                this.autoport = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Input {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "bus")
            protected String bus;

            public Input ()
            {
            	value = new String ();
            	type = new String ();
            	bus = new String ();
            }
            
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }

            public String getBus() {
                return bus;
            }

            public void setBus(String value) {
                this.bus = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
//            "address"
        })
        public static class Memballoon {

/*            @XmlElement(required = true)
            protected DomainConfig.Devices.Memballoon.Address address;
*/            @XmlAttribute(name = "model")
            protected String model;

            public Memballoon ()
            {
//            	address = new DomainConfig.Devices.Memballoon.Address ();
            	model = new String ();
            }
            
 /*           public DomainConfig.Devices.Memballoon.Address getAddress() {
                return address;
            }
*/
            /*public void setAddress(DomainConfig.Devices.Memballoon.Address value) {
                this.address = value;
            }*/

            public String getModel() {
                return model;
            }

            public void setModel(String value) {
                this.model = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Address {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "type")
                protected String type;
                @XmlAttribute(name = "domain")
                protected String domain;
                @XmlAttribute(name = "bus")
                protected String bus;
                @XmlAttribute(name = "slot")
                protected String slot;
                @XmlAttribute(name = "function")
                protected String function;

                public Address ()
                {
                	value = new String ();
                	type = new String ();
                	domain = new String ();
                	bus = new String ();
                	slot = new String ();
                	function = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getType() {
                    return type;
                }

                public void setType(String value) {
                    this.type = value;
                }

                public String getDomain() {
                    return domain;
                }

                public void setDomain(String value) {
                    this.domain = value;
                }

                public String getBus() {
                    return bus;
                }

                public void setBus(String value) {
                    this.bus = value;
                }

                public String getSlot() {
                    return slot;
                }

                public void setSlot(String value) {
                    this.slot = value;
                }

                public String getFunction() {
                    return function;
                }

                public void setFunction(String value) {
                    this.function = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "target"
        })
        public static class Serial {

            @XmlElement(required = true)
            protected DomainConfig.Devices.Serial.Target target;
            @XmlAttribute(name = "type")
            protected String type;

            public Serial ()
            {
            	target = new DomainConfig.Devices.Serial.Target ();
            	type = new String ();
            }
            
            public DomainConfig.Devices.Serial.Target getTarget() {
                return target;
            }

            public void setTarget(DomainConfig.Devices.Serial.Target value) {
            	this.target = new DomainConfig.Devices.Serial.Target ();
                this.target = value;
            }

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Target {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "port")
                protected int port;

                public Target ()
                {
                	value = new String ();
                }
                
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public int getPort() {
                    return port;
                }

                public void setPort(Byte value) {
                    this.port = value;
                }

            }

        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "type",
        "boot"
    })
    public static class Os {

        @XmlElement(required = true)
        protected DomainConfig.Os.Type type;
        @XmlElement(required = true)
        protected DomainConfig.Os.Boot boot;

        public Os ()
        {
        	type = new DomainConfig.Os.Type ();
        	boot = new DomainConfig.Os.Boot ();
        }
        
        public DomainConfig.Os.Type getType() {
            return type;
        }

        public void setType(DomainConfig.Os.Type value) {
        	this.type = new DomainConfig.Os.Type();
        	this.type = value;
        }

        public DomainConfig.Os.Boot getBoot() {
            return boot;
        }

        public void setBoot(DomainConfig.Os.Boot value) {
        	this.boot = new DomainConfig.Os.Boot();
            this.boot = value;
        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Boot {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "dev")
            protected String dev;

            public Boot ()
            {
            	value = new String ();
            	dev = new String ();
            }
            
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getDev() {
                return dev;
            }

            public void setDev(String value) {
                this.dev = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Type {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "arch")
            protected String arch;
            @XmlAttribute(name = "machine")
            protected String machine;

            public Type (String value) {
            	this.value = value;
			}

			public Type() {
				value = new String ();
				arch = new String ();
				machine = new String ("pc-1.0");
			}

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getArch() {
                return arch;
            }

            public void setArch(String value) {
                this.arch = value;
            }

            public String getMachine() {
                return machine;
            }

            public void setMachine(String value) {
            	if (this.machine == null)
            		this.machine = new String ();
                this.machine = value;
            }

        }

    }

}
