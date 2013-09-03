package com.cloud.backend;

public class VMManager 
{
	public static String create (String vmName, int instanceType, int imageID)
	{
		VM vm = new VM ();
		int vmid = vm.create (vmName, instanceType, imageID);
		return JSONResponse.create (vmid);
	}
	
	public static String query (int vmid)
	{
		return JSONResponse.query (vmid);
	}
	
	public static String destroy (int vmid)
	{
		int status;
		VM vm = Global.vmidToVM.get (vmid);
		if (vm == null)
			status = 0;
		else
			status = vm.destroy();
		return JSONResponse.destroy (status);
	}
	
	public static String types ()
	{
		return JSONResponse.types ();
	}
	
	public static String images ()
	{
		return JSONResponse.images ();
	}
}
