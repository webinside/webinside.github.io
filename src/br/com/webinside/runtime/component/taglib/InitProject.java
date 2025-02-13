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

import java.beans.Introspector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.component.BlockElement;
import br.com.webinside.runtime.component.Combo;
import br.com.webinside.runtime.component.ComboRef;
import br.com.webinside.runtime.component.Connector;
import br.com.webinside.runtime.component.CookieRead;
import br.com.webinside.runtime.component.CookieWrite;
import br.com.webinside.runtime.component.Database;
import br.com.webinside.runtime.component.Databases;
import br.com.webinside.runtime.component.DownloadDatabase;
import br.com.webinside.runtime.component.DownloadFtp;
import br.com.webinside.runtime.component.DownloadLocal;
import br.com.webinside.runtime.component.DownloadRef;
import br.com.webinside.runtime.component.EventSelect;
import br.com.webinside.runtime.component.EventUpdate;
import br.com.webinside.runtime.component.FileIn;
import br.com.webinside.runtime.component.FileListFtp;
import br.com.webinside.runtime.component.FileListLocal;
import br.com.webinside.runtime.component.FileOut;
import br.com.webinside.runtime.component.FileRemoveFtp;
import br.com.webinside.runtime.component.FileRemoveLocal;
import br.com.webinside.runtime.component.GetAttach;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.GroovyElement;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.Hosts;
import br.com.webinside.runtime.component.JavaGrid;
import br.com.webinside.runtime.component.JspElement;
import br.com.webinside.runtime.component.KillElement;
import br.com.webinside.runtime.component.ListElement;
import br.com.webinside.runtime.component.MailGet;
import br.com.webinside.runtime.component.MailGetAttach;
import br.com.webinside.runtime.component.MailKill;
import br.com.webinside.runtime.component.MailList;
import br.com.webinside.runtime.component.MailSend;
import br.com.webinside.runtime.component.MailSendAttach;
import br.com.webinside.runtime.component.ObjectElement;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.component.Project;
import br.com.webinside.runtime.component.ProjectProfile;
import br.com.webinside.runtime.component.RedirConditional;
import br.com.webinside.runtime.component.RedirSql;
import br.com.webinside.runtime.component.ReportRef;
import br.com.webinside.runtime.component.SetElement;
import br.com.webinside.runtime.component.SocketElement;
import br.com.webinside.runtime.component.TransactionElement;
import br.com.webinside.runtime.component.TreeViewElement;
import br.com.webinside.runtime.component.UpdateElement;
import br.com.webinside.runtime.component.UploadDatabase;
import br.com.webinside.runtime.component.UploadFtp;
import br.com.webinside.runtime.component.UploadLocal;
import br.com.webinside.runtime.component.UploadRef;
import br.com.webinside.runtime.component.WIObjectGrid;
import br.com.webinside.runtime.component.WebService;
import br.com.webinside.runtime.component.WebServiceClient;
import br.com.webinside.runtime.component.WebServiceFault;
import br.com.webinside.runtime.component.WebServiceMethod;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.ExecuteParamsEnum;

/**
 * Classe que implementa um TagLib para o projeto.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class InitProject extends TagSupport {

	private static final long serialVersionUID = 1L;
	private static boolean INIT_COMPONENTS = true;
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
        initComponents();
        try {
            Object obj = pageContext.getRequest().getAttribute("wiParams");
            ServletRequest req = pageContext.getRequest();
            String projId = (String) req.getAttribute("wiProject");
            if ((projId != null) && obj instanceof ExecuteParams) {
                ExecuteParams wiParams = (ExecuteParams) obj;
                Object cLoader = 
                	pageContext.getServletContext().getAttribute("classloader");
                if (cLoader == null) {
                	cLoader = getClass().getClassLoader();
                }
                wiParams.setParameter(ExecuteParamsEnum.CLASSLOADER, cLoader);
                Project project = new Project(projId);
                wiParams.setParameter(ExecuteParamsEnum.PROJECT, project);
                pageContext.setAttribute("project", project);
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
        return ((exit) ? SKIP_PAGE
                       : EVAL_PAGE);
    }
    
    private void initComponents() {
    	if (INIT_COMPONENTS) {
    		// project
    		getBeanInfo(Project.class);
    		getBeanInfo(ProjectProfile.class);
    		getBeanInfo(Databases.class);
    		getBeanInfo(Database.class);
    		getBeanInfo(Hosts.class);
    		getBeanInfo(Host.class);
    		// project components
    		getBeanInfo(ComboRef.class);
    		getBeanInfo(Combo.class);
    		getBeanInfo(GridRef.class);
    		getBeanInfo(GridHtml.class);
    		getBeanInfo(GridSql.class);
    		getBeanInfo(GridXmlOut.class);
    		getBeanInfo(DownloadRef.class);
    		getBeanInfo(DownloadLocal.class);
    		getBeanInfo(DownloadDatabase.class);
    		getBeanInfo(DownloadFtp.class);
    		getBeanInfo(UploadRef.class);
    		getBeanInfo(UploadLocal.class);
    		getBeanInfo(UploadDatabase.class);
    		getBeanInfo(UploadFtp.class);
    		getBeanInfo(EventSelect.class);
    		getBeanInfo(EventUpdate.class);
    		getBeanInfo(ReportRef.class);
    		getBeanInfo(WebService.class);
    		getBeanInfo(WebServiceMethod.class);
    		getBeanInfo(WebServiceFault.class);
    		// page components
    		getBeanInfo(Page.class);
    		getBeanInfo(BlockElement.class);
    		getBeanInfo(TransactionElement.class);
    		getBeanInfo(SetElement.class);
    		getBeanInfo(KillElement.class);
    		getBeanInfo(ObjectElement.class);
    		getBeanInfo(UpdateElement.class);
    		getBeanInfo(ListElement.class);
    		getBeanInfo(FileIn.class);
    		getBeanInfo(FileOut.class);
    		getBeanInfo(FileListLocal.class);
    		getBeanInfo(FileListFtp.class);
    		getBeanInfo(FileRemoveLocal.class);
    		getBeanInfo(FileRemoveFtp.class);
    		getBeanInfo(RedirConditional.class);
    		getBeanInfo(RedirSql.class);
    		getBeanInfo(MailSend.class);
       		getBeanInfo(MailSendAttach.class);
       		getBeanInfo(MailList.class);
       		getBeanInfo(MailKill.class);
    		getBeanInfo(MailGet.class);
    		getBeanInfo(MailGetAttach.class);
       		getBeanInfo(GetAttach.class);
    		getBeanInfo(Connector.class);
    		getBeanInfo(JspElement.class);
    		getBeanInfo(GroovyElement.class);
    		getBeanInfo(CookieRead.class);
    		getBeanInfo(CookieWrite.class);
    		getBeanInfo(JavaGrid.class);
    		getBeanInfo(WIObjectGrid.class);
    		getBeanInfo(SocketElement.class);
    		getBeanInfo(TreeViewElement.class);
    		getBeanInfo(WebServiceClient.class);
        	INIT_COMPONENTS = false;
    	}
    }

    private void getBeanInfo(Class<?> beanClass) {
    	try {
    		Introspector.getBeanInfo(beanClass);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
}
