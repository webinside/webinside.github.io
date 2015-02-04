package br.com.webinside.runtime.function;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.Function;

public class WebResource extends AbstractFunction {
	
	private static final Map<String, String> wrMap = new HashMap(); 

	@Override
	public String execute(String[] args) throws UserException {
		if (args.length == 0) return "";
		String fname = args[0];
		String proj = getWiMap().get("wi.proj.id");
		if (!fname.startsWith("/")) {
			fname = "/" + proj + "/" + fname;
		}
		File file = new File(getWiMap().get("wi.webapps.path"), fname);
		if (file.isFile()) {
			String key = file.getAbsolutePath();
			long time = file.lastModified();
			String rnd = Function.randomKey(6);
			String value = wrMap.get(key);
			if (value != null) {
				String old = value.split(":")[0];
				if (old.equals(time+"")) {
					rnd = value.split(":")[1];
				}
			}
			wrMap.put(key, time + ":" + rnd);
			return fname + "?rnd=" + rnd;
		}
		return "";
	}

}
