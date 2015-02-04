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

package br.com.webinside.runtime.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que guarda as informações a serem repassadas entre os projetos 
 * @author Geraldo Moraes
 *
 */
public class SingleSignOnRepository { 

	private static Map tokens = new HashMap();
	private static Map owners = new HashMap();
	
	public static void addToken(String token, Map map, String owner) {
		tokens.put(token, map);
		owners.put(token, owner);
	}
	
	public static void delToken(String token) {
		tokens.remove(token);
		owners.remove(token);
	}
	
	public static Map getToken(String token) {
		return (Map)tokens.get(token);
	}

	public static boolean isOwner(String token, String owner) {
		String aux = (String)owners.get(token); 
		return (aux != null && aux.equals(owner));
	}
	
}
