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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.CurrencyWriter;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class NumberFormat extends AbstractFunction {
    /** DOCUMENT ME! */
    private static final String CURRENCY_BRAZIL = "cbr";
    /** DOCUMENT ME! */
    private static final String CURRENCY_BRAZIL_1 = "cbr1";
    /** DOCUMENT ME! */
    private static final String CURRENCY_BRAZIL_2 = "cbr2";
    /** DOCUMENT ME! */
    private static final String CURRENCY_BRAZIL_3 = "cbr3";
    /** DOCUMENT ME! */
    private static final String CURRENCY_USA = "cus";
    /** DOCUMENT ME! */
    private static final String EXTENSO = "ext";
    /** DOCUMENT ME! */
    private static final String REAL = "real";
    /** DOCUMENT ME! */
    private static final String INTEGER_DIGITS = "int";
    /** DOCUMENT ME! */
    private static final String FRACTION_DIGITS = "frc";
    /** DOCUMENT ME! */
    private static final String ROUND = "round";
    /** DOCUMENT ME! */
    private static final String FORMAT = "fmt";
    /** DOCUMENT ME! */
    private static final String CLEARNUMBER = "clr";
    /** DOCUMENT ME! */
    private static final String[] unidade =
    {
        "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove",
        "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis",
        "dezessete", "dezoito", "dezenove"
    };
    /** DOCUMENT ME! */
    private static final String[] dezena =
    {
        "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta",
        "oitenta", "noventa"
    };
    /** DOCUMENT ME! */
    private static final String[] centena =
    {
        "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
        "seiscentos", "setecentos", "oitocentos", "novecentos"
    };
    /** DOCUMENT ME! */
    private static final String[] casaSingular =
    {"mil", "milhão", "bilhão", "trilhão", "quatrilhão"};
    /** DOCUMENT ME! */
    private static final String[] casaPlural =
    {"mil", "milhões", "bilhões", "trilhões", "quatrilhões"};

    /**
     * Creates new NumberFormat
     */
    public NumberFormat() {
    }

    /**
     * Creates new NumberFormat
     */
    public NumberFormat(WIMap wiMap) {
    	setWiMap(wiMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) {
        if (args.length < 1) return "";

        String number = args[0];
        String action = "";
        String result = "";

        try {
            if (args.length == 1 || args.length == 2) {
                if (args.length == 1) action = CLEARNUMBER;
                if (args.length == 2) action = args[1];
                if (action.equalsIgnoreCase(CURRENCY_BRAZIL)) {
                    result = doCurrency(number, new Locale("pt", "br"), 2, true);
                } else if (action.equalsIgnoreCase(CURRENCY_BRAZIL_1)) {
                    result = doCurrency(number, new Locale("pt", "br"), 1, false);
                } else if (action.equalsIgnoreCase(CURRENCY_BRAZIL_2)) {
                    result = doCurrency(number, new Locale("pt", "br"), 2, false);
                } else if (action.equalsIgnoreCase(CURRENCY_BRAZIL_3)) {
                    result = doCurrency(number, new Locale("pt", "br"), 3, false);
                } else if (action.equalsIgnoreCase(CURRENCY_USA)) {
                    result = doCurrency(number, Locale.US, 2, true);
                } else if (action.equalsIgnoreCase(EXTENSO)) {
                    result = doExtenso(number);
                } else if (action.equalsIgnoreCase(REAL)) {
                    result = new CurrencyWriter(true).write(new BigDecimal(number));                    
                } else if (action.equalsIgnoreCase(INTEGER_DIGITS)) {
                    result = getIntegerDigits(number, '.');
                } else if (action.equalsIgnoreCase(FRACTION_DIGITS)) {
                    result = getFractionDigits(number, '.');
                } else if (action.equalsIgnoreCase(ROUND)) {
                	BigDecimal bd = new BigDecimal(number);
                	bd = bd.setScale(0, RoundingMode.HALF_EVEN);
                    result = bd.toString();
                } else if (action.equalsIgnoreCase(CLEARNUMBER)) {
                    result = getCleanNumber(number);
                }
            } else if (args.length == 3) {
            	action = args[1];
                if (action.equalsIgnoreCase(FORMAT)) {
                    result = doFormat(number, args[2]);
                }
            }
        } catch (Exception e) {
        	e.printStackTrace(System.err);
        }
        return result;
    }

    private String doCurrency(String number, Locale locale, int fraction, boolean prefix) {
        DecimalFormat df =
            (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(locale);
        if (!prefix) {
        	df.setPositivePrefix("");
        	df.setNegativePrefix("-");
        }
        df.setDecimalSeparatorAlwaysShown(true);
        df.setMinimumFractionDigits(fraction);
        df.setMaximumFractionDigits(fraction);
    	if (number.trim().equals("")) number = "0";
        char sep = df.getDecimalFormatSymbols().getDecimalSeparator();
        if (number.indexOf(sep) > -1) return number;
        return (df.format(Double.parseDouble(number)));
    }

    private String doFormat(String number, String pattern) {
        DecimalFormat df =
            (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(getLocale());
        df.applyPattern(pattern);
        String ret = "";
        try {
            ret = df.format(Double.parseDouble(number));
        } catch (NumberFormatException e) {
            // Despreza erro de formatação
        }
        return ret;
    }

    private String getIntegerDigits(String number, char delim) {
        int ind = number.indexOf(delim);
        return (ind > -1) ? number.substring(0, ind)
                          : number;
    }

    private String getFractionDigits(String number, char delim) {
        int ind = number.indexOf(delim);
        return (ind > -1) ? number.substring(ind + 1, number.length())
                          : "";
    }

    private String doExtenso(String number) {
    	number = normalize(number);
        int resto = number.length() % 3;
        int quociente = number.length() / 3;
        int numIteracoes = (resto == 0) ? quociente
                                        : (quociente + 1);
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < numIteracoes; i++) {
            String str = "";

            int tam = ((resto != 0) && (i == 0)) ? resto
                                                 : 3;
            str = number.substring(0, tam);
            number = number.substring(tam, number.length());
            if (str.length() == 1) {
                result.append(escreverUnidade(str));
            } else if (str.length() == 2) {
                result.append(escreverDezena(str));
            } else if (str.length() == 3) {
                result.append(escreverCentena(str));
            }
            if ((i < (numIteracoes - 1)) && !str.equals("000")) {
                result.append(" ");
                int it = numIteracoes - 2 - i;
                if (Integer.parseInt(str) == 1) {
                    result.append(casaSingular[it]);
                } else {
                    result.append(casaPlural[it]);
                }
                if (Integer.parseInt(number) > 0) {
                    result.append(" e ");
                }
            }
        }
        return result.toString().trim();
    }

    private String normalize(String number) {
        StringBuffer sb = new StringBuffer(number);
        while ((sb.length() > 0) && (sb.charAt(0) == '0')) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    private String escreverUnidade(String str) {
        return unidade[Integer.parseInt(str) - 1];
    }

    private String escreverDezena(String str) {
        StringBuffer sb = new StringBuffer();

        if (str.startsWith("1")) {
            sb.append(escreverUnidade(str));
        } else {
            if (!str.startsWith("0")) {
                int ind = Integer.parseInt(str.substring(0, 1));
                sb.append(dezena[ind - 2]);
            }
            if (str.charAt(1) != '0') {
                int ind = Integer.parseInt(str.substring(1, 2));
                if (!str.startsWith("0")) {
                    sb.append(" e ");
                }
                sb.append(unidade[ind - 1]);
            }
        }

        return sb.toString();
    }

    private String escreverCentena(String str) {
        StringBuffer sb = new StringBuffer();
        if (str.equalsIgnoreCase("100")) {
            sb.append("cem");
        } else {
            if (!str.startsWith("0")) {
                int ind = Integer.parseInt(str.substring(0, 1));
                sb.append(centena[ind - 1]);
            }
            if (!str.endsWith("00")) {
                if (!str.startsWith("0")) {
                    sb.append(" e ");
                }
                sb.append(escreverDezena(str.substring(1, 3)));
            }
        }
        return sb.toString();
    }

    private String getCleanNumber(String num) {
        if (num.equals("")) return "0";
        try {
        	new BigDecimal(num);
        	return num;
        } catch (NumberFormatException nef) { 
        	// testar se já é um numero limpo
        }	
        DecimalFormat df =
            (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(getLocale());
        String dec = df.getDecimalFormatSymbols().getDecimalSeparator() + "";
        String grp = df.getDecimalFormatSymbols().getGroupingSeparator() + "";
        num = StringA.changeChars(num, dec + grp, ".");
        try {
            BigDecimal n = new BigDecimal(num);
            String ret = n.toString();
            if (ret.endsWith(".0")) {
                ret = StringA.change(ret, ".0", "");
            }
            return ret;
        } catch (Exception e) {
            return "";
        }
    }
}
