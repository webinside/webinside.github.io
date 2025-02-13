package br.com.webinside.runtime.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CrossContextImpl implements CrossContext {
	
	private static Map mjavaStaticHosts = 
	    	Collections.synchronizedMap(new HashMap());

    private static Map mjavaPersistent = 
    	Collections.synchronizedMap(new HashMap());
	
	private static Map<String, SingleSignOn> tokensSSO = 
    	Collections.synchronizedMap(new HashMap());
	
	public Map mjavaStaticHosts() {
		return mjavaStaticHosts;
	}

	public Map mjavaPersistent() {
		return mjavaPersistent;
	}
	
	public void createSSO(String token, String prj, Map map) {
		tokensSSO.put(token, new SingleSignOn(prj, map));
	}

	public void removeSSO(String token, String prj) {
		SingleSignOn sso = tokensSSO.get(token);
		if (sso != null && sso.prj.equals(prj)) {
			tokensSSO.remove(token);
		}
	}

	public Map getSSO(String token) {
		SingleSignOn sso = tokensSSO.get(token);
		if (sso != null) return sso.map;
		return null;
	}

	class SingleSignOn {
		
		private String prj;
		private Map map;
		
		SingleSignOn(String prj, Map map) {
			this.prj = prj;
			this.map = map;
		}
		
	}
	
}
