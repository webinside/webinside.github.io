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

import org.jdom.*;

import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class FileRemoveFtp extends AbstractFileRemove {

	private static final long serialVersionUID = 1L;

    /**
     * Creates a new FileRemoveFtp object.
     */
    public FileRemoveFtp() {
        this.setSeq("");
    }

    /**
     * Creates a new FileRemoveFtp object.
     *
     * @param element DOCUMENT ME!
     */
    public FileRemoveFtp(Element element) {
        if ((element == null) || (!element.getName().equals("REMOVEFILE"))) {
            element = new Element("REMOVEFILE");
        }
        this.removeFile = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setHostId(String value) {
        XMLFunction.setElemValue(this.removeFile, "HOSTID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHostId() {
        return XMLFunction.getElemValue(this.removeFile, "HOSTID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        FileRemoveFtp obj = new FileRemoveFtp((Element) removeFile.clone());
        return obj;
    }
}
