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

package br.com.webinside.runtime.integration;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ProducerParam {
    /** DOCUMENT ME! */
    protected BufferedReader inputR;
    /** DOCUMENT ME! */
    protected StringA outputS = new StringA();
    /** DOCUMENT ME! */
    protected PrintWriter outputW;
    /** DOCUMENT ME! */
    protected WIMap wiMap;
    /** DOCUMENT ME! */
    protected String emptyString = "";
    /** DOCUMENT ME! */
    protected List emptyStart;
    /** DOCUMENT ME! */
    protected List emptyValue;
    /** DOCUMENT ME! */
    protected Set protectedPipe;
    /** DOCUMENT ME! */
    protected boolean recursive = false;
    /** DOCUMENT ME! */
    protected String filterIn = "";
    /** DOCUMENT ME! */
    protected String filterOut = "";
    /** DOCUMENT ME! */
    protected boolean isXml = false;
    /** DOCUMENT ME! */
    protected List generategrid;
    /** DOCUMENT ME! */
    protected ExecuteParams execparam;

    /**
     * Creates a new ProducerParam object.
     */
    public ProducerParam() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param input DOCUMENT ME!
     */
    public void setInput(StringA input) {
        if (input == null) {
            return;
        }
        inputR = new BufferedReader(new StringReader(input.toString()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param input DOCUMENT ME!
     */
    public void setInput(String input) {
        if (input == null) {
            return;
        }
        inputR = new BufferedReader(new StringReader(input));
    }

    /**
     * DOCUMENT ME!
     *
     * @param input DOCUMENT ME!
     */
    public void setInput(Reader input) {
        if (input == null) {
            return;
        }
        inputR = new BufferedReader(input);
    }

    /**
     * DOCUMENT ME!
     *
     * @param output DOCUMENT ME!
     */
    public void setOutput(StringA output) {
        if (output == null) {
            return;
        }
        outputS = output;
        outputW = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param output DOCUMENT ME!
     */
    public void setOutput(String output) {
        outputS = new StringA(output);
        outputW = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param output DOCUMENT ME!
     */
    public void setOutput(Writer output) {
        if (output == null) {
            return;
        }
        outputW = new PrintWriter(output);
        outputS = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param output DOCUMENT ME!
     */
    public void setOutput(OutputStream output) {
        if (output == null) {
            return;
        }
        outputW = new PrintWriter(new OutputStreamWriter(output));
        outputS = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     */
    public void setWIMap(WIMap wiMap) {
        if (wiMap == null) return;
        this.wiMap = wiMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     */
    public void setWIMap(Map wiMap) {
        if (wiMap == null) return;
        this.wiMap = new WIMap(wiMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @param empty DOCUMENT ME!
     */
    public void setEmpty(String empty) {
        if (empty == null) {
            return;
        }
        this.emptyString = new StringA(empty).change("|", "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param empty DOCUMENT ME!
     */
    public void addEmpty(String prefix, String empty) {
        if ((prefix == null) || (empty == null)) {
            return;
        }
        if (emptyStart == null) {
            emptyStart = new ArrayList();
        }
        emptyStart.add(prefix.toLowerCase());
        if (emptyValue == null) {
            emptyValue = new ArrayList();
        }
        emptyValue.add(empty);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     */
    public void addProtectedPipe(String key) {
        if (key == null) {
            return;
        }
        if (protectedPipe == null) {
            protectedPipe = new HashSet();
        }
        if (!key.endsWith(".")) {
            protectedPipe.add(key.toLowerCase());
        } else if (wiMap != null) {
            List keys = new ArrayList(wiMap.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String onekey = (String) keys.get(i);
                if (onekey.startsWith(key)) {
                    protectedPipe.add(onekey);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void resetProtectedPipe() {
        protectedPipe = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param grid DOCUMENT ME!
     */
    public void addGenerateGrid(String grid) {
        if (grid == null) {
            return;
        }
        if (generategrid == null) {
            generategrid = new ArrayList();
        }
        generategrid.add(grid.trim().toLowerCase());
    }

    /**
     * DOCUMENT ME!
     *
     * @param recursive DOCUMENT ME!
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     */
    public void setCharFilter(String source, String target) {
        if ((source == null) || (target == null)) {
            return;
        }
        filterIn = source;
        filterOut = target;
    }

    /**
     * DOCUMENT ME!
     *
     * @param isXml DOCUMENT ME!
     */
    public void setXml(boolean isXml) {
        this.isXml = isXml;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIMap getWIMap() {
        if (wiMap == null) {
            return new WIMap();
        }
        return wiMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOutput() {
        return getOutput(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param concatRET DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOutput(boolean concatRET) {
        if (outputS == null) {
            return "";
        }
        String value = outputS.toString();
        if ((concatRET) && (!value.endsWith("\n"))) {
            value = value + "\n";
        }
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public StringA getOutputA() {
        if (outputS == null) {
            return new StringA();
        }
        return outputS;
    }

    /**
     * DOCUMENT ME!
     *
     * @param execparam DOCUMENT ME!
     */
    public void setExecuteParam(ExecuteParams execparam) {
        this.execparam = execparam;
    }
}
