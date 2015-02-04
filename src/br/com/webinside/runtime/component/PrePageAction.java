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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class PrePageAction extends AbstractPageAction {

	private static final long serialVersionUID = 1L;

    private static Object listMailSync = new Object();
    private static Object getMailSync = new Object();
    private static Object listFileSync = new Object();
    private static Object gridRefSync = new Object();
    private static Object comboRefSync = new Object();
    private static Object reportRefSync = new Object();
    private static Object downloadRefSync = new Object();
    private static Object javagridSync = new Object();
    private static Object wiobjgridSync = new Object();
    private static Object treeviewSync = new Object();
    private Element listMails;
    private Element getMails;
    private Element listFiles;
    private Element gridRefs;
    private Element comboRefs;
    private Element reportRefs;
    private Element downloadRefs;
    private Element javagrids;
    private Element wiobjgrids;
    private Element treeviews;

    /**
     * Creates a new PrePageAction object.
     *
     * @param page DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public PrePageAction(Page page, Element element) {
        if ((element == null) || (!element.getName().equals("PREPAGE"))) {
            element = new Element("PREPAGE");
        }
        this.pageElement = element;
        this.parent = page;
        this.page = page.page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Page getParent() {
        return parent;
    }

    private void chkStructure(String element, String type) {
        if (page.getChild("PREPAGE") == null) {
            page.addContent(new Element("PREPAGE"));
            pageElement = page.getChild("PREPAGE");
        }
        if (pageElement.getChild(element) == null) {
            pageElement.addContent(new Element(element));
        }
        if (pageElement.getChild(element).getChild(type) == null) {
            pageElement.getChild(element).addContent(new Element(type));
        }
    }

    private void chkStructure(String element) {
        if (page.getChild("PREPAGE") == null) {
            page.addContent(new Element("PREPAGE"));
            pageElement = page.getChild("PREPAGE");
        }
        if (pageElement.getChild(element) == null) {
            pageElement.addContent(new Element(element));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getComboIndex() {
        index = pageElement.getChild("INDEX");
        if (index == null) {
            pageElement.addContent(new Element("INDEX"));
            index = pageElement.getChild("INDEX");
        }
        AbstractProject projparent = null;
        if (parent != null) {
            projparent = parent.getProject();
        }
        String ret = "";
        String ident = "";
        List list = index.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Element ix = (Element) list.get(i);
            String seq = ix.getAttribute("SEQ").getValue();
            Element el = pageElement.getChild(ix.getName());
            List vec = XMLFunction.getElemsByAttr(el, "SEQ", seq, false);
            for (int j = 0; j < vec.size(); j++) {
                Element chEl = (Element) vec.get(j);
                String name = chEl.getName();
                String color = "bldColor_" + name;
                if (name.equals("CONNECTOR")) {
                	String cname = chEl.getChildText("CLASSNAME");
                	if (cname != null && cname.endsWith(".Persist")) {
                        color = "bldColor_PERSIST";
                	}
                }
                String desc = "*** Removido ***";
                String cond = chEl.getChildText("CONDITION");
                if (cond == null) cond = "";
                if (name.indexOf("REF") > -1) {
                    desc = StringA.piece(name, "REF", 1);
                    if (desc.equals("COMBO")) {
                        String subID = "";
                        if (chEl.getChild("SUBID") != null) {
                            subID = chEl.getChild("SUBID").getText();
                        }
                        Combo cbo =
                            (Combo) getProjectElement(projparent, "COMBO",
                                chEl.getChildText("COMBOID"));
                        if (cbo != null) {
                            desc = cbo.getDescription();
                            if (!subID.equals("")) {
                                desc += ("." + subID);
                            }
                        } else {
                            desc = "*** Removido ***";
                        }
                    } else if (desc.equals("GRID")) {
                        String subID = "";
                        if (chEl.getChild("SUBID") != null) {
                            subID = chEl.getChild("SUBID").getText();
                        }
                        AbstractGrid grd =
                            (AbstractGrid) getProjectElement(projparent,
                                "GRID", chEl.getChildText("GRIDID"));
                        if (grd != null) {
                            desc = grd.getDescription();
                            if (!subID.equals("")) {
                                desc += ("." + subID);
                            }
                        } else {
                            desc = "*** Removido ***";
                        }
                    } else if (desc.equals("REPORT")) {
                        try {
                            desc =
                                "Relatório: " + chEl.getChildText("REPORTID");
                        } catch (Exception err) {
                            desc = "*** Removido ***";
                        }
                    } else if (desc.equals("DOWNLOAD")) {
                        AbstractDownload dow =
                            (AbstractDownload) getProjectElement(projparent,
                                "DOWNLOAD", chEl.getChildText("DOWNLOADID"));
                        if (dow != null) {
                            desc = dow.getDescription();
                        } else {
                            desc = "*** Removido ***";
                        }
                    } else if (desc.equals("UPLOAD")) {
                        AbstractUpload upl =
                            (AbstractUpload) getProjectElement(projparent,
                                "UPLOAD", chEl.getChildText("UPLOADID"));
                        if (upl != null) {
                            desc = upl.getDescription();
                        } else {
                            desc = "*** Removido ***";
                        }
                    }
                } else {
                    desc = chEl.getChildText("DESCRIPTION");
                }
                if (!cond.trim().equals("")) {
                    desc += " :: " + cond;
                }
                String clazz = "class='" + color + "'";
                if (name.equals("BLOCK")) ident = "";
                ret += ("<option value='" + i + "' " + clazz + ">" + ident + desc + "</option>\r\n");
                if (name.equals("BLOCK") && 
                		!cond.trim().equalsIgnoreCase("true")) {
                	ident = "&nbsp;&nbsp;&nbsp;";
                }
            }
        }
        return ret;
    }

    //--------------------- LISTMAIL
    public int addListMail(MailList listMail) {
        synchronized (listMailSync) {
            int ret = ErrorCode.NOERROR;
            if (listMail == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("LISTMAILS");
                listMails = pageElement.getChild("LISTMAILS");
                ret = listMail.insertInto(listMails);
                if (ret == ErrorCode.NOERROR) {
                    listMail.setSeq(addIndex("LISTMAILS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getListMails() {
        List ret = new ArrayList();
        listMails = pageElement.getChild("LISTMAILS");
        if (listMails == null) {
            return ret;
        }
        List list = listMails.getChildren("LISTMAIL");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new MailList(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MailList getListMail(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        listMails = pageElement.getChild("LISTMAILS");
        if (listMails == null) {
            return null;
        }
        Element listMail =
            XMLFunction.getChildByAttribute(listMails, "LISTMAIL", "SEQ", seq);
        if (listMail == null) {
            return null;
        }
        return new MailList(listMail);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeListMail(String seq) {
        MailList listMail = getListMail(seq);
        if (listMail == null) {
            return ErrorCode.NULL;
        }
        listMails = pageElement.getChild("LISTMAILS");
        if (listMails == null) {
            return ErrorCode.NOEXIST;
        }
        listMails.removeContent(listMail.listMail);
        if (listMails.getChildren().size() == 0) {
            pageElement.removeContent(listMails);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "LISTMAILS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- GETMAIL
    public int addGetMail(MailGet getMail) {
        synchronized (getMailSync) {
            int ret = ErrorCode.NOERROR;
            if (getMail == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("GETMAILS");
                getMails = pageElement.getChild("GETMAILS");
                ret = getMail.insertInto(getMails);
                if (ret == ErrorCode.NOERROR) {
                    getMail.setSeq(addIndex("GETMAILS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getGetMails() {
        List ret = new ArrayList();
        getMails = pageElement.getChild("GETMAILS");
        if (getMails == null) {
            return ret;
        }
        List get = getMails.getChildren("GETMAIL");
        Element ele = null;
        Iterator i = get.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new MailGet(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MailGet getGetMail(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        getMails = pageElement.getChild("GETMAILS");
        if (getMails == null) {
            return null;
        }
        Element getMail =
            XMLFunction.getChildByAttribute(getMails, "GETMAIL", "SEQ", seq);
        if (getMail == null) {
            return null;
        }
        return new MailGet(getMail);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeGetMail(String seq) {
        MailGet getMail = getGetMail(seq);
        if (getMail == null) {
            return ErrorCode.NULL;
        }
        getMails = pageElement.getChild("GETMAILS");
        if (getMails == null) {
            return ErrorCode.NOEXIST;
        }
        getMails.removeContent(getMail.getMail);
        if (getMails.getChildren().size() == 0) {
            pageElement.removeContent(getMails);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "GETMAILS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //----------------  LISTFILES
    public int addListFile(AbstractFileList listFile) {
        synchronized (listFileSync) {
            String type = "";
            if (listFile instanceof FileListLocal) {
                type = "LOCALS";
            }
            if (listFile instanceof FileListFtp) {
                type = "FTPS";
            }
            int ret = ErrorCode.NOERROR;
            if (listFile == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("LISTFILES", type);
                listFiles = pageElement.getChild("LISTFILES");
                ret = listFile.insertInto(listFiles.getChild(type));
                if (ret == ErrorCode.NOERROR) {
                    listFile.setSeq(addIndex("LISTFILES"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getListFiles(String type) {
        List ret = new ArrayList();
        listFiles = pageElement.getChild("LISTFILES");
        if (listFiles == null) {
            return ret;
        }
        if (listFiles.getChild(type) == null) {
            return ret;
        }
        List list = listFiles.getChild(type).getChildren("LISTFILE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                if (type.equals("LOCALS")) {
                    ret.add(new FileListLocal(ele));
                }
                if (type.equals("FTPS")) {
                    ret.add(new FileListFtp(ele));
                }
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractFileList getListFile(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        listFiles = pageElement.getChild("LISTFILES");
        if (listFiles == null) {
            return null;
        }
        String type = "LOCALS";
        Element listFile =
            XMLFunction.getChildByAttribute(listFiles.getChild(type),
                "LISTFILE", "SEQ", seq);
        if (listFile == null) {
            type = "FTPS";
            listFile =
                XMLFunction.getChildByAttribute(listFiles.getChild(type),
                    "LISTFILE", "SEQ", seq);
            if (listFile == null) {
                return null;
            }
        }
        if (type.equals("LOCALS")) {
            return new FileListLocal(listFile);
        }
        if (type.equals("FTPS")) {
            return new FileListFtp(listFile);
        }
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
        AbstractFileList listFile = getListFile(seq);
        if (listFile == null) {
            return ErrorCode.NULL;
        }
        listFiles = pageElement.getChild("LISTFILES");
        if (listFiles == null) {
            return ErrorCode.NOEXIST;
        }
        String type = "";
        if (listFile instanceof FileListLocal) {
            type = "LOCALS";
        }
        if (listFile instanceof FileListFtp) {
            type = "FTPS";
        }
        listFiles.getChild(type).removeContent(listFile.listFile);
        if (listFiles.getChild(type).getChildren().size() == 0) {
            listFiles.removeContent(listFiles.getChild(type));
        }
        if (listFiles.getChildren().size() == 0) {
            pageElement.removeContent(listFiles);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "LISTFILES", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- GRIDREF
    public int addGridRef(GridRef gridRef) {
        synchronized (gridRefSync) {
            int ret = ErrorCode.NOERROR;
            if (gridRef == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("GRIDREFS");
                gridRefs = pageElement.getChild("GRIDREFS");
                ret = gridRef.insertInto(gridRefs);
                if (ret == ErrorCode.NOERROR) {
                    gridRef.setSeq(addIndex("GRIDREFS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getGridRefs() {
        List ret = new ArrayList();
        gridRefs = pageElement.getChild("GRIDREFS");
        if (gridRefs == null) {
            return ret;
        }
        List list = gridRefs.getChildren("GRIDREF");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new GridRef(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GridRef getGridRef(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        gridRefs = pageElement.getChild("GRIDREFS");
        if (gridRefs == null) {
            return null;
        }
        Element gridRef =
            XMLFunction.getChildByAttribute(gridRefs, "GRIDREF", "SEQ", seq);
        if (gridRef == null) {
            return null;
        }
        return new GridRef(gridRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeGridRef(String seq) {
        GridRef gridRef = getGridRef(seq);
        if (gridRef == null) {
            return ErrorCode.NULL;
        }
        gridRefs = pageElement.getChild("GRIDREFS");
        if (gridRefs == null) {
            return ErrorCode.NOEXIST;
        }
        gridRefs.removeContent(gridRef.gridRef);
        if (gridRefs.getChildren().size() == 0) {
            pageElement.removeContent(gridRefs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "GRIDREFS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- COMBOREF
    public int addComboRef(ComboRef comboRef) {
        synchronized (comboRefSync) {
            int ret = ErrorCode.NOERROR;
            if (comboRef == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("COMBOREFS");
                comboRefs = pageElement.getChild("COMBOREFS");
                ret = comboRef.insertInto(comboRefs);
                if (ret == ErrorCode.NOERROR) {
                    comboRef.setSeq(addIndex("COMBOREFS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getComboRefs() {
        List ret = new ArrayList();
        comboRefs = pageElement.getChild("COMBOREFS");
        if (comboRefs == null) {
            return ret;
        }
        List list = comboRefs.getChildren("COMBOREF");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new ComboRef(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ComboRef getComboRef(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        comboRefs = pageElement.getChild("COMBOREFS");
        if (comboRefs == null) {
            return null;
        }
        Element comboRef =
            XMLFunction.getChildByAttribute(comboRefs, "COMBOREF", "SEQ", seq);
        if (comboRef == null) {
            return null;
        }
        return new ComboRef(comboRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeComboRef(String seq) {
        ComboRef comboRef = getComboRef(seq);
        if (comboRef == null) {
            return ErrorCode.NULL;
        }
        comboRefs = pageElement.getChild("COMBOREFS");
        if (comboRefs == null) {
            return ErrorCode.NOEXIST;
        }
        comboRefs.removeContent(comboRef.comboRef);
        if (comboRefs.getChildren().size() == 0) {
            pageElement.removeContent(comboRefs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "COMBOREFS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- REPORTREF
    public int addReportRef(ReportRef reportRef) {
        synchronized (reportRefSync) {
            int ret = ErrorCode.NOERROR;
            if (reportRef == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("REPORTREFS");
                reportRefs = pageElement.getChild("REPORTREFS");
                ret = reportRef.insertInto(reportRefs);
                if (ret == ErrorCode.NOERROR) {
                    reportRef.setSeq(addIndex("REPORTREFS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getReportRefs() {
        List ret = new ArrayList();
        reportRefs = pageElement.getChild("REPORTREFS");
        if (reportRefs == null) {
            return ret;
        }
        List list = reportRefs.getChildren("REPORTREF");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new ReportRef(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ReportRef getReportRef(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        reportRefs = pageElement.getChild("REPORTREFS");
        if (reportRefs == null) {
            return null;
        }
        Element reportRef =
            XMLFunction.getChildByAttribute(reportRefs, "REPORTREF", "SEQ", seq);
        if (reportRef == null) {
            return null;
        }
        return new ReportRef(reportRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeReportRef(String seq) {
        ReportRef reportRef = getReportRef(seq);
        if (reportRef == null) {
            return ErrorCode.NULL;
        }
        reportRefs = pageElement.getChild("REPORTREFS");
        if (reportRefs == null) {
            return ErrorCode.NOEXIST;
        }
        reportRefs.removeContent(reportRef.reportRef);
        if (reportRefs.getChildren().size() == 0) {
            pageElement.removeContent(reportRefs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "REPORTREFS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- DOWNLOADREF
    public int addDownloadRef(DownloadRef downloadRef) {
        synchronized (downloadRefSync) {
            int ret = ErrorCode.NOERROR;
            if (downloadRef == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("DOWNLOADREFS");
                downloadRefs = pageElement.getChild("DOWNLOADREFS");
                ret = downloadRef.insertInto(downloadRefs);
                if (ret == ErrorCode.NOERROR) {
                    downloadRef.setSeq(addIndex("DOWNLOADREFS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getDownloadRefs() {
        List ret = new ArrayList();
        downloadRefs = pageElement.getChild("DOWNLOADREFS");
        if (downloadRefs == null) {
            return ret;
        }
        List list = downloadRefs.getChildren("DOWNLOADREF");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new DownloadRef(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DownloadRef getDownloadRef(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        downloadRefs = pageElement.getChild("DOWNLOADREFS");
        if (downloadRefs == null) {
            return null;
        }
        Element downloadRef =
            XMLFunction.getChildByAttribute(downloadRefs, "DOWNLOADREF", "SEQ",
                seq);
        if (downloadRef == null) {
            return null;
        }
        return new DownloadRef(downloadRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeDownloadRef(String seq) {
        DownloadRef downloadRef = getDownloadRef(seq);
        if (downloadRef == null) {
            return ErrorCode.NULL;
        }
        downloadRefs = pageElement.getChild("DOWNLOADREFS");
        if (downloadRefs == null) {
            return ErrorCode.NOEXIST;
        }
        downloadRefs.removeContent(downloadRef.downloadRef);
        if (downloadRefs.getChildren().size() == 0) {
            pageElement.removeContent(downloadRefs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "DOWNLOADREFS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getJavaGrids() {
        List ret = new ArrayList();
        javagrids = pageElement.getChild("JAVAGRIDS");
        if (javagrids == null) {
            return ret;
        }
        List list = javagrids.getChildren("JAVAGRID");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new JavaGrid(ele));
            }
        }
        return ret;
    }

    //--------------------- JAVAGRIDS
    public int addJavaGrid(JavaGrid javagrid) {
        synchronized (javagridSync) {
            int ret = ErrorCode.NOERROR;
            if (javagrid == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("JAVAGRIDS");
                javagrids = pageElement.getChild("JAVAGRIDS");
                ret = javagrid.insertInto(javagrids);
                if (ret == ErrorCode.NOERROR) {
                    javagrid.setSeq(addIndex("JAVAGRIDS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaGrid getJavaGrid(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        javagrids = pageElement.getChild("JAVAGRIDS");
        if (javagrids == null) {
            return null;
        }
        Element javagrid =
            XMLFunction.getChildByAttribute(javagrids, "JAVAGRID", "SEQ", seq);
        if (javagrid == null) {
            return null;
        }
        return new JavaGrid(javagrid);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeJavaGrid(String seq) {
        JavaGrid javagrid = getJavaGrid(seq);
        if (javagrid == null) {
            return ErrorCode.NULL;
        }
        javagrids = pageElement.getChild("JAVAGRIDS");
        if (javagrids == null) {
            return ErrorCode.NOEXIST;
        }
        javagrids.removeContent(javagrid.javagrid);
        if (javagrids.getChildren().size() == 0) {
            pageElement.removeContent(javagrids);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "JAVAGRIDS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- WIOBJECTGRIDS
    public int addWIObjectGrid(WIObjectGrid wiobjgrid) {
        synchronized (wiobjgridSync) {
            int ret = ErrorCode.NOERROR;
            if (wiobjgrid == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("WIOBJECTGRIDS");
                wiobjgrids = pageElement.getChild("WIOBJECTGRIDS");
                ret = wiobjgrid.insertInto(wiobjgrids);
                if (ret == ErrorCode.NOERROR) {
                    wiobjgrid.setSeq(addIndex("WIOBJECTGRIDS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getWIObjectGrids() {
        List ret = new ArrayList();
        wiobjgrids = pageElement.getChild("WIOBJECTGRIDS");
        if (wiobjgrids == null) {
            return ret;
        }
        List list = wiobjgrids.getChildren("WIOBJECTGRID");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new WIObjectGrid(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIObjectGrid getWIObjectGrid(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        wiobjgrids = pageElement.getChild("WIOBJECTGRIDS");
        if (wiobjgrids == null) {
            return null;
        }
        Element wiobjgrid =
            XMLFunction.getChildByAttribute(wiobjgrids, "WIOBJECTGRID", "SEQ",
                seq);
        if (wiobjgrid == null) {
            return null;
        }
        return new WIObjectGrid(wiobjgrid);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeWIObjectGrid(String seq) {
        WIObjectGrid wiobjgrid = getWIObjectGrid(seq);
        if (wiobjgrid == null) {
            return ErrorCode.NULL;
        }
        wiobjgrids = pageElement.getChild("WIOBJECTGRIDS");
        if (wiobjgrids == null) {
            return ErrorCode.NOEXIST;
        }
        wiobjgrids.removeContent(wiobjgrid.wiobjgrid);
        if (wiobjgrids.getChildren().size() == 0) {
            pageElement.removeContent(wiobjgrids);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "WIOBJECTGRIDS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- TREEVIEW
    public int addTreeView(TreeViewElement treeview) {
        synchronized (treeviewSync) {
            int ret = ErrorCode.NOERROR;
            if (treeview == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("TREEVIEWS");
                treeviews = pageElement.getChild("TREEVIEWS");
                ret = treeview.insertInto(treeviews);
                if (ret == ErrorCode.NOERROR) {
                    treeview.setSeq(addIndex("TREEVIEWS"));
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getTreeViews() {
        List ret = new ArrayList();
        treeviews = pageElement.getChild("TREEVIEWS");
        if (treeviews == null) {
            return ret;
        }
        List list = treeviews.getChildren("TREEVIEW");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new TreeViewElement(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TreeViewElement getTreeView(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        treeviews = pageElement.getChild("TREEVIEWS");
        if (treeviews == null) {
            return null;
        }
        Element treeview =
            XMLFunction.getChildByAttribute(treeviews, "TREEVIEW", "SEQ", seq);
        if (treeview == null) {
            return null;
        }
        return new TreeViewElement(treeview);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeTreeView(String seq) {
        TreeViewElement treeview = getTreeView(seq);
        if (treeview == null) {
            return ErrorCode.NULL;
        }
        treeviews = pageElement.getChild("TREEVIEWS");
        if (treeviews == null) {
            return ErrorCode.NOEXIST;
        }
        treeviews.removeContent(treeview.treeview);
        if (treeviews.getChildren().size() == 0) {
            pageElement.removeContent(treeviews);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "TREEVIEWS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }
}
