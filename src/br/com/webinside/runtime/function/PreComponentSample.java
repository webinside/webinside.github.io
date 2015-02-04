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

package br.com.webinside.runtime.function;

import java.io.*;
import java.sql.*;
import br.com.webinside.runtime.core.*;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

/**
 * Exemplo de conector Java de pré ou pós página.
 *
 * @author Luiz Ruiz
 * @version $Revision: 1.1 $
 */
public class PreComponentSample implements InterfaceConnector, 
	InterfaceParameters {
    /**
     * Construtor padrão.
     */
    public PreComponentSample() {
    }

    /**
     * Método chamado pelo WI.
     *
     * @param wiParams parâmetros do WI.
     * Implementação da interface InterfaceConnector.
     */
    public void execute(ExecuteParams wiParams) {
        WIMap wiMap = wiParams.getWIMap();
        PrintWriter out = wiParams.getWriter();
        DatabaseAliases databases = wiParams.getDatabaseAliases();

        // ler uma variável do contexto do WI
        String nomeProjeto = wiMap.get("wi.proj.id");
        out.println(nomeProjeto);

        // gravar uma variável no contexto do WI
        wiMap.put("text", this.getClass().getName());

        // escrer na saída do servlet
        out.println("Sou o conector original do WI");

        // pegar uma conexão JDBC
        DatabaseHandler dbgen =
            databases.get("Nome do banco de dados definido no Builder");
        ConnectionSql dbsql = (ConnectionSql) dbgen.getDatabaseConnection();
        Connection con = dbsql.getConnection();
        out.println("Conexão:" + con.toString());
    }

    public boolean exit() {
        return false;
    }

    /**
     * Método que retorna os parâmetros de entrada a serem mostrados
     * na tela do WIBuilder. Implementação da interface InterfaceParameters.
     * 
     * @return array de parâmetros
     */
    public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[3];
        params[0] = new JavaParameter("tmp.nome", "Nome", 
                "Nome do cliente a ser cadastrado.");
        params[1] = new JavaParameter("tmp.endereco", "Endereço", 
        "Endereço do cliente a ser cadastrado.");
        params[2] = new JavaParameter("tmp.fone", "Telefone", 
        "Número do telefone do cliente.");
        return params;
    }

    /**
     * Método que retorna os parâmetros de entrada a serem mostrados
     * na tela do WIBuilder. Implementação da interface InterfaceParameters.
     * 
     * @return array de parâmetros
     */
    public JavaParameter[] getOutputParameters() {
        String msg = "Exemplo de conector Java para ser usado em pré ou pós " +
        		"página.<br>Esse conector, <code><b>PreComponentSample</b></code> " +
        		"mostra como gravar variáveis, acessar conexões com banco de " +
        		"dados, ou escrever na saída do servlet.";
        JavaParameter[] outParam = new JavaParameter[1];
        outParam[0] = new JavaParameter("&nbsp;", msg, ""); 
        return outParam;
    }
}
