package br.com.webinside.runtime.validation;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractValidation;
import br.com.webinside.runtime.util.WIMap;

public class In extends AbstractValidation {

	@Override
	public String execute(String var, String[] args) throws UserException {
		WIMap wiMap = getWiParams().getWIMap();
		String value = wiMap.get(var);
		if (!value.equals("") && !value.equals("null")) {
			if (args.length == 0) {
				throw new UserException(getMessage("in_empty"));
			} else {
		        String separador = ":";
		        if (args.length > 1) {
		            separador = args[1];
		        }
		        String texto = separador + value + separador;
		        String lista = separador + args[0] + separador;
		        if (lista.indexOf(texto) < 0) {
		        	wiMap.remove(var);
		        	return getMessage("in_not_found");
		        }
			}
		}	
		return "";
	}
	
}
