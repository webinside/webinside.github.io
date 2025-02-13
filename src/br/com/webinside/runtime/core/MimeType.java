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

import java.io.*;
import java.util.*;

import br.com.webinside.runtime.util.*;
import br.com.webinside.runtime.xml.*;

import org.jdom.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class MimeType {
	
    private static Map mimetypes;

    /**
     * DOCUMENT ME!
     *
     * @param extension DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String get(String extension) {
        if (extension == null) {
            return "";
        }
        if (extension.indexOf("/") != -1) {
            return extension.trim();
        }
        if (mimetypes == null) {
            return "";
        }
        String resp = (String) mimetypes.get(extension.trim().toLowerCase());
        if (resp == null) {
            resp = "";
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param extension DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getType(String extension) {
        String resp = get(extension);
        return StringA.piece(resp, "/", 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param extension DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getSubType(String extension) {
        String resp = get(extension);
        return StringA.piece(resp, "/", 2);
    }

    /**
     * DOCUMENT ME!
     *
     * @param cLoader DOCUMENT ME!
     */
    public synchronized static void readFile(ClassLoader cLoader) {
        if (mimetypes != null) return;
        Document doc = null;
        InputStream in = cLoader.getResourceAsStream("br/com/webinside/runtime/mimes.xml");
        doc = (new Inputter()).input(in);
        Function.closeStream(in);
        if (doc == null) {
            return;
        }
        mimetypes = Collections.synchronizedMap(new HashMap());
        List list = doc.getRootElement().getChildren("mime-mapping");
        for (int i = 0; i < list.size(); i++) {
            Element map = (Element) list.get(i);
            String ext = map.getChildText("extension");
            String type = map.getChildText("mime-type");
            mimetypes.put(ext, type);
        }
        if (mimetypes.isEmpty()) {
            mimetypes = null;
        }
    }
}
