package br.com.webinside.runtime.lw.func.diario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class HtmlToPdfTabular {
	
	public static final int FRACTION = 100;
	// BORDER = indica a borda inferior em pixel e o tamanho 
	// das partes faz sobrar a borda superior
	public static final int BORDER = 26;
	
	private ExecuteParams wiParams = null;
	private String lTitle = null;
	private String cTitle = null;
	private String rTitle = null;
	private String pType = null;
	private List<PdfReader> readerList = null;
		
	public HtmlToPdfTabular(ExecuteParams wiParams, String lTitle, String cTitle, 
			String rTitle, String pType) {
		super();
		this.wiParams = wiParams;
		this.lTitle = lTitle;
		this.cTitle = cTitle;
		this.rTitle = rTitle;
		this.pType = pType;
		readerList = new ArrayList<PdfReader>();
	}

	public void execute(File pdfIn, File pdfOut) throws Exception {
		OutputStream out = new FileOutputStream(pdfOut);
	    PdfReader reader = new PdfReader(pdfIn.getAbsolutePath());
	    readerList.add(reader);
	    PdfReaderContentParser parser = new PdfReaderContentParser(reader);
	    Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
	    PdfWriter writer = PdfWriter.getInstance(doc, out);
	    HtmlToPdfTabularHeader event = 
	    		new HtmlToPdfTabularHeader(wiParams.getWIMap(), lTitle, cTitle, rTitle);
	    writer.setPageEvent(event);
	    doc.open();
	    PdfContentByte cb = writer.getDirectContent();
	    int pagePos = 1;
	    boolean capa = true;
	    int fracLine = 2*FRACTION + 1; 
	    int pageTotal = reader.getNumberOfPages();
	    while (pagePos <= pageTotal && capa) {
	    	if (fracLine > 2*FRACTION) {
	    		newPage(doc, cb, true);
	    		fracLine = 1;
	    	}
		    SimpleTextExtractionStrategy ses = new SimpleTextExtractionStrategy();
		    TextExtractionStrategy strategy = parser.processContent(pagePos, ses);
		    String pageText = strategy.getResultantText().toLowerCase();
	    	if (pageText.indexOf("{lw_pdf_first_content_page}") == -1) {
		        PdfImportedPage page = writer.getImportedPage(reader, pagePos);
		    	float h = page.getHeight();
			    cb.addTemplate(page, 0, h * (FRACTION - ((fracLine-1)/2) - 1) + BORDER);
		    	fracLine = fracLine + 2;			    
			    pagePos++;
	    	} else {
	    		capa = false;
	    	}
	    }
	    int drawLastLine = -1;
	    List<PdfImportedPage> pages = new ArrayList<PdfImportedPage>();
	    while (pagePos <= pageTotal) {
	    	if (fracLine > 2*FRACTION) {
	    		newPage(doc, cb, false);
	    		fracLine = 1;
	    	}
	    	boolean highlight = false;
	    	boolean lastHL = false;
	    	String pageText = pageText(parser, pagePos);
		    if (pageText.indexOf("{lw_pdf_highlight[") == -1) {
		    	pages.add(writer.getImportedPage(reader, pagePos));
		    } else {
		    	highlight = true;
		    	lastHL = true;
		    	if (pagePos < pageTotal) {
		    		String nextPageText = pageText(parser, pagePos + 1);
		    		if (nextPageText.indexOf("{lw_pdf_highlight[") > -1) {
		    			lastHL = false;
		    		}
		    	}
		    }
	    	int maxSize = (2*FRACTION - (fracLine - 1)) * 2;
	    	if (pages.size() == maxSize || pagePos == pageTotal || highlight) {
	    		if (pages.size() > 0) {
	    			int h1 = 0, h2 = 0;
			    	int half = pages.size() / 2;
			    	if (pages.size() % 2 > 0) half++;
				    for (int i=0; i < pages.size(); i++) {
				    	PdfImportedPage page = pages.get(i);
				    	float h = page.getHeight() / 2;
				    	if (i < half) {
					        float hPos = h * (2*FRACTION-(i+fracLine)) + BORDER;
				    		h2 = (int)hPos;
				    		if (i == 0) h1 = (int)(hPos + h);
				        	cb.addTemplate(page, 0.5f, 0, 0, 0.5f, 5, hPos);
				    	} else {
					    	float w = page.getWidth() / 2;
					        float hPos = h * (2*FRACTION-(i+fracLine-half)) + BORDER;
				        	cb.addTemplate(page, 0.5f, 0, 0, 0.5f, w-5, hPos);
				    	}
				    }
			    	float w = doc.getPageSize().getWidth() / 2;
				    cb.moveTo(w, h1+5);
				    cb.lineTo(w, h2);
				    cb.stroke();				    
				    drawLastLine = h2;
				    fracLine = fracLine + half; 
				    pages.clear();
	    		}
	    	}
		    if (highlight) {
		    	fracLine = highlight(readerList, doc, writer, cb, fracLine, pageText, lastHL);
		    	drawLastLine = -1;
		    }
	    	pagePos++;
	    }
	    if (drawLastLine > -1 && false) {
		    cb.moveTo(10, drawLastLine - 5);
		    cb.lineTo(doc.getPageSize().getWidth() - 10, drawLastLine - 5);
		    cb.stroke();				    
	    }
	    closeDoc(doc);
	}

	private int highlight(List<PdfReader> readerList, Document doc, 
			PdfWriter writer, PdfContentByte cb, int fracLine, 
			String pageText, boolean lastHighlight) throws Exception {
		String values = StringA.piece(pageText, "[", 2);
		values = StringA.piece(values, "]", 1);
		int idCont = Function.parseInt(StringA.piece(values, ":", 1));
		int idPag = Function.parseInt(StringA.piece(values, ":", 2));
		String pdfFolder = wiParams.getWIMap().get("pvt.lwpath.priv") + "/diario/destaque";
		File pdfFile = new File(pdfFolder + "/c" + idCont + "p" + idPag + ".pdf");
		if (!pdfFile.isFile()) {
			String pageId = wiParams.getWIMap().get("wi.page.id");
			String msg = "PDF File for page highlight not found";
			wiParams.getErrorLog().write(getClass().getName(), "Page: " + pageId, msg);
			return fracLine;
		}
		PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());
	    readerList.add(reader);
	    int hLine = 0;
	    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
	    	if (fracLine > 2*FRACTION) {
	    		newPage(doc, cb, false);
	    		fracLine = 1;
	    	}
	        PdfImportedPage page = writer.getImportedPage(reader, i);
	        float hPos = page.getHeight() * (2*FRACTION-fracLine) + BORDER;
	        if (i == 1 && false) {
	    	    cb.moveTo(10, hPos);
	    	    cb.lineTo(doc.getPageSize().getWidth() - 10, hPos);
	    	    cb.stroke();				    
	        }
		    cb.addTemplate(page, 0, hPos);
		    hLine = (int)hPos;
		    fracLine++;
	    }
	    if (lastHighlight && false) {
		    cb.moveTo(10, hLine + 5);
		    cb.lineTo(doc.getPageSize().getWidth() - 10, hLine + 5);
		    cb.stroke();				    
	    }
		return fracLine;
	}
	
	private void newPage(Document doc, PdfContentByte cb, boolean first) throws Exception {
		doc.newPage();
		float w = doc.getPageSize().getWidth();
		float h = doc.getPageSize().getHeight();
		if (first) {
			if (pType.equals("E")) {
				h = h - 210;
			} else {
				h = h - 180;
			}
		}
	    cb.moveTo(10, h - 28);
	    cb.lineTo(w - 10, h - 28);
	    cb.lineTo(w - 10, 24);
	    cb.lineTo(10, 24);
	    cb.lineTo(10, h - 28);
	    cb.stroke();				    
	}
	
	private String pageText(PdfReaderContentParser parser, int pos) throws Exception {
	    SimpleTextExtractionStrategy ses = new SimpleTextExtractionStrategy();
    	TextExtractionStrategy strategy = parser.processContent(pos, ses);
	    return strategy.getResultantText().toLowerCase().trim();
	}
	
	private void closeDoc(Document doc) {
	    doc.close();
	    for (PdfReader it : readerList) {
			it.close();
		}
	}
	
}
