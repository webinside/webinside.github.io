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
 * @version $Revision: 1.2 $
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
        	texto = RtmFunction.wiContext(wiMap, content) + "\r\n";
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
