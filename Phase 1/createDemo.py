import libvirt

conf_file = "/home/asmita/SemIII/Cloud/Mini Project/dom.xml"
f = open (conf_file);
xml = f.read ();
conn = libvirt.open ('qemu:///system');
conn.defineXML (xml)

vmname = 'MyNewVM'
dom = conn.lookupByName (vmname)
dom.create ()
print dom.info ()
dom.destroy ()

