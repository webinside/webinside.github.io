package br.com.webinside.runtime.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.WIMap;

public class Email extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		String email = wiMap.get(var);
		if (!email.equals("")) {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(email);
			if (!m.matches()) {
				wiMap.remove(var);
				return getMessage("email");
			}
		}	
		return "";
	}

}
