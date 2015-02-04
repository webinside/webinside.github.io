package br.com.webinside.runtime.function.sv;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SVNodeKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> keys;
	private Set<String> keyUsed;
	
	public SVNodeKey() {
		keys = Collections.synchronizedMap(new LinkedHashMap());
		keyUsed = Collections.synchronizedSet(new LinkedHashSet());
	}
	
	public void add(String key, String value) {
		keys.put(key, value);
		keyUsed.add(key);
	}

	public String get(String key) {
		if (keys.containsKey(key)) {
			return keys.get(key);
		}
		return "";
	}

	public Set ignore() {
		keyUsed.clear();
		return new HashSet(keys.values());
	}
	
	public Set clear() {
		Set valueSet = new HashSet();
    	for (String key : new HashSet<String>(keys.keySet())) {
    		if (keyUsed.contains(key)) {
    			valueSet.add(keys.get(key));
    		} else {
    			keys.remove(key);
    		}
		}
    	keyUsed.clear();
    	return valueSet;
	}

	public boolean isEmpty() {
		return keys.size() == 0;
	}

	@Override
	public String toString() {
		return keys.toString();
	}
	
}
