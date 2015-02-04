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

package br.com.webinside.runtime.component.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.component.Database;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.util.Encrypter;

/**
 * Classe que define a propriedade USER de um componente.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class PropertyUser extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String name;
    private String user;
    private String pass;

    /**
     * Executa a transformação.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
    	Object obj = pageContext.getAttribute(getName());
    	if (!getPass().equals("")) {
        	Encrypter enc = new Encrypter(getPass());
        	pass = enc.decodeDES();
    	}
    	if (obj instanceof Database) {
    		Database db = (Database)obj;
    		db.setUser(getUser(), pass);
    	} else if (obj instanceof Host) {
    		Host host = (Host)obj;
    		host.setUser(getUser(), pass);
    	}
        return SKIP_BODY;
    }

    /**
     * Nome da variável no PageContext.
     *
     * @return indica o nome da variável no PageContext.
     */
    public String getName() {
        return name;
    }

    /**
     * Nome da variável no PageContext.
     *
     * @param name o nome da variável no PageContext.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Senha a ser utilizada.
     *
     * @return indica a senha a ser utilizada.
     */
    public String getPass() {
        return pass;
    }

    /**
     * Senha a ser utilizada.
     *
     * @param pass define a senha a ser utilizada.
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * Usuario a ser utilizado.
     *
     * @return indica o usuario a ser utilizado.
     */
    public String getUser() {
        return user;
    }

    /**
     * Usuario a ser utilizado.
     *
     * @param define o usuario a ser utilizado.
     */
    public void setUser(String user) {
        this.user = user;
    }
}
