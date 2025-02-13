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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.webinside.runtime.component.TreeViewElement;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.integration.TreeView;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreTreeViewElement extends CoreCommon {
    private TreeViewElement tree;

    /**
     * Creates a new CoreTreeViewElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param tree DOCUMENT ME!
     */
    public CoreTreeViewElement(ExecuteParams wiParams, TreeViewElement tree) {
        this.wiParams = wiParams;
        this.tree = tree;
        element = tree;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String wiobj = tree.getWIObj().trim();
        if (wiobj.toLowerCase().startsWith("wi.")) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setRecursive(true);
        prod.addProtectedPipe("-");
        prod.setInput(tree.getTitle());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String title = prod.getOutput().trim();
        wiMap.put(tree.getWIObj() + ".style", tree.getStyle());
        if (StringA.changeChars(tree.getSql(), "\r\n ", "").equals("")) {
            TreeViewElement temp = (TreeViewElement) tree.cloneMe();
            WIMap nodes = temp.getContents();
            List keys = temp.getKeys();
            temp.removeAllContents();
            for (int i = 0; i < keys.size(); i++) {
                String key = (String) keys.get(i);
                String link = nodes.get(key);
                prod.setInput(key);
                prod.addProtectedPipe("nodeID");
                wiParams.getProducer().setParam(prod);
                wiParams.getProducer().execute();
                key = prod.getOutput();
                prod.setInput(link);
                prod.addProtectedPipe("nodeID");
                wiParams.getProducer().setParam(prod);
                wiParams.getProducer().execute();
                link = prod.getOutput().trim();
                temp.addContent(key, link);
            }
            nodes = temp.getContents();
            keys = temp.getKeys();
            if (nodes != null) {
                wiMap.put(wiobj, TreeView.get(nodes, title, keys));
            }
        } else {
            String dbalias = tree.getDatabase();
            DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
            if ((db == null) || (!db.isConnected())) {
                RtmFunction.databaseError(wiParams, dbalias);
                return;
            }
            long ini = new Date().getTime();
            Exception exrs = null;
            ResultSet rs = null;
            try {
                rs = db.execute(tree.getSql(), wiMap);
            } catch (Exception err) {
                exrs = err;
            }
            long fim = new Date().getTime();
            dbTime(db, wiMap, ini, fim, "treeview " + tree.getDescription());
            if (rs != null) {
                List keys = new ArrayList();
                WIMap tv = new WIMap('~', true);
                while (rs.next() > 0) {
                    WIMap newcontext = wiMap.cloneMe();
                    newcontext.putAll(rs.columns(""));
                    String[] names = rs.columnNames();
                    String link = tree.getLink();

                    // para o caso de se usar funcao no link
                    newcontext.put("nodeID", "|nodeID|");
                    prod.setWIMap(newcontext);
                    prod.setInput(link);
                    prod.addProtectedPipe("nodeID");
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    link = prod.getOutput().trim();
                    prod.setInput(tree.getLabel());
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    link += ("~" + prod.getOutput().trim());
                    int columns = names.length;
                    prod.setInput(tree.getColumns());
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    try {
                        columns = Integer.parseInt(prod.getOutput());
                    } catch (NumberFormatException er) {
                    }
                    if (columns > names.length) {
                        columns = names.length;
                    }
                    String key = "";
                    for (int i = 0; i < columns; i++) {
                        String str = rs.column(i + 1);
                        if (str.trim().equals("")) {
                            break;
                        }
                        if (i == 0) {
                            key += str;
                        } else {
                            if (!keys.contains(key)) {
                                keys.add(key);
                            }
                            key += ("~" + str);
                        }
                    }
                    keys.add(key);
                    tv.put(key, link);
                }
                wiMap.put(wiobj, TreeView.get(tv, title, keys));
            } else {
            	queryException(exrs, db, tree.getDescription());
            }
        }
        writeLog();
    }
}
