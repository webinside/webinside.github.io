package br.com.webinside.runtime.integration;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class Validator {
	
	private static Properties aliases;
	
	private Validator() { }
	
	public static void validate(ExecuteParams wiParams) {
		if (aliases == null) {
			loadAliases(wiParams);
		}
		Page page = wiParams.getPage();
		WIMap wiMap = wiParams.getWIMap();
		Map<String, String> messages = new LinkedHashMap<String, String>();
		Iterator it = page.getValidationsMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = 
				(Map.Entry<String, String>) it.next();
			String var = entry.getKey().trim();
			String validations = entry.getValue();
			try {
				if (wiMap.containsKey(var)) {
					wiMap.put(var, wiMap.get(var).trim());
				}	
				String msg = validate(wiParams, var, validations);
				if (!msg.equals("")) {
					wiMap.put(var + ".invalid", msg);
					messages.put(var, msg);
				}	
			} catch (Exception err) {
				wiMap.put(var + ".invalid", err.getMessage());
				messages.put(var, err.getMessage());
		    	wiParams.getErrorLog().write("Validation", "execute", err);
		    	err.printStackTrace();
			}
		}
		if (!messages.isEmpty()) {
			wiMap.put("wi.validation.ok", "false");
			StringBuffer mStr = new StringBuffer();
			Iterator mit = messages.entrySet().iterator();
			while (mit.hasNext()) {
				Map.Entry<String, String> entry = 
					(Map.Entry<String, String>) mit.next();
				mStr.append("<li class=\"validationMessage\" ");
				mStr.append("ref=\"" + entry.getKey() + "\">");
				mStr.append(entry.getValue());
				mStr.append("</li>\n");
			}
			wiMap.put("wi.validation.messages", mStr.toString().trim());
		} else {
			wiMap.put("wi.validation.ok", "true");
		}
	}
	
	private static String validate(ExecuteParams wiParams, 
			String var, String validations) throws Exception {
        List<String> validationList = 
        	IntFunction.tokenizer(validations, ";", false);
        for (String validation : validationList) {
        	if (validation.equals("")) continue;
            int last = validation.lastIndexOf(")") - 1;
            if (last < 0) last = validation.length();
            String args =
                StringA.mid(validation, validation.indexOf("(") + 1, last);
            if (validation.indexOf("(") == -1) args = "";
            String valName = StringA.piece(validation, "(", 1).trim();
        	String valClass = aliases.getProperty(valName.toLowerCase());
        	if (valClass == null) valClass = "";
	        if (!valClass.equals("")) {
				List<String> argsList = 
					IntFunction.tokenizer(args, ",", true);
				Class cls = null;
				try {
					ClassLoader classLoader = 
						ExecuteParams.getThreadClassLoader();
			    	if (classLoader == null) {
			    		String msg = "ExecuteParams.getThreadClassLoader()";
			    		throw new NullPointerException(msg);
			    	}
					cls = classLoader.loadClass(valClass);
		            InterfaceValidation intVal = 
		            	(InterfaceValidation) cls.newInstance();
		            String[] argsArray =
		                (String[]) argsList.toArray(new String[argsList.size()]);
		            String msg = intVal.execute(wiParams, var, argsArray);
		            if (!msg.equals("")) return msg;
				} catch (ClassNotFoundException cnfe) {
		        	String msg = "Validation class \"" + valClass + "\" not found";
	        		throw new Exception(msg);
				}
	        } else {
	        	String msg = "Validation \"" + valName + "\" not registered";
        		throw new Exception(msg);
	        }
		}
        return "";
	}

	private static Properties loadAliases(ExecuteParams wiParams) {
		aliases = new Properties();
		try {
			String valWI = "br/com/webinside/runtime/validation-wi.properties";
			aliases = IntFunction.loadProperties(null, valWI);
			aliases = IntFunction.loadProperties(aliases, 
					"validation-lineweb.properties");
			aliases = IntFunction.loadProperties(aliases, 
					"validation-user.properties");
	    } catch (Exception err) {
	    	wiParams.getErrorLog().write("Validation", "loadClasses", err);
	    	err.printStackTrace();
	    }
	    return aliases;
	}
		
	public static boolean isDisabledCondition(WIMap wiMap, String condition) {
        if (wiMap != null && condition != null) {
        	if (wiMap.get("wi.block.cond").equals("false")) {
        		return true;
        	}
        	boolean invalid = wiMap.get("wi.validation.ok").equals("false");
        	if (wiMap.get("tmp.action").equalsIgnoreCase("validate")) {
        		// rerender no modo validate não deve processar a logica
        		invalid = true;
        	}
    		if (invalid && condition.indexOf("|wi.novalidation|") == -1) {
    			return true;
    		}	
        }
		return false;
	}
		
}
