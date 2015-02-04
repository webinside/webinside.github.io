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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.MailSend;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.SmtpMessage;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreMailSend extends CoreCommon {
    private MailSend send;
    
    /**
     * Creates a new CoreSendmail object.
     *
     * @param wiParams DOCUMENT ME!
     * @param send DOCUMENT ME!
     */
    public CoreMailSend(ExecuteParams wiParams, MailSend send) {
        this.wiParams = wiParams;
        this.send = send;
        element = send;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String hostname = "";
        String user = "";
        String pass = "";
        String port = "";
        boolean secure = false; 
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        if (wiParams.getProject().getHosts() != null) {
        	Host host = wiParams.getProject().getHosts().getHost(send.getHostId());
            if (host != null && host.getProtocol().startsWith("SMTP")) {
            	Producer producer = new Producer();
        		producer.setParam(prod);
        		prod.setInput(host.getUser());
        		producer.execute();
        		user = prod.getOutput().trim();
        		prod.setInput(host.getPass());
        		producer.execute();
        		pass = prod.getOutput().trim();
        		prod.setInput(host.getAddress());
        		producer.execute();
        		hostname = prod.getOutput().trim();
        		prod.setInput(host.getPort());
        		producer.execute();
        		port = prod.getOutput().trim();
        		if (host.getProtocol().toLowerCase().endsWith("-ssl")) {
        			secure = true;
        		}
            }
        }
        prod.setInput(send.getTo());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String sendTo = prod.getOutput().trim();
        if (!hostname.equals("") && !sendTo.equals("")) {
            WIMap orig = wiParams.getWIMap();
            String debug = wiParams.getWIMap().get("pvt.debug.smtp").trim();
            if (!debug.equalsIgnoreCase("true")) {
            	debug = wiParams.getWIMap().get("tmp.debug.smtp").trim();
            }
            String ids = build(hostname, user, pass, port, secure, debug);
            wiParams.setParameter(ExecuteParams.WI_MAP, orig);
            if (!ids.equals("")) {
                String msg = "Emails stored to retry later. " +
                    "Connection failure to " + hostname;
                wiParams.getErrorLog().write("CoreSendmail",
                    "Email retry log", msg);
            }
            wiMap.put("wi.email.ok", "true");
            wiMap.put("wi.email.ids", ids);
        }
        writeLog();
    }

    private String build(String host, String user, String pass, 
    		String port, boolean secure, String debug) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        prod.setInput(send.getTo());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String allTo = prod.getOutput().trim();
        String ids = "";
        if (allTo.equals("")) {
            return "";
        }
        if (send.getEachOne().equals("ON")) {
            allTo = StringA.change(allTo, ";", ",");
            int count = StringA.count(allTo, ',');
            for (int i = 0; i <= count; i++) {
                String sendto = StringA.piece(allTo, ",", i + 1).trim();
            	SmtpMessage message = new SmtpMessage();
                if (debug.equalsIgnoreCase("true")) {
            		message.setDebugDir(wiParams.getErrorLog().getParentDir());
                }
            	message.setHost(host);
            	message.setUser(user);
            	message.setPass(pass);
            	message.setPort(port);
            	message.setSecure(secure);
            	boolean ok = send(message, sendto);
            	if (!ok) {
            		if (!ids.equals("")) ids = ids + ",";
            		ids = ids + message.getId();
            	}
            }
        } else {
        	SmtpMessage message = new SmtpMessage();        	
            if (debug.equalsIgnoreCase("true")) {
        		message.setDebugDir(wiParams.getErrorLog().getParentDir());
            }
        	message.setHost(host);
        	message.setUser(user);
        	message.setPass(pass);
        	message.setPort(port);
        	message.setSecure(secure);
        	ids = message.getId();
       		boolean ok = send(message, allTo);
       		if (ok) ids = "";
        }
        cleanAttachs();
        return ids;
    }

    private boolean send(SmtpMessage message, String sendto) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        prod.setInput(send.getFrom());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String from = prod.getOutput().trim();
        prod.setInput(send.getSender());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String sender = prod.getOutput().trim();
        prod.setInput(send.getSubject());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String subject = prod.getOutput().trim();
        message.setSubject(subject);
        message.setNotification(send.getNotification().equalsIgnoreCase("on"));
        String type = send.getMime().trim().toLowerCase();
        if (type.equals("html")) {
        	message.setHtml(true);
        }
        try {
			message.setFrom(new InternetAddress(from, sender));
		} catch (Exception e) { 
			// ignorado
		}

        // add os TO
        message.setTo(toAddressArray(sendto, null));
        // add os CC
        message.setCc(toAddressArray(send.getCc(), prod));
        // add os BCC
        InternetAddress[] address = toAddressArray(send.getBcc(), prod);
        message.setBcc(address);
        
        WIMap contextClone = wiParams.getWIMap().cloneMe();
        contextClone.put("wi.email", sendto);
        // Recursividade de lógica
        recursiveCore(contextClone);
        prod.setWIMap(contextClone);
        String body = send.getContent();
        prod.setInput(body);
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String regOutput = prod.getOutput();
        message.setBody(regOutput);
        setAttachs(message);
        boolean sendNow = (address.length <= 10);
        return message.send(sendNow);
    }

    private void setAttachs(SmtpMessage message) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        prod.setInput(send.getAttachDir());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String dir = prod.getOutput().trim();
        if (dir.equals("")) {
            return;
        }
        String[] lista = new File(dir).list();
        if (lista == null) {
            lista = new String[0];
        }
        String tmpDir = Function.tmpDir();
        for (int i = 0; i < lista.length; i++) {
            String name = lista[i];
            File fl = new File(dir, name);
            if (!fl.isDirectory()) {
            	File dest = 
            		new File(tmpDir + "/" + message.getId() + ".tmp/" + name);
            	Function.copyFile(fl.getAbsolutePath(), 
            			dest.getAbsolutePath(), true);
            	message.addAttach(dest);
            }
        }
    }

    private void cleanAttachs() {
        if (!send.getAttachRemove().equals("ON")) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        prod.setInput(send.getAttachDir());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String dir = prod.getOutput().trim();
        if (dir.equals("")) {
            return;
        }
        Function.removeFiles(dir, "*");
        new File(dir).delete();
    }

    private void recursiveCore(WIMap auxMap) {
        String name = send.getExecute();
        if (!name.equals("")) {
        	name = StringA.piece(name, ".wsp", 1);
        	if (!name.startsWith("/")) {
        		name = "/" + name;
        	}
            WIMap orig = wiParams.getWIMap();
            auxMap.put("wi.jsp.filename_parent", orig.get("wi.jsp.filename"));
            auxMap.put("wi.jsp.filename", name + "_pre");
            wiParams.setParameter(ExecuteParams.WI_MAP, auxMap);
            wiParams.includePrePage(new Page(name));
            wiParams.setParameter(ExecuteParams.WI_MAP, orig);
        }
    }
    
    private InternetAddress[] toAddressArray(String emails, ProducerParam prod) {
    	if (prod != null) {
	        prod.setInput(emails);
	        wiParams.getProducer().setParam(prod);
	        wiParams.getProducer().execute();
	        emails = prod.getOutput().trim();
    	}    
        emails = StringA.changeChars(emails, "/;", ",,");
        List address = new ArrayList();
        StringTokenizer tk = new StringTokenizer(emails, ",");
        while (tk.hasMoreTokens()) {
        	String email = tk.nextToken().trim();
    		if (!emails.equals("")) {
    			try {
        			address.add(new InternetAddress(email));
    			} catch (AddressException ex) { }
    		}
        }
        InternetAddress[] resp = new InternetAddress[address.size()];
    	return (InternetAddress[])address.toArray(resp);
    }
    
}
