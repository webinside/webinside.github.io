package br.com.webinside.runtime.integration.taglib;

import javax.servlet.jsp.JspException;

/**
 * Classe que implementa um TagLib para a função Script.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Script extends ScriptOrStyle {

	private static final long serialVersionUID = 1L;
	
	@Override
	public int doStartTag() throws JspException {
		type = "script";
		return super.doStartTag();
	}

	public String getSrc() {
		return getPath();
	}

	public void setSrc(String src) {
		setPath(src);
	}
	
}
