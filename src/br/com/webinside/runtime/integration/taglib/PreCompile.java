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

package br.com.webinside.runtime.integration.taglib;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.RtmFunction;
import br.com.webinside.runtime.util.Function;

/**
 * Classe que precompila os JSPs do projeto.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class PreCompile extends TagSupport {

	private static final long serialVersionUID = 1L;
	
    /**
     * Executa o precompile.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        ServletContext app = pageContext.getServletContext();
        PrintWriter out = new PrintWriter(pageContext.getOut());
        int err = 0;
        String prjId = new File(app.getRealPath("")).getName();
        String[] files =
            Function.listDir(app.getRealPath(""), "*.jsp", true, true);
        for (int i = 0; i < files.length; i++) {
            if (files[i].equalsIgnoreCase("precompile.jsp")) {
                continue;
            }
            String file = "/" + prjId + "/" + files[i];
            HttpServletRequest req =
                (HttpServletRequest) pageContext.getRequest();
            int port = RtmFunction.getServerPort(req);
            String host = "localhost:" + port;
            String prot = "http://";
            if (port == 443) {
                prot = "https://";
            }
            String url = prot + host + file + "?jsp_precompile=true";
            int code = RtmFunction.callUrl(url);
            String msg = (i + 1) + "/" + files.length;
            while (msg.length() < (((files.length + "").length() * 2) + 1)) {
                msg = "0" + msg;
            }
            if (code != 200) {
            	err ++;
            }
            for (int a = 0; a < 2; a++) {
                if (a == 1) {
                    msg = file;
                }
                if (code != 200) {                	
                    out.println("<span class='label' style='color:red'>");
                    out.println(
                        "<a class='label' style='color:red;" +
                        "font-weight:bold' href='");
                    out.println(file + "' target='_blank'>" + msg
                        + "</a></span>");
                } else {
                    out.println("<span class='label'>" + msg + "</span>");
                }
                if (a == 0) {
                    if (code != 200) {
                        out.println("&nbsp;&nbsp;&nbsp;");
                    } else {
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                }
            }
            out.println("<br>");
            out.flush();
        }
        if (err > 0) {
          out.println("<span class='label' style='color:red'>" +
          		"Erros encontrados: " + err + "</span>");
        } else {
            out.println("<span class='label'>" +
              		"Nenhum erro encontrado</span>");
        }
        out.flush();
        return SKIP_BODY;
    }
}
