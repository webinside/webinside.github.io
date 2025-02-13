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

package br.com.webinside.runtime.util;

/**
 * Classe que identifica a versão do WI e de seus componentes.
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
    /** Indica a versão do WI */ 
    public final static String VERSION = getVersion();
    public final static String VERSION_FILE = getFileVersion();
    public final static String VERSION_WIPLUS = getVersionWIPlus();
    /** Indica a versão do Mjava */
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
