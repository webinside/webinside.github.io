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
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.webinside.runtime.component.AbstractFileList;
import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.FileListFtp;
import br.com.webinside.runtime.component.FileListLocal;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.ClientFtp;
import br.com.webinside.runtime.net.FtpEntry;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.StringComparator;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreFileList extends CoreCommon {
    private AbstractFileList fileList;
    private int countid;

    /**
     * Creates a new CoreFileList object.
     *
     * @param wiParams DOCUMENT ME!
     * @param list DOCUMENT ME!
     */
    public CoreFileList(ExecuteParams wiParams, AbstractFileList list) {
        this.wiParams = wiParams;
        this.fileList = list;
        element = list;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        if (fileList instanceof FileListLocal) {
            local();
        }
        if (fileList instanceof FileListFtp) {
            remote((FileListFtp) fileList);
        }
        writeLog();
    }

    private void local() {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(fileList.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String directory = prod.getOutput();
        if (!wiParams.getProject().getGrids().containsKey(fileList.getGridId())) {
            wiParams.includeCode("/grids/" + fileList.getGridId() + "/grid.jsp");
        }
        AbstractGrid grid =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(fileList
                        .getGridId());
        if ((grid != null) && !(grid instanceof GridHtml)) {
            grid = null;
        } else if (grid instanceof GridSql) {
            grid = null;
        }
        File dir = new File(directory);
        if ((grid == null) || (!dir.exists()) || (!dir.isDirectory())) {
            String msg = "Directory not found (" + dir + ")";
            String jspFile = wiMap.get("wi.jsp.filename");
            wiParams.getErrorLog().write(jspFile, fileList.getDescription(), msg);
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                Exception ex = new FileNotFoundException(msg);
                wiParams.setRequestAttribute("wiException", ex);
            }
            return;
        }
        Map repository = new HashMap();
        List dnames = new ArrayList();
        List fnames = new ArrayList();
        prod.setInput(fileList.getMask());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String fullMask = prod.getOutput();
        int cont = StringA.count(fullMask, ',');
        for (int i = 1; i <= (cont + 1); i++) {
            String mask = StringA.piece(fullMask, ",", i).trim();
            String[] files = Function.listDir(directory, mask, false);
            for (int a = 0; a < files.length; a++) {
                String name = files[a].trim();
                if (!repository.containsKey(name.toLowerCase())) {
                    repository.put(name.toLowerCase(), "");
                    File fl = new File(directory, name);
                    if (fl.isDirectory()) {
                        dnames.add(name);
                    } else {
                        fnames.add(name);
                    }
                }
            }
        }
        countid = 0;
        List mapList = new ArrayList();
        addLocal(directory, mapList, dnames, 'D');
        addLocal(directory, mapList, fnames, 'F');
		Map[] array = (Map[])mapList.toArray(new Map[0]); 
        GridLinearNavigator linear = new GridLinearNavigator(wiParams);
        linear.execute((AbstractGridLinear) grid, array, 1, false);
    }

    private void remote(FileListFtp lstftp) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(fileList.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String directory = prod.getOutput();
        Host host =
            wiParams.getProject().getHosts().getHost(lstftp.getHostId());
        ClientFtp ftp = null;
        if ((host != null) && (host.getProtocol().equals("FTP"))) {
            ftp = new ClientFtp(host.getAddress(), host.getUser(),
                    host.getPass());
            if (ftp.isConnected()) {
                if (!ftp.existDir(directory)) {
                    ftp.close();
                    ftp = null;
                }
            } else {
                EngFunction.hostError(wiParams, lstftp.getHostId());
                return;
            }
        }
        if (!wiParams.getProject().getGrids().containsKey(fileList.getGridId())) {
            wiParams.includeCode("/grids/" + fileList.getGridId() + "/grid.jsp");
        }
        AbstractGrid grid =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(fileList
                        .getGridId());
        if ((grid != null) && !(grid instanceof GridHtml)) {
            grid = null;
        } else if (grid instanceof GridSql) {
            grid = null;
        }
        if (grid == null) {
            return;
        }
        Map repository = new HashMap();
        FtpEntry[] dnames = new FtpEntry[0];
        FtpEntry[] fnames = new FtpEntry[0];
        prod.setInput(fileList.getMask());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String fullMask = prod.getOutput();
        int cont = StringA.count(fullMask, ',');
        for (int i = 1; i <= (cont + 1); i++) {
            String mask = StringA.piece(fullMask, ",", i).trim();
            FtpEntry[] list = ftp.listFull(directory);
            for (int a = 0; a < list.length; a++) {
                FtpEntry entry = list[a];
                String name = entry.getName();
                if (!repository.containsKey(name.toLowerCase())) {
                    if (Function.validateMask(name, mask, false)) {
                        repository.put(name.toLowerCase(), "");
                        if (entry.isDirectory()) {
                            dnames = entryAdd(dnames, entry);
                        } else {
                            fnames = entryAdd(fnames, entry);
                        }
                    }
                }
            }
        }
        ftp.close();
        List mapList = new ArrayList();
        addRemote(mapList, dnames, 'D');
        addRemote(mapList, fnames, 'F');
		Map[] array = (Map[])mapList.toArray(new Map[0]); 
        GridLinearNavigator linear = new GridLinearNavigator(wiParams);
        linear.execute((AbstractGridLinear) grid, array, 1, false);
    }

    private void addLocal(String directory, List mapList, List lista,
        char type) {
        StringComparator sc = new StringComparator();
        sc.setIgnoreCase(true);
        Collections.sort(lista, sc);
        for (int a = 0; a < lista.size(); a++) {
            countid = countid + 1;
            Map aux = new HashMap();
            File fl = new File(directory, (String)lista.get(a));
            aux.put("rowid", countid + "");
            aux.put("rowid0", countid - 1 + "");
            aux.put("name", (String)lista.get(a));
            aux.put("type", "" + type);
            aux.put("size", fl.length() + "");
            SimpleDateFormat frmdata =
                new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
            String mydate = frmdata.format(new Date(fl.lastModified()));
            aux.put("date", StringA.piece(mydate, ",", 1));
            aux.put("time", StringA.piece(mydate, ",", 2));
            mapList.add(aux);
        }
    }

    private FtpEntry[] entryAdd(FtpEntry[] array, FtpEntry entry) {
        if (array == null) {
            array = new FtpEntry[0];
        }
        FtpEntry[] resp = new FtpEntry[array.length + 1];
        for (int a = 0; a < array.length; a++) {
            String newname = entry.getName();
            String name = array[a].getName();
            if (name.compareTo(newname) <= 0) {
                resp[a] = array[a];
            } else {
                resp[a] = entry;
                entry = array[a];
            }
        }
        resp[array.length] = entry;
        return resp;
    }

    private void addRemote(List mapList, FtpEntry[] entrysList, char type) {
        if (entrysList != null) {
	        for (int a = 0; a < entrysList.length; a++) {
	            countid = countid + 1;
	            FtpEntry entry = entrysList[a];
	            Map aux = new HashMap();
	            aux.put("rowid", countid + "");
	            aux.put("rowid0", countid - 1 + "");
	            aux.put("name", entry.getName());
	            aux.put("type", "" + type);
	            aux.put("size", entry.getSize() + "");
	            String month = convertMonth(entry.getMonth());
	            String date = entry.getDay() + "/" + month + "/" + entry.getYear();
	            aux.put("date", date);
	            String time = entry.getHour();
	            if (time.equals("")) {
	                time = "00:00";
	            }
	            aux.put("time", time + ":00");
	            aux.put("permission", entry.getPermission());
	            aux.put("owner", entry.getOwn());
	            aux.put("group", entry.getGroup());
	            mapList.add(aux);
	        }
        }
    }

    private String convertMonth(String month) {
        if ((month == null) || (month.equals(""))) {
            return "";
        }
        month = month.trim().toUpperCase();
        if (month.equals("JAN")) {
            return "01";
        }
        if (month.equals("FEB")) {
            return "02";
        }
        if (month.equals("MAR")) {
            return "03";
        }
        if (month.equals("APR")) {
            return "04";
        }
        if (month.equals("MAY")) {
            return "05";
        }
        if (month.equals("JUN")) {
            return "06";
        }
        if (month.equals("JUL")) {
            return "07";
        }
        if (month.equals("AUG")) {
            return "08";
        }
        if (month.equals("SEP")) {
            return "09";
        }
        if (month.equals("OCT")) {
            return "10";
        }
        if (month.equals("NOV")) {
            return "11";
        }
        if (month.equals("DEC")) {
            return "12";
        }
        return month;
    }
}
