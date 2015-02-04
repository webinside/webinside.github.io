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

package br.com.webinside.runtime.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.MimeType;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * @author Luiz Ruiz
 *
 */
public final class CoreReportShow {

    private final ExecuteParams wiParams;
    private final WIMap wiMap;
    private String outputDir = "";

    public CoreReportShow (WIMap wiMap, ExecuteParams wiParams) {
        this.wiMap = wiMap;
        this.wiParams = wiParams;        
    }

    public void showReport() {
        HttpServletResponse response = wiParams.getHttpResponse();
        WISession session = wiParams.getWISession();
        String projId = wiMap.get("wi.proj.id");
        wiMap.put("reportProject", projId);
        String reportPath = wiMap.get("reportFileName");
        JRReport report = null;
        Object source = null;
        try {
            if (reportPath.equals("")) {
                throw new UserException("Report file name is empty.");
            }
            wiMap.put("wi.report.path", new File(reportPath).getParent() + "/");
            try {
                report = (JRReport) JRLoader.loadObject(reportPath);
            } catch (JRException e) {
                throw new UserException("Error on load report " + reportPath);
            }
            Map param = getParametersMap(report);
            source = getSource(param);
            if (source == null) {
                source = new JREmptyDataSource();
            }
            outputDir = wiMap.get("rpt.outputDir");
            String type = wiMap.get("rpt.outputFormat").toUpperCase();
            if (type.equals("")) {
                type = "PDF";
            }
            wiMap.remove("rpt.");
            param = ReportFunction.wiMapConverter(wiMap);
            JasperReport jasper = (JasperReport) report;
            if (type.equals("PDF")) {
                JRExporter exporter = new JRPdfExporter();
                fillReport(response, jasper, param, source, exporter, "pdf");
            } else if (type.equals("HTML")) {
            	// Formato HTML não pode gerar arquivo
            	if (!"".equals(outputDir)) {
            		return;
            	}
                String imgPath = "tmpimg/";
                JRExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
                        imgPath);

                String uri = imgPath + session.getId() + "/";
                File f = new File(wiMap.get("wi.proj.path"), uri);
                f.mkdirs();
                param.put("IMAGE_DIR", f);
                uri = "/" + wiMap.get("wi.proj.id") + "/" + uri;
                param.put("IMAGE_URI", uri);
                fillReport(response, jasper, param, source, exporter, "html");
            } else if (type.equals("CSV")) {
                JRExporter exporter = new JRCsvExporter();
                fillReport(response, jasper, param, source, exporter, "csv");
            } else if (type.equals("XLS")) {
                
                JRXlsExporter xls = new JRXlsExporter();
                xls.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
                xls.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
                xls.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xls.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

                JRExporter exporter = xls;
                
                fillReport(response, jasper, param, source, exporter, "xls");
            }
        } catch (Exception e) {
        	ServletContext sc = wiParams.getServletContext();
            String logDir = sc.getRealPath("/WEB-INF/logs");
        	ErrorLog prjLog = wiParams.getErrorLog(); 
        	if (prjLog != null) {
                logDir = prjLog.getParentDir();
        	}
            ErrorLog log = ErrorLog.getInstance(logDir, "wireport.log");
            log.write(getClass().getName(), "Error on show report.", e);
            String msg = e.getMessage();
            if (null == msg) {
                Writer sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                msg = StringA.piece(sw.toString(), ".HttpServlet.", 1) + "...";
            }
            showError(response, e.getMessage(), false);
        } finally {
            if (source != null) {
            	if (source instanceof ConnectionSql) {
                    ((ConnectionSql) source).close();
            	} else if (source instanceof Connection) {
                    try {
                        ((Connection) source).close();
                    } catch (Exception e) {
                    	// ignorado
                    }
            	}	
            }
        }
    }

    private Object getSource(Map param) throws UserException {
        Object source = null;
        JRParameter dbParam = (JRParameter) param.get("reportDB");
        if (dbParam != null) {
            String database = dbParam.getDefaultValueExpression().getText();
            database = database.replaceAll("\"", "");
            wiMap.put("database", database);
            if (database.equals("")) {
                source = new JREmptyDataSource();
            } else {
                source = ReportFunction.getWIConnection(wiMap);
                if (source == null) {
                    source = ReportFunction.getConnection(database);
                }
                if (source == null) {
                    //source = new JREmptyDataSource(); 
                    throw new UserException("Error on getting datasource for: "
                            + database);
                }
            }
        }
        return source;
    }

    private Map getParametersMap(JRReport report) {
        Map params = new HashMap();
        for (int i = 0; i < report.getParameters().length; i++) {
            JRParameter p = report.getParameters()[i];
            params.put(p.getName(), p);
        }
        return params;
    }

    private JasperPrint fillReport(HttpServletResponse response,
            JasperReport report, Map parameters, Object objDatasource,
            JRExporter exporter, String ext) throws JRException, IOException {
        if (response == null || objDatasource == null) {
            return null;
        }
        boolean toFile = ! "".equals(outputDir); 
        OutputStream out = null;
        String fileName = "";
        if (ext.equals("html")) {
            out = response.getOutputStream();
        } else {
            if (toFile) {
                fileName = outputDir + "/" + report.getName() + "." + ext;
            } else {
                fileName = Function.rndTmpFile("report", ext);
            }
            new File(fileName).getParentFile().mkdirs();
            try {
                out = new FileOutputStream(fileName);
            } catch (FileNotFoundException e) {
                throw new IOException("Erro ao gravar o relatório: " +
                		fileName);
            }
        }
        response.setHeader("Cache-Control", "must-revalidate, max-age=0");
        response.setDateHeader("Expires", new Date().getTime() + 5000);
        if (objDatasource == null)
            return null;

        JasperPrint jasperPrint = null;
        if (objDatasource instanceof ConnectionSql) {
        	objDatasource = ((ConnectionSql) objDatasource).getConnection();
        }
        if (objDatasource instanceof JRDataSource) {
            jasperPrint = JasperFillManager.fillReport(report, parameters,
                    (JRDataSource) objDatasource);
        } else if (objDatasource instanceof Connection) {
            Connection conn = (Connection) objDatasource;
            jasperPrint = JasperFillManager
                    .fillReport(report, parameters, conn);
        }
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,
                "ISO-8859-1");
        if (ext.equals("html")) {
            response.setContentType("text/html");
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR,
                    parameters.get("IMAGE_DIR"));
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
                    parameters.get("IMAGE_URI"));
            exporter.setParameter(
                    JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR,
                    Boolean.TRUE);
            exporter.setParameter(
                    JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
                    Boolean.FALSE);
            StringBuffer preLoad = new StringBuffer();
            preLoad.append("<script>img = new Image();\n");
            preLoad.append("img.src = '");
            preLoad.append(parameters.get("IMAGE_URI"));
            preLoad.append("img_0';</script>\n");
            out.write(preLoad.toString().getBytes());
            StringWriter sw = new StringWriter();
            exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, sw);
            exporter.exportReport();
            out.write(sw.toString().getBytes());
        } else {
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            try {
                exporter.exportReport();
            } catch (Throwable e) {
                new File(fileName).delete();
                out = null;
                throw new JRException(e);
            }
            if (out instanceof FileOutputStream) {
                out.close();
                if (!toFile) {
                    MimeType.readFile(getClass().getClassLoader());
                    response.setContentType(MimeType.get(ext));
                    String dispType = "attachment";
                    if ("pdf".equals(ext)) {
                        dispType = "inline";
                    }
                    String dispname = dispType + "; filename=\"" + report.getName() 
                    	+ "." + ext + "\"";
                    response.setHeader("Content-disposition", dispname);
                    out = response.getOutputStream();
                    byte[] trecho = new byte[10240];
                    int quant = 0;
                    BufferedInputStream in = null;
                    try {
                        in = new BufferedInputStream(new FileInputStream(fileName));
                        while ((quant = in.read(trecho)) > -1) {
                            out.write(trecho, 0, quant);
                            out.flush();
                        }
                    } catch (IOException err) {
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                        new File(fileName).delete();
                    }
                }
            }
        }
        return jasperPrint;
    }

    private void showError(HttpServletResponse response, String err,
            boolean isCompileError) {
        try {
            response.setContentType("text/html");
            response.setHeader("Content-disposition", "inline");

            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            String msg = isCompileError ? "compilar" : "preencher";
            out.println("<html>");
            out.println("<head>");
            out.println("<title>WIReport - Erro ao " + msg
                    + " o relatório</title>");
            out.println("</head>");
            out.println("<body bgcolor=\"white\">");
            out.println("<pre>Erros encontrados ao " + msg + " o relatório:\n");
            out.println(err);
            out.println("</pre>");
            out.println("</body>");
            out.println("</html>");
            response.getOutputStream().println(sw.toString());
            wiParams.setRequestAttribute("wiExit", "true");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
