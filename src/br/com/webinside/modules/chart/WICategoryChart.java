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

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe abstrata que serve como base para a construção das classes concretas
 * que irão oferecer a implementação específica gráficos que utilizem o
 * dataset do tipo <code>CategoryDataser</code>.
 * 
 * <p>
 * A implementação do método <code>createChart()</code> deverá instanciar a
 * propriedade <code>chart</code> com um objeto do tipo
 * <code>JFreeChart</code> correspondente ao tipo de gráfico a ser montado.
 * </p>
 * 
 * <p>
 * O método <code>setParameters(WIMap ctx)</code> deverá instanciar as
 * propriedades específicas do gráfico que está sendo montado de acordo com as
 * variáveis que estão num contexto de um projeto do WI. Às
 * subclasses que sobrescreverem esse método é recomendado que façam a chamada
 * ao método da superclasse.
 * </p>
 *
 * @author Luiz Ricardo
 */
public abstract class WICategoryChart extends WIChart {
    /** Nome da variável de sessão do WI que informa o rótulo do eixo X. */
    public static final String XLABEL_VAR = "tmp.chart.xlabel";
    /** Nome da variável de sessão do WI que informa o rótulo do eixo Y. */
    public static final String YLABEL_VAR = "tmp.chart.ylabel";
    /**
     * Noma da variável de sessão do WI que informa como estão dispostos os
     * valores (<code>line</code> ou <code>column</code>) dentro do objeto
     * (dataset). Se não for informado nenhum valor para essa variável ou se
     * for informado um valor diferente de <code>column</code>, será assumido
     * que os dados estão dispostos em linha.
     */
    public static final String CATEGORYDATASET_DISPOSITION_VAR =
        "tmp.chart.categoryDatasetDisposition";
    /**
     * Noma da variável de sessão do WI que informa qual a orientação
     * (<code>vertical</code> ou <code>horizontal</code>) que o plot irá
     * utilizar para desenhar o gráfico. Se não for informado nenhum valor
     * para essa variável ou se for informado um valor diferente de
     * <code>horizontal</code>, será assumido que a orientação é vertical.
     */
    public static final String PLOT_ORIENTATION_VAR =
        "tmp.chart.plotOrientation";
    public static final String CATEGORY_LABEL_UP_45 =
        "tmp.chart.categoryLabelUp45";
    public static final String TICK_LABEL_SIZE =
        "tmp.chart.tickLabelSize";

    protected String xLabel;
    protected String yLabel;
    private String plotOrientationStr;
    private CategoryDataset categoryDataset;
    private boolean categoryLabelUp45;
    private int tickLabelSize;

    /**
     * Creates a new WICategoryChart object.
     */
    public WICategoryChart() {
        addInParameter(WICategoryChart.XLABEL_VAR, "Rótulo do eixo X", "");
        addInParameter(WICategoryChart.YLABEL_VAR, "Rótulo do eixo Y", "");
        addInParameter(WICategoryChart.CATEGORYDATASET_DISPOSITION_VAR,
            "Disposição dos dados das categorias no objeto",
            "line (default): os dados das séries de uma categoria estão numa "
            + "única linha do objeto. column: os dados das séries das "
            + "categorias estão nas colunas.");
        addInParameter(WICategoryChart.PLOT_ORIENTATION_VAR,
            "Orientação do plot",
            "Indica a orientação que será seguida para desenhar o gráfico. "
            + "Os valores possíveis são: vertical (default) ou horizontal.");
        addInParameter(BarChart.STACKED_VAR, "Empilhar barras",
        "Indica se as barras devem ser empilhadas, o valor default é false.");
        addInParameter(WICategoryChart.CATEGORY_LABEL_UP_45,
                "Rótulo das séries na diagonal",
        		"Indica se o rótulo das séries deve ficar na diagonal, o valor default é false.");
        addInParameter(WICategoryChart.TICK_LABEL_SIZE, 
        		"Tamanho da fonte das séries", 
        		"Indica o tamanho da fonte a ser usado nas séries, o valor padrão é o default do JFreeChart");
    }

    /**
     * DOCUMENT ME!
     */
    public abstract void createChart();

    /**
     * Método que configura os parâmetros que são comuns aos gráficos que
     * utilizam o dataset do tipo <code>CategoryDataset</code>. Os parâmetros
     * são: os rótulos dos eixos "x" e "y", a orientação do plot e a
     * disposição dos dados dentro do objeto de fonte de dados.
     *
     * @param context sessão do WI de onde serão recuperados os valores para
     *        configurar as propriedades do gráfico.
     */
    public void setParameters(WIMap context) {
        plotOrientationStr = context.get(WICategoryChart.PLOT_ORIENTATION_VAR);

        if (context.get(WICategoryChart.CATEGORYDATASET_DISPOSITION_VAR)
                    .equalsIgnoreCase("column")) {
            setCategoryDataset(ChartUtil.getCategoryDatasetFromColumn(context,
                    objectId));
        } else {
            setCategoryDataset(ChartUtil.getCategoryDatasetFromLine(context,
                    objectId));
        }

        setXLabel(context.get(WICategoryChart.XLABEL_VAR));

        setYLabel(context.get(WICategoryChart.YLABEL_VAR));
        
        setCategoryLabelUp45(context.get(WICategoryChart.CATEGORY_LABEL_UP_45).equalsIgnoreCase("true"));
        
        setTickLabelSize(Function.parseInt(context.get(WICategoryChart.TICK_LABEL_SIZE)));
        
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public CategoryDataset getCategoryDataset() {
        return categoryDataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PlotOrientation getPlotOrientation() {
        return plotOrientationStr.equalsIgnoreCase("horizontal")
        ? PlotOrientation.HORIZONTAL
        : PlotOrientation.VERTICAL;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getXLabel() {
        return xLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getYLabel() {
        return yLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dataset DOCUMENT ME!
     */
    public void setCategoryDataset(CategoryDataset dataset) {
        categoryDataset = dataset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    public void setXLabel(String string) {
        xLabel = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    public void setYLabel(String string) {
        yLabel = string;
    }

	public void setCategoryLabelUp45(boolean categoryLabelUp45) {
		this.categoryLabelUp45 = categoryLabelUp45;
	}

	public void setTickLabelSize(int tickLabelSize) {
		this.tickLabelSize = tickLabelSize;
	}
    
    protected void defineCategoryFont(JFreeChart chart) {
        CategoryAxis domAxis = chart.getCategoryPlot().getDomainAxis();
        if (categoryLabelUp45) {
        	domAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
        if (tickLabelSize > 0) {
        	Font font = new Font(Font.SANS_SERIF, Font.PLAIN, tickLabelSize);
        	domAxis.setTickLabelFont(font);
        }	
    }
    
}
