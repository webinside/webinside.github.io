package br.com.webinside.runtime.util;

public abstract class Compat {
	
	private static Object compatImpl; 
	
	public static void legacySession(WISession wiSession, String prjId, WIMap sessionMap) {
		if (getCompatImpl() instanceof Compat) {
			((Compat)compatImpl).session(wiSession, prjId, sessionMap);
		}
	}
	
	public abstract void session(WISession wiSession, String prjId, WIMap sessionMap);
	
	private static Object getCompatImpl() {
		if (compatImpl == null) {
			try {
				compatImpl = Class.forName("br.com.itx.engine.CompatImpl").getConstructor().newInstance();
			} catch (Throwable t) { 
				compatImpl = new Object();
			}
		}
		return compatImpl;
	}
	
}
