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

package br.com.webinside.modules;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ricardo To change the template for this generated type comment
 *         go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Util {
    /**
     * DOCUMENT ME!
     *
     * @param str DOCUMENT ME!
     * @param delim DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String[] split(String str, String delim) {
        StringTokenizer st = new StringTokenizer(str, delim);
        String[] tokens = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i++] = st.nextToken();
        }
        return tokens;
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getAbsolutePath(File f) {
        return (f.isFile()) ? f.getParentFile().getAbsolutePath()
                            : f.getAbsolutePath();
    }

    /**
     * Retorna o caminho do diretório do arquivo <code>file</code> relativo ao
     * diretório <code>dir</code>.
     *
     * @param file o arquivo.
     * @param dir o diretório.
     *
     * @return o caminho do diretório do arquivo <code>file</code> relativo ao
     *         diretório <code>dir</code>.
     */
    public static String getRelativePath(File file, File dir) {
        String path = getAbsolutePath(file);
        int i = path.compareTo(dir.getAbsolutePath());
        return (i > 0) ? path.substring(i + 1)
                       : "";
    }
}
