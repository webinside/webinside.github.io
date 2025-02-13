package br.com.webinside.runtime.export;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class GridPdf extends GridBase {
	
	private Document document = null;
    private boolean exportAll = false;

	public GridPdf(ExecuteParams wiParams) {
		super(wiParams);
	}

	@Override
	protected void before(ResultSet rs) throws Exception {
		document = new Document();
		HttpServletResponse response = wiParams.getHttpResponse();
		response.setContentType("application/pdf");
		String filename = gridSql.getDescription();
		filename = StringA.changeChars(filename, " ", "_");
		response.setHeader("Content-Disposition", "inline; filename=" + filename + ".pdf");
		PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
		createParam(node);
        if (node.getExportOut().toLowerCase().startsWith("all")) exportAll = true;
		if (rs.rowCount() == 0) {
	    	Paragraph par = new Paragraph();
	    	par.setAlignment(Element.ALIGN_CENTER);
	    	par.add("Nenhum registro encontrado");
	    	document.add(par);
		}
	}

	@Override
	protected void iteration(ResultSet rs, int pos) throws Exception {
		if (!exportAll && getExportOutList(node).size() == 0) {
			document.add(new Chunk());
			return; 
		}
    	WIMap auxMap = wiParams.getWIMap().cloneMe();
    	auxMap.putAll(rs.columns(""));
    	if (pos > 1) {
    		DottedLineSeparator ls = new DottedLineSeparator();
            ls.setOffset(5);
            document.add(new Chunk(ls));        
            document.add(Chunk.NEWLINE);		
    	}
    	int col = 1;
        if (exportAll) {
            for (String name : rs.columnNames()) {
            	String value = Producer.execute(auxMap, rs.column(name));
            	String rText = (col == 1) ? "Reg: " + pos : "";
            	line(name, value, rText);
            	col++;
    		}
        } else {
            for (String[] str : getExportOutList(node)) {
            	String value = Producer.execute(auxMap, str[1]);
            	String rText = (col == 1) ? "Reg: " + pos : "";
            	line(str[0], value, rText);
            	col++;
            }
        }
	}

	@Override
	protected void after() throws IOException {
		document.close();
	}
	
	private void createParam(GridNode node) throws Exception {
		if (getExportInList(node).size() == 0) return;
		String imgLogo = getExportInLogo(node);
		if (!imgLogo.equals("")) {
			String imgPath = StringA.piece(imgLogo, "(", 1);
			File imgFile = new File(wiParams.getServletContext().getRealPath(imgPath));
			if (imgFile.isFile()) {
				Image img = Image.getInstance(imgFile.getAbsolutePath());
				if (imgLogo.toLowerCase().indexOf("(center)") > -1) {
					img.setAlignment(Image.ALIGN_CENTER);
				} else if (imgLogo.toLowerCase().indexOf("(right)") > -1) {
					img.setAlignment(Image.ALIGN_RIGHT);
				}
		        document.add(img);		
		        document.add(new Paragraph(""));		
			}
		}
        for (String[] str : getExportInList(node)) {
        	if (str[0].equalsIgnoreCase("logo")) continue;
        	line(str[0], str[1], "");
        }  
        LineSeparator ls = new LineSeparator();
        ls.setOffset(5);
        document.add(new Chunk(ls));        
        document.add(Chunk.NEWLINE);		
	}
	
	private void line(String key, String value, String rText) throws Exception {
    	Phrase phrase = new Phrase();      
    	phrase.getFont().setSize(10);
		Chunk id = new Chunk(key + ": ");
        id.getFont().setStyle(Font.BOLD);
        phrase.add(id);
    	phrase.add(new Chunk(value));
    	if (!rText.equals("")) {
	    	Chunk glue = new Chunk(new VerticalPositionMark());
	    	phrase.add(new Chunk(glue));
	    	phrase.add(rText);
    	}
    	document.add(phrase);
        document.add(Chunk.NEWLINE);		
	}

}
