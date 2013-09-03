package com.cloud.backend;
import java.util.ArrayList;
import java.util.List;

public class Capabilities 
{
    protected List <Capabilities.Guest> guest;
    
    public Capabilities ()
    {
    	guest = new ArrayList <Capabilities.Guest> ();
    }
    
    public List <Capabilities.Guest> getGuest() 
    {
        if (guest == null) 
        {
            guest = new ArrayList <Capabilities.Guest> ();
        }
        return this.guest;
    }

    public static class Guest 
    {
        protected String osType;
        protected Capabilities.Guest.Arch arch;
        protected Capabilities.Guest.Features features;

        public Guest ()
        {
        	osType = new String ("hvm");
        	arch = new Capabilities.Guest.Arch();
        	features = new Capabilities.Guest.Features();
        }
        
        public String getOsType() 
        {
            return osType;
        }

        public void setOsType (String value) 
        {
            this.osType = value;
        }

        public Capabilities.Guest.Arch getArch() 
        {
            return arch;
        }

        public void setArch (Capabilities.Guest.Arch value) 
        {
            this.arch = value;
        }

        public Capabilities.Guest.Features getFeatures() 
        {
            return features;
        }

        public void setFeatures (Capabilities.Guest.Features value) 
        {
            this.features = value;
        }


        public static class Arch 
        {
            protected byte wordSize;
            protected String emulator;
            protected List <String> machine;
            protected List <Capabilities.Guest.Arch.Domain> domain;
            protected String name;

            public Arch ()
            {
            	wordSize = 32;
            	emulator = new String ("/usr/bin/qemu");
            	machine = new ArrayList <String> ();
            	domain = new ArrayList <Capabilities.Guest.Arch.Domain> ();
            	name = new String ("i686");
            }
            
            public byte getWordSize() 
            {
                return wordSize;
            }

            public void setWordSize (byte value) 
            {
                this.wordSize = value;
            }

            public String getEmulator() 
            {
                return emulator;
            }

            public void setEmulator (String value) 
            {
                this.emulator = value;
            }

            public List <String> getMachine() 
            {
                if (machine == null)
                    machine = new ArrayList<String>();
                return this.machine;
            }

            public List <Capabilities.Guest.Arch.Domain> getDomain() 
            {
                if (domain == null)
                    domain = new ArrayList <Capabilities.Guest.Arch.Domain>();
                return this.domain;
            }

            public String getName() 
            {
                return name;
            }

            public void setName (String value) 
            {
                this.name = value;
            }


            public static class Domain 
            {
                protected String type;
                
                public Domain ()
                {
                	type = new String ("qemu");
                }
                
                public String getType() 
                {
                    return type;
                }

                public void setType (String value) 
                {
                    this.type = value;
                }
            }

        }


        public static class Features 
        {
        	protected boolean pae;
            protected boolean nonpae;
            protected Capabilities.Guest.Features.Acpi acpi;
            protected Capabilities.Guest.Features.Apic apic;

            public Features ()
            {
            	pae = false;
            	nonpae = false;
            }
            
            public boolean getPae() 
            {
                return pae;
            }

            public void setPae (boolean value) 
            {
                this.pae = value;
            }

            public boolean getNonpae() 
            {
                return nonpae;
            }

            public void setNonpae (boolean value) 
            {
                this.nonpae = value;
            }

            public Capabilities.Guest.Features.Acpi getAcpi() 
            {
                return acpi;
            }

            public void setAcpi (Capabilities.Guest.Features.Acpi value) 
            {
                this.acpi = value;
            }

            public Capabilities.Guest.Features.Apic getApic() 
            {
                return apic;
            }

            public void setApic (Capabilities.Guest.Features.Apic value) 
            {
                this.apic = value;
            }


            public static class Acpi 
            {
                protected String value;
                protected String _default;
                protected String toggle;

                public Acpi ()
                {
                	value = new String ();
                	_default = new String ("on");
                	toggle = new String ("yes");
                }
                
                public String getValue() 
                {
                    return value;
                }

                public void setValue (String value) 
                {
                    this.value = value;
                }

                public String getDefault() 
                {
                    return _default;
                }

                public void setDefault (String value) 
                {
                    this._default = value;
                }

                public String getToggle() 
                {
                    return toggle;
                }

                public void setToggle (String value) 
                {
                    this.toggle = value;
                }

            }


            public static class Apic 
            {
                protected String value;
                protected String _default;
                protected String toggle;

                public Apic ()
                {
                	value = new String ();
                	_default = new String ("on");
                	toggle = new String ("no");
                }
                
                public String getValue() 
                {
                    return value;
                }

                public void setValue (String value) 
                {
                    this.value = value;
                }

                public String getDefault() 
                {
                    return _default;
                }

                public void setDefault (String value) 
                {
                    this._default = value;
                }

                public String getToggle() 
                {
                    return toggle;
                }

                public void setToggle (String value) 
                {
                    this.toggle = value;
                }
            }
        }
    }
}