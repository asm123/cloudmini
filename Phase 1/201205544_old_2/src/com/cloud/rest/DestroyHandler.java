package com.cloud.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.cloud.backend.VMManager;


public class DestroyHandler extends AbstractHandler 
{
	@Override
	public void handle (String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		// settings
		
		response.setContentType ("application/json;charset=utf-8");
		response.setStatus (HttpServletResponse.SC_OK);
		baseRequest.setHandled (true);
		
		// take parameters
		
		int vmid = Integer.parseInt (baseRequest.getParameter ("vmid"));
		
		System.out.println ("vmid: " + vmid);
		String resp = VMManager.destroy (vmid);
		response.getWriter().write (resp);
	}
}
