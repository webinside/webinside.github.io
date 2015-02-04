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

import java.awt.Color;
import java.awt.Paint;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.WIMap;

/**
 * <p>
 * Classe abstrata que serve como base para a construção das classes concretas
 * que irão oferecer a implementação específica para um determinado tipo de
 * gráfico.
 * </p>
 * 
 * <p>
 * A implementação do método <code>createChart()</code> deverá instanciar a
 * propriedade <code>chart</code> com um objeto do tipo
 * <code>JFreeChart</code> correspondente ao tipo de gráfico a ser montado.
 * </p>
 * 
 * <p>
 * O método <code>setParameters(Hash ctx)</code> deverá instanciar as
 * propriedades específicas do gráfico que está sendo montado de acordo com as
 * variáveis que estão num contexto de um projeto do WI.
 * </p>
 *
 * @author Luiz Ricardo To change the template for this generated type comment
 *         go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class WIChart extends AbstractConnector
    implements InterfaceParameters {
    //parâmetros de entrada
    /** DOCUMENT ME! */
    public static final String CHART_TYPE_VAR = "tmp.chart.type";
    /** DOCUMENT ME! */
    public static final String CHART_WIDTH_VAR = "tmp.chart.width";
    /** DOCUMENT ME! */
    public static final String CHART_HEIGHT_VAR = "tmp.chart.height";
    /** DOCUMENT ME! */
    public static final String CHART_TITLE_VAR = "tmp.chart.title";
    /** DOCUMENT ME! */
    public static final String CHART_3D_VAR = "tmp.chart.draw3d";
    /** DOCUMENT ME! */
    public static final String CHART_PLOT_FOREGROUND_VAR =
        "tmp.chart.plot.foregroundAlpha";
    /** DOCUMENT ME! */
    public static final String CHART_DATASET_OBJ_VAR =
        "tmp.chart.dataset.objectId";
    /** DOCUMENT ME! */
    public static final String CHART_BACKGROUND_COLOR_VAR =
        "tmp.chart.backgroundColor";

    //parâmetro de saída
    /** DOCUMENT ME! */
    public static final String CHART_ERROR_VAR = "tmp.chart.error";
    /** DOCUMENT ME! */
    protected JFreeChart chart;
    /** DOCUMENT ME! */
    protected int width;
    /** DOCUMENT ME! */
    protected int height;
    /** DOCUMENT ME! */
    protected String title;
    /** DOCUMENT ME! */
    protected String type;
    /** DOCUMENT ME! */
    protected boolean draw3d;
    /** DOCUMENT ME! */
    protected float plotForegroundAlpha;
    /** DOCUMENT ME! */
    protected Paint backgroundColor;
    /** DOCUMENT ME! */
    protected String objectId;
    /** DOCUMENT ME! */
    protected List inParameters;
    /** DOCUMENT ME! */
    protected List outParameters;

    /**
     * Creates a new WIChart object.
     */
    public WIChart() {
        width = 620;
        height = 460;
        draw3d = false;
        plotForegroundAlpha = Plot.DEFAULT_FOREGROUND_ALPHA;
        backgroundColor = Plot.DEFAULT_BACKGROUND_PAINT;
        inParameters = new ArrayList(7);
        //addJavaParameter(WIChart.CHART_TYPE_VAR, "Tipo de gráfico", "Tipo de gráfico a ser montado.");
        addInParameter(WIChart.CHART_TITLE_VAR, "Título do gráfico",
            "Título do gráfico.");
        addInParameter(WIChart.CHART_WIDTH_VAR, "Largura",
            "Largura do gráfico gerado.");
        addInParameter(WIChart.CHART_HEIGHT_VAR, "Altura",
            "Altura do gráfico gerado.");
        addInParameter(WIChart.CHART_3D_VAR, "3D",
            "Indica se o gráfico será desenhado em perspectiva 3D.");
        addInParameter(WIChart.CHART_PLOT_FOREGROUND_VAR,
            "Nível de opacidade do gráfico",
            "Indica o nível de opacidade que o gráfico "
            + "terá. O valor vai de 1.0f a 0.0f sendo que quanto menor o número menor será a opacidade, ou seja,"
            + "maior será a transparência para o gráfico gerado.");
        addInParameter(WIChart.CHART_BACKGROUND_COLOR_VAR,
            "Cor de fundo da imagem (hexadecimal) <a href=# onclick=\"colorWindow('jParam."
            + WIChart.CHART_BACKGROUND_COLOR_VAR + "');return false;\">"
            + "<img border=\"0\" src=\"images/icons/paleta2.jpg\"></a>", "");
        addInParameter(WIChart.CHART_DATASET_OBJ_VAR,
            "Objeto (<code>dataset</code>)",
            "Identificador do objeto que "
            + "servirá como fonte dos dados para popular o dataset usado " 
            + "pelo gráfico. O nome do objeto não deve estar entre pipes.");
        outParameters = new ArrayList(1);
        addOutParameter(WIChart.CHART_ERROR_VAR,
            "Variável que conterá o stack trace se ocorrer algum erro durante a "
            + "geração do gráfico.", "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDraw3d() {
        return draw3d;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getHeight() {
        return height;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWidth() {
        return width;
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setDraw3d(boolean b) {
        draw3d = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     */
    public void setHeight(int i) {
        height = i;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    public void setTitle(String string) {
        title = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    public void setType(String string) {
        type = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     */
    public void setWidth(int i) {
        width = i;
    }

    /**
     * DOCUMENT ME!
     *
     * @param varId DOCUMENT ME!
     * @param description DOCUMENT ME!
     * @param hint DOCUMENT ME!
     */
    protected void addInParameter(String varId, String description, String hint) {
        JavaParameter jp = new JavaParameter(varId, description, hint);
        inParameters.add(jp);
    }

    /**
     * DOCUMENT ME!
     *
     * @param varId DOCUMENT ME!
     * @param description DOCUMENT ME!
     * @param hint DOCUMENT ME!
     */
    protected void addOutParameter(String varId, String description, String hint) {
        JavaParameter jp = new JavaParameter(varId, description, hint);
        outParameters.add(jp);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getInParameters() {
        return inParameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getOutParameters() {
        return outParameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getPlotForegroundAlpha() {
        return plotForegroundAlpha;
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     */
    public void setPlotForegroundAlpha(float f) {
        plotForegroundAlpha = f;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    public void setObjectId(String string) {
        objectId = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param p DOCUMENT ME!
     */
    public void setBackgroundColor(Paint p) {
        backgroundColor = p;
    }

    /**
     * DOCUMENT ME!
     */
    public abstract void createChart();

    /**
     * DOCUMENT ME!
     *
     * @param ctx DOCUMENT ME!
     */
    public abstract void setParameters(WIMap ctx);

    //testando implementações abaixo...
    public JavaParameter[] getInputParameters() {
        List list = getInParameters();
        JavaParameter[] jp = new JavaParameter[list.size()];
        list.toArray(jp);
        return jp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getOutputParameters() {
        List list = getOutParameters();
        JavaParameter[] jp = new JavaParameter[list.size()];
        list.toArray(jp);
        return jp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param databases DOCUMENT ME!
     * @param headers DOCUMENT ME!
     */
    public void execute(WIMap context, DatabaseAliases databases,
        InterfaceHeaders headers) {
        PrintStream out = getOutputStream();
        try {
            setTitle(context.get(WIChart.CHART_TITLE_VAR));
            String w = context.get(WIChart.CHART_WIDTH_VAR);
            if (!w.equals("")) {
                setWidth(Integer.parseInt(w));
            }
            String h = context.get(WIChart.CHART_HEIGHT_VAR);
            if (!h.equals("")) {
                setHeight(Integer.parseInt(h));
            }
            String foregroundAlpha =
                context.get(WIChart.CHART_PLOT_FOREGROUND_VAR);
            if (!foregroundAlpha.equals("")) {
                setPlotForegroundAlpha(Float.parseFloat(foregroundAlpha));
            }
            String bgColor = context.get(WIChart.CHART_BACKGROUND_COLOR_VAR);
            if (bgColor.startsWith("#")) {
                setBackgroundColor(Color.decode(bgColor));
            }
            setDraw3d(context.get(WIChart.CHART_3D_VAR).equalsIgnoreCase("true"));
            setObjectId(context.get(WIChart.CHART_DATASET_OBJ_VAR));

            //chamada aos métodos que devem ser implementados pelas subclasses
            setParameters(context);
            createChart();

            //configurações gerais da área do gráfico
            Plot plot = chart.getPlot();
            plot.setForegroundAlpha(getPlotForegroundAlpha());

            //configurações gerais da área da imagem
            chart.setBackgroundPaint(getBackgroundColor());

            headers.setContentType("image/png");
            ImageIO.write(chart.createBufferedImage(getWidth(), getHeight()),
                "png", out);
        } catch (Exception e) {
            ErrorLog log = getParams().getErrorLog();
            String id = "Page: " + context.get("wi.page.id");
            log.write("WIChart", id, e);
            context.put(WIChart.CHART_ERROR_VAR, Util.stackTraceToString(e));
        }
    }
}
