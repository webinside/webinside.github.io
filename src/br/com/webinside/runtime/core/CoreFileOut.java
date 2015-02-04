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

import java.io.File;
import br.com.webinside.runtime.component.FileOut;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreFileOut extends CoreCommon {
    private FileOut fileout;

    /**
     * Creates a new CoreFileOut object.
     *
     * @param wiParams DOCUMENT ME!
     * @param fileout DOCUMENT ME!
     */
    public CoreFileOut(ExecuteParams wiParams, FileOut fileout) {
        this.wiParams = wiParams;
        this.fileout = fileout;
        element = fileout;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(fileout.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String directory = prod.getOutput().trim();
        prod.setInput(fileout.getFile());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String file = prod.getOutput().trim();
        String fullfile = new File(directory, file).toString();
        String content = StringA.changeChars(fileout.getContent(), "| ", "");
        FileIO fl = new FileIO(fullfile, 'w');
        if (content.toLowerCase().startsWith("grid.")) {
            grid(content);
            if (wiParams.mustExit()) {
                return;
            }
        }
        prod.setInput("|" + content + "|");
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        fl.setAppend(fileout.isAppend());
        if (fileout.getCreateDir().equals("ON")) {
        	new File(directory).mkdirs();
        }
        String texto = prod.getOutput();
        if (fileout.isAppend()) {
            texto = texto + "\r\n";
        }
        if (content.toLowerCase().startsWith("wi.context")) {
        	texto = EngFunction.wiContext(wiMap, content) + "\r\n";
        }
        fl.writeText(texto);
        fl.close();
        writeLog();
    }

    private void grid(String content) {
        if (!wiMap.get(content).trim().equals("")) {
            return;
        }
        GridRef ele = new GridRef();
        ele.setId(StringA.piece(content.toLowerCase(), "grid.", 2));
        CoreGrid cgrid = new CoreGrid(wiParams, ele);
        cgrid.execute();
    }
}
