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

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ricardo To change the template for this generated type comment
 *         go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ChartUtil {
    /**
     * <p>
     * Retorna um <code>CategoryDataset</code> a partir de um objeto que esteja
     * no contexto do WI. Para montar o <code>dataset</code> o
     * método assume que a primeira coluna contém o nome da categoria e os
     * restantes das colunas possuem os valores de cada uma das séries que
     * compõem a categoria, o nome da coluna onde o valor se encontra
     * corresponde ao nome da série.
     * </p>
     * 
     * <p>
     * Cada linha do objeto que irá popular o <code>dataset</code> deve
     * oferecer todos os valores das séries das categorias, ou seja, em única
     * linha do objeto deve ter todos os valores das séries de uma determinada
     * categoria.
     * </p>
     *
     * @param ctx o contexto do WI que contém o objeto do qual será
     *        criado o <code>dataset</code>.
     * @param objectId identificador do objeto.
     *
     * @return um <code>CategoryDataset</code> a partir de um objeto que esteja
     *         no contexto do WI.
     */
    public static CategoryDataset getCategoryDatasetFromLine(WIMap ctx,
        String objectId) {
        DefaultCategoryDataset dcd = new DefaultCategoryDataset();
        int size = Function.parseInt(ctx.get(objectId + ".size()"));
        String[] columnNames =
            Util.split(ctx.get(objectId + ".columnNames()"), ",");
        String[] categoryNames = new String[size];

        for (int i = 1; i <= size; i++) {
            //os nomes das categorias devem estar relacionados na primeira 
            //coluna do objeto
            categoryNames[i - 1] =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[0]));
            for (int j = 1; j < columnNames.length; j++) {
                //os nomes das séries das categorias são os nomes das colunas 
                String sValue =
                    ctx.get(normalize(objectId + "[" + i + "]."
                            + columnNames[j]));
                String colName = columnNames[j];
				try {
					String dvar = 
						ctx.get(WICategoryChart.CATEGORYDATASET_DISPOSITION_VAR);
					if (dvar.equalsIgnoreCase("line_utf")) {
						colName = new String(colName.getBytes(), "utf-8");
					}	
				} catch (UnsupportedEncodingException e) {
					// ignorado
				}
                dcd.addValue(Double.valueOf(sValue), colName, categoryNames[i - 1]);
            }
        }
        return dcd;
    }

    /**
     * <p>
     * Retorna um <code>CategoryDataset</code> a partir de um objeto que esteja
     * no contexto do WI. Para montar o <code>dataset</code> o
     * método assume que a primeira coluna contém os nomes das categorias, a
     * segunda contém os nomes das séries e a terceira coluna possue os
     * valores de cada uma das séries que compõem a categoria.
     * </p>
     * 
     * <p>
     * Cada linha do objeto deve informar apenas o nome da categoria, o nome da
     * série e o respectivo valor para esse par (categoria, série).
     * </p>
     *
     * @param ctx o contexto do WI que contém o objeto do qual será
     *        criado o <code>dataset</code>.
     * @param objectId identificador do objeto.
     *
     * @return um <code>CategoryDataset</code> a partir de um objeto que esteja
     *         no contexto do WI.
     */
    public static CategoryDataset getCategoryDatasetFromColumn(WIMap ctx,
        String objectId) {
        DefaultCategoryDataset dcd = new DefaultCategoryDataset();
        int size = 0;
        try {
			size = Integer.parseInt(ctx.get(objectId + ".size()"));
        } catch (NumberFormatException err) {
        	throw new NullPointerException(objectId + ".size() not found");
        }
        String[] columnNames =
            Util.split(ctx.get(objectId + ".columnNames()"), ",");

        for (int i = 1; i <= size; i++) {
            //os nomes das categorias devem estar relacionados na primeira
            //coluna do objeto
            String categoryName =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[0]));

            //os nomes das séries devem estar relacionados na segunda coluna do
            //objeto
            String serieName =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[1]));

            //os valores das séries devem estar relacionados na terceira coluna
            //do objeto
            String serieValue =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[2]));

            //adicione ao dataset o valor correspondente à série de uma categoria
            dcd.addValue(Double.valueOf(serieValue), serieName, categoryName);
        }
        return dcd;
    }

    /**
     * DOCUMENT ME!
     *
     * @param str DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String normalize(String str) {
        StringTokenizer st = new StringTokenizer(str);
        StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
        }
        return sb.toString().toLowerCase();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ctx DOCUMENT ME!
     * @param objectId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static PieDataset getPieDataset(WIMap ctx, String objectId) {
        DefaultPieDataset data = new DefaultPieDataset();
        int size = Integer.parseInt(ctx.get(objectId + ".size()"));
        String[] columnNames =
            Util.split(ctx.get(objectId + ".columnNames()"), ",");

        for (int i = 1; i <= size; i++) {
            //os nomes das categorias devem estar relacionados na primeira
            //coluna do objeto
            String categoryName =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[0]));

            //os valores das categorias devem estar relacionados na segunda
            //coluna do objeto
            String categoryValue =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[1]));
            data.setValue(categoryName, Double.valueOf(categoryValue));
        }
        return data;
    }
}
