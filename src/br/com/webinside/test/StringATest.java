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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import br.com.webinside.runtime.util.StringA;

public class StringATest extends TestCase {

    public void testMid() {
        String text = "abcde";
        assertTrue(StringA.mid(text, 1, 3).equals("bcd"));
        assertTrue(StringA.mid(text, -2, 2).equals("abc"));
        assertTrue(StringA.mid(text, 2, 20).equals("cde"));
        assertTrue(StringA.mid(text, 1, 1).equals("b"));
        assertTrue(StringA.mid(text, 2, 1).equals(""));
    }

    public void testLeft() {
        String text = "abcde";
        assertTrue(StringA.left(text, 0).equals(""));
        assertTrue(StringA.left(text, 2).equals("ab"));
        assertTrue(StringA.left(text, 9).equals("abcde"));
        assertTrue(StringA.left("ab", 5, "x").equals("abxxx"));
    }

    public void testRight() {
        String text = "abcde";
        assertTrue(StringA.right(text, 0).equals(""));
        assertTrue(StringA.right(text, 2).equals("de"));
        assertTrue(StringA.right(text, 9).equals("abcde"));
        assertTrue(StringA.right("ab", 5, "x").equals("xxxab"));
    }

    public void testPiece() {
        String text = "a,b,c,d,e";
        assertTrue(StringA.piece(text, ",", 2).equals("b"));
        assertTrue(StringA.piece(text, ",", 2, 4).equals("b,c,d"));
        String text2 = "aXbXcXdXe";
        assertTrue(StringA.piece(text2, "x", 2, 4, false).equals("bXcXd"));
        assertTrue(StringA.piece(text2, "x", 2, 4, true).equals(""));
        assertTrue(StringA.piece(text2, "x", 1, 2, true).equals(text2));
    }

    public void testPieceAsList() {
        String text = "a,b,c,d,e";
        assertTrue(StringA.pieceAsList(text, ",", 2, 2, true).size() == 1);
        assertTrue(StringA.pieceAsList(text, ",", 2, 4, true).size() == 3);
        String text2 = "aXbXcXdXe";
        assertTrue(StringA.pieceAsList(text2, "x", 2, 4, false).size() == 3);
        assertTrue(StringA.pieceAsList(text2, "x", 2, 4, true).size() == 0);
        assertTrue(StringA.pieceAsList(text2, "x", 1, 2, true).size() == 1);
    }

    public void testChangeChars() {
        String text = "geraldo";
        assertEquals(StringA.changeChars(text, "arg", "AR"), "eRAldo");
        assertEquals(StringA.changeChars(text, "ARg", "AR", false), "eRAldo");
        assertEquals(StringA.changeChars(text, "ARg", "AR", true), "eraldo");
    }

    public void testChange() {
        String text = "geraldo moraes";
        assertEquals(StringA.change(text, "aes", "AES"), "geraldo morAES");
        assertEquals(StringA.change(text, "AES", "AES", true), "geraldo moraes");
    }

    public void testCount() {
        String text = "geraldo moraes";
        assertTrue(StringA.count(text, 'e') == 2);
        assertTrue(StringA.count(text, "ld", true) == 1);
    }

    public void testFormatSubDir() {
		String text = "\\dir\\xxx";
		assertEquals(StringA.formatSubDir(text),"dir/xxx");
    }

    public void testChangeLinebreak() {
    	String text = "geraldo\r\nmoraes\r\n";
    	assertEquals(StringA.changeLinebreak(text,","),"geraldo,moraes");
    }

    public void testGetXml() {
    	StringA text = new StringA("geraldo <> moraes ");
    	assertEquals(text.getXml(),"geraldo &lt;&gt; moraes&nbsp;");
    }

    public void testSetXml() {
		StringA text = new StringA();
		text.setXml("geraldo &lt;&gt; moraes&nbsp;");
		assertEquals(text.toString(),"geraldo <> moraes ");
    	
    }
    
    public void testEquals() {
        StringA text1 = new StringA("text");
        StringA text2 = new StringA("text");
        Set set = new HashSet();
        assertTrue(text1.equals(text1));
        assertTrue(text1.equals(text2));
        assertTrue(text2.equals(text1));
        assertFalse(text1.equals("text"));
        assertTrue(set.add("text"));
        assertTrue(set.add(text1));
        assertFalse(set.add(text2));
        assertTrue(set.add(new StringBuffer("text")));
        set.remove("text");
        assertFalse(set.contains("text"));
        assertTrue(set.contains(text2));
    }
    
}
