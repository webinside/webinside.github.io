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

import br.com.webinside.runtime.component.FileIn;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreFileIn extends CoreCommon {
    private FileIn fileIn;

    /**
     * Creates a new CoreFileIn object.
     *
     * @param wiParams DOCUMENT ME!
     * @param fileIn DOCUMENT ME!
     */
    public CoreFileIn(ExecuteParams wiParams, FileIn fileIn) {
        this.wiParams = wiParams;
        this.fileIn = fileIn;
        element = fileIn;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (fileIn == null || isDisabledCondition()) {
            return;
        }
        String file = "";
        if (isValidCondition()) {
            file = fileIn.getSourceIfTrue().trim();
        } else {
            file = fileIn.getSourceIfFalse().trim();
        }
        if (file.equals("")) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(file);
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        file = prod.getOutput();
        file = StringA.change(file, "../", "/");
        FileIO fl = new FileIO(file, 'r');
        String content = fl.readText();
        fl.close();
        String wiobj = fileIn.getWIObj().trim();
        if (fileIn.getActivePage().equals("ON")) {
            prod.setInput(content);
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            content = prod.getOutput();
        }
        if (fileIn.getDecodeXML().equals("ON")) {
            new CoreXmlImport(wiMap, wiobj).execute(content);
        } else {
            wiMap.put(wiobj, content);
        }
        writeLog();
    }
}
