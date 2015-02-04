package br.com.webinside.runtime.validation;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.WIMap;

public class Required extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		String value = wiMap.get(var);
		if (value.equals("") || value.equals("null")) {
			return getMessage("required");
		}
		return "";
	}

}
