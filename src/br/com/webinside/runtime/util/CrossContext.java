package br.com.webinside.runtime.util;

import java.util.Map;

public interface CrossContext {
	
	// MJAVA
	
	public Map mjavaStaticHosts(); 
	
	public Map mjavaPersistent();
	
	// SingleSignOn
	
	public void createSSO(String token, String prj, Map map);

	public void removeSSO(String token, String prj);
	
	public Map getSSO(String token);

}
