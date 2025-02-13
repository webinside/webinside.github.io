package br.com.webinside.runtime.function;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.simple.JSONValue;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class SecureLinkManager {

	private static Map<String, SLNode> sessionsMap = 
	    	Collections.synchronizedMap(new HashMap());	
	
	private static Properties slinkHosts;

	private SecureLinkManager() { }
	
	public static SLNode getSession(WIMap wiMap) {
		String session = wiMap.get("wi.session.id");
		SLNode slnode = (new SecureLinkManager()).new SLNode();
		if (sessionsMap.containsKey(session)) {
			slnode = sessionsMap.get(session);
		} else {
			sessionsMap.put(session, slnode);
		}
        WIMap auxMap = new WIMap();
        auxMap.putObj("tmp.", wiMap.getObj("tmp."));
        auxMap.putObj("pvt.", wiMap.getObj("pvt."));
        auxMap.remove("pvt.wimenu");
		slnode.wiMap = auxMap;
		return slnode;
	}

	public static void removeSession(String session) {
		sessionsMap.remove(session);
	}	
	
	public static String getTokenJson(String token) {
		for (SLNode slnode : sessionsMap.values()) {
			if (slnode.hasToken(token)) {
				return slnode.getTokenJson(token);
			}
		}
		return "";
	}
	
	public static void importToken(String slink, WIMap wiMap) {
		String classname = SecureLinkManager.class.getName();
		ErrorLog log = ExecuteParams.get().getErrorLog();
		if (slinkHosts == null) {
			try {
				slinkHosts = IntFunction.loadProperties(null, "wisecurelink.properties");
			} catch (Exception e) {
				log.write(classname, "Error Loading Properties", e);
				slinkHosts = new Properties();
			}
		}	
		try {
			String server = StringA.piece(slink, "/", 1);
			String projId = StringA.piece(slink, "/", 2);
			String token = StringA.piece(slink, "/", 3);
			String propUrl = slinkHosts.getProperty(server+ "/" + projId);
			if (propUrl == null) {
				propUrl = slinkHosts.getProperty(server);
			}
			if (propUrl == null && slinkHosts.isEmpty()) {
				propUrl = wiMap.get("wi.server.localhost") +  ",WISecureLink.wsp";
			}
			if (propUrl == null) {
				String msg = "Host " + server + " not found in wisecurelink.properties";
				throw new UnknownHostException(msg);
			}
			String host = StringA.piece(propUrl, ",", 1);
			String page = StringA.piece(propUrl, ",", 2);
			String url = host + "/" + projId + "/" + page;
			HttpClient httpclient = new HttpClient();
			GetMethod httpget = new GetMethod(url + "?token=" + token);
			HttpMethodRetryHandler retry = new DefaultHttpMethodRetryHandler(2, false);
			httpget.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retry);
		    int status = httpclient.executeMethod(httpget);
			if (status == HttpStatus.SC_OK) {
				String json = httpget.getResponseBodyAsString();
				if (!json.trim().equals("")) {
					Map map = (Map) JSONValue.parse(json);
					wiMap.putAll(map);
					wiMap.put("wi.slink", "ok");
				} else {
					log.write(classname, "Empty SecureLink Response", "Server " + url);
				}
			}
		} catch (Exception e) {
			log.write(classname, "Error Requesting", e);
		}
	}
	
	public class SLNode {
		
		private WIMap wiMap = null;
		private List<String> maskList = Collections.synchronizedList(new ArrayList());
		private List<String> tokenList = Collections.synchronizedList(new ArrayList());
		
		public boolean hasToken(String token) {
			return tokenList.contains(token);
		}

		public String addTokenMask(String mask) {
			if (mask.trim().equals("")) mask = "pvt.*";
			mask = StringA.changeChars(mask, " ", "").trim();
			if (maskList.contains(mask)) {
				return tokenList.get(maskList.indexOf(mask));  
			}
			String token = Function.randomKey(15);
			maskList.add(mask);
			tokenList.add(token);
			return token;
		}
		
		public String getTokenJson(String token) {
			if (!tokenList.contains(token)) return "";
			WIMap auxMap = new WIMap();
			String mask = maskList.get(tokenList.indexOf(token)); 
			StringTokenizer st = new StringTokenizer(mask, "&");
			while (st.hasMoreTokens()) {
				String m = st.nextToken().trim();
				if (m.equals("pvt.*")) {
					auxMap.putObj("pvt.", wiMap.getObj("pvt."));
				} else if (m.equals("tmp.*")) {
					auxMap.putObj("tmp.", wiMap.getObj("tmp."));
				} else {
					String value = wiMap.get(m).trim();
					if (!value.equals("")) {
						auxMap.put(m, value);
					}
				}	
			}
			return JSONValue.toJSONString(auxMap.getAsMap());
		}
	}
}
