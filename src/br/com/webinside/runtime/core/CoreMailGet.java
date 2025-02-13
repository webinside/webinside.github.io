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

package br.com.webinside.runtime.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.MailGet;
import br.com.webinside.runtime.component.ProjectElementsMap;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class CoreMailGet extends CoreCommon {

	private MailGet get;

	Store store = null;

	/**
	 * Creates a new CoreMailGet object.
	 * 
	 * @param wiParams
	 *            DOCUMENT ME!
	 * @param get
	 *            DOCUMENT ME!
	 */
	public CoreMailGet(ExecuteParams wiParams, MailGet get) {
		this.wiParams = wiParams;
		this.get = get;
		element = get;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void execute() {
		if (!isValidCondition()) return;
		try {
			store = null;
			mailGet();
        	if (store != null) {
        		store.close();
        	}
		} catch (Exception err) {
			wiParams.getErrorLog().write(getClass().getName(), "execute", err);
		}	
	}

	private void mailGet() throws MessagingException, IOException {
		ProducerParam prod = new ProducerParam();
		prod.setWIMap(wiMap);
		Host host = wiParams.getProject().getHosts().getHost(get.getHostId());
		if (host != null 
				&& (host.getProtocol().startsWith("POP3") || 
						host.getProtocol().startsWith("IMAP"))) {
			store = IntFunction.getStoreConnection(wiMap, host);
			if (store == null) {
				RtmFunction.hostError(wiParams, get.getHostId());
				return;
			}
		}
		if (!wiMap.get("wi.email.attid").equals("")) {
			sendAttach();
			wiParams.setRequestAttribute("wiExit", "true");
			return;
		}
		prod.setInput(get.getMailId());
		wiParams.getProducer().setParam(prod);
		wiParams.getProducer().execute();
		String tid = prod.getOutput();
		int id = 0;
		try {
			id = Integer.parseInt(tid);
			if (id < 0)
				id = 0;
		} catch (NumberFormatException err) {
		}
		String fld = Producer.execute(wiMap, get.getFolder()).trim();
		if (fld.equals(""))
			fld = "INBOX";
		Folder folder = store.getFolder(fld);
		folder.open(Folder.READ_ONLY);
		String wiobj = get.getWIObj().trim();
		if (id > 0) {
			Message mail = folder.getMessage(id);
			Enumeration e = mail.getAllHeaders();
			while (e.hasMoreElements()) {
				Header header = (Header) e.nextElement();
				String name = header.getName();
				String value = filter(header.getValue());
				wiMap.put(wiobj + "." + name, value);
			}
			wiMap.put(wiobj + ".id", id + "");
			wiMap.put(wiobj + ".folder", mail.getFolder().getFullName());
			Object content = mail.getContent();
			Map bodyInfo = findBodyInfo(content, mail.getContentType(), true);
			if (bodyInfo != null) {
				String type = (String) bodyInfo.get("type");
				if (type.indexOf("text/plain") > -1) {
					String body = (String) bodyInfo.get("body");
					body = StringA.change(body, "\r", "");
					body = StringA.change(body, "\n", "<br>\n");
					bodyInfo.put("body", body);
				}
			}
			if (!(content instanceof Multipart)) {
				content = null;
			}
			attachs(mail, (Multipart) content);
			wiMap.put(wiobj + ".body", (String) bodyInfo.get("body"));
		}
		folder.close(false);
		writeLog();
	}

	private Map findBodyInfo(Object content, String type, boolean recursive)
			throws MessagingException, IOException {
		Map bodyInfo = getBodyInfo(content, type);
		if (bodyInfo != null) return bodyInfo;
		List bodyParts = new ArrayList();
		Multipart mp = (Multipart) content;
		for (int i = 0; i < mp.getCount(); i++) {
			BodyPart bp = mp.getBodyPart(i);
			if (bp.getFileName() != null) continue;
			Object auxcontent = bp.getContent();
			String auxtype = bp.getContentType();
			Map auxBodyInfo = getBodyInfo(auxcontent, auxtype);
			if (auxBodyInfo != null) {
				boolean isHtml = (auxtype.indexOf("text/html") > -1);
				if (bodyInfo == null || isHtml) {
					bodyInfo = auxBodyInfo;
				}
			} else {
				if (auxcontent instanceof Multipart) {
					bodyParts.add(bp);
				}
			}
		}
		if (bodyInfo == null && recursive) {
			for (Iterator it = bodyParts.iterator(); it.hasNext();) {
				BodyPart bp = (BodyPart) it.next();
				Object auxcontent = bp.getContent();
				String auxtype = bp.getContentType();
				bodyInfo = findBodyInfo(auxcontent, auxtype, false);
				if (bodyInfo != null) return bodyInfo;
			}
		}
		return bodyInfo;
	}

	private Map getBodyInfo(Object content, String type)
			throws MessagingException, IOException {
		Map bodyInfo = null;
		if (content instanceof String) {
			bodyInfo = new HashMap();
			bodyInfo.put("body", content);
			bodyInfo.put("type", type);
		}
		return bodyInfo;
	}

	private void attachs(Message mail, Multipart mp) throws MessagingException {
		ProjectElementsMap pem = wiParams.getProject().getGrids();
		if (!pem.containsKey(get.getGridAttId())) {
			wiParams.includeCode("/grids/" + get.getGridAttId() + "/grid.jsp");
		}
		AbstractGrid grid = (AbstractGrid) pem.getElement(get.getGridAttId());
		if ((grid == null) || !(grid instanceof AbstractGridLinear)) {
			return;
		}
		List mapList = new ArrayList();
		if (mp != null) {
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart bp = mp.getBodyPart(i);
				String fname = filter(bp.getFileName());
				if (fname != null) {
					Map aux = new HashMap();
					aux.put("rowid", (i + 1) + "");
					aux.put("rowid0", i + "");
					aux.put("name", fname);
					String mime = StringA.piece(bp.getContentType(), ";", 1);
					aux.put("mime", mime);
					boolean bin = (bp.getContentType().indexOf("text") == -1);
					aux.put("binary", bin + "");
					aux.put("size", bp.getSize() + "");
					String glbid = mail.getFolder().getName() + "-"
							+ mail.getMessageNumber() + "-" + i;
					String pageId = wiParams.getPage().getId();
					String href = pageId + ".wsp?wi.email.attid=" + glbid;
					aux.put("href", href);
					String link = "<a href=\"" + href + "\">" + fname + "</a>";
					aux.put("link", link);
					mapList.add(aux);
				}
			}
		}
		Map[] array = (Map[]) mapList.toArray(new Map[0]);
		GridLinearNavigator linear = new GridLinearNavigator(wiParams);
		linear.execute((AbstractGridLinear) grid, array, 1, false);
	}

	private void sendAttach() throws MessagingException, IOException {
		String fullid = wiMap.get("wi.email.attid").trim();
		String fld = StringA.piece(fullid, "-", 1);
		String id = StringA.piece(fullid, "-", 2);
		String seq = StringA.piece(fullid, "-", 3);
		Folder folder = store.getFolder(fld);
		folder.open(Folder.READ_ONLY);
		Message mail = folder.getMessage(Function.parseInt(id));
		Multipart mp = (Multipart) mail.getContent();
		BodyPart bp = mp.getBodyPart(Function.parseInt(seq));
		String dispname = "attachment; filename=\"" + filter(bp.getFileName())
				+ "\"";
		wiParams.getHttpResponse().setHeader("Content-disposition", dispname);
		String mime = StringA.piece(bp.getContentType(), ";", 1);
		wiParams.setContentType(mime);
		Function.copyStream(bp.getInputStream(), wiParams.getOutputStream());
		folder.close(false);
	}

	private String filter(String value) {
		try {
			if (value != null) {
				value = MimeUtility.decodeText(value);
			}
		} catch (UnsupportedEncodingException uex) {
		}
		return value;
	}

}
