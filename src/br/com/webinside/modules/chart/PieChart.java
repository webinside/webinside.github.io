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

package br.com.webinside.modules.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;

import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ricardo
 */
public class PieChart extends WIChart {
    /** DOCUMENT ME! */
    public static final String PIECHART_SECTION_LABEL_TYPE_VAR =
        "tmp.chart.pie.sectionLabelType";
    private String sectionLabelMask;
    private PieDataset pieDataset;

    /**
     * Creates a new PieChart object.
     */
    public PieChart() {
    	sectionLabelMask = "{0}";
        addInParameter(PieChart.PIECHART_SECTION_LABEL_TYPE_VAR,
            "Modo de exibição dos rótulos das seções",
            "no (default): não exibe nenhum tipo de rótulo. "
            + "all: exibe todos os dados. "
            + "name: exibe apenas os nomes das seções. "
            + "value: exibe apenas os valores das seções. "
            + "percent: exibe apenas as porcentagens ocupadas pelas seções. "
            + "name_and_value: exibe os nomes das seções e os valores entre " 
            + "parêntesis. "
            + "name_and_percent: exibe os nomes das seções e as porcentagens "
            + "entre parêntesis. "
            + "value_and_percent: exibr os valores das seções e as porcentagens "
            + "entre parêntesis. ");
    }

    /**
     * DOCUMENT ME!
     */
    public void createChart() {
        if (draw3d) {
            chart = ChartFactory.createPieChart3D(title, getPieDataset(), hasLegend(),
                    false, false);
        } else {
            chart = ChartFactory.createPieChart(title, getPieDataset(), hasLegend(),
                    false, false);
        }
    	PiePlot plot = (PiePlot) chart.getPlot();
        if (!sectionLabelMask.equals("")) {
        	PieSectionLabelGenerator pslg = 
        		new StandardPieSectionLabelGenerator(sectionLabelMask);
        	plot.setLabelGenerator(pslg);
        } else {
        	plot.setLabelGenerator(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSectionLabelMask() {
        return sectionLabelMask;
    }

    /**
     * DOCUMENT ME!
     *
     * @param labelType DOCUMENT ME!
     */
    public void setSectionLabelType(String labelType) {
        if (labelType.equals("no")) {
            sectionLabelMask = "";
        } else if (labelType.equals("all")) {
        	sectionLabelMask = "{0} = {1} ({2})";
        } else if (labelType.equals("name")) {
        	sectionLabelMask = "{0}";
        } else if (labelType.equals("value")) {
        	sectionLabelMask = "{1}";
        } else if (labelType.equals("percent")) {
        	sectionLabelMask = "{2}";
        } else if (labelType.equals("name_and_value")) {
        	sectionLabelMask = "{0} ({1})";
        } else if (labelType.equals("name_and_percent")) {
        	sectionLabelMask = "{0} ({2})";
        } else if (labelType.equals("value_and_percent")) {
        	sectionLabelMask = "{1} ({2})";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PieDataset getPieDataset() {
        return pieDataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dataset DOCUMENT ME!
     */
    public void setPieDataset(PieDataset dataset) {
        pieDataset = dataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ctx DOCUMENT ME!
     */
    public void setParameters(WIMap ctx) {
        String slt = ctx.get(PieChart.PIECHART_SECTION_LABEL_TYPE_VAR);
        if (!slt.equals("")) {
            setSectionLabelType(slt);
        }
        setPieDataset(ChartUtil.getPieDataset(ctx, objectId));
    }
}
