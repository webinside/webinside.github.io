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

import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ricardo
 */
public class BarChart extends WICategoryChart {
    /**
     * Nome da variável de sessão do WI que informa a posição
     * (<code>vertical</code> ou <code>horizontal</code>) de como serão
     * geradas as barras.
     */

    //public static final String BARCHART_POSITION_VAR = "tmp.chart.bar.position";
    /** Nome da variável de sessão do WI que informa o rótulo do eixo X. */

    //public static final String BARCHART_XLABEL_VAR = "tmp.chart.bar.xlabel";
    /** Nome da variável de sessão do WI que informa o rótulo do eixo Y. */

    //public static final String BARCHART_YLABEL_VAR = "tmp.chart.bar.ylabel";
    /**
     * Nome da variável de sessão do WI que informa se as barras de uma
     * categoria serão empilhadas.
     */
    public static final String STACKED_VAR = "tmp.barchart.stacked";
    /**
     * Noma da variável de sessão do WI que informa como estão dispostos os
     * valores (<code>line</code> ou <code>column</code>) no objeto (dataset).
     */

    //public static final String BARCHART_CATEGORY_DATASET_DISPOSITION_VAR =
    //    "tmp.chart.bar.categoryDatasetDisposition";
    //private String position;
    //private String xLabel;
    //private String yLabel;
    private boolean stacked;

    //private String categoryDatasetDisposition;
    //private CategoryDataset categoryDataset;
    /**
     * Creates a new BarChart object.
     */
    public BarChart() {
        stacked = false;
        //position = "vertical";
        //categoryDatasetDisposition = "line";
        //addInParameter(BarChart.BARCHART_POSITION_VAR, "Posição das barras",
        //    "vertical (default) ou horizontal.");
        addInParameter(BarChart.STACKED_VAR, "Empilhar barras",
            "Indica se as barras devem ser empilhadas, o valor default é false.");
        //addInParameter(BarChart.BARCHART_XLABEL_VAR, "Rótulo do eixo X", "");
        //addInParameter(BarChart.BARCHART_YLABEL_VAR, "Rótulo do eixo Y", "");

        /*addInParameter(BarChart.BARCHART_CATEGORY_DATASET_DISPOSITION_VAR,
           "Disposição dos dados das categorias no objeto",
           "line (default): os dados das séries de uma categoria estão numa "
           + "única linha do objeto.\r\ncolumn: os dados das séries das "
           + "categorias estão nas colunas.");*/
    }

    /**
     * DOCUMENT ME!
     */
    public void createChart() {
        if (draw3d) {
            if (stacked) {
                chart = ChartFactory.createStackedBarChart3D(title, xLabel, yLabel,
                        getCategoryDataset(), getPlotOrientation(), true, true,
                        false);
            } else {
                chart = ChartFactory.createBarChart3D(title, xLabel, yLabel,
                        getCategoryDataset(), getPlotOrientation(), true, true,
                        false);
            }
        } else {
            if (stacked) {
                chart = ChartFactory.createStackedBarChart(title, xLabel, yLabel,
                        getCategoryDataset(), getPlotOrientation(), true, true,
                        false);
            } else {
                chart = ChartFactory.createBarChart(title, xLabel, yLabel,
                        getCategoryDataset(), getPlotOrientation(), true, true,
                        false);
            }
        }
        defineCategoryFont(chart);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public PlotOrientation getPlotOrientation() {
       return position.equalsIgnoreCase("horizontal")
       ? PlotOrientation.HORIZONTAL
       : PlotOrientation.VERTICAL;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public String getPosition() {
       return position;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public String getXLabel() {
       return xLabel;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public String getYLabel() {
       return yLabel;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public void setPosition(String string) {
       position = string;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public void setXLabel(String string) {
       xLabel = string;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public void setYLabel(String string) {
       yLabel = string;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public CategoryDataset getCategoryDataset() {
       return categoryDataset;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */

    /*public void setCategoryDataset(CategoryDataset dataset) {
       categoryDataset = dataset;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */
    public void setParameters(WIMap context) {
        super.setParameters(context);
        setStacked(context.get(BarChart.STACKED_VAR).equals("true"));
        /*String position = context.get(BarChart.BARCHART_POSITION_VAR);
           if (!position.equals("")) {
               setPosition(position);
           }
           String cdsp =
               context.get(BarChart.BARCHART_CATEGORY_DATASET_DISPOSITION_VAR);
           if (!cdsp.equals("")) {
               setCategoryDatasetDisposition(cdsp);
           }
           setStacked(context.get(BarChart.BARCHART_STACKED_VAR).equals("true"));
           if (getCategoryDatasetDisposition().equalsIgnoreCase("column")) {
               setCategoryDataset(ChartUtil.getCategoryDatasetFromColumn(context,
                       objectId));
           } else if (getCategoryDatasetDisposition().equalsIgnoreCase("line")) {
               setCategoryDataset(ChartUtil.getCategoryDatasetFromLine(context,
                       objectId));
           }
           setXLabel(context.get(BarChart.BARCHART_XLABEL_VAR));
           setYLabel(context.get(BarChart.BARCHART_YLABEL_VAR));*/
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */

    /*public String getCategoryDatasetDisposition() {
       return categoryDatasetDisposition;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */

    /*public void setCategoryDatasetDisposition(String string) {
       categoryDatasetDisposition = string;
       }*/
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStacked() {
        return stacked;
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setStacked(boolean b) {
        stacked = b;
    }
}
