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

package br.com.webinside.runtime.function;

import java.io.*;

import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class HtmlFilter extends AbstractFunction {
    // arg[0] = texto a ser filtrado
    // retorno = texto sem as tags html
    public String execute(String[] args) {
        if ((args == null) || (args.length < 1)) {
            return "";
        }
        try {
            boolean tag = false;
            BufferedReader in = new BufferedReader(new StringReader(args[0]));
            StringBuffer text = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                boolean empty = true;
                int pos = 0;
                while (pos < line.length()) {
                    int p1 = line.indexOf("<", pos);
                    if (p1 == -1) {
                        p1 = line.length();
                    }
                    int p2 = line.indexOf(">", pos);
                    if (p2 == -1) {
                        p2 = line.length();
                    }
                    if (p2 < p1) {
                        p1 = p2;
                    }
                    if (p1 == -1) {
                        p1 = line.length();
                    }
                    String seg = StringA.mid(line, pos, p1 - 1);
                    if (!tag) {
                        text.append(seg);
                        if (seg.trim().length() > 0) {
                            empty = false;
                        }
                    }
                    pos = p1 + 1;
                    if (p1 != line.length()) {
                        if (line.charAt(p1) == '<') {
                            tag = true;
                        } else {
                            tag = false;
                        }
                    }
                }
                if (!empty) {
                    text.append("\r\n");
                }
            }
            in.close();
            return text.toString();
        } catch (IOException err) {
        }
        return "";
    }
}
