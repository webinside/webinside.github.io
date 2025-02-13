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

package br.com.webinside.test;

import java.util.Map;
import java.util.HashMap;

import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.util.WIMap;
import junit.framework.TestCase;

public class ConditionTest extends TestCase {

	private WIMap aux;
	
	public void testExecute() {
		// Cria mapa de variaveis
		aux = new WIMap();
		aux.put("tmp.empresa", "lineweb");
		aux.put("tmp.sigla", "LW");
		aux.put("tmp.ano", "2011");
		// Registra funcoes para testes
		Map func = new HashMap();
		func.put("if","br.com.webinside.runtime.function.If");
		aux.putObj("wi.functions", func);
		// Inicio dos testes
		assertTrue(execute("1=1"));
		assertTrue(execute("1==1"));
		assertTrue(execute("lw=|tmp.sigla|"));
		assertFalse(execute("|tmp.empresa|=lw"));
		assertTrue(execute(" LW ==|tmp.sigla|"));
		assertFalse(execute("lw==|tmp.sigla|"));
		assertFalse(execute(" lw ==|tmp.sigla|"));
		//
		assertTrue(execute("2>1"));
		assertFalse(execute("2>2"));
		assertFalse(execute("2>3"));
		assertTrue(execute("2>=1"));
		assertTrue(execute("2>=2"));
		assertFalse(execute("2>=3"));
		assertFalse(execute("2!>1"));
		assertTrue(execute("2!>2"));
		assertTrue(execute("2!>3"));
		//
		assertTrue(execute("1<2"));
		assertFalse(execute("2<2"));
		assertFalse(execute("3<2"));
		assertTrue(execute("1<=2"));
		assertTrue(execute("2<=2"));
		assertFalse(execute("3<=2"));
		assertFalse(execute("1!<2"));
		assertTrue(execute("2!<2"));
		assertTrue(execute("3!<2"));
		//
		assertFalse("Falha na condicao vazia", execute(""));
		String exp = "true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "true && true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "true && false";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "false || false || true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "false || false";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "(true && false) && true";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "(true && false) && (false && false)";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "(true && false) || true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "(true || false) && true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = " 1<2 && 2!<1 && 2>1 && 1!>2 && 1!=2 && 1=1 && 1==1";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "(true && (1=1)) && ((2<3) && (5!=6)";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "|$if(1!=1,true,false)$|";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "|$if(true && true,true,false)$| && true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		exp = "|$if(true && false,true,false)$| && true";
		assertFalse("Falha na condicao: " + exp, execute(exp));
		exp = "|$if(true || false,true,false)$| && true";
		assertTrue("Falha na condicao: " + exp, execute(exp));
		assertTrue(execute("|tmp.ano?tmp.cod=12345|==2011"));
		assertTrue(execute("|tmp.ano?tmp.cod=12345|!=2012"));
		assertTrue(execute("|tmp.ano|#=2011"));
		assertFalse(execute("|tmp.anoX|#=2011"));
		assertTrue(execute("a#="));
		assertFalse(execute("-3#=0"));
		assertFalse(execute("-3#=3"));
		assertFalse(execute("3#=-3"));
		assertTrue(execute("10>6,0"));
	}
	
	private boolean execute(String expression) {
		return new Condition(aux, expression).execute();
	}

}
