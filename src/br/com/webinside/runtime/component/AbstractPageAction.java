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

import java.io.Serializable;
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
 * @version $Revision: 1.2 $
 */
public abstract class AbstractPageAction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Object blockSync = new Object();
	private static Object redirSync = new Object();
    private static Object sendMailSync = new Object();
    private static Object killMailSync = new Object();
    private static Object objectSync = new Object();
    private static Object transactionSync = new Object();
    private static Object listSync = new Object();
    private static Object setSync = new Object();
    private static Object killSync = new Object();
    private static Object updateSync = new Object();
    private static Object removeFileSync = new Object();
    private static Object uploadRefSync = new Object();
    private static Object cookieSync = new Object();
    private static Object groovySync = new Object();
    private static Object connectorSync = new Object();
    private static Object fileinSync = new Object();
    private static Object fileoutSync = new Object();
    private static Object socketSync = new Object();
    private static Object webserviceSync = new Object();
    private static Object jspSync = new Object();
    /** DOCUMENT ME! */
    protected Element page;
    /** DOCUMENT ME! */
    public Page parent;
    /** DOCUMENT ME! */
    protected Element pageElement;
    /** DOCUMENT ME! */
    protected Element index;
    private Element blocks;
    private Element redirs;
    private Element sendMails;
    private Element killMails;
    private Element transactions;
    private Element objects;
    private Element lists;
    private Element sets;
    private Element kills;
    private Element updates;
    private Element removeFiles;
    private Element uploadRefs;
    private Element cookies;
    private Element groovys;
    private Element connectors;
    private Element fileins;
    private Element fileouts;
    private Element sockets;
    private Element webservices;
    private Element jsps;

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement getPageElement(String type, String seq) {
        if (type.equals("BLOCKS")) {
            return getBlock(seq);
        } else if (type.equals("REDIRS")) {
            return getRedir(seq);
        } else if (type.equals("KILLS")) {
            return getKill(seq);
        } else if (type.equals("SETS")) {
            return getSet(seq);
        } else if (type.equals("FILEINS")) {
            return getFileIn(seq);
        } else if (type.equals("FILEOUTS")) {
            return getFileOut(seq);
        } else if (type.equals("TRANSACTIONS")) {
            return getTransaction(seq);
        } else if (type.equals("OBJECTS")) {
            return getObject(seq);
        } else if (type.equals("TREEVIEWS")) {
            return getTreeView(seq);
        } else if (type.equals("LISTS")) {
            return getList(seq);
        } else if (type.equals("UPDATES")) {
            return getUpdate(seq);
        } else if (type.equals("SENDMAILS")) {
            return getSendMail(seq);
        } else if (type.equals("COOKIES")) {
            return getCookie(seq);
        } else if (type.equals("GROOVYS")) {
            return getGroovy(seq);
        } else if (type.equals("CONNECTORS")) {
            return getConnector(seq);
        } else if (type.equals("SOCKETS")) {
            return getSocketElement(seq);
        } else if (type.equals("WEBSERVICES")) {
            return getWebServiceClient(seq);
        } else if (type.equals("JAVAGRIDS")) {
            return getJavaGrid(seq);
        } else if (type.equals("WIOBJECTGRIDS")) {
            return getWIObjectGrid(seq);
        } else if (type.equals("REMOVEFILES")) {
            return getRemoveFile(seq);
        } else if (type.equals("KILLMAILS")) {
            return getKillMail(seq);
        } else if (type.equals("GETMAILS")) {
            return getGetMail(seq);
        } else if (type.equals("LISTFILES")) {
            return getListFile(seq);
        } else if (type.equals("LISTMAILS")) {
            return getListMail(seq);
        } else if (type.equals("COMBOREFS")) {
            return getComboRef(seq);
        } else if (type.equals("GRIDREFS")) {
            return getGridRef(seq);
        } else if (type.equals("DOWNLOADREFS")) {
            return getDownloadRef(seq);
        } else if (type.equals("UPLOADREFS")) {
            return getUploadRef(seq);
        } else if (type.equals("REPORTREFS")) {
            return getReportRef(seq);
        } else if (type.equals("JSPELEMENTS")) {
            return getJspElement(seq);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public void addPageElement(String type, AbstractActionElement element) {
        if (type.equals("BLOCKS")) {
            addBlock((BlockElement) element);
        } else if (type.equals("REDIRS")) {
            addRedir((AbstractRedir) element);
        } else if (type.equals("KILLS")) {
            addKill((KillElement) element);
        } else if (type.equals("SETS")) {
            addSet((SetElement) element);
        } else if (type.equals("FILEINS")) {
            addFileIn((FileIn) element);
        } else if (type.equals("FILEOUTS")) {
            addFileOut((FileOut) element);
        } else if (type.equals("TRANSACTIONS")) {
            addTransaction((TransactionElement) element);
        } else if (type.equals("OBJECTS")) {
            addObject((ObjectElement) element);
        } else if (type.equals("TREEVIEWS")) {
            addTreeView((TreeViewElement) element);
        } else if (type.equals("LISTS")) {
            addList((ListElement) element);
        } else if (type.equals("UPDATES")) {
            addUpdate((UpdateElement) element);
        } else if (type.equals("SENDMAILS")) {
            addSendMail((MailSend) element);
        } else if (type.equals("COOKIES")) {
            addCookie((AbstractCookie) element);
        } else if (type.equals("GROOVYS")) {
            addGroovy((GroovyElement) element);
        } else if (type.equals("CONNECTORS")) {
            addConnector((Connector) element);
        } else if (type.equals("SOCKETS")) {
            addSocketElement((SocketElement) element);
        } else if (type.equals("WEBSERVICES")) {
            addWebServiceClient((WebServiceClient) element);
        } else if (type.equals("JAVAGRIDS")) {
            addJavaGrid((JavaGrid) element);
        } else if (type.equals("WIOBJECTGRIDS")) {
            addWIObjectGrid((WIObjectGrid) element);
        } else if (type.equals("REMOVEFILES")) {
            addRemoveFile((AbstractFileRemove) element);
        } else if (type.equals("KILLMAILS")) {
            addKillMail((MailKill) element);
        } else if (type.equals("GETMAILS")) {
            addGetMail((MailGet) element);
        } else if (type.equals("LISTFILES")) {
            addListFile((AbstractFileList) element);
        } else if (type.equals("LISTMAILS")) {
            addListMail((MailList) element);
        } else if (type.equals("COMBOREFS")) {
            addComboRef((ComboRef) element);
        } else if (type.equals("GRIDREFS")) {
            addGridRef((GridRef) element);
        } else if (type.equals("DOWNLOADREFS")) {
            addDownloadRef((DownloadRef) element);
        } else if (type.equals("UPLOADREFS")) {
            addUploadRef((UploadRef) element);
        } else if (type.equals("REPORTREFS")) {
            addReportRef((ReportRef) element);
        } else if (type.equals("JSPELEMENTS")) {
            addJspElement((JspElement) element);
        }
    }

    public void removePageElement(String type, String seq) {
        if (type.equals("BLOCKS")) {
            removeBlock(seq);
        } else if (type.equals("REDIRS")) {
            removeRedir(seq);
        } else if (type.equals("KILLS")) {
            removeKill(seq);
        } else if (type.equals("SETS")) {
            removeSet(seq);
        } else if (type.equals("FILEINS")) {
            removeFileIn(seq);
        } else if (type.equals("FILEOUTS")) {
            removeFileOut(seq);
        } else if (type.equals("TRANSACTIONS")) {
            removeTransaction(seq);
        } else if (type.equals("OBJECTS")) {
            removeObject(seq);
        } else if (type.equals("TREEVIEWS")) {
            removeTreeView(seq);
        } else if (type.equals("LISTS")) {
            removeList(seq);
        } else if (type.equals("UPDATES")) {
            removeUpdate(seq);
        } else if (type.equals("SENDMAILS")) {
            removeSendMail(seq);
        } else if (type.equals("COOKIES")) {
            removeCookie(seq);
        } else if (type.equals("GROOVYS")) {
            removeGroovy(seq);
        } else if (type.equals("CONNECTORS")) {
            removeConnector(seq);
        } else if (type.equals("SOCKETS")) {
            removeSocketElement(seq);
        } else if (type.equals("WEBSERVICES")) {
            removeWSClient(seq);
        } else if (type.equals("JAVAGRIDS")) {
            removeJavaGrid(seq);
        } else if (type.equals("WIOBJECTGRIDS")) {
            removeWIObjectGrid(seq);
        } else if (type.equals("REMOVEFILES")) {
            removeRemoveFile(seq);
        } else if (type.equals("KILLMAILS")) {
            removeKillMail(seq);
        } else if (type.equals("GETMAILS")) {
            removeGetMail(seq);
        } else if (type.equals("LISTFILES")) {
            removeListFile(seq);
        } else if (type.equals("LISTMAILS")) {
            removeListMail(seq);
        } else if (type.equals("COMBOREFS")) {
            removeComboRef(seq);
        } else if (type.equals("GRIDREFS")) {
            removeGridRef(seq);
        } else if (type.equals("DOWNLOADREFS")) {
            removeDownloadRef(seq);
        } else if (type.equals("UPLOADREFS")) {
            removeUploadRef(seq);
        } else if (type.equals("REPORTREFS")) {
            removeReportRef(seq);
        } else if (type.equals("JSPELEMENTS")) {
            removeJspElement(seq);
        }
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
        String elementType = "PREPAGE";
        if (this instanceof PosPageAction) {
            elementType = "POSPAGE";
        }
        if (page.getChild(elementType) == null) {
            page.addContent(new Element(elementType));
            pageElement = page.getChild(elementType);
        }
        if (pageElement.getChild(element) == null) {
            pageElement.addContent(new Element(element));
        }
        if (pageElement.getChild(element).getChild(type) == null) {
            pageElement.getChild(element).addContent(new Element(type));
        }
    }

    private void chkStructure(String element) {
        String elementType = "PREPAGE";
        if (this instanceof PosPageAction) {
            elementType = "POSPAGE";
        }
        if (page.getChild(elementType) == null) {
            page.addContent(new Element(elementType));
            pageElement = page.getChild(elementType);
        }
        if (pageElement.getChild(element) == null) {
            pageElement.addContent(new Element(element));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String addIndex(String type) {
        index = pageElement.getChild("INDEX");
        if (index == null) {
            pageElement.addContent(new Element("INDEX"));
            index = pageElement.getChild("INDEX");
        }
        int i;
        for (i = 1;
                    XMLFunction.getChildByAttribute(index, type, "SEQ", i + "") != null;
                    i++) {
        }
        index.addContent(new Element(type).setAttribute("SEQ", i + ""));
        return i + "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param redir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addRedir(AbstractRedir redir) {
        synchronized (redirSync) {
            String type = "";
            if (redir instanceof RedirSql) {
                type = "SQLS";
            }
            if (redir instanceof RedirConditional) {
                type = "CONDITIONALS";
            }
            int ret = ErrorCode.NOERROR;
            if (redir == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("REDIRS", type);
                redirs = pageElement.getChild("REDIRS");
                ret = redir.insertInto(redirs.getChild(type));
                if (ret == ErrorCode.NOERROR) {
                    redir.setSeq(addIndex("REDIRS"));
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
    public List getRedirs(String type) {
        List ret = new ArrayList();
        redirs = pageElement.getChild("REDIRS");
        if (redirs == null) {
            return ret;
        }
        if (redirs.getChild(type) == null) {
            return ret;
        }
        List list = redirs.getChild(type).getChildren("REDIR");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                if (type.equals("SQLS")) {
                    ret.add(new RedirSql(ele));
                }
                if (type.equals("CONDITIONALS")) {
                    ret.add(new RedirConditional(ele));
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
    public AbstractRedir getRedir(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        redirs = pageElement.getChild("REDIRS");
        if (redirs == null) {
            return null;
        }
        String type = "SQLS";
        Element redir =
            XMLFunction.getChildByAttribute(redirs.getChild(type), "REDIR",
                "SEQ", seq);
        if (redir == null) {
            type = "CONDITIONALS";
            redir =
                XMLFunction.getChildByAttribute(redirs.getChild(type), "REDIR",
                    "SEQ", seq);
            if (redir == null) {
                return null;
            }
        }
        if (type.equals("SQLS")) {
            return new RedirSql(redir);
        }
        if (type.equals("CONDITIONALS")) {
            return new RedirConditional(redir);
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
    public int removeRedir(String seq) {
        AbstractRedir redir = getRedir(seq);
        if (redir == null) {
            return ErrorCode.NULL;
        }
        redirs = pageElement.getChild("REDIRS");
        if (redirs == null) {
            return ErrorCode.NOEXIST;
        }
        String type = "";
        if (redir instanceof RedirSql) {
            type = "SQLS";
        }
        if (redir instanceof RedirConditional) {
            type = "CONDITIONALS";
        }
        redirs.getChild(type).removeContent(redir.redir);
        if (redirs.getChild(type).getChildren().size() == 0) {
            redirs.removeContent(redirs.getChild(type));
        }
        if (redirs.getChildren().size() == 0) {
            pageElement.removeContent(redirs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "REDIRS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- SENDMAIL
    public int addSendMail(MailSend sendMail) {
        synchronized (sendMailSync) {
            int ret = ErrorCode.NOERROR;
            if (sendMail == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("SENDMAILS");
                sendMails = pageElement.getChild("SENDMAILS");
                ret = sendMail.insertInto(sendMails);
                if (ret == ErrorCode.NOERROR) {
                    sendMail.setSeq(addIndex("SENDMAILS"));
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
    public List getSendMails() {
        List ret = new ArrayList();
        sendMails = pageElement.getChild("SENDMAILS");
        if (sendMails == null) {
            return ret;
        }
        List list = sendMails.getChildren("SENDMAIL");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new MailSend(ele));
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
    public MailSend getSendMail(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        sendMails = pageElement.getChild("SENDMAILS");
        if (sendMails == null) {
            return null;
        }
        Element sendMail =
            XMLFunction.getChildByAttribute(sendMails, "SENDMAIL", "SEQ", seq);
        if (sendMail == null) {
            return null;
        }
        MailSend send = new MailSend(sendMail);
        send.prjID = getParent().getProject().getId();
        return send;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeSendMail(String seq) {
        MailSend sendMail = getSendMail(seq);
        if (sendMail == null) {
            return ErrorCode.NULL;
        }
        sendMails = pageElement.getChild("SENDMAILS");
        if (sendMails == null) {
            return ErrorCode.NOEXIST;
        }
        sendMails.removeContent(sendMail.sendMail);
        if (sendMails.getChildren().size() == 0) {
            pageElement.removeContent(sendMails);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "SENDMAILS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- KILLMAIL
    public int addKillMail(MailKill killMail) {
        synchronized (killMailSync) {
            int ret = ErrorCode.NOERROR;
            if (killMail == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("KILLMAILS");
                killMails = pageElement.getChild("KILLMAILS");
                ret = killMail.insertInto(killMails);
                if (ret == ErrorCode.NOERROR) {
                    killMail.setSeq(addIndex("KILLMAILS"));
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
    public List getKillMails() {
        List ret = new ArrayList();
        killMails = pageElement.getChild("KILLMAILS");
        if (killMails == null) {
            return ret;
        }
        List list = killMails.getChildren("KILLMAIL");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new MailKill(ele));
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
    public MailKill getKillMail(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        killMails = pageElement.getChild("KILLMAILS");
        if (killMails == null) {
            return null;
        }
        Element killMail =
            XMLFunction.getChildByAttribute(killMails, "KILLMAIL", "SEQ", seq);
        if (killMail == null) {
            return null;
        }
        return new MailKill(killMail);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeKillMail(String seq) {
        MailKill killMail = getKillMail(seq);
        if (killMail == null) {
            return ErrorCode.NULL;
        }
        killMails = pageElement.getChild("KILLMAILS");
        if (killMails == null) {
            return ErrorCode.NOEXIST;
        }
        killMails.removeContent(killMail.killMail);
        if (killMails.getChildren().size() == 0) {
            pageElement.removeContent(killMails);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "KILLMAILS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- OBJECT
    public int addObject(ObjectElement object) {
        synchronized (objectSync) {
            int ret = ErrorCode.NOERROR;
            if (object == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("OBJECTS");
                objects = pageElement.getChild("OBJECTS");
                ret = object.insertInto(objects);
                if (ret == ErrorCode.NOERROR) {
                    object.setSeq(addIndex("OBJECTS"));
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
    public List getObjects() {
        List ret = new ArrayList();
        objects = pageElement.getChild("OBJECTS");
        if (objects == null) {
            return ret;
        }
        List list = objects.getChildren("OBJECT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new ObjectElement(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ObjectElement getObjectByID(String id) {
        ObjectElement ret = null;
        objects = pageElement.getChild("OBJECTS");
        if (objects == null) {
            return ret;
        }
        List list = objects.getChildren("OBJECT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret = new ObjectElement(ele);
                if (ret.getWIObj().equals(id)) {
                    return ret;
                }
            }
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
    public ObjectElement getObject(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        objects = pageElement.getChild("OBJECTS");
        if (objects == null) {
            return null;
        }
        Element object =
            XMLFunction.getChildByAttribute(objects, "OBJECT", "SEQ", seq);
        if (object == null) {
            return null;
        }
        return new ObjectElement(object);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeObject(String seq) {
        ObjectElement object = getObject(seq);
        if (object == null) {
            return ErrorCode.NULL;
        }
        objects = pageElement.getChild("OBJECTS");
        if (objects == null) {
            return ErrorCode.NOEXIST;
        }
        objects.removeContent(object.object);
        if (objects.getChildren().size() == 0) {
            pageElement.removeContent(objects);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "OBJECTS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- TRANSACTION
    public int addTransaction(TransactionElement transaction) {
        synchronized (transactionSync) {
            int ret = ErrorCode.NOERROR;
            if (transaction == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("TRANSACTIONS");
                transactions = pageElement.getChild("TRANSACTIONS");
                ret = transaction.insertInto(transactions);
                if (ret == ErrorCode.NOERROR) {
                    transaction.setSeq(addIndex("TRANSACTIONS"));
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
    public List getTransactions() {
        List ret = new ArrayList();
        transactions = pageElement.getChild("TRANSACTIONS");
        if (transactions == null) {
            return ret;
        }
        List list = transactions.getChildren("TRANSACTION");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new TransactionElement(ele));
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
    public TransactionElement getTransaction(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        transactions = pageElement.getChild("TRANSACTIONS");
        if (transactions == null) {
            return null;
        }
        Element transaction =
            XMLFunction.getChildByAttribute(transactions, "TRANSACTION", "SEQ", seq);
        if (transaction == null) {
            return null;
        }
        return new TransactionElement(transaction);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeTransaction(String seq) {
        TransactionElement transaction = getTransaction(seq);
        if (transaction == null) {
            return ErrorCode.NULL;
        }
        transactions = pageElement.getChild("TRANSACTIONS");
        if (transactions == null) {
            return ErrorCode.NOEXIST;
        }
        transactions.removeContent(transaction.transaction);
        if (transactions.getChildren().size() == 0) {
            pageElement.removeContent(transactions);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "TRANSACTIONS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- LIST
    public int addList(ListElement list) {
        synchronized (listSync) {
            int ret = ErrorCode.NOERROR;
            if (list == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("LISTS");
                lists = pageElement.getChild("LISTS");
                ret = list.insertInto(lists);
                if (ret == ErrorCode.NOERROR) {
                    list.setSeq(addIndex("LISTS"));
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
    public List getLists() {
        List ret = new ArrayList();
        lists = pageElement.getChild("LISTS");
        if (lists == null) {
            return ret;
        }
        List list = lists.getChildren("LIST");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new ListElement(ele));
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
    public ListElement getList(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        lists = pageElement.getChild("LISTS");
        if (lists == null) {
            return null;
        }
        Element list =
            XMLFunction.getChildByAttribute(lists, "LIST", "SEQ", seq);
        if (list == null) {
            return null;
        }
        return new ListElement(list);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeList(String seq) {
        ListElement list = getList(seq);
        if (list == null) {
            return ErrorCode.NULL;
        }
        lists = pageElement.getChild("LISTS");
        if (lists == null) {
            return ErrorCode.NOEXIST;
        }
        lists.removeContent(list.list);
        if (lists.getChildren().size() == 0) {
            pageElement.removeContent(lists);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index, "LISTS",
                    "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- SET
    public int addSet(SetElement set) {
        synchronized (setSync) {
            int ret = ErrorCode.NOERROR;
            if (set == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("SETS");
                sets = pageElement.getChild("SETS");
                ret = set.insertInto(sets);
                if (ret == ErrorCode.NOERROR) {
                    set.setSeq(addIndex("SETS"));
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
    public List getSets() {
        List ret = new ArrayList();
        sets = pageElement.getChild("SETS");
        if (sets == null) {
            return ret;
        }
        List set = sets.getChildren("SET");
        Element ele = null;
        Iterator i = set.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new SetElement(ele));
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
    public SetElement getSet(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        sets = pageElement.getChild("SETS");
        if (sets == null) {
            return null;
        }
        Element set = XMLFunction.getChildByAttribute(sets, "SET", "SEQ", seq);
        if (set == null) {
            return null;
        }
        return new SetElement(set);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeSet(String seq) {
        SetElement set = getSet(seq);
        if (set == null) {
            return ErrorCode.NULL;
        }
        sets = pageElement.getChild("SETS");
        if (sets == null) {
            return ErrorCode.NOEXIST;
        }
        sets.removeContent(set.set);
        if (sets.getChildren().size() == 0) {
            pageElement.removeContent(sets);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index, "SETS",
                    "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- BLOCK
    public int addBlock(BlockElement block) {
        synchronized (blockSync) {
            int ret = ErrorCode.NOERROR;
            if (block == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("BLOCKS");
                blocks = pageElement.getChild("BLOCKS");
                ret = block.insertInto(blocks);
                if (ret == ErrorCode.NOERROR) {
                	block.setSeq(addIndex("BLOCKS"));
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
    public List getBlocks() {
        List ret = new ArrayList();
        blocks = pageElement.getChild("BLOCKS");
        if (blocks == null) {
            return ret;
        }
        List block = blocks.getChildren("BLOCK");
        Element ele = null;
        Iterator i = block.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new BlockElement(ele));
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
    public BlockElement getBlock(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        blocks = pageElement.getChild("BLOCKS");
        if (blocks == null) {
            return null;
        }
        Element BLOCK = XMLFunction.getChildByAttribute(blocks, "BLOCK", "SEQ", seq);
        if (BLOCK == null) {
            return null;
        }
        return new BlockElement(BLOCK);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeBlock(String seq) {
        BlockElement block = getBlock(seq);
        if (block == null) {
            return ErrorCode.NULL;
        }
        blocks = pageElement.getChild("BLOCKS");
        if (blocks == null) {
            return ErrorCode.NOEXIST;
        }
        blocks.removeContent(block.block);
        if (blocks.getChildren().size() == 0) {
            pageElement.removeContent(blocks);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index, "BLOCKS",
                    "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }
    
    //--------------------- KILL
    public int addKill(KillElement kill) {
        synchronized (killSync) {
            int ret = ErrorCode.NOERROR;
            if (kill == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("KILLS");
                kills = pageElement.getChild("KILLS");
                ret = kill.insertInto(kills);
                if (ret == ErrorCode.NOERROR) {
                    kill.setSeq(addIndex("KILLS"));
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
    public List getKills() {
        List ret = new ArrayList();
        kills = pageElement.getChild("KILLS");
        if (kills == null) {
            return ret;
        }
        List list = kills.getChildren("KILL");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new KillElement(ele));
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
    public KillElement getKill(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        kills = pageElement.getChild("KILLS");
        if (kills == null) {
            return null;
        }
        Element kill =
            XMLFunction.getChildByAttribute(kills, "KILL", "SEQ", seq);
        if (kill == null) {
            return null;
        }
        return new KillElement(kill);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeKill(String seq) {
        KillElement kill = getKill(seq);
        if (kill == null) {
            return ErrorCode.NULL;
        }
        kills = pageElement.getChild("KILLS");
        if (kills == null) {
            return ErrorCode.NOEXIST;
        }
        kills.removeContent(kill.kill);
        if (kills.getChildren().size() == 0) {
            pageElement.removeContent(kills);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index, "KILLS",
                    "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- JSPElement
    public int addJspElement(JspElement jsp) {
        synchronized (jspSync) {
            int ret = ErrorCode.NOERROR;
            if (jsp == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("JSPELEMENTS");
                jsps = pageElement.getChild("JSPELEMENTS");
                ret = jsp.insertInto(jsps);
                if (ret == ErrorCode.NOERROR) {
                    jsp.setSeq(addIndex("JSPELEMENTS"));
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
    public List getJspElements() {
        List ret = new ArrayList();
        jsps = pageElement.getChild("JSPELEMENTS");
        if (jsps == null) {
            return ret;
        }
        List list = jsps.getChildren("JSPELEMENT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new JspElement(ele));
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
    public JspElement getJspElement(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        jsps = pageElement.getChild("JSPELEMENTS");
        if (jsps == null) {
            return null;
        }
        Element jsp =
            XMLFunction.getChildByAttribute(jsps, "JSPELEMENT", "SEQ", seq);
        if (jsp == null) {
            return null;
        }
        return new JspElement(jsp);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeJspElement(String seq) {
        JspElement jsp = getJspElement(seq);
        if (jsp == null) {
            return ErrorCode.NULL;
        }
        jsps = pageElement.getChild("JSPELEMENTS");
        if (jsps == null) {
            return ErrorCode.NOEXIST;
        }
        jsps.removeContent(jsp.jspElement);
        if (jsps.getChildren().size() == 0) {
            pageElement.removeContent(jsps);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "JSPELEMENTS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- UPDATE
    public int addUpdate(UpdateElement update) {
        synchronized (updateSync) {
            int ret = ErrorCode.NOERROR;
            if (update == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("UPDATES");
                updates = pageElement.getChild("UPDATES");
                ret = update.insertInto(updates);
                if (ret == ErrorCode.NOERROR) {
                    update.setSeq(addIndex("UPDATES"));
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
    public List getUpdates() {
        List ret = new ArrayList();
        updates = pageElement.getChild("UPDATES");
        if (updates == null) {
            return ret;
        }
        List list = updates.getChildren("UPDATE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new UpdateElement(ele));
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
    public UpdateElement getUpdate(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        updates = pageElement.getChild("UPDATES");
        if (updates == null) {
            return null;
        }
        Element update =
            XMLFunction.getChildByAttribute(updates, "UPDATE", "SEQ", seq);
        if (update == null) {
            return null;
        }
        return new UpdateElement(update);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeUpdate(String seq) {
        UpdateElement update = getUpdate(seq);
        if (update == null) {
            return ErrorCode.NULL;
        }
        updates = pageElement.getChild("UPDATES");
        if (updates == null) {
            return ErrorCode.NOEXIST;
        }
        updates.removeContent(update.update);
        if (updates.getChildren().size() == 0) {
            pageElement.removeContent(updates);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "UPDATES", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //----------------  REMOVEFILES
    public int addRemoveFile(AbstractFileRemove removeFile) {
        synchronized (removeFileSync) {
            String type = "";
            if (removeFile instanceof FileRemoveLocal) {
                type = "LOCALS";
            }
            if (removeFile instanceof FileRemoveFtp) {
                type = "FTPS";
            }
            int ret = ErrorCode.NOERROR;
            if (removeFile == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("REMOVEFILES", type);
                removeFiles = pageElement.getChild("REMOVEFILES");
                ret = removeFile.insertInto(removeFiles.getChild(type));
                if (ret == ErrorCode.NOERROR) {
                    removeFile.setSeq(addIndex("REMOVEFILES"));
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
    public List getRemoveFiles(String type) {
        List ret = new ArrayList();
        removeFiles = pageElement.getChild("REMOVEFILES");
        if (removeFiles == null) {
            return ret;
        }
        if (removeFiles.getChild(type) == null) {
            return ret;
        }
        List list = removeFiles.getChild(type).getChildren("REMOVEFILE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                if (type.equals("LOCALS")) {
                    ret.add(new FileRemoveLocal(ele));
                }
                if (type.equals("FTPS")) {
                    ret.add(new FileRemoveFtp(ele));
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
    public AbstractFileRemove getRemoveFile(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        removeFiles = pageElement.getChild("REMOVEFILES");
        if (removeFiles == null) {
            return null;
        }
        String type = "LOCALS";
        Element removeFile =
            XMLFunction.getChildByAttribute(removeFiles.getChild(type),
                "REMOVEFILE", "SEQ", seq);
        if (removeFile == null) {
            type = "FTPS";
            removeFile =
                XMLFunction.getChildByAttribute(removeFiles.getChild(type),
                    "REMOVEFILE", "SEQ", seq);
            if (removeFile == null) {
                return null;
            }
        }
        if (type.equals("LOCALS")) {
            return new FileRemoveLocal(removeFile);
        }
        if (type.equals("FTPS")) {
            return new FileRemoveFtp(removeFile);
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
    public int removeRemoveFile(String seq) {
        AbstractFileRemove removeFile = getRemoveFile(seq);
        if (removeFile == null) {
            return ErrorCode.NULL;
        }
        removeFiles = pageElement.getChild("REMOVEFILES");
        if (removeFiles == null) {
            return ErrorCode.NOEXIST;
        }
        String type = "";
        if (removeFile instanceof FileRemoveLocal) {
            type = "LOCALS";
        }
        if (removeFile instanceof FileRemoveFtp) {
            type = "FTPS";
        }
        removeFiles.getChild(type).removeContent(removeFile.removeFile);
        if (removeFiles.getChild(type).getChildren().size() == 0) {
            removeFiles.removeContent(removeFiles.getChild(type));
        }
        if (removeFiles.getChildren().size() == 0) {
            pageElement.removeContent(removeFiles);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "REMOVEFILES", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    // --------------- COOKIE
    public int addCookie(AbstractCookie cookie) {
        synchronized (cookieSync) {
            String type = "";
            if (cookie instanceof CookieRead) {
                type = "READS";
            }
            if (cookie instanceof CookieWrite) {
                type = "WRITES";
            }
            int ret = ErrorCode.NOERROR;
            if (cookie == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("COOKIES", type);
                cookies = pageElement.getChild("COOKIES");
                ret = cookie.insertInto(cookies.getChild(type));
                if (ret == ErrorCode.NOERROR) {
                    cookie.setSeq(addIndex("COOKIES"));
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
    public List getCookies(String type) {
        List ret = new ArrayList();
        cookies = pageElement.getChild("COOKIES");
        if (cookies == null) {
            return ret;
        }
        if (cookies.getChild(type) == null) {
            return ret;
        }
        List list = cookies.getChild(type).getChildren("COOKIE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                if (type.equals("READS")) {
                    ret.add(new CookieRead(ele));
                }
                if (type.equals("WRITES")) {
                    ret.add(new CookieWrite(ele));
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
    public AbstractCookie getCookie(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        cookies = pageElement.getChild("COOKIES");
        if (cookies == null) {
            return null;
        }
        String type = "READS";
        Element cookie =
            XMLFunction.getChildByAttribute(cookies.getChild(type), "COOKIE",
                "SEQ", seq);
        if (cookie == null) {
            type = "WRITES";
            cookie =
                XMLFunction.getChildByAttribute(cookies.getChild(type),
                    "COOKIE", "SEQ", seq);
            if (cookie == null) {
                return null;
            }
        }
        if (type.equals("READS")) {
            return new CookieRead(cookie);
        }
        if (type.equals("WRITES")) {
            return new CookieWrite(cookie);
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
    public int removeCookie(String seq) {
        AbstractCookie cookie = getCookie(seq);
        if (cookie == null) {
            return ErrorCode.NULL;
        }
        cookies = pageElement.getChild("COOKIES");
        if (cookies == null) {
            return ErrorCode.NOEXIST;
        }
        String type = "";
        if (cookie instanceof CookieRead) {
            type = "READS";
        }
        if (cookie instanceof CookieWrite) {
            type = "WRITES";
        }
        cookies.getChild(type).removeContent(cookie.cookie);
        if (cookies.getChild(type).getChildren().size() == 0) {
            cookies.removeContent(cookies.getChild(type));
        }
        if (cookies.getChildren().size() == 0) {
            pageElement.removeContent(cookies);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "COOKIES", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- GROOVYS
    public int addGroovy(GroovyElement groovy) {
        synchronized (groovySync) {
            int ret = ErrorCode.NOERROR;
            if (groovy == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("GROOVYS");
                groovys = pageElement.getChild("GROOVYS");
                ret = groovy.insertInto(groovys);
                if (ret == ErrorCode.NOERROR) {
                	groovy.setSeq(addIndex("GROOVYS"));
                }
            }
            return ret;
        }
    }

    //--------------------- CONNECTORS
    public int addConnector(Connector connector) {
        synchronized (connectorSync) {
            int ret = ErrorCode.NOERROR;
            if (connector == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("CONNECTORS");
                connectors = pageElement.getChild("CONNECTORS");
                ret = connector.insertInto(connectors);
                if (ret == ErrorCode.NOERROR) {
                    connector.setSeq(addIndex("CONNECTORS"));
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
    public List getGroovys() {
        List ret = new ArrayList();
        groovys = pageElement.getChild("GROOVYS");
        if (groovys == null) {
            return ret;
        }
        List list = groovys.getChildren("GROOVY");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new GroovyElement(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getConnectors() {
        List ret = new ArrayList();
        connectors = pageElement.getChild("CONNECTORS");
        if (connectors == null) {
            return ret;
        }
        List list = connectors.getChildren("CONNECTOR");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new Connector(ele));
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
    public GroovyElement getGroovy(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        groovys = pageElement.getChild("GROOVYS");
        if (groovys == null) {
            return null;
        }
        Element groovy =
            XMLFunction.getChildByAttribute(groovys, "GROOVY", "SEQ", seq);
        if (groovy == null) {
            return null;
        }
        return new GroovyElement(groovy);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Connector getConnector(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        connectors = pageElement.getChild("CONNECTORS");
        if (connectors == null) {
            return null;
        }
        Element connector =
            XMLFunction.getChildByAttribute(connectors, "CONNECTOR", "SEQ", seq);
        if (connector == null) {
            return null;
        }
        return new Connector(connector);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeGroovy(String seq) {
        GroovyElement groovy = getGroovy(seq);
        if (groovy == null) {
            return ErrorCode.NULL;
        }
        groovys = pageElement.getChild("GROOVYS");
        if (groovys == null) {
            return ErrorCode.NOEXIST;
        }
        groovys.removeContent(groovy.groovy);
        if (groovys.getChildren().size() == 0) {
            pageElement.removeContent(groovys);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "GROOVYS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeConnector(String seq) {
        Connector connector = getConnector(seq);
        if (connector == null) {
            return ErrorCode.NULL;
        }
        connectors = pageElement.getChild("CONNECTORS");
        if (connectors == null) {
            return ErrorCode.NOEXIST;
        }
        connectors.removeContent(connector.connector);
        if (connectors.getChildren().size() == 0) {
            pageElement.removeContent(connectors);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "CONNECTORS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- FILEIN
    public int addFileIn(FileIn filein) {
        synchronized (fileinSync) {
            int ret = ErrorCode.NOERROR;
            if (filein == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("FILEINS");
                fileins = pageElement.getChild("FILEINS");
                ret = filein.insertInto(fileins);
                if (ret == ErrorCode.NOERROR) {
                    filein.setSeq(addIndex("FILEINS"));
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
    public List getFileIns() {
        List ret = new ArrayList();
        fileins = pageElement.getChild("FILEINS");
        if (fileins == null) {
            return ret;
        }
        List list = fileins.getChildren("FILEIN");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new FileIn(ele));
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
    public FileIn getFileIn(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        fileins = pageElement.getChild("FILEINS");
        if (fileins == null) {
            return null;
        }
        Element filein =
            XMLFunction.getChildByAttribute(fileins, "FILEIN", "SEQ", seq);
        if (filein == null) {
            return null;
        }
        FileIn ht = new FileIn(filein);
        ht.prjID = getParent().getProject().getId();
        return ht;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeFileIn(String seq) {
        FileIn filein = getFileIn(seq);
        if (filein == null) {
            return ErrorCode.NULL;
        }
        fileins = pageElement.getChild("FILEINS");
        if (fileins == null) {
            return ErrorCode.NOEXIST;
        }
        fileins.removeContent(filein.html);
        if (fileins.getChildren().size() == 0) {
            pageElement.removeContent(fileins);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "FILEINS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- FILEOUTS
    public int addFileOut(FileOut fileout) {
        synchronized (fileoutSync) {
            int ret = ErrorCode.NOERROR;
            if (fileout == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("FILEOUTS");
                fileouts = pageElement.getChild("FILEOUTS");
                ret = fileout.insertInto(fileouts);
                if (ret == ErrorCode.NOERROR) {
                    fileout.setSeq(addIndex("FILEOUTS"));
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
    public List getFileOuts() {
        List ret = new ArrayList();
        fileouts = pageElement.getChild("FILEOUTS");
        if (fileouts == null) {
            return ret;
        }
        List list = fileouts.getChildren("FILEOUT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new FileOut(ele));
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
    public FileOut getFileOut(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        fileouts = pageElement.getChild("FILEOUTS");
        if (fileouts == null) {
            return null;
        }
        Element fileout =
            XMLFunction.getChildByAttribute(fileouts, "FILEOUT", "SEQ", seq);
        if (fileout == null) {
            return null;
        }
        return new FileOut(fileout);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeFileOut(String seq) {
        FileOut fileout = getFileOut(seq);
        if (fileout == null) {
            return ErrorCode.NULL;
        }
        fileouts = pageElement.getChild("FILEOUTS");
        if (fileouts == null) {
            return ErrorCode.NOEXIST;
        }
        fileouts.removeContent(fileout.file);
        if (fileouts.getChildren().size() == 0) {
            pageElement.removeContent(fileouts);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "FILEOUTS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- SOCKETS
    public int addSocketElement(SocketElement socket) {
        synchronized (socketSync) {
            int ret = ErrorCode.NOERROR;
            if (socket == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("SOCKETS");
                sockets = pageElement.getChild("SOCKETS");
                ret = socket.insertInto(sockets);
                if (ret == ErrorCode.NOERROR) {
                    socket.setSeq(addIndex("SOCKETS"));
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
    public List getSocketElements() {
        List ret = new ArrayList();
        sockets = pageElement.getChild("SOCKETS");
        if (sockets == null) {
            return ret;
        }
        List list = sockets.getChildren("SOCKETELEMENT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new SocketElement(ele));
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
    public SocketElement getSocketElement(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        sockets = pageElement.getChild("SOCKETS");
        if (sockets == null) {
            return null;
        }
        Element socket =
            XMLFunction.getChildByAttribute(sockets, "SOCKETELEMENT", "SEQ", seq);
        if (socket == null) {
            return null;
        }
        return new SocketElement(socket);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeSocketElement(String seq) {
        SocketElement socket = getSocketElement(seq);
        if (socket == null) {
            return ErrorCode.NULL;
        }
        sockets = pageElement.getChild("SOCKETS");
        if (sockets == null) {
            return ErrorCode.NOEXIST;
        }
        sockets.removeContent(socket.socket);
        if (sockets.getChildren().size() == 0) {
            pageElement.removeContent(sockets);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "SOCKETS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- WEBSERVICES
    public int addWebServiceClient(WebServiceClient webservice) {
        synchronized (webserviceSync) {
            int ret = ErrorCode.NOERROR;
            if (webservice == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("WEBSERVICES");
                webservices = pageElement.getChild("WEBSERVICES");
                ret = webservice.insertInto(webservices);
                if (ret == ErrorCode.NOERROR) {
                    webservice.setSeq(addIndex("WEBSERVICES"));
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
    public List getWebServiceClients() {
        List ret = new ArrayList();
        webservices = pageElement.getChild("WEBSERVICES");
        if (webservices == null) {
            return ret;
        }
        List list = webservices.getChildren("WEBSERVICE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new WebServiceClient(ele));
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
    public WebServiceClient getWebServiceClient(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        webservices = pageElement.getChild("WEBSERVICES");
        if (webservices == null) {
            return null;
        }
        Element webservice =
            XMLFunction.getChildByAttribute(webservices, "WEBSERVICE", "SEQ",
                seq);
        if (webservice == null) {
            return null;
        }
        return new WebServiceClient(webservice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeWSClient(String seq) {
        WebServiceClient webservice = getWebServiceClient(seq);
        if (webservice == null) {
            return ErrorCode.NULL;
        }
        webservices = pageElement.getChild("WEBSERVICES");
        if (webservices == null) {
            return ErrorCode.NOEXIST;
        }
        webservices.removeContent(webservice.webservice);
        if (webservices.getChildren().size() == 0) {
            pageElement.removeContent(webservices);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "WEBSERVICES", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //--------------------- UPLOADREF
    public int addUploadRef(UploadRef uploadRef) {
        synchronized (uploadRefSync) {
            int ret = ErrorCode.NOERROR;
            if (uploadRef == null) {
                ret = ErrorCode.NULL;
            } else {
                chkStructure("UPLOADREFS");
                uploadRefs = pageElement.getChild("UPLOADREFS");
                ret = uploadRef.insertInto(uploadRefs);
                if (ret == ErrorCode.NOERROR) {
                    uploadRef.setSeq(addIndex("UPLOADREFS"));
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
    public List getUploadRefs() {
        List ret = new ArrayList();
        uploadRefs = pageElement.getChild("UPLOADREFS");
        if (uploadRefs == null) {
            return ret;
        }
        List list = uploadRefs.getChildren("UPLOADREF");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new UploadRef(ele));
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
    public UploadRef getUploadRef(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        uploadRefs = pageElement.getChild("UPLOADREFS");
        if (uploadRefs == null) {
            return null;
        }
        Element uploadRef =
            XMLFunction.getChildByAttribute(uploadRefs, "UPLOADREF", "SEQ", seq);
        if (uploadRef == null) {
            return null;
        }
        return new UploadRef(uploadRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeUploadRef(String seq) {
        UploadRef uploadRef = getUploadRef(seq);
        if (uploadRef == null) {
            return ErrorCode.NULL;
        }
        uploadRefs = pageElement.getChild("UPLOADREFS");
        if (uploadRefs == null) {
            return ErrorCode.NOEXIST;
        }
        uploadRefs.removeContent(uploadRef.uploadRef);
        if (uploadRefs.getChildren().size() == 0) {
            pageElement.removeContent(uploadRefs);
        }
        index = pageElement.getChild("INDEX");
        if (index != null) {
            index.removeContent(XMLFunction.getChildByAttribute(index,
                    "UPLOADREFS", "SEQ", seq));
        }
        return ErrorCode.NOERROR;
    }

    //------------
    public int size() {
        Element ix = pageElement.getChild("INDEX");
        if (ix == null) {
            return 0;
        }
        return ix.getChildren().size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String get(String seq) {
        Element ix = pageElement.getChild("INDEX");
        if (ix == null) {
            return "";
        }
        int i = 0;
        try {
            i = Integer.parseInt(seq);
        } catch (NumberFormatException er) {
        }
        if ((i < 0) || (i > (ix.getChildren().size() - 1))) {
            return "";
        }
        Element el = (Element) ix.getChildren().get(i);
        String sq = "";
        if (el.getAttribute("SEQ") != null) {
            sq = el.getAttribute("SEQ").getValue();
        }
        String ret = el.getName() + "," + sq;
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param order DOCUMENT ME!
     */
    public void setIndex(String order) {
        Element indexElement = pageElement.getChild("INDEX");
        List ix = StringA.pieceAsList(order, ",", 0, 0, true);
        Element newIndex = new Element("INDEX");
        int lix = indexElement.getChildren().size();
        int lor = ix.size();
        int max = (lix < lor) ? lix
                              : lor;
        for (int i = 0; i < max; i++) {
            int j = 0;
            try {
                j = Integer.parseInt((String) ix.get(i));
            } catch (NumberFormatException err) {
            }
            Object obj = indexElement.getChildren().get(j);
            newIndex.getChildren().add(((Element) obj).clone());
        }
        pageElement.removeContent(indexElement);
        pageElement.addContent(newIndex);
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
                    if (desc.equals("UPLOAD")) {
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

    /**
     * DOCUMENT ME!
     *
     * @param prj DOCUMENT ME!
     * @param type DOCUMENT ME!
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected ProjectElement getProjectElement(AbstractProject prj,
        String type, String id) {
        ProjectElement ele = null;
        while ((ele == null) && (prj != null)) {
            if (type.equals("COMBO")) {
                ele = prj.getCombos().getElement(id);
            } else if (type.equals("GRID")) {
                ele = prj.getGrids().getElement(id);
            } else if (type.equals("DOWNLOAD")) {
                ele = prj.getDownloads().getElement(id);
            } else if (type.equals("UPLOAD")) {
                ele = prj.getUploads().getElement(id);
            }
            if (ele == null) {
                prj = prj.getParent();
            }
        }
        return ele;
    }

    /**
     * DOCUMENT ME!
     *
     * @param treeview DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addTreeView(TreeViewElement treeview);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getTreeViews();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract TreeViewElement getTreeView(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeTreeView(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param javagrid DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addJavaGrid(JavaGrid javagrid);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getJavaGrids();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract JavaGrid getJavaGrid(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeJavaGrid(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param wiobjgrid DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addWIObjectGrid(WIObjectGrid wiobjgrid);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract WIObjectGrid getWIObjectGrid(String seq);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getWIObjectGrids();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeWIObjectGrid(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param getMail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addGetMail(MailGet getMail);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract MailGet getGetMail(String seq);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getGetMails();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeGetMail(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param listFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addListFile(AbstractFileList listFile);

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getListFiles(String type);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract AbstractFileList getListFile(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeListFile(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param listMail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addListMail(MailList listMail);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getListMails();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract MailList getListMail(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeListMail(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param comboRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addComboRef(ComboRef comboRef);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getComboRefs();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ComboRef getComboRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeComboRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param gridRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addGridRef(GridRef gridRef);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getGridRefs();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract GridRef getGridRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeGridRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param downloadRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addDownloadRef(DownloadRef downloadRef);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getDownloadRefs();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract DownloadRef getDownloadRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeDownloadRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param reportRef DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int addReportRef(ReportRef reportRef);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract List getReportRefs();

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ReportRef getReportRef(String seq);

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int removeReportRef(String seq);
}
