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

package br.com.webinside.test;

import junit.framework.TestCase;
import br.com.webinside.runtime.util.StringAHtml;

public class StringAHtmlTest extends TestCase {

	public void testHtmlToText() {
		String text = "&lt;texto&gt;cora&ccedil;&atilde;o"
				+ " &amp; estrela&lt;/texto&gt;";
		String result = "<texto>coração & estrela</texto>";
		String resultc = "&lt;texto&gt;coração &amp; estrela&lt;/texto&gt;";
		String results = "<texto>cora&ccedil;&atilde;o & estrela</texto>";
		assertTrue(StringAHtml.htmlToText(text, false, false).equals(
				text.toString()));
		assertTrue(StringAHtml.htmlToText(text, true, false).equals(resultc));
		assertTrue(StringAHtml.htmlToText(text, false, true).equals(results));
		assertTrue(StringAHtml.htmlToText(text, true, true).equals(result));
		String text2 = "nome & ; & &maria;";
		assertTrue(StringAHtml.htmlToText(text2, true, true).equals(text2));
	}

	public void testTextToHtml() {
		String text = "<texto>coração & estrela</texto>";
		String result = "&lt;texto&gt;cora&ccedil;&atilde;o"
				+ "&nbsp;&amp;&nbsp;estrela&lt;/texto&gt;";
		String resultc = "<texto>cora&ccedil;&atilde;o" + " & estrela</texto>";
		String results = "&lt;texto&gt;coração"
				+ "&nbsp;&amp;&nbsp;estrela&lt;/texto&gt;";
		assertTrue(StringAHtml.textToHtml(text, false, false).equals(
				text.toString()));
		assertTrue(StringAHtml.textToHtml(text, true, false).equals(resultc));
		assertTrue(StringAHtml.textToHtml(text, false, true).equals(results));
		assertTrue(StringAHtml.textToHtml(text, true, true).equals(result));
	}

}
