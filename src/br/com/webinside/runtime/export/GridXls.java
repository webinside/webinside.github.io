package br.com.webinside.runtime.export;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class GridXls extends GridBase {
	
	private XSSFWorkbook workbook = null;
    private XSSFSheet exportSheet = null;
    private XSSFSheet paramSheet = null;
    private boolean exportAll = false;

	public GridXls(ExecuteParams wiParams) {
		super(wiParams);
	}

	@Override
	protected void before(ResultSet rs) {
		workbook = new XSSFWorkbook();
		exportSheet = workbook.createSheet("Exportação");
		exportSheet.createFreezePane(0,1);        
		createParamSheet(node);
        Row row = exportSheet.createRow(0);
        int col = 0;
        if (node.getExportOut().toLowerCase().startsWith("all")) {
            for (String name : rs.columnNames()) {
                Cell cell = row.createCell(col);
                cell.setCellValue(name);
                cell.setCellStyle(getTitleStyle());
                col++;
    		}
        	exportAll = true;
        } else {
            for (String[] str : getExportOutList(node)) {
                Cell cell = row.createCell(col);
                cell.setCellValue(str[0]);
                cell.setCellStyle(getTitleStyle());
                col++;
    		}
        }
	}

	@Override
	protected void iteration(ResultSet rs, int pos) {
    	WIMap auxMap = wiParams.getWIMap().cloneMe();
    	auxMap.putAll(rs.columns(""));
        Row row = exportSheet.createRow(pos);
        if (exportAll) {
        	for (int i = 0; i < rs.columnNames().length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rs.column(i+1));
			}
        } else {
            int col = 0;
            for (String[] str : getExportOutList(node)) {
            	Cell cell = row.createCell(col);
            	String value = Producer.execute(auxMap, str[1]);
                cell.setCellValue(value);
                col++;
            }
        }
	}

	@Override
	protected void after() throws IOException {
		for (int i = 0; i < exportSheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			exportSheet.autoSizeColumn(i);
	    }
		if (paramSheet != null) {
			for (int i = 0; i < paramSheet.getRow(0).getPhysicalNumberOfCells(); i++) {
				paramSheet.autoSizeColumn(i);
		    }
		}
		HttpServletResponse response = wiParams.getHttpResponse();
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String filename = gridSql.getDescription();
		filename = StringA.changeChars(filename, " ", "_");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
        workbook.write(wiParams.getOutputStream());
        response.flushBuffer();
	}
	
	private void createParamSheet(GridNode node) {
		int size = getExportInList(node).size();
		if (size == 0 || (size == 1 && !getExportInLogo(node).equals(""))) return;
        paramSheet = workbook.createSheet("Parâmetros");
        Row row0 = paramSheet.createRow(0);
        Cell cell0 = row0.createCell(0);
        cell0.setCellValue("Parâmetro");
        cell0.setCellStyle(getTitleStyle());
        Cell cell1 = row0.createCell(1);
        cell1.setCellValue("Valor");
        cell1.setCellStyle(getTitleStyle());
        int line = 1;
        for (String[] str : getExportInList(node)) {
        	if (str[0].equalsIgnoreCase("logo")) continue;
            Row row = paramSheet.createRow(line);
            cell0 = row.createCell(0);
            cell0.setCellValue(str[0]);
            cell1 = row.createCell(1);
            cell1.setCellValue(str[1]);
            line++;
        }    
	}
	
	private CellStyle getTitleStyle() {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
//        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        style.setWrapText(true);
        return style;
	}

}
