package br.com.webinside.runtime.validation;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class Number extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		String number = wiMap.get(var).trim();
		if (!number.equals("") && !number.equals("0") && Function.parseLong(number) == 0) {
			wiMap.remove(var);
			return getMessage("number");
		}	
		return "";
	}

}
