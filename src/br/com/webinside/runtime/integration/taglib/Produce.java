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
							StringBuilder nbody = new StringBuilder();
							char last = ' ';
							for (int i = 0; i < body.length(); i++) {
								char c = body.charAt(i);
								if (c == '\t') c = ' ';
								if (c == '\r' || (c == ' ' && last == ' ')) {
									continue;
								}
								if (c == '\n' && last != '>') {
									if (last == '\n' || last == ' ') continue;
									c = ' ';
								}
								nbody.append(c);
								last = c;
							}
							body = nbody.toString();
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