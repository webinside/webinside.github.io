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
public class MailSend extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element sendMail;
    /** DOCUMENT ME! */
    protected String prjID = "";

    /**
     * Creates a new Sendmail object.
     */
    public MailSend() {
        sendMail = new Element("SENDMAIL");
        sendMail.setAttribute("SEQ", "");
    }

    /**
     * Creates a new Sendmail object.
     *
     * @param element DOCUMENT ME!
     */
    public MailSend(Element element) {
        if ((element == null) || (!element.getName().equals("SENDMAIL"))) {
            element = new Element("SENDMAIL");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.sendMail = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int insertInto(Element element) {
        if (element == null) {
            return ErrorCode.NULL;
        }
        element.addContent(sendMail);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrjID() {
        return prjID;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        sendMail.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return sendMail.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(sendMail, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(sendMail, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(sendMail, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(sendMail, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setExecute(String value) {
        XMLFunction.setElemValue(sendMail, "EXECUTE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExecute() {
        return XMLFunction.getElemValue(sendMail, "EXECUTE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTo(String value) {
        XMLFunction.setElemValue(sendMail, "TO", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTo() {
        return XMLFunction.getElemValue(sendMail, "TO");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCc(String value) {
        XMLFunction.setElemValue(sendMail, "CC", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCc() {
        return XMLFunction.getElemValue(sendMail, "CC");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setBcc(String value) {
        XMLFunction.setElemValue(sendMail, "BCC", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBcc() {
        return XMLFunction.getElemValue(sendMail, "BCC");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setFrom(String value) {
        XMLFunction.setElemValue(sendMail, "FROM", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFrom() {
        return XMLFunction.getElemValue(sendMail, "FROM");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSender(String value) {
        XMLFunction.setElemValue(sendMail, "SENDER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSender() {
        return XMLFunction.getElemValue(sendMail, "SENDER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSubject(String value) {
        XMLFunction.setElemValue(sendMail, "SUBJECT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSubject() {
        return XMLFunction.getElemValue(sendMail, "SUBJECT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContent(String value) {
        XMLFunction.setCDATAValue(sendMail, "HTMLCONTENT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContent() {
        return XMLFunction.getCDATAValue(sendMail, "HTMLCONTENT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setEachOne(String value) {
        XMLFunction.setElemValue(sendMail, "EACHONE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getEachOne() {
        return XMLFunction.getElemValue(sendMail, "EACHONE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNotification(String value) {
        XMLFunction.setElemValue(sendMail, "NOTIFICATION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNotification() {
        return XMLFunction.getElemValue(sendMail, "NOTIFICATION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMime(String value) {
        XMLFunction.setElemValue(sendMail, "MIME", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMime() {
        return XMLFunction.getElemValue(sendMail, "MIME");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setHostId(String value) {
        XMLFunction.setElemValue(sendMail, "HOSTID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHostId() {
        return XMLFunction.getElemValue(sendMail, "HOSTID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAttachDir(String value) {
        XMLFunction.setElemValue(sendMail, "ATTACHDIR", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAttachDir() {
        return XMLFunction.getElemValue(sendMail, "ATTACHDIR");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAttachRemove(String value) {
        XMLFunction.setElemValue(sendMail, "ATTACHREMOVE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAttachRemove() {
        return XMLFunction.getElemValue(sendMail, "ATTACHREMOVE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        MailSend obj = new MailSend((Element) sendMail.clone());
        return obj;
    }
}
