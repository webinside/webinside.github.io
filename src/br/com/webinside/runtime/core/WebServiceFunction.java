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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import br.com.webinside.runtime.util.StringA;

public class WebServiceFunction {

    public static MimeHeaders getHeaders(HttpServletRequest request) {
        MimeHeaders headers = new MimeHeaders();
        Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = (String) e.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.addHeader(headerName, headerValue);
        }
        return headers;
    }

    public static void putHeaders(MimeHeaders headers,
        HttpServletResponse response) {
        Iterator it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader) it.next();
            String value = header.getValue();
            response.setHeader(header.getName(), value);            
        }
    }

    public static String toString(SOAPMessage message) {
        String aux = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            baos.close();
            String text = baos.toString("ISO-8859-1");
            String search = "SOAP-ENV:Body>";
            if (text.toLowerCase().indexOf(search.toLowerCase())==-1) {
            	search="soap:Body>";
            }
            aux = StringA.piece(text, "<" + search, 2, 2, false);
            aux = StringA.piece(aux, "</" + search, 1, 1, false);
            aux = StringA.change(aux, "><", ">\r\n<");
        } catch (UnsupportedEncodingException err) {
        } catch (IOException err) {
        } catch (SOAPException err) {
        }
        return aux;
    }
}
