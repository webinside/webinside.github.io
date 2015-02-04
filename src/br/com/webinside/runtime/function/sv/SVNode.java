package br.com.webinside.runtime.function.sv;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import br.com.webinside.runtime.util.Function;

public class SVNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String,SVNodeKey> pages;
	private Set<String> pageUsed;
	private Map<String,String> values;

	public SVNode() {
		pages = Collections.synchronizedMap(new LinkedHashMap());
		pageUsed = Collections.synchronizedSet(new LinkedHashSet());
		values = Collections.synchronizedMap(new LinkedHashMap());
	}

	public void addPage(String page) {
		pageUsed.add(page);
	}

	public String addValue(String page, String value) {
		SVNodeKey pageNode = pages.get(page);
    	if (pageNode == null) {
    		pageNode = new SVNodeKey();
    		pages.put(page, pageNode);
    	}
    	String key = Function.randomKey(10); 
		if (values.containsKey(value)) {
			key = values.get(value);
		} else {
			values.put(value, key);
		}
		pageNode.add(key, value);
		return key;
	}

	public String getValue(String key) {
    	for (String page : pages.keySet()) {
			SVNodeKey pageNode = pages.get(page);
			String value = pageNode.get(key);
			if (!value.equals("")) return value;
    	}
		return "";
	}
	
	public void clear() {
		Set valueSet = new HashSet();
    	for (String page : new HashSet<String>(pages.keySet())) {
    		SVNodeKey pageNode = pages.get(page);
    		if (pageUsed.contains(page)) {
        		valueSet.addAll(pageNode.clear());
        		if (pageNode.isEmpty()) pages.remove(page);
    		} else {	
        		valueSet.addAll(pageNode.ignore());
    		}
		}
    	for (String value : new HashSet<String>(values.keySet())) {
			if (!valueSet.contains(value)) values.remove(value);
		}
    	pageUsed.clear();
	}
	
	public boolean isEmpty() {
		return pages.size() == 0;
	}

	@Override
	public String toString() {
		return pages.toString();
	}
				
}
