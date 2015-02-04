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

import java.io.IOException;
import java.io.InputStream;
import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.xml.Inputter;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class DriversDef {
    private static Element drivers;

    private synchronized static void read() throws IOException {
        if (drivers != null) {
            return;
        }
        String resource = "br/com/webinside/runtime/JDBC_Drivers.xml";
        InputStream in =
            DriversDef.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IOException(
                "Resource " + resource + " cannot be found");
        }
        Document doc = new Inputter().input(in);
        if (doc == null) {
            return;
        }
        drivers = doc.getRootElement();
        if (!drivers.getName().equals("DRIVERS")) {
            drivers = new Element("DRIVERS");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean getJDBCShowOnlyFounds() {
        if (drivers == null) {
            try {
                read();
            } catch (IOException e) {
                return false;
            }
        }
        return XMLFunction.getElemValue(drivers, "JDBCS", "SHOWONLYFOUNDS")
                .equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static JdbcAliases getJDBCAliases() throws IOException {
        if (drivers == null) {
            read();
        }
        if (drivers != null) {
            Element jdbcs = drivers.getChild("JDBCS");
            if (jdbcs != null) {
                return new JdbcAliases(jdbcs.getChild("ALIASES"));
            }
        }
        return new JdbcAliases();
    }
}
