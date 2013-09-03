package com.cloud.backend;

public class InstanceType 
{
	int tid;
	long cpu;
	long ram;
	long disk;
	
	public InstanceType (int tid, long cpu, long ram, long disk)
	{
		this.tid = tid;
		this.cpu = cpu;
		this.ram = ram;
		this.disk = disk;
	}
}
