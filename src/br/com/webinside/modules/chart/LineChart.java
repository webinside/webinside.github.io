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

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ricardo
 */
public class LineChart extends WICategoryChart {
    /**
     * DOCUMENT ME!
     */
    public void createChart() {
        if (draw3d) {
            chart = ChartFactory.createLineChart3D(title, xLabel, yLabel,
                    getCategoryDataset(), getPlotOrientation(), hasLegend(), true,
                    false);
        } else {
            chart = ChartFactory.createLineChart(title, xLabel, yLabel,
                    getCategoryDataset(), getPlotOrientation(), hasLegend(), true,
                    false);
        }
        defineCategoryFont(chart);
    }
}
