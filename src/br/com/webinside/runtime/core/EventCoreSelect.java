/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
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
 * @version $Revision: 1.2 $
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
        db.setCharFilter(RtmFunction.cleanSpace(select.getSqlFilter()), "");
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
