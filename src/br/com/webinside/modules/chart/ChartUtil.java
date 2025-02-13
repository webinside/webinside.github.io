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
     * m�todo assume que a primeira coluna cont�m o nome da categoria e os
     * restantes das colunas possuem os valores de cada uma das s�ries que
     * comp�em a categoria, o nome da coluna onde o valor se encontra
     * corresponde ao nome da s�rie.
     * </p>
     * 
     * <p>
     * Cada linha do objeto que ir� popular o <code>dataset</code> deve
     * oferecer todos os valores das s�ries das categorias, ou seja, em �nica
     * linha do objeto deve ter todos os valores das s�ries de uma determinada
     * categoria.
     * </p>
     *
     * @param ctx o contexto do WI que cont�m o objeto do qual ser�
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
                //os nomes das s�ries das categorias s�o os nomes das colunas 
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
     * m�todo assume que a primeira coluna cont�m os nomes das categorias, a
     * segunda cont�m os nomes das s�ries e a terceira coluna possue os
     * valores de cada uma das s�ries que comp�em a categoria.
     * </p>
     * 
     * <p>
     * Cada linha do objeto deve informar apenas o nome da categoria, o nome da
     * s�rie e o respectivo valor para esse par (categoria, s�rie).
     * </p>
     *
     * @param ctx o contexto do WI que cont�m o objeto do qual ser�
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

            //os nomes das s�ries devem estar relacionados na segunda coluna do
            //objeto
            String serieName =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[1]));

            //os valores das s�ries devem estar relacionados na terceira coluna
            //do objeto
            String serieValue =
                ctx.get(normalize(objectId + "[" + i + "]." + columnNames[2]));

            //adicione ao dataset o valor correspondente � s�rie de uma categoria
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
