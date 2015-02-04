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
public abstract class AbstractUpload implements ProjectElement {

	private static final long serialVersionUID = 1L;
	
	/** DOCUMENT ME! */
    public final static String TYPE_LOCAL = "LOCAL";
    /** DOCUMENT ME! */
    public final static String TYPE_FTP = "FTP";
    /** DOCUMENT ME! */
    public final static String TYPE_DATABASE = "DATABASE";
    /** DOCUMENT ME! */
    public final static String DIRECTORY = "uploads";
    /** DOCUMENT ME! */
    protected Element upload;
    private AbstractProject project;

    /**
     * Creates a new AbstractUpload object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractUpload(String id) {
        if (id == null) {
            id = "";
        }
        upload = new Element("UPLOAD");
        upload.setAttribute("ID", id);
    }

    /**
     * Creates a new AbstractUpload object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractUpload(String id, Element element) {
        if ((element == null) || (!element.getName().equals("UPLOAD"))) {
            element = new Element("UPLOAD");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.upload = element;
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
        if (obj instanceof AbstractUpload) {
            String id = ((AbstractUpload) obj).getId();
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
    public String getId() {
        return upload.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(upload, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(upload, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setFormField(String value) {
        XMLFunction.setElemValue(upload, "FORMFIELD", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFormField() {
        return XMLFunction.getElemValue(upload, "FORMFIELD");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractUpload cloneMe() {
        AbstractUpload obj = null;
        if (this instanceof UploadLocal) {
            obj = new UploadLocal(getId(), (Element) upload.clone());
        } else if (this instanceof UploadFtp) {
            obj = new UploadFtp(getId(), (Element) upload.clone());
        } else if (this instanceof UploadDatabase) {
            obj = new UploadDatabase(getId(), (Element) upload.clone());
        }
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    protected void setType(String type) {
        upload.setAttribute("TYPE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        String ret = "";
        if (this instanceof UploadDatabase) {
            ret = TYPE_DATABASE;
        }
        if (this instanceof UploadFtp) {
            ret = TYPE_FTP;
        }
        if (this instanceof UploadLocal) {
            ret = TYPE_LOCAL;
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return upload;
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
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        String cName = this.getClass().getSimpleName();
        resp.append("<w:setProjectElement\n");
    	resp.append("  type=\"" + cName + "\" name=\"upload_" + getId() + "\"\n");
        resp.append("/><jsp:useBean\n");
        resp.append("  id=\"upload_" + getId() + "\" ");
        resp.append("type=\"br.com.webinside.runtime.component." + cName + "\"\n");
        resp.append("/>");        
        resp.append(CompFunction.setProperties(this, "upload_" + getId()));
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param project DOCUMENT ME!
     */
    public void setProject(AbstractProject project) {
        this.project = project;
    }
}
