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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que implementa o Menu com hasrole.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class Menu extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public int doAfterBody() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
			try {
				BodyContent bodycontent = getBodyContent();
				String body = bodycontent.getString();
				JspWriter out = bodycontent.getEnclosingWriter();
				if (body != null) {
					WIMap wiMap = wiParams.getWIMap();
					if (wiMap.get("pvt.wimenu").equals("")) {
					    ProducerParam param = new ProducerParam();
					    param.setWIMap(wiParams.getWIMap());
					    param.setInput(body);
					    wiParams.getProducer().setParam(param);
					    wiParams.getProducer().execute();
					    Document doc = Jsoup.parse(param.getOutput().trim());
						processHasRole(wiMap, doc.body());
						wiParams.getWIMap().put("pvt.wimenu", doc.body().html());
					}
					out.print(wiMap.get("pvt.wimenu"));
				}
			} catch (IOException ioe) {
				throw new JspException("Error:" + ioe.getMessage());
			}
        }	
        reset();
		return SKIP_BODY;
	}
	
	private void processHasRole(WIMap wiMap, Element ele) {
		Elements list = ele.getElementsByAttribute("hasrole");
		for (Element child : list) {
			if (!child.nodeName().equals("li")) return;
			if (!hasRole(wiMap, child.attr("hasrole"))) {
				remove(child.parent(), child);
			}
		}
	}
	
	private void remove(Element ul, Element li) {
		li.remove();
		if (ul != null && ul.children().size() == 0) {
			if (ul.parent() != null && ul.parent().nodeName().equals("li")) {
				li = ul.parent();
				remove(li.parent(), li);
			} else {
				ul.remove();
			}
		}
	}
	
	private boolean hasRole(WIMap wiMap, String menuRole) {
    	String[] mods = wiMap.get("pvt.login.role.modules").split(",");
		for (String mod : mods) {
			String modRole = "module_" + mod.trim();
			if (modRole.equalsIgnoreCase(menuRole)) return true;
		}
		int size = Function.parseInt(wiMap.get("pvt.login.role.size()"));
		for (int i = 1; i <= size; i++) {
			String loginRole = wiMap.get("pvt.login.role[" + i + "].name");
			if (loginRole.equalsIgnoreCase(menuRole)) return true;
		}
		return false;
	}
	
    private void reset() {
    	// limpar os atributos
    }
	
}