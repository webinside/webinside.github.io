package br.com.webinside.runtime.lw.mdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterInputStream;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class BinUpload extends AbstractConnector implements InterfaceParameters {
	
	private final static long MAX_LENGTH = 5242880;

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		try {
			String db = wiMap.get("tmp.mongoupl.database");
			DB mdb = BaseUtil.getDB(getParams(), db);
			GridFS gfs = new GridFS(mdb, "tb_arquivo");
			String regId = wiMap.get("tmp.mongoupl.regid");
			if (Function.parseLong(regId) == 0) return;
			gfs.remove(regId);
			String field = wiMap.get("tmp.mongoupl.formfield");
	        String mytemp = Function.rndTmpFile("upl", "tmp");
	        getParams().getFileUpload().saveFile(field, mytemp);
	        Map meta = new HashMap();
			meta.put("fname", wiMap.get(field));
			BaseUtil.fileMetadata(meta, new File(mytemp));
			String ext = wiMap.get(field + ".ext").toLowerCase().trim();
			if (ext.equals("")) ext = "bin";
			if (!checkMaxLength(meta, ext)) {
				throw new IOException("Exceeds max file length of 5mb");
			}
	        InputStream fIn = new FileInputStream(mytemp);
			DeflaterInputStream zIn = new DeflaterInputStream(fIn);
			GridFSInputFile mFile = gfs.createFile(zIn, regId);
			mFile.setMetaData(new BasicDBObject(meta));
			mFile.setContentType(ext);
			mFile.save();		
			fIn.close();
		} catch (Exception err) {
        	EngFunction.invalidateTransaction(wiMap, err.toString());
			String msg = "Page: " + wiMap.get("wi.page.id");
			getParams().getErrorLog().write(getClass().getName(), msg, err);
		}
	}
	
	private boolean checkMaxLength(Map meta, String ext) {
		if (ext.equals("pdf")) {
			return true;
		} else {
			if ((Long)meta.get("length") > MAX_LENGTH) return false;
		}
		return true;
	}
	
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[3];
		in[0] = new JavaParameter("tmp.mongoupl.database", "Mongo Database");
		in[1] = new JavaParameter("tmp.mongoupl.regid", "Id do Arquivo");
		in[2] = new JavaParameter("tmp.mongoupl.formfield", "Campo do Fomulário");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
