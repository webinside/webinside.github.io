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

package br.com.webinside.runtime.component;

import org.jdom.Element;

import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public abstract class AbstractDownload implements ProjectElement {

	private static final long serialVersionUID = 1L;
	
	/** DOCUMENT ME! */
    public final static String TYPE_LOCAL = "LOCAL";
    /** DOCUMENT ME! */
    public final static String TYPE_FTP = "FTP";
    /** DOCUMENT ME! */
    public final static String TYPE_DATABASE = "DATABASE";
    /** DOCUMENT ME! */
    public final static String DIRECTORY = "downloads";
    /** DOCUMENT ME! */
    protected Element download;
    private AbstractProject project;

    /**
     * Creates a new AbstractDownload object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractDownload(String id) {
        if (id == null) {
            id = "";
        }
        download = new Element("DOWNLOAD");
        download.setAttribute("ID", id);
    }

    /**
     * Creates a new AbstractDownload object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractDownload(String id, Element element) {
        if ((element == null) || (!element.getName().equals("DOWNLOAD"))) {
            element = new Element("DOWNLOAD");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.download = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractDownload) {
            String id = ((AbstractDownload) obj).getId();
            if (id.equalsIgnoreCase(getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractProject getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param project DOCUMENT ME!
     */
    public void setProject(AbstractProject project) {
        this.project = project;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return download.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(download, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(download, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMime(String value) {
        XMLFunction.setElemValue(download, "MIME", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMime() {
        return XMLFunction.getElemValue(download, "MIME");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNoFile(String value) {
        XMLFunction.setElemValue(download, "NOFILE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNoFile() {
        return XMLFunction.getElemValue(download, "NOFILE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setActivePage(String value) {
        XMLFunction.setElemValue(download, "ACTIVEPAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getActivePage() {
        return XMLFunction.getElemValue(download, "ACTIVEPAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractDownload cloneMe() {
        AbstractDownload obj = null;
        if (this instanceof DownloadLocal) {
            obj = new DownloadLocal(getId(), (Element) download.clone());
        } else if (this instanceof DownloadFtp) {
            obj = new DownloadFtp(getId(), (Element) download.clone());
        } else if (this instanceof DownloadDatabase) {
            obj = new DownloadDatabase(getId(), (Element) download.clone());
        }
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    protected void setType(String type) {
        download.setAttribute("TYPE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        String cName = this.getClass().getSimpleName();
        resp.append("<w:setProjectElement\n");
    	resp.append("  type=\"" + cName + "\" name=\"download_" + getId() + "\"\n");
        resp.append("/><jsp:useBean\n");
        resp.append("  id=\"download_" + getId() + "\" ");
        resp.append("type=\"br.com.webinside.runtime.component." + cName + "\"\n");
        resp.append("/>");        
        resp.append(CompFunction.setProperties(this, "download_" + getId()));
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        String ret = "";
        if (this instanceof DownloadDatabase) {
            ret = TYPE_DATABASE;
        }
        if (this instanceof DownloadFtp) {
            ret = TYPE_FTP;
        }
        if (this instanceof DownloadLocal) {
            ret = TYPE_LOCAL;
        }
        return ret;
    }
}
