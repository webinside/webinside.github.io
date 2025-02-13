package br.com.webinside.runtime.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class CrossContextFactory {
	
	private static Map mjavaStaticHosts;
    private static Map mjavaPersistent;
	
	public static CrossContext getInstance() {
		try {
			Class c = Class.forName("br.com.webinside.runtime.util.CrossContextImpl");
			return (CrossContext) c.getConstructor().newInstance();
		} catch (Throwable t) {
			return null;
		}
	}
	
	public static synchronized Map mjavaStaticHosts() {
		if (mjavaStaticHosts == null) {
			CrossContext cross = getInstance();
			if (cross != null) {
				mjavaStaticHosts = cross.mjavaStaticHosts();
			} else {
				mjavaStaticHosts = Collections.synchronizedMap(new HashMap());
			}
		}
		return mjavaStaticHosts;
	}

	public static synchronized Map mjavaPersistent() {
		if (mjavaPersistent == null) {
			CrossContext cross = getInstance();
			if (cross != null) {
				mjavaPersistent = cross.mjavaPersistent();
			} else {
				mjavaPersistent = Collections.synchronizedMap(new HashMap());
			}
		}
		return mjavaPersistent;
	}
	
}
