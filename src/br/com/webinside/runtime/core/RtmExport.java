/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.core;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.InflaterOutputStream;

import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class RtmExport {
    private ExecuteParams param;

    /**
     * Creates a new Export object.
     *
     * @param param DOCUMENT ME!
     */
    public RtmExport(ExecuteParams param) {
        this.param = param;
    }

    /**
     * DOCUMENT ME!
     *
     * @param msg DOCUMENT ME!
     */
    public void showMessage(String msg) {
        if (param.getWriter() == null) {
            return;
        }

        // usado para erro de conex�o em WebService
        if (param.getHttpRequest() == null) {
            param.getWriter().print(msg);
            param.getWriter().flush();
            return;
        }
        param.setContentType("text/html");
        param.getWriter().println("<html><head>");
        param.getWriter().println("<title>WI-Error</title>");
        param.getWriter().println("<meta http-equiv='pragma' content='no-cache'>");
        param.getWriter().println("<meta http-equiv='expires' content='0'>");
        param.getWriter().println("</head><body>");
        param.getWriter().println("<h4><font face='Arial'>" + msg
            + "</font></h4>");
        param.getWriter().println("</body></html>");
        param.getWriter().flush();
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param exec DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean sendFile(WIMap wiMap, String filename, boolean produce) {
        if (param.getHttpResponse() == null) {
            return false;
        }
        if (filename == null) {
            filename = "";
        }
        File arq = new File(filename);
        if (!arq.exists()) {
            return false;
        }
        String tname = Thread.currentThread().getName();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        Function.setThreadName(df.format(new Date()) + " - Exporting file " + 
        		filename + " processing " + produce + " - Proj:" + 
            	wiMap.get("wi.proj.id") + ",Page:" + wiMap.get("wi.page.id")); 
        if (produce) {
            try {
                ProducerParam prod = new ProducerParam();
                prod.setWIMap(wiMap);
                BufferedReader in =
                    new BufferedReader(new FileReader(filename));
                String line = "";
                while ((line = in.readLine()) != null) {
                    prod.setInput(line);
                    param.getProducer().setParam(prod);
                    param.getProducer().execute();
                    param.getWriter().println(prod.getOutput().trim());
                    param.getWriter().flush();
                }
                in.close();
                return true;
            } catch (Exception err) {
                return false;
            }
        } else {
            try {
            	FileInputStream fin = new FileInputStream(filename);
            	sendInputStream(fin, false);
            	fin.close();
            } catch (IOException err) {
                return false;
            }
        }
        Function.setThreadName(tname);    
        return true;
    }
    
    public void sendInputStream(InputStream inputStream, boolean inflate)
    throws IOException {
        OutputStream out = param.getOutputStream();
        if (inflate) out = new InflaterOutputStream(out);
        Function.copyStream(inputStream, out);
    }
    
}
