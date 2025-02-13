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

package br.com.webinside.runtime.integration.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.ProducerParam;

/**
 * Classe que implementa o Producer do body.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Produce extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String var;
	private String enabled;
	private String compact;

	public int doAfterBody() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
			try {
				BodyContent bodycontent = getBodyContent();
				String body = bodycontent.getString();
				JspWriter out = bodycontent.getEnclosingWriter();
				if (body != null) {
					if (enabled != null && 
							enabled.trim().equalsIgnoreCase("false")) {
						if (var != null) {
							wiParams.getWIMap().put(var, body.trim());
						} else {
							out.print(body.trim());
						}	
					} else {
						if (compact != null && 
								compact.trim().equalsIgnoreCase("true")) {
							body = body.replace("\r", "");
							body = body.replace('\t', ' ');
							body = body.trim().replaceAll(" +", " ");
						}
					    ProducerParam param = new ProducerParam();
					    param.setWIMap(wiParams.getWIMap());
					    param.setInput(body);
					    wiParams.getProducer().setParam(param);
					    wiParams.getProducer().execute();
						String textBody = param.getOutput().trim();
						if (var != null) {
							wiParams.getWIMap().put(var, textBody);
						} else {
							out.print(textBody);
						}	
					}	
				}
			} catch (IOException ioe) {
				throw new JspException("Error:" + ioe.getMessage());
			}
        }	
        reset();
		return SKIP_BODY;
	}
	
    private void reset() {
    	var = null;
    	enabled = null;
    	compact = null;
    }
    
    public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getCompact() {
		return compact;
	}

	public void setCompact(String compact) {
		this.compact = compact;
	}
	
}