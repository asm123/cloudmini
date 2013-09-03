package com.vm.main;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import restServer.reqHandler_Creation;
import restServer.reqHandler_Destroy;
import restServer.reqHandler_Query;
import restServer.reqHandler_Type;


public class rServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server sr=new Server(10000);
		ContextHandler cl1 = new ContextHandler();
		cl1.setContextPath("/vm/create");
		cl1.setHandler(new reqHandler_Creation());
		
		ContextHandler cl2 = new ContextHandler();
		cl2.setContextPath("/vm/query");
		cl2.setHandler(new reqHandler_Query());
		
		ContextHandler cl3 = new ContextHandler();
		cl3.setContextPath("/vm/destroy");
		cl3.setHandler(new reqHandler_Destroy());
		
		ContextHandler cl4 = new ContextHandler();
		cl4.setContextPath("/vm/types");
		cl4.setHandler(new reqHandler_Type());
		
		ContextHandler cl5 = new ContextHandler();
		cl5.setContextPath("/image/list");
		cl5.setHandler(new reqHandler_Query());
		
		ContextHandlerCollection collection = new ContextHandlerCollection();
		ContextHandler [] contextualHandles = {cl1, cl2,cl3,cl4,cl5};
		collection.setHandlers(contextualHandles);
		
		sr.setHandler(collection);
		try {
			sr.start();
			 sr.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}

}
