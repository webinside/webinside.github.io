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

package br.com.webinside.runtime.report;

import java.util.Arrays;

/**
 * Define as constantes utilizadas no WIReport
 *
 * @author Luiz Ruiz
 * @version $Revision: 1.1 $
 *
 * @since 3.0.10
 */
public class Constants {
    /** DOCUMENT ME! */
    public static final String ZOOM = "zoom";
    
    public static final String WIREPORT_APPNAME = "wireport";
    /**
     * Lista de ações. Deve haver uma constante declarada com o índice
     * equivalente à acão.
     */
    private static final String[] ACTION_NAMES =
    {
        "save", "add", "remove", "moveup", "movedown", "fieldwizard",
        "showexpressionwizard", "evalexpression", "newfield", "newgroup", 
		"undo", "saveas", "setborders"
    };
    /** Ação salvar */
    public static final int ACTION_SAVE = 0;
    /** Ação adicionar */
    public static final int ACTION_ADD = 1;
    /** Ação remover */
    public static final int ACTION_REMOVE = 2;
    /** Ação mover uma posição acima */
    public static final int ACTION_MOVE_UP = 3;
    /** Ação mover uma posição abaixo */
    public static final int ACTION_MOVE_DOWN = 4;
    /** Ação WIzard de campo */
    public static final int ACTION_FIELD_WIZARD = 5;
    /** Ação mostra WIzard de expressão */
    public static final int ACTION_EXPRESSION_WIZARD = 6;
    /** Ação avaliar expressão */
    public static final int ACTION_EVAL_EXPRESSION = 7;
    /** Ação adicionar campo */
    public static final int ACTION_NEW_FIELD = 8;
    /** Ação adicionar grupo */
    public static final int ACTION_NEW_GROUP = 9;
    /** Ação desfazer */
    public static final int ACTION_UNDO = 10;
    /** Ação salvar como*/
    public static final int ACTION_SAVEAS = 11;
    /** Ação configurar bordas*/
    public static final int ACTION_SET_BORDERS = 12;

    /**
     * Transforma a ação em um inteiro constante. Geralmente utilizado para
     * converter o parametro passado pela página HTML para o tipo  utilizado
     * nas classes.
     *
     * @param action a String da ação.
     *
     * @return o inteiro correspondente à constante da ação.
     */
    public static final int parseAction(String action) {
        int ret = Arrays.asList(ACTION_NAMES).indexOf(action.toLowerCase());
        return ret;
    }
}
