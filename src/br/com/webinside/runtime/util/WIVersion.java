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

package br.com.webinside.runtime.util;

/**
 * Classe que identifica a vers�o do WI e de seus componentes.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.53 $
 *
 * @since 3.0
 */
public abstract class WIVersion {
    private final static int VERSION_MAJOR = 5; 
    private final static int VERSION_MINOR = 1;
    private final static int VERSION_RELEASE = 5;
    private final static int SERVICE_PACK = 0;
    /** Indica a vers�o do WI */ 
    public final static String VERSION = getVersion();
    public final static String VERSION_FILE = getFileVersion();
    public final static String VERSION_WIPLUS = getVersionWIPlus();
    /** Indica a vers�o do Mjava */
    public final static String MJAVAVERSION = "3.1";

    private static String getVersion() {
        StringBuffer ret = new StringBuffer();
        ret.append(VERSION_MAJOR).append(".").append(VERSION_MINOR);
        ret.append(".").append(VERSION_RELEASE);
        if (SERVICE_PACK > 0) ret.append(".sp").append(SERVICE_PACK);
        return ret.toString();
    }
    
    private static String getFileVersion() {
    	return StringA.change(VERSION, ".", "_");
    }

    private static String getVersionWIPlus() {
    	String vmi = VERSION_MINOR + "";
    	if (vmi.length() < 2) vmi = "0" + vmi;
    	String vre = VERSION_RELEASE + "";
    	if (vre.length() < 2) vre = "0" + vre;
        return VERSION_MAJOR + vmi + vre;
    }
        
    // Usado no ANT para gerar os JARS e WAR
    public static void main(String[] args) {
		System.out.println(VERSION_FILE);
	}
}
