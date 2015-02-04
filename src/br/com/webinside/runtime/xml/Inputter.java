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

package br.com.webinside.runtime.xml;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;

/**
 * Classe gera um Document Jdom a partir de um arquivo ou texto Xml.
 *
 * @author Geraldo Moraes
 * @version 1.0
 */
public class Inputter {
    private String errorMsg;

    /**
     * Cria um novo Inputter.
     */
    public Inputter() {
    }

    /**
     * Retorna a mensagem caso tenha havido erro na geração do Document Jdom.
     * Só passa a existir após a execução de algum método input.
     *
     * @return a mensagem de erro.
     */
    public String getErrorMsg() {
        if (errorMsg == null) {
            return "";
        }
        return errorMsg;
    }

    /**
     * Gera um Document Jdom a partir de um arquivo Xml.
     *
     * @param file o arquivo a ser processado. Seu conteúdo  deve iniciar com
     *        &lt;?xml ... ?&gt;.
     *
     * @return o Document Jdom do arquivo.
     */
    public Document input(File file) {
        return input(file, null, null);
    }

    /**
     * Gera um Document Jdom a partir de um texto Xml.
     *
     * @param text o texto Xml. Deve iniciar com &lt;?xml ... ?&gt;.
     *
     * @return o Document Jdom do texto.
     */
    public Document input(String text) {
        return input(null, text, null);
    }

    /**
     * Gera um Document Jdom a partir de um InputStream.
     *
     * @param in o InputStream a ser utilizado. Deve iniciar com &lt;?xml ...
     *        ?&gt;.
     *
     * @return o Document Jdom do InputStream.
     */
    public Document input(InputStream in) {
        return input(null, null, in);
    }

    private Document input(File file, String text, InputStream in) {
        errorMsg = "";
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            String url = 
            	"http://apache.org/xml/features/nonvalidating/load-external-dtd";
            builder.setFeature(url, false);
            if (text != null) {
                StringReader reader = new StringReader(text);
                doc = builder.build(reader);
                reader.close();
            } else if (file != null) {
                doc = builder.build(file);
            } else {
                doc = builder.build(in);
            }
        } catch (JDOMException e) {
            errorMsg = e.getMessage();
        } catch (IOException e) {
            errorMsg = e.getMessage();
        }
        if (doc != null) {
            doc.getRootElement().removeAttribute("ID");
        }
        return doc;
    }
}
