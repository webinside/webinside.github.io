package br.com.webinside.runtime.lw.func.diario;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import br.com.webinside.runtime.util.WIMap;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HtmlToPdfTabularHeader extends PdfPageEventHelper {

	private String lTitle = null;
	private String cTitle = null;
	private String rTitle = null;
	private BufferedImage logo = null;
	
	public HtmlToPdfTabularHeader(WIMap wiMap, String lTitle, String cTitle, String rTitle) {
		super();
		this.lTitle = lTitle;
		this.cTitle = cTitle;
		this.rTitle = rTitle;
		try {
			String projPath = wiMap.get("wi.proj.path");
			File img = new File(projPath, "/images/diario/brasao_aracaju_mini.png");
			logo = ImageIO.read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onEndPage(PdfWriter writer, Document doc) {
		int page = writer.getPageNumber();
		if (page == 1) return; 
		PdfContentByte cb = writer.getDirectContent();
	    float w = doc.getPageSize().getWidth();
		float h = doc.getPageSize().getHeight();
		int xlogo = 12;
		if (page % 2 == 0) {
			Phrase pg = new Phrase(page + "", FontFactory.getFont("Arial", 12, Font.BOLD));
			ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, pg, 12 , h - 22, 0);
			Phrase p1 = new Phrase("Edição Nº " + rTitle, FontFactory.getFont("Arial", 8));
			ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p1, 60 , h - 22, 0);
			Phrase p2 = new Phrase(lTitle, FontFactory.getFont("Arial", 8));
			int x = (int)doc.getPageSize().getWidth() - 12;
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, p2, x - 20, h - 22, 0);
			xlogo = (int)w - 28;
		} else {
			int x = (int)doc.getPageSize().getWidth() - 12;
			Phrase pg = new Phrase(page + "", FontFactory.getFont("Arial", 12, Font.BOLD));
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, pg, x , h - 22, 0);
			Phrase p1 = new Phrase(lTitle, FontFactory.getFont("Arial", 8));
			ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p1, 33 , h - 22, 0);
			Phrase p2 = new Phrase("Edição Nº " + rTitle, FontFactory.getFont("Arial", 8));
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, p2, x - 50, h - 22, 0);
		}
		Phrase p = new Phrase(cTitle, FontFactory.getFont("Times New Roman", 12, Font.BOLD));
		ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p, w/2 , h - 22, 0);
		if (logo != null) {
			PdfGraphics2D g2d = new PdfGraphics2D(cb, w, h);
			g2d.drawImage(logo, xlogo, 9, null);
			g2d.dispose(); 			
		}
		line(cb, doc, h-8);
	}

	private void line(PdfContentByte cb, Document doc, float hLine) {
	    cb.moveTo(10, hLine);
	    cb.lineTo(doc.getPageSize().getWidth() - 10, hLine);
	    cb.stroke();				    
	}
	
}
