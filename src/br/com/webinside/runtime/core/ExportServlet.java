package br.com.webinside.runtime.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.export.GridPdf;
import br.com.webinside.runtime.export.GridXls;
import br.com.webinside.runtime.util.WIMap;

public class ExportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        ExecuteParams wiParams = null;
        try {
        	wiParams = ExecuteParams.initInstance(request, response, getServletContext());
            request.setAttribute("wiParams", wiParams);
            super.service(request, response);
        } catch (Exception ex) {
        	if (wiParams != null && wiParams.getErrorLog() != null) {
        		wiParams.getErrorLog().write(getClass().getName(), "Service", ex);
        	}
        } finally {
        	if (wiParams != null) {
        		wiParams.getDatabaseAliases().closeAll();
        	}
        }
    }
    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doPost(request, response);
    }
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
 		ExecuteParams wiParams = (ExecuteParams) request.getAttribute("wiParams");
 		WIMap wiMap = wiParams.getWIMap();
 		String gridKey = "grid";
 		String typeKey = "type";
        if (wiParams.getProject().isTmpRequestVar()) {
        	gridKey = "tmp.grid";
     		typeKey = "tmp.type";
        }
        String gridId = wiMap.get(gridKey);
        String type = wiMap.get(typeKey);
        if (type.equals("")) type = "xls";
        if (type.equalsIgnoreCase("xls")) {
        	new GridXls(wiParams).execute(gridId);
        } else if (type.equalsIgnoreCase("pdf")) {
        	new GridPdf(wiParams).execute(gridId);
        }
	}
    
}
