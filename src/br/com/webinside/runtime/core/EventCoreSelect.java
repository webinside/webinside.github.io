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

import java.io.IOException;
import java.io.PrintWriter;

import br.com.webinside.runtime.component.EventSelect;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class EventCoreSelect {
    private ExecuteParams wiParams;
    private EventSelect select;

    /**
     * Creates a new EventCoreSelect object.
     *
     * @param evtParams DOCUMENT ME!
     * @param select DOCUMENT ME!
     */
    public EventCoreSelect(ExecuteParams wiParams, EventSelect select) {
        this.wiParams = wiParams;
        this.select = select;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        String cond = select.getCondition();
        if (!new Condition(wiParams.getWIMap(), cond).execute()) {
            try {
                wiParams.getHttpResponse().sendError(601);
            } catch (IOException e) {
            	// ignorado.
            }
            return;
        }
        String dbalias = select.getDatabase();
        DatabaseHandler db =
            wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            try {
                wiParams.getHttpResponse().sendError(602);
            } catch (IOException e) {
            }
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        db.setCharFilter(EngFunction.cleanSpace(select.getSqlFilter()), "");
        ResultSet rs = null;
        try {
            rs = db.execute(select.getSql(), wiParams.getWIMap());
        } catch (Exception err) {
            try {
                wiParams.getHttpResponse().sendError(603);
            } catch (IOException e) { 
            	// ignorado
            }
            return;
        }
        PrintWriter out = wiParams.getWriter();
        out.println("<selectdb id=\"" + select.getId() + "\">");
        if (rs != null) {
            int pos = 0;
            while ((pos = rs.next()) > 0) {
                out.println("<div seq=\"" + pos + "\">");
                String[] cols = rs.columnNames();
                for (int i = 0; i < cols.length; i++) {
                    String cname = StringA.getXml(cols[i]).toLowerCase();
                    String value = rs.column(i + 1);
                    String cvalue = "<![CDATA[" + value + "]]>";
                    out.println("<span seq=\"" + (i + 1) + "\" id=\"" + cname
                        + "\">" + cvalue + "</span>");
                }
                out.println("</div>");
            }
        }
        out.println("</selectdb>");
    }
}
