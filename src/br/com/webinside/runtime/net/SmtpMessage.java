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

package br.com.webinside.runtime.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

public class SmtpMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	// propriedades de controle
	private String id;
	// propriedades de envio
	private String host = "";
	private String user = "";
	private String pass = "";
	private String port = "";
	private boolean secure = false;
	private String debugDir = "";
	// propriedades da mensagem
	private String subject;
	private InternetAddress from;
	private InternetAddress[] to;
	private InternetAddress[] cc;
	private InternetAddress[] bcc;
	private boolean html = false;
	private boolean notification = false;
	private String body;
	private List attachs = new ArrayList(); // list de File
	
	public SmtpMessage() {
		File aux = new File(Function.rndTmpFile("smtp-", "tmp"));
		id = StringA.piece(aux.getName(), ".tmp", 1);
	}
		
	public String getId() {
		return id;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setTo(InternetAddress[] to) {
		this.to = to;
	}

	public void setCc(InternetAddress[] cc) {
		this.cc = cc;
	}

	public void setBcc(InternetAddress[] bcc) {
		this.bcc = bcc;
	}

	public void setDebugDir(String debugDir) {
		this.debugDir = debugDir;
	}

	public void setFrom(InternetAddress from) {
		this.from = from;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public void addAttach(File attach) {
		attachs.add(attach);
	}

	public boolean send() {
		return send(true);
	}

	public boolean send(boolean sendNow) {
    	String folder = Function.tmpDir() + "/" + id + ".tmp";
    	String threadMsg = "Must use thread";
		try { 
			if (sendNow) {
				sendMessage();
			} else {
				throw new MessagingException(threadMsg);
			}
			Function.removeDir(folder);
			return true;
		} catch (MessagingException ex) {
			String msg = ex.getMessage();
	    	if (ex.getMessage() != null && ex.getMessage().startsWith("250")) {
				Function.removeDir(folder);
	    		return true;
	    	}
	    	if (msg != null && !msg.equals(threadMsg)) {
	    		ex.printStackTrace(System.out);
	    	}	
	    	if (ex instanceof SendFailedException) {
				Function.removeDir(folder);
	    	} else {
				try {
			    	SmtpMessageThread.verifyFolder = true;
					new File(folder).mkdirs();
					String file = folder + "/message.obj";
			    	FileOutputStream out = new FileOutputStream(file);
			    	ObjectOutputStream s = new ObjectOutputStream(out);
			    	s.writeObject(this);
			    	s.flush();
			    	out.close();
				} catch (Exception err) {
					// ignorado
				}
	    	}	
		}
		return false;
	}	
	
	private void sendMessage() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        if (port.equals("")) port = (secure ? "465" : "587");
        props.put("mail.smtp.port", port);        
        props.put("mail.smtp.sendpartial", "true"); 
        props.put("mail.smtp.starttls.enable","true");
        if (secure) {
            props.put("mail.smtp.ssl.enable", "true");
        	props.put("mail.smtp.socketFactory.port", port); 
        	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
        	props.put("mail.smtp.socketFactory.fallback", "false");  
        }
        Authenticator auth = null;
        if (!user.trim().equals("")) {
        	props.put("mail.smtp.auth", "true");            	
        	auth = new SMTPAuthenticator();
        }
        Session session = Session.getInstance(props, auth);
        if (!debugDir.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            String logid = sdf.format(new Date());
	        FileOutputStream logFile;
			try {
				String f = debugDir + "/" + "smtp-" + logid + ".log";
				logFile = new FileOutputStream(f);
	            session.setDebugOut(new PrintStream(logFile));
	            session.setDebug(true);
			} catch (FileNotFoundException e) {
				// Ignorado
			}
        }	
	   	MimeMessage message = new MimeMessage(session);
	    message.setSubject(subject);
	    message.setSentDate(new Date());
	    message.setFrom(from);
	    message.setRecipients(Message.RecipientType.TO, to);         
	    message.setRecipients(Message.RecipientType.CC, cc);         
	    message.setRecipients(Message.RecipientType.BCC, bcc);
	    if (notification) {
	    	message.setHeader("Disposition-Notification-To", from.getAddress());
	    }
        String mimetype = "text/plain";
        if (html) mimetype = "text/html";
	    if (attachs.size() == 0) {
		    message.setContent(body, mimetype);
	    } else {
		    Multipart multipart = new MimeMultipart("related");
		    message.setContent(multipart);
	        MimeBodyPart mbp = new MimeBodyPart();
	       	mbp.setContent(body, mimetype);
	       	multipart.addBodyPart(mbp);
	        for (Iterator it = attachs.iterator(); it.hasNext();) {
				File file = (File) it.next();
	            MimeBodyPart att = new MimeBodyPart();
	            DataSource source = new FileDataSource(file);
	            att.setDataHandler(new DataHandler(source));
	            att.setFileName(file.getName());
	            att.setContentID("<" + file.getName() + ">");
	            multipart.addBodyPart(att);
			}
	    }    
        try {
        	Transport.send(message);
        } catch (MessagingException ex) {
        	throw ex;
        } finally { 
            if (!debugDir.equals("")) {
            	session.getDebugOut().close();
            }	
        }
	}
	
   class SMTPAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user.trim(), pass.trim());
		}
    }
   
}
