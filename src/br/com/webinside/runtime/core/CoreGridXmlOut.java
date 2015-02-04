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

package br.com.webinside.runtime.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreGridXmlOut {
    /** DOCUMENT ME! */
    private ExecuteParams wiParams;
    private GridXmlOut xml;
    private WIMap wiMap;
    private int xmlident = 0;
    private int xmlref = 0;
    private boolean directout = false;

    /**
     * Creates a new CoreGridXmlOut object.
     *
     * @param wiParams DOCUMENT ME!
     * @param xml DOCUMENT ME!
     */
    public CoreGridXmlOut(ExecuteParams wiParams, GridXmlOut xml) {
        this.wiParams = wiParams;
        this.xml = xml;
    }

    /**
     * DOCUMENT ME!
     *
     * @param directout DOCUMENT ME!
     */
    public void execute(boolean directOut) {
        if (xml == null) {
            return;
        }
        wiMap = wiParams.getWIMap();
        this.directout = directOut;
        String wiobj = "grid." + xml.getId().trim();
        if (wiMap.get(wiobj + ".recursivecontrol").equals("true")) {
            return;
        }
        xmlident = Function.parseInt(xml.getIdentation().trim());
        if (xmlident < 0) xmlident = 0;
        xmlref = Function.parseInt(wiMap.get(wiobj + ".reference"));
        if (xmlref < 0) xmlref = 0;
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setXml(true);
        prod.setInput(xml.getRoot().trim());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String root = prod.getOutput().trim();
        wiMap.put("wi.xml.root", root);
        prod.setInput(xml.getChild().trim());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String child = prod.getOutput().trim();
        wiMap.put("wi.xml.child", child);
        StringBuffer resp = new StringBuffer();
        if (!xml.getHeader().trim().equals("")) {
            prod.setInput(xml.getHeader());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            if (directOut) {
                wiParams.getWriter().print(prod.getOutput());
            } else {
                resp.append(prod.getOutput());
            }
        }
        if ((!resp.toString().endsWith("\n")) && (resp.length() > 0)) {
            resp.append("\r\n");
        }
        if (!root.equals("")) {
            if (directOut) {
                wiParams.getWriter().print(space(xmlref) + "<" + root + ">");
            } else {
                resp.append(space(xmlref) + "<" + root + ">\r\n");
            }
            xmlref = xmlref + xmlident;
        }
        if (StringA.changeChars(xml.getSql(), "\r\n ", "").equals("")) {
            String template = xml.getXmlTemplate();
            if (directOut) {
                wiParams.getWriter().print(coreChild(template, wiMap.cloneMe()));
            } else {
                resp.append(coreChild(template, wiMap.cloneMe()));
            }
        } else {
            String dbalias = xml.getDatabase().trim();
            DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
            if ((db == null) || (!db.isConnected())) {
                EngFunction.databaseError(wiParams, dbalias);
                return;
            }
            db.setCharFilter(EngFunction.cleanSpace(xml.getSqlFilter()), "");
            Exception exrs = null;
            ResultSet rs = null;
            try {
                rs = db.execute(xml.getSql(), wiMap, 1, 0);
            } catch (Exception err) {
                exrs = err;
            }
            if (rs != null) {
                List rset = new ArrayList();
                int next = 0;
                while ((next = rs.next()) > 0) {
                    WIMap newcontext = wiMap.cloneMe();
                    newcontext.putAll(rs.columns(""));
                    newcontext.put("rowid", next);
                    newcontext.put("rowid0", next - 1);
                    rset.add(newcontext);
                }
                for (int i = 0; i < rset.size(); i++) {
                    if ((!resp.toString().endsWith("\n"))
                                && (resp.length() > 0)) {
                        resp.append("\r\n");
                    }
                    String template = xml.getXmlTemplate();
                    if (directOut) {
                        wiParams.getWriter().print(coreChild(template,
                                (WIMap) rset.get(i)));
                    } else {
                        resp.append(coreChild(template, (WIMap) rset.get(i)));
                    }
                }
            } else {
                wiMap.put(wiobj, "");
                wiMap.put("wi.sql.query", db.getExecutedSQL());
                wiMap.put("wi.sql.error", db.getErrorMessage());
                String jspFile = wiMap.get("wi.jsp.filename");
                String msgDetail = db.getErrorMessage() + 
                	"\r\n--- SQL ---\r\n" + db.getExecutedSQL();
                WIMap psMap = db.getExecutedSQLParams(wiMap); 
                if (psMap.keySet().size() > 0) {
                	msgDetail += "\r\n--- PARAMS ---\r\n";
                	msgDetail += psMap.toString();
                }	
                wiParams.getErrorLog().write("Page: " + jspFile,
                    xml.getDescription(), msgDetail);
                if (!wiParams.getPage().getErrorPageName().equals("")) {
                    wiParams.setRequestAttribute("wiException", exrs);
                }
            }
        }
        if ((!resp.toString().endsWith("\n")) && (resp.length() > 0)) {
            resp.append("\r\n");
        }
        if (directOut) {
            wiParams.getWriter().println();
        }
        if (!root.equals("")) {
            xmlref = xmlref - xmlident;
            String croot = StringA.piece(root, " ", 1);
            if (directOut) {
                wiParams.getWriter().print(space(xmlref) + "</" + croot + ">");
            } else {
                resp.append(space(xmlref) + "</" + croot + ">");
            }
        }
        if (!directOut) {
            wiMap.put(wiobj, resp.toString());
        }
    }

    private String coreChild(String template, WIMap values) {
        StringBuffer resp = new StringBuffer();
        template = recursive(template, values);
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(values);
        prod.addProtectedPipe("grid.");
        prod.setXml(true);
        prod.setInput(xml.getChild().trim());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String child = prod.getOutput();
        if (!child.trim().equals("")) {
            resp.append(space(xmlref) + "<" + child + ">");
            xmlref = xmlref + xmlident;
        }
        prod.setInput(template);
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        prod.resetProtectedPipe();
        prod.setXml(false);
        prod.setInput(prod.getOutput());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        try {
            BufferedReader in =
                new BufferedReader(new StringReader(prod.getOutput()));
            String line = null;
            while ((line = in.readLine()) != null) {
                if ((!resp.toString().endsWith("\n")) && (resp.length() > 0)) {
                    resp.append("\r\n");
                }
                resp.append(space(xmlref) + line);
            }
            in.close();
        } catch (IOException err) {
        }
        if ((!resp.toString().endsWith("\n")) && (resp.length() > 0)) {
            resp.append("\r\n");
        }
        if (!child.trim().equals("")) {
            if (directout) {
                wiParams.getWriter().println();
            }
            xmlref = xmlref - xmlident;
            resp.append(space(xmlref) + "</" + StringA.piece(child, " ", 1)
                + ">");
        }
        return resp.toString();
    }

    private String space(int identation) {
        StringBuffer resp = new StringBuffer();
        for (int i = 1; i <= identation; i++) {
            resp.append(" ");
        }
        return resp.toString();
    }

    private String recursive(String template, WIMap values) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(values);
        int from = 0;
        int pos = -1;
        String lowtemp = template.toLowerCase();
        while ((pos = lowtemp.indexOf("|grid.", from)) > -1) {
            int last = lowtemp.indexOf("|", pos + 1);
            if (last == -1) {
                last = lowtemp.length();
            }
            String id = StringA.mid(template, pos + 6, last - 1);
            if (!wiParams.getProject().getGrids().containsKey(id)) {
                wiParams.includeCode("/grids/" + id + "/grid.jsp");
            }
            AbstractGrid newgrid =
                (AbstractGrid) wiParams.getProject().getGrids().getElement(id);
            if ((newgrid != null) && (newgrid instanceof GridXmlOut)) {
                values.put("grid." + xml.getId().trim() + ".recursivecontrol",
                    "true");
                WIMap origMap = wiParams.getWIMap();
                wiParams.setParameter(ExecuteParams.WI_MAP, values);
                new CoreGridXmlOut(wiParams, (GridXmlOut) newgrid).execute(false);
                wiParams.setParameter(ExecuteParams.WI_MAP, origMap);
                values.remove("grid." + xml.getId().trim() + ".recursivecontrol");
            }
            from = last + 1;
        }
        return template;
    }
}
