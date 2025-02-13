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

package br.com.webinside.runtime.function;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.ExecuteParamsEnum;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.InterfaceConnector;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.net.FileUpload;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class Resize implements InterfaceConnector, InterfaceParameters {

    public Resize() { }

    public void execute(ExecuteParams wiParams) throws UserException {
        WIMap wiMap = wiParams.getWIMap();
        String source = wiMap.get("tmp.sourcefile").trim();
        int lastDot = source.lastIndexOf(".");
        String ext = StringA.mid(source, lastDot + 1, source.length());
        ext = ext.toLowerCase().trim();
        String dest = wiMap.get("tmp.destfile").trim();
        if (dest.equals("")) dest = source;
        if (!dest.endsWith("." + ext)) {
        	dest = StringA.changeChars(dest, ".", "_") + "." + ext;
        }
    	if (ext.equals("jpg") || ext.equals("png")) {
	        try {
	            BufferedImage bi = ImageIO.read(new File(source));
	            File out = new File(dest);
	    		out.getParentFile().mkdirs();
	            out.delete();
            	int w = bi.getWidth();
            	int h = bi.getHeight();
            	float fw = (float) w / h;
            	float fh = (float) h / w;
            	int minW = Function.parseInt(wiMap.get("tmp.minwidth"));
            	if (minW > 0 && w < minW) { w = minW; h = Math.round(w * fh); }
            	int maxW = Function.parseInt(wiMap.get("tmp.maxwidth"));
            	if (maxW > 0 && w > maxW) { w = maxW; h = Math.round(w * fh); }
            	int minH = Function.parseInt(wiMap.get("tmp.minheight"));
            	if (minH > 0 && h < minH) { h = minH; w = Math.round(h * fw); }
            	if (maxW > 0 && w > maxW) w = maxW;
            	int maxH = Function.parseInt(wiMap.get("tmp.maxheight"));
            	if (maxH > 0 && h > maxH) { h = maxH; w = Math.round(h * fw); }
            	if (minW > 0 && w < minW) w = minW;
            	if (w != bi.getWidth() || h != bi.getHeight()) {
		            Image img = bi.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		            int bi2Type = BufferedImage.TYPE_INT_RGB;
		            if (ext.equals("png")) bi2Type = BufferedImage.TYPE_INT_ARGB;
		            BufferedImage bi2 = new BufferedImage(w, h, bi2Type);
		            bi2.createGraphics().drawImage(img, 0, 0, w, h, null);	            
		            Iterator it = ImageIO.getImageWritersByFormatName(ext);
		            ImageWriter writer = (ImageWriter)it.next();
		            ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		            writer.setOutput(ios);
		            ImageWriteParam iwparam = null;
		            if (ext.equals("jpg")) {
		            	iwparam = new JPEGImageWriteParam(Locale.getDefault());
			            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			            iwparam.setCompressionQuality(0.9f);
		            }			            
		            writer.write(null, new IIOImage(bi2, null, null), iwparam);	   
		            writer.dispose();
		            ios.close();
            	} else {
            		ImageIO.write(bi, ext, out);
            	}
   	        } catch (IOException e) {
   	        	throw new UserException(e);
   	        }
    	}    
        String temp = wiMap.get("tmp.tempfile").trim();
        if (temp.equalsIgnoreCase("true")) {        	
    		FileUpload fu = wiParams.getFileUpload();
    		if (fu == null) {
    			fu = new FileUpload();
        		wiParams.setParameter(ExecuteParamsEnum.FILE_UPLOAD, fu);
    		}
    		fu.addExtraFile("resize", new File(dest));
        }
    }

    public boolean exit() {
        return false;
    }
    
    public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[7];
        params[0] = new JavaParameter("tmp.sourcefile", "Arquivo Origem");
        params[1] = new JavaParameter("tmp.destfile", "Arquivo Destino");
        params[2] = new JavaParameter("tmp.minwidth", "Largura Min");
        params[3] = new JavaParameter("tmp.maxwidth", "Largura Max");
        params[4] = new JavaParameter("tmp.minheight", "Altura Min");
        params[5] = new JavaParameter("tmp.maxheight", "Altura Max");
        params[6] = new JavaParameter("tmp.tempfile", "Temporário");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        return new JavaParameter[0];
    }
    
}
