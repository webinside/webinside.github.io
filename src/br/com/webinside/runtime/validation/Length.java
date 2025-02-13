package br.com.webinside.runtime.validation;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class Length extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		if (args.length == 0) {
			throw new UserException(getMessage("length_empty"));
		} else if (args.length == 1) {
			return checkMax(wiMap, var, args[0]);
		} else {
			int min = Function.parseInt(args[0]);
			if (wiMap.get(var).length() < min) {
				wiMap.remove(var);
				return getMessage("length_min", min);
			}
			return checkMax(wiMap, var, args[1]);
		}	
	}
	
	private String checkMax(WIMap wiMap, String var, String value) {
		int max = Function.parseInt(value);
		if (wiMap.get(var).length() > max) {
			wiMap.remove(var);
			return getMessage("length_max", max);
		}
		return "";
	}

}
