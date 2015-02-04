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

package br.com.webinside.runtime.lw.func;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.lw.img.ImgEnum;
import br.com.webinside.runtime.lw.img.ImgUtil;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class EbookSinglePdf extends AbstractConnector implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			String pdfFile = wiMap.get("tmp.lwr.source_pdf");
			String destFolder = wiMap.get("tmp.lwr.dest_folder");
			if (false) {
				new File(destFolder, "lwreader.xml").delete();
				Function.removeDir(destFolder + "/texts");
				Function.removeDir(destFolder + "/images");
			}
			if (!new File(pdfFile).isFile() || new File(destFolder,"lwreader.xml").exists()) {
				return;
			}
			PDDocument pdfDoc = PDDocument.load(pdfFile);
			int pages = pdfDoc.getNumberOfPages();
			PDPage firstPage = (PDPage)pdfDoc.getDocumentCatalog().getAllPages().get(0);
			int pgW = Math.round(firstPage.findCropBox().getWidth());
			int pgH = Math.round(firstPage.findCropBox().getHeight());
			pdfDoc.close();
			generateXml(wiMap, destFolder, pages, pgW, pgH);
			new File(destFolder + "/texts").mkdirs();
			new FileIO(destFolder + "/texts/index.txt", FileIO.WRITE).writeText("");
			new FileIO(destFolder + "/texts/empty.txt", FileIO.WRITE).writeText("");
			String rndKey = Function.randomKey().toLowerCase();
			String tmpFolder = Function.tmpDir() + "/pdf-" + rndKey;
	        String tmpFile = tmpFolder + "/file.pdf";
	        Function.copyFile(pdfFile, tmpFile, true);
	        // img1
			List<String> cmd = ImgUtil.cmdPdfToImg("", ImgEnum.PNG_COLOR, "x100");
			ImgUtil.execute(cmd, tmpFolder, "file", "pg", false);
			copyImages(tmpFolder, destFolder, 1);
	        // img2
			cmd = ImgUtil.cmdPdfToImg("", ImgEnum.PNG_COLOR, "x900");
			ImgUtil.execute(cmd, tmpFolder, "file", "pg", false);
			copyImages(tmpFolder, destFolder, 2);
	        // img3
			cmd = ImgUtil.cmdPdfToImg("", ImgEnum.PNG_COLOR, "x1600");
			ImgUtil.execute(cmd, tmpFolder, "file", "pg", false);
			copyImages(tmpFolder, destFolder, 3);
			Function.removeDir(tmpFolder);
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
	
	private void copyImages(String tmpFolder, String destFolder, int index) {
		File single = new File(tmpFolder, "pg.png");
		if (single.isFile()) single.renameTo(new File(tmpFolder, "pg-0.png"));
        String[] files = Function.listDir(tmpFolder, "*.png", false, false);
		for (int i = 0; i < files.length; i++) {
        	String srcFile = tmpFolder + "/" + files[i];
        	String destFile = destFolder + "/images/img" + index + "/" + files[i];
        	Function.copyFile(srcFile, destFile, true);
        }
		Function.removeFiles(tmpFolder, "*.png");
	}
	
	public void generateXml(WIMap wiMap, String destFolder, int pgQtd, int pgW, int pgH) 
	throws IOException {
		Element rootEle = new Element("LWReader");
		Element docsEle = new Element("docs");
		rootEle.addContent(docsEle);
		Element docEle = new Element("doc");
		docEle.setAttribute("id", "doc01");
		docsEle.addContent(docEle);
		Element docTitleEle = new Element("title");
		docTitleEle.setText(wiMap.get("tmp.lwr.ebook_title"));
		docEle.addContent(docTitleEle);
		Element indexEle = new Element("index");
		indexEle.setText(wiMap.get("tmp.lwr.dest_url") + "/texts/index.txt");
		docEle.addContent(indexEle);
		Element pages = new Element("pages");
		docEle.addContent(pages);
		for (int c = 0; c < pgQtd; c++) {
			Element pageEle = new Element("page");
/*			
			if (pageData.getTitle() != null) {
				Element pageTitleEle = new Element("title");
				pageTitleEle.setText(pageData.getTitle());
				pageEle.addContent(pageTitleEle);
			}
*/			
			Element sizeEle = new Element("size");
			sizeEle.setText(pgW + "x" + pgH);
			pageEle.addContent(sizeEle);
			Element txtEle = new Element("text");
			txtEle.setText(wiMap.get("tmp.lwr.dest_url") + "/texts/empty.txt");
			pageEle.addContent(txtEle);
			pages.addContent(pageEle);
			for (int i = 1; i <= 3; i++) {
				Element imgEle = new Element("img" + i);
				String imgName = "images/img" + i + "/pg-" + c + ".png";
				imgEle.setText(wiMap.get("tmp.lwr.dest_url") + "/" + imgName);
				pageEle.addContent(imgEle);
			}
		}
		new File(destFolder).mkdirs();
//		File jsFile = new File(wiMap.get("tmp.lwr.dest_folder"), "lwreader.js");
//		new FileIO(jsFile.getAbsolutePath(), 'W').writeText("var ebookPages = " + pgQtd);
		Document doc = new Document(rootEle);
		XMLOutputter out = new XMLOutputter();
		out.setFormat(defineXmlFormat(out.getFormat()));
		File xmlFile = new File(wiMap.get("tmp.lwr.dest_folder"), "lwreader.xml");
		FileWriter fw = new FileWriter(xmlFile);
		out.output(doc, fw);
		fw.close();
	}
	
	private Format defineXmlFormat(Format format) {
		format.setOmitDeclaration(false);
		format.setExpandEmptyElements(false);
		format.setEncoding("ISO-8859-1");
		format.setIndent("    ");
		format.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
		return format;
	}
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[4];
		in[0] = new JavaParameter("tmp.lwr.source_pdf", "Arquigo PDF");
		in[1] = new JavaParameter("tmp.lwr.dest_folder", "Pasta Destino");
		in[2] = new JavaParameter("tmp.lwr.dest_url", "Url Destino");
		in[3] = new JavaParameter("tmp.lwr.ebook_title", "Título do Ebook");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
