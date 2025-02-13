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
            "Modo de exibi��o dos r�tulos das se��es",
            "no (default): n�o exibe nenhum tipo de r�tulo. "
            + "all: exibe todos os dados. "
            + "name: exibe apenas os nomes das se��es. "
            + "value: exibe apenas os valores das se��es. "
            + "percent: exibe apenas as porcentagens ocupadas pelas se��es. "
            + "name_and_value: exibe os nomes das se��es e os valores entre " 
            + "par�ntesis. "
            + "name_and_percent: exibe os nomes das se��es e as porcentagens "
            + "entre par�ntesis. "
            + "value_and_percent: exibr os valores das se��es e as porcentagens "
            + "entre par�ntesis. ");
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
