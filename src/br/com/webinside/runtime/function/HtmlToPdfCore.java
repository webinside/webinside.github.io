package br.com.webinside.runtime.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.Export;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

import com.lowagie.text.pdf.BaseFont;

public class HtmlToPdfCore {

	public static File generatePage(ExecuteParams params, String page) throws Exception {
		File tmpPdf = new File(Function.rndTmpFile("tmp", "pdf"));
		FileOutputStream fos = new FileOutputStream(tmpPdf);
        String url = getURL(params, page);
        Document doc = getDocument(url);
        makeBookmarks(doc);
	    ITextRenderer renderer = getRenderer(params.getWIMap());
		renderer.setDocument(doc, url);
		renderer.layout();
		renderer.createPDF(fos);
		fos.close();
		return tmpPdf;
	}
	
	public static void exportPdf(ExecuteParams params, File pdf, String name)
	throws Exception {
		HttpServletResponse response = params.getHttpResponse();
		response.setContentType("application/octetstream");
		String dispname = "attachment; filename=\"" + name;
		response.setHeader("Content-disposition", dispname);
		response.setContentLength((int) pdf.length());
		try {
			response.flushBuffer();
		} catch (IOException err) {}  
		new Export(params).sendFile(params.getWIMap(), pdf.getAbsolutePath(), false);
	}
	
	public static String getURL(ExecuteParams params, String page) {
		if (page.startsWith("http")) return page;
        int port = EngFunction.getServerPort(params.getHttpRequest());
        String prot = (port == 443) ? "https://" : "http://";
        String host = params.getWIMap().get("wi.server.host");
        if (host.equals("")) host = "localhost";
        String url = prot + host +":" + port + "/";
		if (!page.startsWith("/")) page = "/" + page;
		if (page.indexOf(".wsp") == -1) page += ".wsp";
		return url + params.getWIMap().get("wi.proj.id") + page; 
	}

	public static Document getDocument(String url) throws Exception {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		tidy.setBreakBeforeBR(true);
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod(url);
	    int status = httpclient.executeMethod(httpget);
		if (status != HttpStatus.SC_OK) {
			throw new HttpException("Erro ao ler url " + url);
		}
		InputStream in = httpget.getResponseBodyAsStream();
		String charset = httpget.getResponseCharSet();
	    Reader reader = new InputStreamReader(in, charset); 
		Document document = tidy.parseDOM(reader, null);
	    httpget.releaseConnection();
		return generalizeDoc(document);
	}
	
	public static void makeBookmarks(Document doc) throws Exception {
        int docLevel = 0;
        StringBuilder sb = new StringBuilder("<bookmarks>\n");
        NodeList nl = doc.getElementsByTagName("a");
        for (int i = 0; i < nl.getLength(); i++) {
        	Node node = nl.item(i);
        	if (((Element)node).getAttribute("class").equals("bookmark")) {
        		String name = ((Element)node).getAttribute("name");
        		int level = Function.parseInt(((Element)node).getAttribute("level"));
        		if (level == 0) level = 1;
        		while (docLevel >= level) {
        			sb.append("</bookmark>\n");
        			docLevel--;
        		}
        		String text = ((Element)node).getTextContent();
        		sb.append("<bookmark href='#" + name + "' name='" + text + "'>\n");
        		docLevel++;
        	}
		}
        while (docLevel > 0) {
            sb.append("</bookmark>\n");
			docLevel--;
        }
        sb.append("</bookmarks>\n");
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document bm = docBuilder.parse(new InputSource(new StringReader(sb.toString())));
        Element docEle = doc.getDocumentElement();
        Element head = (Element) docEle.getElementsByTagName("head").item(0);
        head.appendChild(doc.importNode(bm.getDocumentElement(), true));
	}	

	public static ITextRenderer getRenderer(WIMap wiMap) throws Exception {
		ITextRenderer renderer = new ITextRenderer();
		String path = wiMap.get("wi.proj.path");
		File[] fonts = new File(path, "WEB-INF/fonts").listFiles();
		for (int i = 0; fonts != null && i < fonts.length; i++) {
			if (fonts[i].getName().endsWith(".ttf")) {
				String font = fonts[i].getAbsolutePath();
				renderer.getFontResolver().addFont(font, BaseFont.EMBEDDED);
			}
		}
		return renderer;
	}	

	private static Document generalizeDoc(Document tidyDoc) throws Exception {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer tx = tfactory.newTransformer();
		DOMSource source = new DOMSource(tidyDoc);
		DOMResult result = new DOMResult();
		tx.transform(source, result);
		return (Document) result.getNode();
	}
		
}
