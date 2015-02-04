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

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Piece extends AbstractFunction {
    // uso: |$piece(texto,delim,inicio,fim)$|
    // Parametros:
    // texto = texto a ser particionado
    // delim = delimitador, se for vírgula, usar \,
    // inicio = posiçao inicial. Se delim é vazio, é a posiçao na string
    // fim = posiçao final. Se delim é vazio, é a posiçao na string
    public String execute(String[] args) {
        if (args.length < 3) {
            return "";
        }

        StringA text = new StringA(args[0]);
        String delim = args[1];
        String strIni = args[2];
        String strFim = "";
        if (args.length > 3) {
            strFim = args[3];
        }
        String result = "";

        if (args.length == 3) {
            if (strIni.equals("?")) {
                result = text.pieceAsList(delim, 0, 0, false).size() + "";
            } else {
                int ini = parseInt(strIni);
                if (ini != 0) {
                    if (delim.equals("")) {
                        result = text.mid(ini - 1, ini - 1);
                    } else {
                        result = text.piece(delim, ini);
                    }
                }
            }
        } else if (args.length == 4) {
            int ini = parseInt(strIni);
            int fim = parseInt(strFim);
            if ((ini != 0) && (fim != 0)) {
                if (delim.equals("")) {
                    result = text.mid(ini - 1, fim - 1);
                } else {
                    result = text.piece(delim, ini, fim);
                }
            }
        }

        return result;
    }

    private int parseInt(String number) {
        int ret = 0;
        try {
            ret = Integer.parseInt(number);
        } catch (Exception ex) {
        }
        return ret;
    }
}
