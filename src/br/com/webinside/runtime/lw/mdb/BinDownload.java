package br.com.webinside.runtime.lw.mdb;

import java.io.IOException;

import br.com.webinside.runtime.core.Export;
import br.com.webinside.runtime.core.MimeType;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

public class BinDownload extends AbstractConnector implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		try {
			String db = wiMap.get("tmp.mongodow.database");
			DB mdb = BaseUtil.getDB(getParams(), db);
			String regId = wiMap.get("tmp.mongodow.regid");
			if (Function.parseLong(regId) == 0) return;
			GridFS gfs = new GridFS(mdb, "tb_arquivo");
			GridFSDBFile mFile = gfs.findOne(regId);
			String ext = mFile.getContentType();
            String mime = MimeType.get(ext);
            if (mime.equals("")) mime = "application/octet-stream";
            getParams().setContentType(mime);
            String dispname = "attachment; filename=\""; 
            String fname = (String) mFile.getMetaData().get("fname");
            if (fname.equals("")) fname = "id-" + regId + "." + ext;
        	dispname += StringA.getUsAscii(fname) + "\"";
        	getParams().getHttpResponse().setHeader("Content-disposition", dispname);
            try  {
            	getParams().getHttpResponse().flushBuffer();
            } catch (IOException err) {}  
            new Export(getParams()).sendInputStream(mFile.getInputStream(), true);
		} catch (Exception err) {
			String msg = "Page: " + wiMap.get("wi.page.id");
			getParams().getErrorLog().write(getClass().getName(), msg, err);
		}
	}
	
	@Override
	public boolean exit() {
		return true;
	}

	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[2];
		in[0] = new JavaParameter("tmp.mongodow.database", "Mongo Database");
		in[1] = new JavaParameter("tmp.mongodow.regid", "Id do Arquivo");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
