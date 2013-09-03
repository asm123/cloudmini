package restServer;
import com.vm.main.*;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;

import javax.print.attribute.standard.SheetCollate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class reqHandler_Creation extends AbstractHandler {
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String name=baseRequest.getParameter("name");
        String type=baseRequest.getParameter("instance_type");
        String iid=baseRequest.getParameter("image_id");
        System.out.println(name + type + iid);
        String ret=Sheduler.obj.createVM(name, Long.valueOf(type), Long.valueOf(iid));
         
		response.getWriter().write(ret);
}


		
	}
