package com.cloud.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.cloud.backend.VMManager;

public class CreateHandler extends AbstractHandler 
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
		
		String name = baseRequest.getParameter ("name");
		int instanceType = Integer.parseInt (baseRequest.getParameter ("instance_type"));
		int imageID = Integer.parseInt (baseRequest.getParameter ("image_id"));
		
		System.out.println ("Name: " + name + " instance_type: " + instanceType + " imageID: " + imageID);
		String resp = VMManager.create (name, instanceType, imageID);
		response.getWriter ().write (resp);
	}
}
