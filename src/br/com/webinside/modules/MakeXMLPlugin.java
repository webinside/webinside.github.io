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

package br.com.webinside.modules;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import br.com.webinside.modules.chart.AreaChart;
import br.com.webinside.modules.chart.BarChart;
import br.com.webinside.modules.chart.LineChart;
import br.com.webinside.modules.chart.PieChart;
import br.com.webinside.runtime.integration.InterfaceConnector;
import br.com.webinside.runtime.integration.InterfaceFunction;
import br.com.webinside.runtime.integration.InterfaceGrid;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;

/**
 * @author Luiz Ruiz
 *
 * Gera xml a partir de uma classe de conector para registro de plugin
 *
 */
public class MakeXMLPlugin {


    private final String className;
    private PrintStream out;
    private Class cl;
    private String ident = "    ";

    public MakeXMLPlugin(String className, String fileOutput) {
        this.className = className;
        out = System.out;
        if (null != fileOutput) {
            try {            
                out = new PrintStream(new FileOutputStream(fileOutput));
            } catch (FileNotFoundException e) {
                System.out.println("Arquivo não encontrado: " + fileOutput);
            }
        }
    }
    
    public void make() {
        openTag("PLUGIN");
        print("TITLE", "");
        print("CLASS", className);
        String type = "";
        try {
            type = getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        print("TYPE", type);
        openTag("PARAMETERS");
        getParameters();
        closeTag("PARAMETERS");
        closeTag("PLUGIN");
    }
    
    private void getParameters() {
        InterfaceParameters par = null;
        try {
            par = (InterfaceParameters) cl.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        openTag("IN");
        JavaParameter[] pars = par.getInputParameters();
        printParameters(pars);
        closeTag("IN");
        openTag("OUT");
        pars = par.getOutputParameters();
        printParameters(pars);
        closeTag("OUT");
    }
    
    private void printParameters(JavaParameter[] pars) {
        for (int i = 0; i < pars.length; i++) {
            out.println(ident + "<PARAMETER ID=\"" + pars[i].getVarId()
                    + "\">");
            incIdent();
            print("DESCRIPTION", pars[i].getDescription());
            print("HINT", pars[i].getHint());
            print("VALUE", "|" + pars[i].getVarId() + "|");
            closeTag("PARAMETER");
        }
    }
    
    private void print(String tag, String content) {
        out.println(ident + "<" + tag + ">" + content + "</" + tag + ">");
    }

    private void openTag(String tag) {
        out.println(ident + "<" + tag + ">");
        incIdent();
    }
    private void closeTag(String tag) {
        decIdent();
        out.println(ident + "</" + tag + ">");
    }
    
    private void incIdent() {
        ident += "  ";
    }

    private void decIdent() {
        if (ident.length() >= 2) {
            ident = ident.substring(0, ident.length() - 2);
        }
    }

    private String getType() 
    		throws ClassNotFoundException, InstantiationException,
    		IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String type = null;
        cl = Class.forName(className);
        if (cl.getConstructor().newInstance() instanceof InterfaceConnector) {
            type = "CONNECTOR";
        }
        if (cl.getConstructor().newInstance() instanceof InterfaceFunction) {
            type = "FUNCTION";
        }
        if (cl.getConstructor().newInstance() instanceof InterfaceGrid) {
            type = "JAVAGRID";
        }
        return type;
    }
    
    public static void main(String[] args) {
        new MakeXMLPlugin(PieChart.class.getName(), "plugins-pie.xml").make();
        new MakeXMLPlugin(BarChart.class.getName(), "plugins-bar.xml").make();
        new MakeXMLPlugin(LineChart.class.getName(), "plugins-line.xml").make();
        new MakeXMLPlugin(AreaChart.class.getName(), "plugins-area.xml").make();
    	if (true) return;
    	if (args.length == 1) {
            String[] ar = {args[0], null};
            args = ar;
        }
        if (args.length == 0) {
            System.out.println(
            		"Use MakeXMLPlugin <nome da classe> [<arquivo saida>]");
            return;
        }
        new MakeXMLPlugin(args[0], args[1]).make();
    }

}
