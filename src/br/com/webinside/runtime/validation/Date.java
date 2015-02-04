package br.com.webinside.runtime.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.WIMap;

public class Date extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		String date = wiMap.get(var);
		if (!date.equals("")) {
			boolean ok = false;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    if (date.length() == sdf.toPattern().length()) {
			    sdf.setLenient(false);
			    try {
			    	sdf.parse(date);
			    	ok = true;
			    } catch (ParseException pe) {
			    	// ignorado
			    }
		    } 
			if (!ok) {
				wiMap.remove(var);
				return getMessage("date");
			}
		}	
		return "";
	}
}
