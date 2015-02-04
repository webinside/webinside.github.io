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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import br.com.webinside.runtime.util.WIMap;

public class WIMapTest extends TestCase {

    public void testDelimiter() {
        WIMap map3 = create(',', true);
        map3.setChangedKeys(true);
        map3.put("Author", "Autor");
        map3.put("PVT,author", "Autor Privado");
        assertTrue(map3.get("author").equals(""));
        Map intMap = ((WIMap) map3.getObj("PVT,")).getInternalMap();
        assertTrue(intMap.containsKey("author"));
    }

    public void testCloneMe() {
        WIMap map1 = create('.', false);
        map1.setChangedKeys(true);
        WIMap map2 = map1.cloneMe();
        assertFalse(map1 == map2);
        assertTrue(map2.getDelimiter() == '.');
        assertTrue(!map2.isSensitive());
        assertTrue(map2.getChangedKeys().size() == 0);
        assertTrue(map2.get("TEXT").equals("texto simples"));
        assertTrue(map2.get("pvt.author").equals("Autor Privado"));
    }

    public void testPutAll() {
        WIMap map1 = new WIMap();
        map1.setChangedKeys(true);
        Map map2 = new HashMap();
        map2.put("tmp.list", new HashMap());
        map2.put("tmp.text", "full text");
        map2.put("text", null);
        map1.putAll(map2);
        assertTrue(map1.get("tmp.text").equals("full text"));
        assertTrue((HashMap) map1.getObj("tmp.list") != null);
        assertTrue(map1.keySet().size() == 2);
        assertTrue(map1.getChangedKeys().size() == 2);
        WIMap map3 = new WIMap(map1.getAsMap());
        assertTrue(map1.getAsMap().equals(map3.getAsMap()));
    }

    public void testChangedKeys() {
        WIMap map1 = create('.', false);
        map1.setChangedKeys(true);
        map1.put("author", "Autor");
        map1.put("Author", "Autor Teste");
        map1.put("tmp.author", "Autor temp");
        map1.remove("tmp.text");
        map1.remove("tmp.author");
        assertTrue(map1.getChangedKeys().size() == 3);
        map1.putObj("tmp.", new WIMap());
        WIMap map3 = new WIMap();
        map3.setChangedKeys(true);
        map3.put("tmp.nome.end", "rua");
        map3.remove("tmp.nome.end3");
        assertTrue(map3.getChangedKeys().size() == 1);
        map1.setChangedKeys(false);
        assertTrue(map1.getChangedKeys().size() == 0);
    }

    public void testPutObj() {
        WIMap map = new WIMap();
        map.putObj(null, null);
        map.putObj("extra", null);
        map.putObj("extra text", "full text");
        assertTrue(map.get("extratext").equals("full text"));
        map.putObj(".text", "full text");
        assertTrue(map.getInternalMap().keySet().size() == 1);
        map.putObj("extra.", "full text");
        assertTrue(map.getInternalMap().keySet().size() == 1);
        map.putObj("extra.text", "full text");
        map.putObj("extra.text2", "full text2");
        Map intMap = ((WIMap) map.getObj("EXTRA.")).getInternalMap();
        assertTrue(intMap.keySet().size() == 2);
    }

    public void testGetObj() {
        WIMap map = create('.', false);
        assertTrue(map.getObj(null) == null);
        assertTrue(map.getObj("tmp.other") == null);
        assertTrue(map.getObj("Text").equals("texto simples"));
        assertTrue(map.getObj(".text") == null);
        assertTrue(map.getObj("document.text") == null);
        assertTrue(map.getObj("document.") == null);
        assertTrue(map.getObj("pvt.author").equals("Autor Privado"));
        assertTrue(map.get("document.").equals(""));
        assertTrue(map.get("tmp.").equals("[object]"));
    }

    public void testContainsKey() {
        WIMap map = create('.', false);
        assertFalse(map.containsKey(null));
        assertFalse(map.containsKey("document"));
        assertTrue(map.containsKey("text"));
        assertTrue(map.containsKey("tmp."));
        assertFalse(map.containsKey("document."));
        assertFalse(map.containsKey(".author"));
        assertFalse(map.containsKey("document.author"));
        assertTrue(map.containsKey("pvt.author"));
    }

    public void testRemove() {
        WIMap map = create('.', false);
        map.setChangedKeys(true);
        assertTrue(map.containsKey("text"));
        map.remove("text");
        assertFalse(map.containsKey("text"));
        map.remove("tmp.");
        assertFalse(map.containsKey("tmp.author"));
        map.remove("pvt.text");
        assertFalse(map.containsKey("pvt.text"));
        assertTrue(map.containsKey("pvt.author"));
    }

    public void testGetAsMap() {
        WIMap map1 = create('.', false);
        Map map2 = map1.getAsMap();
        assertTrue(map2.get("author") == null);
        assertTrue(map2.get("text").equals("texto simples"));
        assertTrue(map2.get("tmp.").equals("[object]"));
        assertTrue(map2.get("tmp.text").equals("texto temporario"));
    }

    public void testKeySet() {
        WIMap map1 = create('.', false);
        Set keys = map1.keySet();
        assertTrue(keys.contains("text"));
        assertTrue(keys.contains("tmp.text"));
        assertTrue(keys.contains("pvt.author"));
    }

    public void testGetAsText() {
        WIMap map = new WIMap();
        map.put("text", "initial text");
        map.put("name", "initial name");
        map.put("tmp.text", "temporary \r\n text initial");
        map.put("tmp.text.value", "the value of text");
        map.put("xxx", "final value");
        String text = map.getAsText("", true);
        assertTrue(text.indexOf("name =") > -1);
        assertTrue(text.indexOf("text =") > -1);
        assertTrue(text.indexOf("tmp.text =") == -1);
        text = map.getAsText("tmp.", true);
        assertTrue(text.equals(map.getAsText("tmp", true)));
        assertTrue(text.indexOf("tmp.text =") > -1);
        assertTrue(text.indexOf("tmp.text.value =") == -1);
        assertTrue(map.getAsText("pvt*", true).equals(""));
        text = map.getAsText("tmp.*", true);
        assertTrue(text.equals(map.getAsText("tmp*", true)));
        assertTrue(text.indexOf("tmp.text.value =") > -1);
        text = map.getAsText();
        assertTrue(text.indexOf("name =") > -1);
        assertTrue(text.indexOf("text =") > -1);
        assertTrue(text.indexOf("tmp.text =") > -1);
        assertTrue(text.indexOf("tmp.text.value =") > -1);
    }

    private WIMap create(char delimiter, boolean sensitive) {
        WIMap map = new WIMap(delimiter, sensitive);
        map.put("the\r\rkey", "empty");
        map.put("text", "texto simples");
        map.put("tmp.text", "texto temporario");
        map.put("tmp.Author", "autor do texto");
        map.put("pvt.text", "texto Privado");
        map.put("PVT.author", "Autor Privado");
        return map;
    }
    
}
