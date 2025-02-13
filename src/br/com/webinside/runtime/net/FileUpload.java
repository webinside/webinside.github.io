/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.8 $
 */
public class FileUpload extends ServletFileUpload {
    private List<FileItem> items;
    private Map fields;
    private Map files;
    private Map extraFiles;

    public FileUpload() {
    	super(getDefaultFactory());
    }
    
    public FileUpload(FileItemFactory fileItemFactory) {
    	super(fileItemFactory);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     *
     * @throws FileUploadException DOCUMENT ME!
     */
    public void parse(HttpServletRequest request) throws FileUploadException {
        items = parseRequest(request);
        fields = new HashMap();
        files = new HashMap();
        Iterator it = items.iterator();
        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            String field = item.getFieldName().toLowerCase();
            if (field.toLowerCase().startsWith("tmp_")) {
            	field = "tmp." + StringA.mid(field, 4, field.length());
            }
            if (item.isFormField()) {
            	addField(field, item.getString());
            } else {
            	Map<String,String> fnameMap = getItemNameMap(item);
            	String fname = fnameMap.get("fname");
                files.put(field, item);
            	addField(field, fname);
            	String name = fnameMap.get("name");
                String ext = fnameMap.get("ext");
            	addField(field + ".name", name);
            	addField(field + ".ext", ext);
            	addField(field + ".size", item.getSize() + "");
            	try {
	        		InputStream fIn = item.getInputStream();
	            	addField(field + ".sha1", DigestUtils.sha1Hex(fIn));
        		fIn.close();
            	} catch (IOException e) {
            		throw new FileUploadException("sha1 error", e);
            	}
            }
        }
    }
    
    private void addField(String field, String value) {
        List list = (List) fields.get(field);
        if (list == null) {
            list = new ArrayList();
            fields.put(field, list);
        }
        list.add(value);
    }

    public void addExtraFile(String field, File file) {
    	if (files == null) files = new HashMap();
    	if (extraFiles == null) extraFiles = new HashMap();
        extraFiles.put(field.toLowerCase(), file);                
    }
    
    public void removeFiles() {
    	if (files != null) {
			Collection aux = files.values();
			for (Iterator it = aux.iterator(); it.hasNext();) {
				((FileItem) it.next()).delete();
			}
    	}	
    	if (extraFiles != null) {
    		Collection aux = extraFiles.values();
    		for (Iterator it = aux.iterator(); it.hasNext();) {
				((File) it.next()).delete();
			}
    	}
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getItems() {
        return items;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getFieldsMap() {
        return fields;
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getFilesMap() {
        return files;
    }
    
    public List<FileItem> getMultFileField(String field) {
    	List<FileItem> resp = new ArrayList<>();
        for (FileItem item : items) {
            if (!item.isFormField()) {
                String fname = item.getFieldName().toLowerCase();
                if (fname.toLowerCase().startsWith("tmp_")) {
                	fname = "tmp." + StringA.mid(fname, 4, fname.length());
                }
                if (fname.equals(field)) {
                	resp.add(item);
                }
            }
		}
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getFieldsMapSimple() {
    	Map resp = new HashMap();
	    for (Iterator it = getFieldsMap().keySet().iterator(); it.hasNext();) {
	        String name = (String) it.next();
	        List vValues = (List) getFieldsMap().get(name);
	        if (vValues.size() > 0) {
	        	resp.put(name, vValues.get(0));
	        }	
	    }
	    return resp;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hasFile(String field) {
        FileItem item = (FileItem) files.get(field.toLowerCase());
        boolean has = ((item != null) && (item.getSize() > 0));
        if (has) return true;
    	if (extraFiles == null) extraFiles = new HashMap();
        File file = (File) extraFiles.get(field.toLowerCase());
        return ((file != null) && (file.length() > 0));
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map<String, String> getItemNameMap(FileItem item) {
    	Map<String, String> map = new HashMap<>();
        String fname = StringA.change(item.getName(), "\\", "/");
        int last = fname.lastIndexOf("/");
        if (last > -1) {
            fname = StringA.mid(fname, last + 1, fname.length());
        }
        int ldot = fname.lastIndexOf(".");
        if (ldot == -1) ldot = fname.length();
        String name = StringA.mid(fname, 0, ldot - 1);
        String ext = StringA.mid(fname, ldot + 1, fname.length() - 1);
        if (ext.equalsIgnoreCase("JPEG")) ext = "JPG";
        if (ext.length() >= 2 && ext.length() <= 4) {
        	ext = ext.toLowerCase();
        	fname = name + "." + ext;
        }
        map.put("fname", fname);
        map.put("name", name);
        map.put("ext", ext);
    	return map;
    }

    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     * @param targetFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean saveFile(String field, String targetFile) {
        if (hasFile(field)) {
            try {
                FileItem item = (FileItem) files.get(field.toLowerCase());
                if (item != null) {
                    item.write(new File(targetFile));
                } else {
                	File file = (File) extraFiles.get(field.toLowerCase());
                	FileUtils.copyFile(file, new File(targetFile));
                	file.delete();
                }
                return true;
            } catch (Exception err) {
                System.err.println(getClass().getName() + ": " + err);
            }
        }
        return false;
    }
    
    public static FileItemFactory getDefaultFactory() {
	    DiskFileItemFactory diskFactory = new DiskFileItemFactory();
	    diskFactory.setSizeThreshold(256000);
	    diskFactory.setRepository(new File(Function.tmpDir()));
	    return diskFactory;
    }    

}
