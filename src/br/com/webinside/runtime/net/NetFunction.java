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

package br.com.webinside.runtime.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import br.com.webinside.runtime.net.ssl.HostnameVerifier;
import br.com.webinside.runtime.net.ssl.SSLSocketFactory;

/**
 * Classe de funções para uso relacionado com comunicação.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class NetFunction {

	private NetFunction() { }
	
    /**
     * Retorna uma conexão http ou https sem validar o certificado e o hostname.
     *
     * @param url a Url a conextar.
     *
     * @return a conexão.
     *
     * @throws IOException DOCUMENT ME!
     * @throws MalformedURLException DOCUMENT ME!
     */
    public static HttpURLConnection openConnection(String url)
        throws IOException, MalformedURLException {
        if (url.startsWith("https://")) {
        	SSLSocketFactory socketFactory = new SSLSocketFactory();
        	HostnameVerifier hostnameVerifier = new HostnameVerifier();        	
            URLConnection con = new URL(url).openConnection();
            ((HttpsURLConnection) con).setSSLSocketFactory(socketFactory);
            ((HttpsURLConnection) con).setHostnameVerifier(hostnameVerifier);
            return (HttpsURLConnection) con;
        } else {
            return (HttpURLConnection) new URL(url).openConnection();
        }
    }
    
}
