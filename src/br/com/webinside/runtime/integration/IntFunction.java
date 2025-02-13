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

package br.com.webinside.runtime.integration;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.core.RtmFunction;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.function.sv.SVNode;
import br.com.webinside.runtime.net.ssl.SSLSocketFactory;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.12 $
 */
public class IntFunction {

    private IntFunction() { }

    public static String executeFunctionWI(String fullFunction, 
    		ExecuteParams wiParams, WIMap wiMap) throws Exception {
        if (wiMap == null) wiMap = wiParams.getWIMap();
        String fname = StringA.piece(fullFunction, "(", 1).toLowerCase();
        if(fname.startsWith("wi.listmeta")) {
            throw new UserException("Function \"wi.listmeta\" or " +
            		"\"wi.listmetastruct\" not allowed in page");
        } 
        int last = fullFunction.lastIndexOf(")") - 1;
        if (last < 0) last = fullFunction.length();
        String args =
            StringA.mid(fullFunction, fullFunction.indexOf("(") + 1, last);
        if (fullFunction.indexOf("(") == -1) args = "";
        List argsList = tokenizer(args, ",", true);
        String funcClass = "";
        if (wiMap.getObj("wi.functions") != null) {
            funcClass = (String)((Map) wiMap.getObj("wi.functions")).get(fname);
        }
        if (funcClass == null) {
            funcClass = "";
        }
        if (fname.equals("wi.i18n")) {
            I18N i18n = new I18N();
            if (wiMap.containsKey("wi.i18n")) {
                i18n = (I18N) wiMap.getObj("wi.i18n");
            }
            if (argsList.size() > 0) {
                return i18n.get((String) argsList.get(0));
            } else {
                return "";
            }
        }
        if (!funcClass.equals("")) {
        	if (ExecuteParams.get().getClassLoader() == null) {
        		String msg = "ExecuteParams.getThreadClassLoader()";
        		throw new NullPointerException(msg);
        	}
            Class cl = ExecuteParams.get().getClassLoader().loadClass(funcClass);
            String[] argsArray = (String[]) argsList.toArray(new String[argsList.size()]);
            String resp = "";
            if (IntFunction.useCompat(cl)) {
            	Class c = Class.forName("br.com.itx.engine.CompatImpl");
            	Method m = c.getMethod("function", Class.class, ExecuteParams.class, String[].class, WIMap.class);
            	resp = (String) m.invoke(c.getConstructor().newInstance(), cl, wiParams, argsArray, wiMap);
            } else {
                InterfaceFunction function = (InterfaceFunction) cl.getConstructor().newInstance();
                function.setWiMap(wiMap);
                resp = function.execute(wiParams, argsArray);
            }
            return resp;
        } else {
        	String msg = "Function \"" + fname + "\" not registered";
        	if (wiMap.get("wi.builder").equals("true")) {
        		throw new UserException(msg);
        	} else {
        		throw new Exception(msg);
        	}
        }
    }

    /**
     * Tokeniza a linha pelo delimitador e o ignora se ele estiver
     * com escape ou dentro de outra função
     */
    public static List<String> tokenizer(String line, String delim, boolean filter) {
    	List<String> resp = new ArrayList<String>();
    	int start = 0;
	    int next = line.indexOf(delim);
	    while (next > -1) {
	    	String subfunc = StringA.mid(line, start, next - 1);
	    	int ct1 = StringA.count(subfunc, "|$", true);
	    	int ct2 = StringA.count(subfunc, "$|", true);
	    	boolean go = (ct1 == ct2); 
	    	if ((next > 0) && (line.charAt(next - 1) == '\\')) {
	    		go = false;
	    	}
	        if (go) {
	            String aux = StringA.mid(line, start, next - 1).trim();
	            if (filter) {
	            	aux = filterParams(aux);
	            }	
	            resp.add(aux);
	            start = next + 1;
	            next = line.indexOf(delim, start);
	        } else {
	            next = line.indexOf(delim, next + 1);
	        }
	    }
	    String comp = StringA.mid(line, start, line.length()).trim();
	    if (filter) {
	    	comp = filterParams(comp);
	    }	
	    resp.add(comp);
	    return resp;
    }
    
    private static String filterParams(String text) {
    	String[] symbs = {"\\s", "\\,"};
    	for (int i=0;i<symbs.length;i++) {
    		String symb = symbs[i];
    		int from = 0;
    		int pos = 0;
    		while ((pos = text.indexOf(symb, from)) > -1) {
    			String before = StringA.mid(text, 0, pos - 1);
    			String after = StringA.mid(text, pos + 2, text.length());
            	int ct1 = StringA.count(before, "|$", true);
            	int ct2 = StringA.count(before, "$|", true);
    			if (ct1 == ct2) {
    				if (symb.equals("\\s")) {
    					text = before + " " + after;
    				} else if (symb.equals("\\,")) {
    					text = before + "," + after;
    				}
    			}
   				from = pos + 1;
    		}			
    	}
    	return text;    	
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     */
    public static void setSystemOut(String file) {
        try {
            FileOutputStream fl = new FileOutputStream(file);
            PrintStream out = new PrintStream(fl, true);
            System.setOut(out);
        } catch (Exception e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param pars DOCUMENT ME!
     */
    public static void importParameters(ExecuteParams wiParams, String pars) {
    	importParameters(wiParams.getWIMap(), pars);
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param pars DOCUMENT ME!
     */
    public static void importParameters(WIMap wiMap, String pars) {
        if ((pars == null) || (pars.indexOf("?") == -1)) {
            return;
        }
        pars = StringA.mid(pars, pars.indexOf("?") + 1, pars.length());
        ProducerParam par = new ProducerParam();
        par.setWIMap(wiMap);
        StringA params = new StringA(pars);
        StringA aux = new StringA();
        for (int i = 0; i <= params.count('&'); i++) {
            String strparam = params.piece("&", i + 1);
            aux.setCgi(StringA.piece(strparam, "=", 1));
            String key = aux.toString().toLowerCase().trim();
            aux.setCgi(StringA.piece(strparam, "=", 2));
            String value = aux.toString();
            if (key.startsWith("wi.")) {
                continue;
            }
            if (key.startsWith("pvt.")) {
                continue;
            }
            if (key.startsWith("combo.") && key.endsWith(".selected")) {
            	value = "|" + value + "|";
            }
            par.setInput(value);
            new Producer(par).execute();
            wiMap.put(key, par.getOutput().trim());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param project DOCUMENT ME!
     */
    public static void loadInitParams(WIMap wiMap, AbstractProject project) {
        loadInitParams(wiMap, project, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param project DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public static void loadInitParams(WIMap wiMap, AbstractProject project,
        String profile) {
        if ((wiMap == null) || (project == null)) {
            return;
        }
        if (profile == null) {
            profile = "";
        }
        Iterator it = project.getInitParamIds(profile).iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            String value = project.getInitParam(id, profile);
            wiMap.put(id, Producer.execute(wiMap, value));
        }
    }

    public static Store getStoreConnection(WIMap wiMap, Host host) 
		throws MessagingException {
		ProducerParam prod = new ProducerParam();
		prod.setWIMap(wiMap);
    	Producer producer = new Producer();
		producer.setParam(prod);
		prod.setInput(host.getUser());
		producer.execute();
		String user = prod.getOutput().trim();
		prod.setInput(host.getPass());
		producer.execute();
		String pass = prod.getOutput().trim();
		prod.setInput(host.getAddress());
		producer.execute();
		String strhost = prod.getOutput().trim();
		prod.setInput(host.getPort());
		producer.execute();
		String port = prod.getOutput().trim();
		String protocol = host.getProtocol().toLowerCase();
		boolean secure = false;
		if (protocol.endsWith("-ssl")) {
			secure = true;
			protocol = StringA.change(protocol, "-ssl", "");
		}
		Properties props = new Properties();
		if (!port.equals("")) {
			props.put("mail." + protocol + ".port", port);
		}
		if (secure) {
        	String SSL_FACTORY = SSLSocketFactory.class.getName();
        	props.put("mail." + protocol + ".starttls.enable","true");
        	if (port.equals("")) {
        		if (protocol.equals("pop3")) port = "995";
        		if (protocol.equals("imap")) port = "993";
        	}
        	props.put("mail." + protocol + ".socketFactory.port", port); 
        	props.put("mail." + protocol + ".socketFactory.class", SSL_FACTORY); 
        	props.put("mail." + protocol + ".socketFactory.fallback", "false");  
		}
		Session session = Session.getInstance(props, null);            
		Store store = session.getStore(protocol);
		store.connect(strhost, user, pass);
		if (!store.isConnected()) {
		    return null;
		}
		return store;
	}
    
    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param obj DOCUMENT ME!
     */
    public static void killObjAndVector(WIMap wiMap, String obj) {
        if ((wiMap == null) || (obj == null)) {
            return;
        }
        if (!obj.endsWith(".")) {
            wiMap.remove(obj);
        } else if (obj.indexOf(".") > -1) {
            int last1 = obj.lastIndexOf(".");
            int last2 = obj.lastIndexOf(".", last1 - 1);
            String parent = StringA.mid(obj, 0, last2 - 1);
            String name = StringA.mid(obj, last2 + 1, last1 - 1);
            WIMap auxMap = null;
            try {
                if (!parent.equals("")) {
                    auxMap = (WIMap) wiMap.getObj(parent + ".");
                }
            } catch (Exception err) {
            	// ignorado.
            }
            if (auxMap == null) {
                auxMap = wiMap;
            }
            Set keys = new HashSet(auxMap.getInternalMap().keySet());
            for (Iterator e = keys.iterator(); e.hasNext();) {
                String key = (String) e.next();
                boolean rem = false;
                if (key.toLowerCase().equals(name.toLowerCase() + ".")) {
                    rem = true;
                }
                if (key.toLowerCase().startsWith(name.toLowerCase() + "[")) {
                    if (key.toLowerCase().endsWith("].")) {
                        rem = true;
                    }
                }
                if (rem) {
                    if (parent.equals("")) {
                    	wiMap.remove(key);
                    } else {
                    	wiMap.remove(parent + "." + key);                    	
                    }	
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     * @param sContext DOCUMENT ME!
     * @param projId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static AbstractProject getProject(HttpServletRequest request,
        HttpServletResponse response, ServletContext sContext, String projId) {
        request.setAttribute("wiProject", projId);
        ExecuteParams wiParams = new ExecuteParams(request, response, sContext);
        request.setAttribute("wiParams", wiParams);
        try {
            sContext.getRequestDispatcher("/project.jsp").include(request, response);
        } catch (Exception err) {
            System.err.println(IntFunction.class.getName() + ": " + err);
        }
        return wiParams.getProject();
    }
        
    public static Locale getLocale(WIMap wiMap) {
		Locale locale = Locale.getDefault();
		if (wiMap != null) {
			String loc = wiMap.get("wi.locale");
			if (!loc.equals("")) {
				loc = StringA.changeChars(loc, "-", "_");
				String p1 = StringA.piece(loc, "_", 1);
				String p2 = StringA.piece(loc, "_", 2);
				locale = new Locale(p1, p2);
			}
		}	
		return locale;
    }

	public static Properties loadProperties(Properties props, String propsName) 
	throws Exception {
		ClassLoader classLoader = ExecuteParams.get().getClassLoader();
    	if (classLoader == null) {
    		String msg = "ExecuteParams.getThreadClassLoader()";
    		throw new NullPointerException(msg);
    	}
		if (props == null) {
			props = new Properties();
		}
        URL url = classLoader.getResource(propsName);
        if (url == null) {
        	return props;
        }
        InputStream is = url.openStream();
        props.load(is);    
        is.close();
        return props;
	}

	public static Map getSVMap(WISession wisession) {
    	Map svMap = (Map) wisession.getAttribute("WISecureVars");
		if (svMap == null) {
			synchronized (wisession.getHttpSession()) {
				svMap = (Map) wisession.getAttribute("WISecureVars");
				if (svMap == null) {
		    		svMap = Collections.synchronizedMap(new HashMap());
		    		wisession.setAttribute("WISecureVars", svMap);
				}
			}
		}
    	return svMap;
	}

	public static SVNode getSVNode(WISession session, String var) {
		Map svMap = getSVMap(session);
    	SVNode svNode = (SVNode) svMap.get(var);
    	if (svNode == null) {
    		svNode = new SVNode();
    		svMap.put(var, svNode);
    	}
    	return svNode;
	}

	public static void clearSVMap(WISession wisession) {
		if (!wisession.isValid()) return;
    	Map<String,SVNode> svMap = (Map) wisession.getAttribute("WISecureVars");
    	if (svMap == null) return;
    	for (String key : new HashSet<String>(svMap.keySet())) {
			SVNode svNode = svMap.get(key);
			if (svNode != null) {
				svNode.clear();
				if (svNode.isEmpty()) {
					svMap.remove(key);
				}
			}
		}
	}
	
    public static boolean isSecureVar(Set<String> secureVars, String var) {
    	for (String sv : secureVars) {
			if (sv.equals(var)) return true;
			int pos = sv.indexOf("*");
			if (pos > -1) {
				String p1 = StringA.mid(sv, 0, pos - 1);
				String p2 = StringA.mid(sv, pos + 1, sv.length());
				if (var.startsWith(p1) && var.endsWith(p2)) {
					return true;
				}
			}
		}
    	return false;
    }
	
	public static boolean useCompat(Class cl) {
		String cname = "";
		if (cl.getSuperclass() != null) {
			cname = cl.getSuperclass().getName();
		}
		Class[] interfaces = cl.getInterfaces(); 
		String iname = "";
		if (interfaces != null && interfaces.length > 0) {
			iname = interfaces[0].getName();
		}	
		String key = "br.com.itx.integration";
		if (iname.indexOf(key) > -1 && cname.indexOf(key) == -1) {
			return true;
		}
		return false;
	}
	
    public static void loginRoles(ExecuteParams wiParams) {
    	AbstractProject project = wiParams.getProject();
    	WIMap wiMap = wiParams.getWIMap();
    	if (project.getLoginRolesSql().equals("")) return;
        String dbalias = project.getLoginRolesDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            RtmFunction.databaseError(wiParams, dbalias);
            return;
        }
        db.setCharFilter(project.getLoginRolesSqlFilter(), "");
        ResultSet rs = null;
        try {
            rs = db.execute(project.getLoginRolesSql(), wiMap);
        } catch (Exception err) {
        	wiParams.getErrorLog().write("IntFunction", "Roles", err);
        }
		wiMap.put("pvt.login.role[1].name", "wilogin");
		wiMap.put("pvt.login.role.size()", 1);
		wiMap.remove("pvt.login.role.modules");
		wiMap.remove("pvt.wimenu");
		Set<String> grpList = new LinkedHashSet();
        if (rs != null) {
            int pos = 0;
            while ((pos = rs.next()) > 0) {
				wiMap.put("pvt.login.role[" + (pos + 1) + "].name", rs.column(1));
				wiMap.put("pvt.login.role.size()", (pos + 1));
				if (rs.columnNames().length>1) {
					String col2 = rs.column(2).trim();
					if (!col2.equals("")) grpList.add(col2);
				}
            }
            if (grpList.size() > 0) {
            	StringBuilder sb = new StringBuilder();
            	for (String col2 : grpList) {
					if (sb.length() > 1) sb.append(",");
					sb.append(col2);
				}
        		wiMap.put("pvt.login.role.modules", sb.toString());
            }
        } else {
            wiMap.put("wi.sql.query", db.getExecutedSQL());
            String sqlmsg = db.getErrorMessage();
            wiMap.put("wi.sql.error", sqlmsg.toString());
            wiMap.put("wi.sql.msg", StringA.piece(sqlmsg, ")", 2, 0).trim());
        }
    }	

    public static void setMessageError(WIMap wiMap, String var, String msg) {
		if (var.equals("tmp.message") && wiMap.get("tmp.msg_error").equals("")) {
			wiMap.put("tmp.msg_error", msg);
		}
    }
    
}
