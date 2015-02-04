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

package br.com.webinside.runtime.xml;

/**
 * Classe que contém os possíveis resultados das operações com os elementos que
 * integram o WI.
 *
 * @author Geraldo Moraes
 * @version 1.0
 */
public class ErrorCode {
    /** Sucesso na execução. Não houve erro. */
    public static final int NOERROR = 1;
    /** Erro desconhecido. */
    public static final int UNKNOWN = 0;
    /** Elemento é nulo. */
    public static final int NULL = -1;
    /** Elemento é vazio. */
    public static final int EMPTY = -2;
    /** Elemento já existe. */
    public static final int EXIST = -5;
    /** Elemento não existe. */
    public static final int NOEXIST = -6;
	/** Erro do Servidor WebApp. */
	public static final int WEBAPP = -7;
}
