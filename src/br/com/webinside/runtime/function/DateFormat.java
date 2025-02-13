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

package br.com.webinside.runtime.function;

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.10 $
 */
public class DateFormat extends AbstractFunction {
    /** DOCUMENT ME! */
    private static final String ANSI_FORMAT = "yyyy-MM-dd";
    /** DOCUMENT ME! */
    private static final String EXTENSO= "ext";
    /** DOCUMENT ME! */
    private static final String EXTENSO2 = "ext2";
    /** DOCUMENT ME! */
    private static final String EXTENSO3 = "ext3";
    /** DOCUMENT ME! */
    private static final String EXTENSO4 = "ext4";
    /** DOCUMENT ME! */
    private static final String FORMAT = "fmt";
    /** DOCUMENT ME! */
    private static final String FORMAT_DMY = "FMTdmy";
    /** DOCUMENT ME! */
    private static final String FORMAT_DMY2 = "FMTdmy2";
    /** DOCUMENT ME! */
    private static final String FORMAT_DMYHM = "FMTdmyhm";
    /** DOCUMENT ME! */
    private static final String FORMAT_DMYHMS = "FMTdmyhms";
    /** DOCUMENT ME! */
    private static final String FORMAT_TSEC = "FMTtsec";
    /** DOCUMENT ME! */
    private static final String FORMAT_TSEC2 = "FMTtsec2";
    /** DOCUMENT ME! */
    private static final String FORMAT_TSECT = "FMTtsect";
    /** DOCUMENT ME! */
    private static final String FORMAT_TSECT2 = "FMTtsect2";
    /** DOCUMENT ME! */
    private static final String FORMAT_YMD = "fmtymd";
    /** DOCUMENT ME! */
    private static final String FORMAT_YMDHM = "FMTymdhm";
    /** DOCUMENT ME! */
    private static final String FORMAT_YMDHMS = "FMTymdhms";
    /** DOCUMENT ME! */
    private static final String FORMAT_HM = "FMThm";
    /** DOCUMENT ME! */
    private static final String FORMAT_HMS = "FMThms";
    /** DOCUMENT ME! */
    private static final String INCREMENT = "inc";
    /** DOCUMENT ME! */
    private static final String DECREMENT = "dec";
    /** DOCUMENT ME! */
    private static final String SUB = "sub";
    /** DOCUMENT ME! */
    private static final String WDAY = "wday";
    /** DOCUMENT ME! */
    private static final String DAYNAME = "dayname";
    
    /**
     * Creates new DateFormat
     */
    public DateFormat() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) {
        if (args.length < 2) {
            return "";
        }
        String date = args[0].trim();
        String action = args[1].trim();
        String arg3 = "";
        String arg4 = "";
        if (args.length >= 3) {
            arg3 = args[2];
        }
        if (args.length >= 4) {
            arg4 = args[3];
        }
        String result = "";

        // retorna vazio para data nula
        if (date.equals("0000-00-00")) {
            return "";
        }
        if (date.equals("") || date.equals("&nbsp;")) return "";
        if (action.equalsIgnoreCase(EXTENSO)) {
            result = doExtenso(date, java.text.DateFormat.FULL);
        } else if (action.equalsIgnoreCase(EXTENSO2)) {
        	result = doExtenso(date, java.text.DateFormat.LONG);
        } else if (action.equalsIgnoreCase(EXTENSO3)) {
            result = doFormat(date, "yyyy-MM-dd", "dd MMM yyyy");
        } else if (action.equalsIgnoreCase(EXTENSO4)) {
        	String dia = doFormat(date, "yyyy-MM-dd", "dd");
        	if (dia.equals("01")) dia = "1º";
        	String mes_ano = doFormat(date, "yyyy-MM-dd", "'de' MMMM 'de' yyyy");
        	result = dia + " " + mes_ano;
        } else if (action.equalsIgnoreCase(INCREMENT)) {
            result = doIncrement(date, arg3, arg4);
        } else if (action.equalsIgnoreCase(DECREMENT)) {
            result = doIncrement(date, "-" + arg3, arg4);
        } else if (action.equalsIgnoreCase(SUB)) {
            result = doSub(date, arg3);
        } else if (action.equalsIgnoreCase(WDAY)) {
            result = weekDay(date, arg3);
        } else if (action.equalsIgnoreCase(DAYNAME)) {
            result = dayname(date, arg3);
        } else if (action.equalsIgnoreCase(FORMAT)) {
            result = doFormat(date, arg3, arg4);
        } else if (action.equalsIgnoreCase(FORMAT_DMY)) {
            result = doFormat(date, "yyyy-MM-dd", "dd/MM/yyyy");
        } else if (action.equalsIgnoreCase(FORMAT_DMY2)) {
            result = doFormat(date, "yyyy-MM-dd", "dd-MM-yyyy");
        } else if (action.equalsIgnoreCase(FORMAT_DMYHM)) {
        	if (date.length() == 10) date += " 00:00:00";
        	if (date.length() == 16) date += ":00";
            result = doFormat(date, "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm");
        } else if (action.equalsIgnoreCase(FORMAT_DMYHMS)) {
        	if (date.length() == 10) date += " 00:00:00";
        	if (date.length() == 16) date += ":00";
            result = doFormat(date, "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss");
        } else if (action.equalsIgnoreCase(FORMAT_TSEC)) {
        	result = timesec(date, false, false);
        } else if (action.equalsIgnoreCase(FORMAT_TSEC2)) {
        	result = timesec(date, false, true);
        } else if (action.equalsIgnoreCase(FORMAT_TSECT)) {
        	result = timesec(date, true, false);
        } else if (action.equalsIgnoreCase(FORMAT_TSECT2)) {
        	result = timesec(date, true, true);
        } else if (action.equalsIgnoreCase(FORMAT_YMD)) {
            result = doFormat(date, "dd/MM/yyyy", "yyyy-MM-dd");
	    } else if (action.equalsIgnoreCase(FORMAT_YMDHM)) {
        	if (date.length() == 10) date += " 00:00:00";
        	if (date.length() == 16) date += ":00";
	        result = doFormat(date, "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm");
	    } else if (action.equalsIgnoreCase(FORMAT_YMDHMS)) {
        	if (date.length() == 10) date += " 00:00:00";
        	if (date.length() == 16) date += ":00";
	        result = doFormat(date, "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
	    } else if (action.equalsIgnoreCase(FORMAT_HM)) {
	    	result = date;
	    } else if (action.equalsIgnoreCase(FORMAT_HMS)) {
	    	result = date;
	    }
        return result;
    }

    private String doIncrement(String date, String interval, String intervalType) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(getDateFromPattern(date, ANSI_FORMAT));
        int[] types =
        {
            Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR,
            Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
        };
        if (intervalType.equals("")) {
            intervalType = "D";
        }
        int type = "YMDHmsS".indexOf(intervalType);
        if (type == -1) {
            return "";
        }
    	if (interval.trim().equals("")) {
    		interval = "0";
    	}
        gc.add(types[type], Integer.parseInt(interval));
        SimpleDateFormat sdf = new SimpleDateFormat(ANSI_FORMAT);
        return sdf.format(gc.getTime());
    }

    private String doSub(String newerDate, String olderDate) {
        Date d1 = getDateFromPattern(olderDate, ANSI_FORMAT);
        Date d2 = getDateFromPattern(newerDate, ANSI_FORMAT);
        long dif = d2.getTime() - d1.getTime();
        dif = dif / 1000 / 60 / 60 / 24;
        return String.valueOf(dif);
    }

    private String doExtenso(String date, int type) {
        Date d = null;
        if (date.equals("")) {
            // se a data vier vazia pegue a data atual do sistema
            d = Calendar.getInstance().getTime();
        } else {
            // pega a data no formato ANSI
            d = getDateFromPattern(date, ANSI_FORMAT);
        }
        return java.text.DateFormat.getDateInstance(type, getLocale()).format(d);
    }

    private Date getDateFromPattern(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(date, new ParsePosition(0));
    }

    private String weekDay(String date, String oldPattern) {
        if (!oldPattern.equals("")) {
            Date d = getDateFromPattern(date, oldPattern);
            if (d != null) {
            	Calendar cal = new GregorianCalendar();
            	cal.setTime(d);
            	return cal.get(Calendar.DAY_OF_WEEK) + "";
            }
        }
        return "";
    }
    
    private String timesec(String date, boolean text, boolean reduce) {
    	long fullsecs = Function.parseLong(date);
    	long days = (fullsecs / (3600 * 24));
    	long hours = (fullsecs / 3600) % 24;
    	long mins = (fullsecs / 60) % 60;
    	long secs = fullsecs % 60;
    	String aux = "";
    	if (text && reduce) secs = 0;
    	if (secs > 0 || !text) {
    		String s = String.format("%d", secs);
    		if (!text && s.length() < 2) s = "0" + s;
    		aux = !text ? s : s + " seg";
    	}
    	if (mins > 0 || !text) {
    		String m = String.format("%d", mins);
    		if (!text && m.length() < 2) m = "0" + m;
    		aux = !text ? m + ":" + aux : m + " min " + aux;
    	}
    	if (hours > 0 || !text) {
    		if (!text && reduce) {
    			hours += days * 24;
    			days = 0;
    		}
    		String h = String.format("%d", hours);
    		if (!text && h.length() < 2) h = "0" + h;
    		aux = !text ? h + ":" + aux : h + " hr " + aux;
    	}
    	if (days > 0) {
    		String d = String.format("%d", days);
    		String txt = (days == 1 ? "dia" : "dias");
    		aux = !text ? d + "d " + aux : d + " " + txt + " " + aux;
    	}
    	if (text && secs == 0 && mins == 0 && hours == 0 && days == 0) {
    		aux = reduce ? fullsecs % 60 + " seg" : "0 seg";
    	}
    	return aux;
    }

    private String dayname(String day, String type) {
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        if (type.trim().equalsIgnoreCase("short")) {
        	weekdays = new DateFormatSymbols().getShortWeekdays(); 
        }
        int nday = Function.parseInt(day);
        if (nday > 0 && nday < 8) {
        	String ret = weekdays[nday];
        	if (type.trim().equalsIgnoreCase("simple")) {
        		ret = StringA.piece(ret, "-", 1);
        	}
        	return ret;
        }
        return "";
    }
    
    private String doFormat(String date, String oldPattern, String newPattern) {
        if (oldPattern.equals("") || newPattern.equals("")) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(newPattern, getLocale());
        Date d = getDateFromPattern(date, oldPattern);
        if (d != null) {
            return sdf.format(d);
        } else {
        	d = getDateFromPattern(date, newPattern);
        	return (d != null) ? date : "";
        }
    }
}
