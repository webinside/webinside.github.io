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

package br.com.webinside.runtime.component.taglib;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.component.AbstractDownload;
import br.com.webinside.runtime.component.AbstractEvent;
import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractUpload;
import br.com.webinside.runtime.component.Combo;
import br.com.webinside.runtime.component.Database;
import br.com.webinside.runtime.component.DownloadDatabase;
import br.com.webinside.runtime.component.DownloadFtp;
import br.com.webinside.runtime.component.DownloadLocal;
import br.com.webinside.runtime.component.EventSelect;
import br.com.webinside.runtime.component.EventUpdate;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.UploadDatabase;
import br.com.webinside.runtime.component.UploadFtp;
import br.com.webinside.runtime.component.UploadLocal;
import br.com.webinside.runtime.component.WebService;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.util.StringA;

/**
 * Classe que implementa um TagLib para um elemento do projeto.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class ProjectElement extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String type;
    private String name;
    private String realName;
    private boolean exit;

    /**
     * Executa a função.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
    	exit = true;
        try {
            Object obj = pageContext.getRequest().getAttribute("wiParams");
            if (obj instanceof ExecuteParams) {
                ExecuteParams wiParams = (ExecuteParams) obj;
                setName(StringA.piece(name, "_", 2, 0));
                if (realName == null) {
                	setRealName(getName());
                }
                if (getType().equals("Combo")) {
                    Combo cbo = new Combo(getName());
                    wiParams.getProject().getCombos().putElement(cbo);
                    pageContext.setAttribute("combo_" + getName(), cbo);
                } else if (getType().startsWith("Grid")) {
                    grid(wiParams);
                } else if (getType().startsWith("Event")) {
                    event(wiParams);
                } else if (getType().startsWith("Download")) {
                    download(wiParams);
                } else if (getType().startsWith("Upload")) {
                    upload(wiParams);
                } else if (getType().equals("WebService")) {
                    WebService wsv = new WebService(getName());
                    wiParams.getProject().getWebServices().putElement(wsv);
                    pageContext.setAttribute("webservice_" + getName(), wsv);
                } else if (getType().equals("Database")) {
                    Database db = new Database(getRealName());
                    wiParams.getProject().getDatabases().addDatabase(db);                    
                    pageContext.setAttribute("database_" + getName(), db);
                } else if (getType().equals("Host")) {
                    Host host = new Host(getRealName());
                    wiParams.getProject().getHosts().addHost(host);
                    pageContext.setAttribute("host_" + getName(), host);
                }    
                exit = false;
            } else {
                HttpServletResponse response =
                    (HttpServletResponse) pageContext.getResponse();
                (response).sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (Exception err) {
        	throw new JspException(err);
        }
        return SKIP_BODY;
    }

    /**
     * Finaliza a tag.
     *
     * @return se a página deve ser processada.
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doEndTag() throws JspException {
    	setRealName(null);
        return ((exit) ? SKIP_PAGE
                       : EVAL_PAGE);
    }

    private void grid(ExecuteParams wiParams) {
        AbstractGrid grd = null;
        if (getType().equals("GridSql")) {
            grd = new GridSql(getName());
        } else if (getType().equals("GridHtml")) {
            grd = new GridHtml(getName());
        } else if (getType().equals("GridXmlOut")) {
            grd = new GridXmlOut(getName());
        }
        wiParams.getProject().getGrids().putElement(grd);
        pageContext.setAttribute("grid_" + getName(), grd);
    }

    private void event(ExecuteParams wiParams) {
        AbstractEvent evt = null;
        if (getType().equals("EventSelect")) {
            evt = new EventSelect(getName());
        } else if (getType().equals("EventUpdate")) {
            evt = new EventUpdate(getName());
        }
        wiParams.getProject().getEvents().putElement(evt);
        pageContext.setAttribute("event_" + getName(), evt);
    }

    private void download(ExecuteParams wiParams) {
        AbstractDownload down = null;
        if (getType().equals("DownloadLocal")) {
            down = new DownloadLocal(getName());
        } else if (getType().equals("DownloadFtp")) {
            down = new DownloadFtp(getName());
        } else if (getType().equals("DownloadDatabase")) {
            down = new DownloadDatabase(getName());
        }
        wiParams.getProject().getDownloads().putElement(down);
        pageContext.setAttribute("download_" + getName(), down);
    }

    private void upload(ExecuteParams wiParams) {
        AbstractUpload upl = null;
        if (getType().equals("UploadLocal")) {
            upl = new UploadLocal(getName());
        } else if (getType().equals("UploadFtp")) {
            upl = new UploadFtp(getName());
        } else if (getType().equals("UploadDatabase")) {
            upl = new UploadDatabase(getName());
        }
        wiParams.getProject().getUploads().putElement(upl);
        pageContext.setAttribute("upload_" + getName(), upl);
    }

    /**
     * Name do elemento do projeto.
     *
     * @return o nome do elemento do projeto.
     */
    public String getName() {
        return name;
    }

    /**
     * Name do elemento do projeto.
     *
     * @param name indica o id do elemento do projeto.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tipo de elemento do projeto.
     *
     * @return o tipo de elemento do projeto.
     */
    public String getType() {
        return type;
    }

    /**
     * Tipo de elemento do projeto.
     *
     * @param type indica o tipo de elemento do projeto.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Nome real do elemento do projeto.
     *
     * @return o nome real do elemento do projeto.
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Nome real do elemento do projeto.
     *
     * @param realName indica o nome real do elemento do projeto.
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

}
