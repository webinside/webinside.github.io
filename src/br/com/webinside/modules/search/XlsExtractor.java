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

package br.com.webinside.modules.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Classe para extrair texto de planilhas Excel
 * 
 * @author Luiz Ruiz
 */
public class XlsExtractor {

    /**
     * Constructor
     */
    public XlsExtractor() {
    }

    public String extractText(InputStream in) throws IOException  {
        StringBuffer text = new StringBuffer();
        HSSFWorkbook hwb = new HSSFWorkbook(in);
        for (int i = 0; i < hwb.getNumberOfSheets(); i++) {
            HSSFSheet sheet = hwb.getSheetAt(i);
            for (Iterator itRow = sheet.rowIterator(); itRow.hasNext();) {
                StringBuffer rowText = new StringBuffer();
                HSSFRow row = (HSSFRow) itRow.next();
                for (Iterator itCell = row.cellIterator(); itCell.hasNext();) {
                    HSSFCell cell = (HSSFCell) itCell.next();
                    String content = "";
                    switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_STRING:
                        content = cell.getStringCellValue();
                    	break;
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        content = Double.toString(cell.getNumericCellValue());
                    	break;
                    }
                    content = content.trim();
                    if (! "".equals(content)) {
                        rowText.append(content).append(" ");
                    }
                }
                String content = rowText.toString().trim();
                if (! "".equals(content)) {
                    text.append(content).append("\n");
                }
            }
        }
        
        return text.toString();
    }
}
