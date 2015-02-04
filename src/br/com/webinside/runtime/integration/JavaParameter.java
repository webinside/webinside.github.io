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

package br.com.webinside.runtime.integration;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class JavaParameter {
    private String varId;
    private String description;
    private String hint = "";
    private String value = "";

    /**
     * Creates a new JavaParameter object.
     *
     * @param varID DOCUMENT ME!
     * @param description DOCUMENT ME!
     * @param hint DOCUMENT ME!
     */
    public JavaParameter(String varID, String description, String hint) {
        this.varId = varID;
        this.description = description;
        this.hint = hint;
    }

    /**
     * Creates a new JavaParameter object.
     *
     * @param varID DOCUMENT ME!
     * @param description DOCUMENT ME!
     */
    public JavaParameter(String varID, String description) {
        this.varId = varID;
        this.description = description;
    }

    /**
     * Creates a new JavaParameter object.
     *
     * @param varID DOCUMENT ME!
     */
    public JavaParameter(String varID) {
        this.varId = varID;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVarId() {
        return varId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHint() {
        return hint;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "{varId=[" + varId + "], description=[" + description
        + "], hint=[" + hint + "], value=[" + value + "]}";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValue() {
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setValue(String value) {
        this.value = value;
    }
}
