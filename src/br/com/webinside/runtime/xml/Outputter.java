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

package br.com.webinside.runtime.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import br.com.webinside.runtime.exception.OutputterException;
import br.com.webinside.runtime.util.WIVersion;

public class Outputter {
    private static String encoding = "ISO-8859-1";
    private XMLOutputter jdom;
    private Document templateXml;
    private boolean putFileTag = false;
    private boolean useDocType = false;

    /**
     * Creates a new Outputter object.
     */
    public Outputter() {
        jdom = new XMLOutputter();
        features(jdom);
    }

    public Outputter(boolean useDocType) {
    	this();
    	this.useDocType = useDocType;
    }

    /**
     * Creates a new Outputter object.
     *
     * @param templateXml DOCUMENT ME!
     */
    public Outputter(Document templateXml) {
        this();
        setTemplateXML(templateXml);
    }

    /**
     * DOCUMENT ME!
     *
     * @param templateXml DOCUMENT ME!
     */
    public void setTemplateXML(Document templateXml) {
        if (templateXml == null) {
            return;
        }
        this.templateXml = templateXml;
    }

    /**
     * DOCUMENT ME!
     *
     * @param put DOCUMENT ME!
     */
    public void putFileTag(boolean put) {
        putFileTag = put;
    }

    /**
     * DOCUMENT ME!
     *
     * @param indentLevel DOCUMENT ME!
     */
    public void setIndentLevel(int indentLevel) {
        if (indentLevel < 0) {
            indentLevel = 0;
        }
        String empty = "";
        for (int i = 0; i <= indentLevel; i++) {
            empty = empty + " ";
        }
        jdom.getFormat().setIndent(empty);
    }

    /**
     * DOCUMENT ME!
     *
     * @param doc DOCUMENT ME!
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean output(Document doc, File file) {
        if ((doc == null) || (file == null)) {
            return false;
        }
        boolean resp = false;
        try {
            file.getParentFile().mkdirs();
            FileOutputStream fout = new FileOutputStream(file);
            resp = output(doc, makeWriter(fout));
            fout.close();
        } catch (IOException err) {
        	throw new OutputterException("Erro ao gravar definições: \n" +
        	        err.getMessage());
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param doc DOCUMENT ME!
     * @param writer DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean output(Document doc, Writer writer) {
        if ((doc == null) || (writer == null)) {
            return false;
        }
        boolean resp = false;
        try {
        	if (!useDocType) {
        		doc.setDocType(null);
        	}	
            if (putFileTag) {
                putVersionTag(doc.getRootElement());
            }
            Document auxDoc = 
                new Document((Element) doc.getRootElement().clone());
            auxDoc.getRootElement().removeAttribute("ID");
            if (doc.getDocType() != null) {
            	auxDoc.setDocType((DocType)doc.getDocType().clone());
            }
            if (templateXml != null) {
                Element root = auxDoc.getRootElement();
                Element temp = templateXml.getRootElement();
                auxDoc = new Document(Sorter.sort(root, temp));
            }
            jdom.output(auxDoc, writer);
            resp = true;
        } catch (IOException err) {
        	throw new OutputterException("Erro ao gravar definições: \n" +
        	        err.getMessage());
        }
        return resp;
    }

    private Writer makeWriter(OutputStream out) {
        if (out == null) {
            return null;
        }
        Writer writer = null;
        while (writer == null) {
            try {
                writer =
                    new OutputStreamWriter(new BufferedOutputStream(out),
                        encoding);
            } catch (UnsupportedEncodingException err) {
            }
        }
        return writer;
    }

    private void putVersionTag(Element parent) {
        XMLFunction.setElemValue(parent, "WIVERSION", WIVersion.VERSION);
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param writer DOCUMENT ME!
     */
    public static void output(Element element, Writer writer) {
        XMLOutputter jdom = new XMLOutputter();
        features(jdom);
        try {
            jdom.output(element, writer);
        } catch (IOException err) {
        }
    }

    public static void outputContent(Element element, Writer writer) {
        XMLOutputter jdom = new XMLOutputter();
        features(jdom);
        try {
            jdom.output(element.getContent(), writer);
        } catch (IOException err) {
        }
    }

    private static void features(XMLOutputter jdom) {
    	Format format = jdom.getFormat();
        format.setEncoding(encoding);
        format.setIndent("  ");
        format.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        jdom.setFormat(format);
    }
}
