package br.com.webinside.runtime.integration.taglib;

import javax.servlet.jsp.JspException;

/**
 * Classe que implementa um TagLib para a função Style.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Style extends ScriptOrStyle {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		type = "style";
		return super.doStartTag();
	}

	public String getHref() {
		return getPath();
	}

	public void setHref(String src) {
		setPath(src);
	}
	
}
