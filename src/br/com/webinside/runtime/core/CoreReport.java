/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import br.com.webinside.runtime.component.ReportRef;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.report.CoreReportShow;
import br.com.webinside.runtime.report.ReportFunction;
import br.com.webinside.runtime.util.Crypto;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class CoreReport extends CoreCommon {

	private ReportRef reportRef;

    /**
     * Creates a new CoreReport object.
     *
     * @param wiParams DOCUMENT ME!
     * @param reportref DOCUMENT ME!
     */
    public CoreReport(ExecuteParams wiParams, ReportRef reportref) {
        this.wiParams = wiParams;
        this.reportRef = reportref;
        element = reportref;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        wiMap = wiParams.getWIMap();
        WISession wiSession = wiParams.getWISession();
        String reportURL = getReportURL();
        boolean isRedirect = wiMap.containsKey("tmp.reportRedirect");
        boolean isShow = wiMap.containsKey("tmp.reportShow");
        if (isRedirect) {
        	String base64 = wiMap.get("tmp.reportRef");
        	wiSession.setAttribute("reportRef", decode(base64));
        	wiMap.remove("tmp.reportRef");
        	base64 = wiMap.get("tmp.reportVars");
        	wiSession.setAttribute("reportVars", decode(base64));
        	wiMap.remove("tmp.vars");
        } else if (!isShow && reportURL.equals("")) {
        	String serverKey = wiMap.get("pvt.report.key");
        	wiMap.put("tmp.report.keymd5", getReporKeyMD5(serverKey));
        	wiSession.setAttribute("reportRef", reportRef);
        	wiSession.setAttribute("reportVars", getReportVars());
        }
        if (isRedirect || isShow) {
        	reportRef = (ReportRef) wiSession.getAttribute("reportRef");
        	element = reportRef;
        	Map vars = (Map) wiSession.getAttribute("reportVars");
        	wiMap.putAll(vars);
        }
    	if (isShow) {
    		wiSession.removeAttribute("reportRef");
        	wiSession.removeAttribute("reportVars");
    	}

    	if (!isValidCondition()) {
            return;
        }
    	
        if (!isRedirect && !isShow && !reportURL.equals("")) {
        	redirectPage();
            wiParams.setRequestAttribute("wiExit", "true");
        	return;
        }	
        boolean isOutputDir = ! "".equals(reportRef.getOutputDir());
        String format = Producer.execute(wiMap, reportRef.getOutputFormat());
        boolean doPage = (isOutputDir || "HTML".equals(format));
        if (!isShow) {
        	isShow = ("CSV".equals(format)) || ("XLS".equals(format));
        }
        if (isShow || doPage) {
        	if (checkReportKey()) {
	            setParameters();
	            showReport();
        	} else {
        		wiParams.getWriter().print("<h3>Wrong Report Key</h3>");
        	}
        } else {
            showPage();
        }
    	if (!doPage) {
    		wiParams.setRequestAttribute("wiExit", "true");
    	}	
        // se for chamado do WIReport o projeto é null
        if (null != wiParams.getProject()){
            writeLog();
        }
    }

	private void setParameters() {
		WIMap wiPar = reportRef.getParameters();
		for (Iterator it = wiPar.keySet().iterator(); it.hasNext();) {
		    String key = (String) it.next();
		    String value = wiPar.get(key);
		    value = Producer.execute(wiMap, value);
		    wiPar.put(key, value);
		}
		reportRef.setParameters(wiPar);
	}

    private void showReport() {
        if (!wiMap.containsKey("bld-reportFileName")) {
            File f = new File(wiParams.getServletContext().getRealPath("/"));
            String reportFileName = f.getAbsolutePath() + "/WEB-INF/reports/"
            + reportRef.getId() + ".jasper";
            wiMap.put("reportFileName", reportFileName);
        }
        WIMap myMap = new WIMap();
        myMap.putAll(wiMap.getAsMap());
        
        Map params = reportRef.getParameters().getAsMap();
        
        for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
			Entry e = (Entry) it.next();
			e.setValue(Producer.execute(myMap, (String) e.getValue()));
		}
        
        myMap.putAll(params);
        
        String format = Producer.execute(myMap, reportRef.getOutputFormat());
        myMap.put("rpt.outputFormat", format.toUpperCase());
        
        String dir = Producer.execute(myMap, reportRef.getOutputDir());
        myMap.put("rpt.outputDir", dir);

        if (!myMap.containsKey(ReportFunction.DATABASEALIASES)) {
            myMap.putObj("wi.databasealiases", 
                wiParams.getDatabaseAliases().getAliasesMap());
        }
        new CoreReportShow(myMap, wiParams).showReport();
    }

    private void redirectPage() {
    	String serverKey = wiMap.get("pvt.report.key");
    	if (serverKey.equals("")) serverKey = "qwerty";
    	String md5 = getReporKeyMD5(serverKey);
        wiMap.put("tmp.report.keymd5", md5);
        // preparando pagina de envio
        Map vars = getReportVars();
        wiMap.put("tmp.reportRef", encode(reportRef));
        wiMap.put("tmp.reportVars", encode(vars));
        String reportURL = getReportURL();
        if (StringA.piece(reportURL, "/", 4).equals("")) {
        	reportURL += "/" + wiMap.get("wi.proj.id");
        }
        wiMap.put("tmp.report.url_full", reportURL);
        String template = loadFromResource("reportRedirect.html");
        String page = Producer.execute(wiMap, template);
        wiParams.getHttpResponse().setContentType("text/html");
        wiParams.getWriter().write(page);
    }
    
    private void showPage() {
        String showReport = wiMap.get("wi.showReport");
        if ("true".equals(showReport)) {
            wiMap.put("wi.proj.id", "wireport");
        }
        String template = loadFromResource("reportShow.html");
        String page = Producer.execute(wiMap, template);
        wiParams.getHttpResponse().setContentType("text/html");
        wiParams.getWriter().write(page);
    }

    private Map getReportVars() {
        Map vars = wiMap.getAsMap();
        Iterator it = new HashSet(vars.entrySet()).iterator();
        while (it.hasNext()) {
        	Map.Entry entry = (Map.Entry) it.next();
        	if (entry.getValue() != null) {
	        	String key = (String) entry.getKey();
	        	if (key.startsWith("wi.") || 
	        			key.startsWith("grid.") || 
	        			key.startsWith("combo.") ||
	        			entry.getValue().equals("[object]")) {
	        		vars.remove(key);
	        	}
        	}	
		}
        vars.remove("pvt.report.key");
        return vars;
    }
    
    private String getReportURL() {
        if (wiMap.get("tmp.report.url").equals("")) {
        	String reportURL = wiMap.get("pvt.report.url");
        	wiMap.put("tmp.report.url", reportURL);
        }
        String reportUrl = wiMap.get("tmp.report.url");
        if (reportUrl.endsWith("/")) {
        	reportUrl = reportUrl.substring(0, reportUrl.length() - 1);
        }
        return reportUrl;
    }
    
    private String getReporKeyMD5(String key) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	return Crypto.encodeMD5(key + "-" + sdf.format(new Date()));
    }
    
    private boolean checkReportKey() {
		String requestKey = wiMap.get("tmp.report.keymd5");
    	String serverKey = wiMap.get("pvt.report.key");
    	String md5 = getReporKeyMD5(serverKey);
    	return md5.equals(requestKey);
    }
    
    private String loadFromResource(String html) {
        OutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
    	String path = "/br/com/webinside/runtime/report/";
        try {
            InputStream is = getClass().getResourceAsStream(path + html);
            in = new BufferedInputStream(is);
            byte[] trecho = new byte[10240];
            int quant = 0;
            while ((quant = in.read(trecho)) > -1) {
                out.write(trecho, 0, quant);
            }
        } catch (IOException err) {
        	System.err.println("Error reading " + path + html);
        	err.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        } 
        return out.toString();
    }
    
    private String encode(Object obj) {
    	String result = "";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream obj_out = new ObjectOutputStream(baos);
			obj_out.writeObject(obj);
	    	byte[] bytes = baos.toByteArray();
			result = new BASE64Encoder().encode(bytes);
			obj_out.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    private Object decode(String text) {
    	Object result = null;
		try {
			byte[] bytes = new BASE64Decoder().decodeBuffer(text);			
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream obj_in = new ObjectInputStream (bais);
			result = obj_in.readObject();
			obj_in.close();
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }

}
