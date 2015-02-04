/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.*;

import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public abstract class AbstractGridLinear extends AbstractGrid {

	private static final long serialVersionUID = 1L;

	/**
     * Creates a new AbstractGridLinear object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractGridLinear(String id) {
        super(id);
    }

    /**
     * Creates a new AbstractGridLinear object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractGridLinear(String id, Element element) {
        super(id, element);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLimit(String value) {
        XMLFunction.setElemValue(this.grid, "LIMIT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLimit() {
        return XMLFunction.getElemValue(this.grid, "LIMIT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setColSize(String value) {
        XMLFunction.setElemValue(this.grid, "HSIZE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColSize() {
        return XMLFunction.getElemValue(this.grid, "HSIZE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setColDisp(String value) {
        XMLFunction.setElemValue(this.grid, "HDISPOSITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColDisp() {
        return XMLFunction.getElemValue(this.grid, "HDISPOSITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setColor(String value) {
        XMLFunction.setElemValue(this.grid, "COLOR", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColor() {
        return XMLFunction.getElemValue(this.grid, "COLOR");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setStyle(String value) {
    	XMLFunction.setElemValue(this.grid, "STYLE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStyle() {
    	return XMLFunction.getElemValue(this.grid, "STYLE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param layout DOCUMENT ME!
     */
    public void setLayout(Element layout) {
    	Element content = grid.getChild("CONTENT");
    	content.removeChild("LAYOUT");
    	content.addContent((Element) layout.clone());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getLayout() {
    	Element content = grid.getChild("CONTENT");
    	Element layout = null;
    	if (content != null) {
    		layout = content.getChild("LAYOUT");
    	}
    	return layout;
    }
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentUnique(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "UNIQUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentUnique() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "UNIQUE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRegisterLine(String value) {
        Element filesrc = grid.getChild("CONTENT");
        if (filesrc == null) {
            grid.addContent(new Element("CONTENT"));
            filesrc = grid.getChild("CONTENT");
        }
        XMLFunction.setAttrValue(filesrc, "UNIQUE", "REGISTERLINE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRegisterLine() {
        Element filesrc = this.grid.getChild("CONTENT");
        if (filesrc == null) {
            return "";
        }
        return XMLFunction.getAttrValue(filesrc, "UNIQUE", "REGISTERLINE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentStart(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "START", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentStart() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "START");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentRowStart(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "ROWSTART", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentRowStart() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "ROWSTART");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentRegister(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "REGISTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentRegister() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "REGISTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentNoRegister(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "NOREGISTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentNoRegister() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "NOREGISTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentRowEnd(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "ROWEND", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentRowEnd() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "ROWEND");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentNoRow(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "NOROW", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentNoRow() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "NOROW");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentEnd(String value) {
        XMLFunction.setCDATAValue(grid, "CONTENT", "END", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentEnd() {
        return XMLFunction.getCDATAValue(grid, "CONTENT", "END");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setContentSharedGrid(String value) {
        XMLFunction.setElemValue(grid, "CONTENT", "SHAREDGRID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentSharedGrid() {
        return XMLFunction.getElemValue(grid, "CONTENT", "SHAREDGRID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getChildrenGrids() {
        List ret = new ArrayList();
        Element content = grid.getChild("CONTENT");
        if (content == null) {
            return new String[0];
        }
        if (content.getChild("CHILDRENGRIDS") != null) {
            for (Iterator i =
                    content.getChild("CHILDRENGRIDS").getChildren().iterator();
                        i.hasNext();) {
                Element ele = (Element) i.next();
                ret.add(ele.getText());
            }
        }
        return (String[]) ret.toArray(new String[ret.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param gridId DOCUMENT ME!
     */
    public void addChildGrid(String gridId) {
        Element content = grid.getChild("CONTENT");
        if (content == null) {
            content = new Element("CONTENT");
            grid.addContent(content);
        }
        Element grids = content.getChild("CHILDRENGRIDS");
        if (grids == null) {
            content.addContent(new Element("CHILDRENGRIDS"));
            grids = content.getChild("CHILDRENGRIDS");
        }
        Element newGrid = new Element("CHILDGRID");
        newGrid.setText(gridId);
        grids.addContent(newGrid);
    }

    /**
     * DOCUMENT ME!
     *
     * @param gridId DOCUMENT ME!
     */
    public void removeChildGrid(String gridId) {
        Element content = grid.getChild("CONTENT");
        if (content == null) {
            return;
        }
        Element grids = content.getChild("CHILDRENGRIDS");
        if (grids == null) {
            return;
        }
        for (Iterator i = grids.getChildren().iterator(); i.hasNext();) {
            Element ele = (Element) i.next();
            if (ele.getText().equals(gridId)) {
                grids.removeContent(ele);
                break;
            }
        }
        if (grids.getChildren().size() == 0) {
            content.removeContent(grids);
        }
        if (content.getChildren().size() == 0) {
            grid.removeContent(content);
        }
    }
    
    public void clearContents() {
        setContentEnd("");
        setContentNoRegister("");
        setContentNoRow("");
        setContentRegister("");
        setContentRowEnd("");
        setContentRowStart("");
        setContentStart("");
        setContentUnique("");
    }
}
