package com.cloud.backend;

import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONResponse 
{
	static JSONObject obj;
	
	@SuppressWarnings("unchecked")
	public static String create (int vmid)
	{   	 
   	 	obj = new JSONObject();
   	 	obj.put ("vmid", vmid);
   	 	return obj.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static String query (int vmid)
	{
		obj = new JSONObject ();
		
		VM vm = Global.vmidToVM.get (vmid);
		if (vm == null)
		{
			obj.put ("status:", 0);
		}
		else
		{
			obj.put ("vmid", vm.vmid);
			obj.put ("name", vm.vmName);
			obj.put ("instance_type", vm.instanceType);
			obj.put ("pmid", vm.pmid);
		}
		
		return obj.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static String destroy (int status)
	{
		obj = new JSONObject ();
		obj.put ("status", status);
		
		return obj.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static String images ()
	{
		JSONObject root = new JSONObject();
		
		JSONArray arr = new JSONArray();
		
		for (int i = 0; i < Global.images.size() ; i++)
		{
			JSONObject obj = new JSONObject();
			
			obj.put ("id", i);
   	     	obj.put("name", Global.images.get (i));
   	     	
   	     	arr.add (obj);
   	 	}
		root.put ("images", arr);
		
		return root.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static String types ()
	{
		JSONObject root = new JSONObject();
		JSONArray arr = new JSONArray();
				
		for (Entry <?, ?> entry : Global.instanceType.entrySet())
		{
			InstanceType it = (InstanceType) entry.getValue();
			
			JSONObject obj = new JSONObject();
			
			obj.put ("tid", it.tid);
			obj.put ("cpu", it.cpu);
			obj.put ("ram", it.ram);
			obj.put ("disk", it.disk);
			
			arr.add (obj);
		}
		root.put ("types", arr);
		return root.toJSONString();
	}
}
