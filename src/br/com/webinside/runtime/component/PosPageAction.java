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

import java.util.List;
import org.jdom.Element;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class PosPageAction extends AbstractPageAction {

	private static final long serialVersionUID = 1L;

	/**
     * Creates a new PosPageAction object.
     *
     * @param page DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public PosPageAction(Page page, Element element) {
        if ((element == null) || (!element.getName().equals("POSPAGE"))) {
            element = new Element("POSPAGE");
        }
        this.pageElement = element;
        this.parent = page;
        this.page = page.page;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ComboRef getComboRef(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GridRef getGridRef(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DownloadRef getDownloadRef(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaGrid getJavaGrid(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIObjectGrid getWIObjectGrid(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MailList getListMail(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TreeViewElement getTreeView(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractFileList getListFile(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ReportRef getReportRef(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MailGet getGetMail(String seq) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param treeview DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addTreeView(TreeViewElement treeview) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getTreeViews() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeTreeView(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param javagrid DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addJavaGrid(JavaGrid javagrid) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getJavaGrids() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeJavaGrid(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiobjgrid DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addWIObjectGrid(WIObjectGrid wiobjgrid) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getWIObjectGrids() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeWIObjectGrid(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param getMail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addGetMail(MailGet getMail) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getGetMails() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeGetMail(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param listFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addListFile(AbstractFileList listFile) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getListFiles(String type) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeListFile(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param listMail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addListMail(MailList listMail) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getListMails() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeListMail(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param comboRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addComboRef(ComboRef comboRef) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getComboRefs() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeComboRef(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param gridRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addGridRef(GridRef gridRef) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getGridRefs() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeGridRef(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param downloadRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addDownloadRef(DownloadRef downloadRef) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getDownloadRefs() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeDownloadRef(String seq) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param reportRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addReportRef(ReportRef reportRef) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getReportRefs() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeReportRef(String seq) {
        return 0;
    }
}
