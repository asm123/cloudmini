package com.cloud.rest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class RESTServer
{	
	public void run ()
	{
		Server server = new Server (8180);
		
		ContextHandler create = new ContextHandler ();
		create.setContextPath ("/vm/create");
		create.setHandler (new CreateHandler ());
		
		ContextHandler query = new ContextHandler ();
		query.setContextPath ("/vm/query");
		query.setHandler (new QueryHandler ());
		
		ContextHandler destroy = new ContextHandler ();
		destroy.setContextPath ("/vm/destroy");
		destroy.setHandler (new DestroyHandler ());
		
		ContextHandler types = new ContextHandler ();
		types.setContextPath ("/vm/types");
		types.setHandler (new TypesHandler ());
		
		ContextHandler list = new ContextHandler ();
		list.setContextPath ("/image/list");
		list.setHandler (new ImagesHandler ());
		
		ContextHandlerCollection collection = new ContextHandlerCollection ();
		ContextHandler [] contextualHandles = {create, query, destroy, types, list};
		collection.setHandlers (contextualHandles);
		
		server.setHandler (collection);
	
		try 
		{
			server.start();
			server.join();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
