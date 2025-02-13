/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe que possui internamente um objeto StringBuffer  e possui opera��es
 * adicionais para manipula��o de texto
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.7 $
 *
 * @since 3.0
 */
public class StringA {
    /** S�mbolos no formato HTML. */
    private static final String[] XML_SYMBOLS =
        {"&nbsp;", "&lt;", "&gt;", "&quot;", "&apos;", "&amp;", "&cr;", "&lf;"};
    /** S�mbolos no formato de texto. */
    private static final String[] TEXT_SYMBOLS =
        {" ", "<", ">", "\"", "'", "&", "\r", "\n"};
    private StringBuffer buffer;
    private static final String FOREIGN_CHARS =
        "������������������������������������������������";
    
    private static final String US_CHARS =
        "aAeEiIoOuUaAeEiIoOuUaAeEiIoOuUaAeEiIoOuUaAoOcCnN";

    /**
     * Cria um novo objeto da classe.
     */
    public StringA() {
        buffer = new StringBuffer();
    }

    /**
     * Cria um novo objeto da classe.
     *
     * @param text o texto inicial.
     */
    public StringA(String text) {
        buffer = new StringBuffer();
        if (text != null) {
            buffer.append(text);
        }
    }

    /**
     * Cria um novo objeto da classe.
     *
     * @param text o texto inicial.
     */
    public StringA(StringA text) {
        buffer = new StringBuffer();
        if (text != null) {
            buffer.append(text.toString());
        }
    }

    /**
     * Define o texto a ser utilizado.
     *
     * @param text define o texto a ser utilizado.
     */
    public void set(String text) {
        buffer.setLength(0);
        if (text != null) {
            buffer.append(text);
        }
    }

    /**
     * Define o texto a ser utilizado.
     *
     * @param text define o texto a ser utilizado.
     */
    public void set(StringA text) {
        buffer.setLength(0);
        if (text != null) {
            buffer.append(text.toString());
        }
    }

    /**
     * Retorna o texto interno como String.
     *
     * @return texto interno como String.
     */
    public String toString() {
        return buffer.toString();
    }

    /**
     * Adiciona uma String ao texto interno.
     *
     * @param text o texto a ser adicionado ao texto interno.
     *
     * @return o pr�prio StringA.
     */
    public StringA append(String text) {
        if (text != null) {
            buffer.append(text);
        }
        return this;
    }

    /**
     * Adiciona um char ao texto interno.
     *
     * @param chr o carcter a ser adicionado ao texto interno.
     *
     * @return o pr�prio StringA.
     */
    public StringA append(char chr) {
        buffer.append(chr);
        return this;
    }

    /**
     * Retorna o tamanho do texto.
     *
     * @return tamanho do texto.
     */
    public int length() {
        return buffer.length();
    }

    /**
     * Extrai um peda�o do texto interno.
     *
     * @param start a posi��o de inicio (inclusive).
     * @param end a posi��o final (inclusive).
     *
     * @return peda�o do texto interno.
     */
    public String mid(int start, int end) {
        return mid(buffer.toString(), start, end);
    }

    /**
     * Extrai um peda�o do texto.
     *
     * @param text o texto a ser processado
     * @param start a posi��o de inicio (inclusive).
     * @param end a posi��o final (inclusive).
     *
     * @return peda�o do texto.
     */
    public static String mid(String text, int start, int end) {
        if ((text == null) || (text.length() == 0) || (end < start)) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end > (text.length() - 1)) {
            end = text.length() - 1;
        }
        return text.substring(start, end + 1);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c -
     * usando o delimitador "," a posi��o 2 � b.
     *
     * @param delim o delimitador a ser utilizado. Comparado em mai�sculo.
     * @param position a posi��o desejada. Come�a em 1.
     *
     * @return a parte do texto.
     */
    public String piece(String delim, int position) {
        return piece(buffer.toString(), delim, position, position, false);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c -
     * usando o delimitador "," a posi��o 2 � b.
     *
     * @param text o texto a ser utilizado.
     * @param delim o delimitador a ser utilizado. Comparado em mai�sculo.
     * @param position a posi��o desejada. Come�a em 1.
     *
     * @return a parte do texto.
     */
    public static String piece(String text, String delim, int position) {
        return piece(text, delim, position, position, false);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c,d -
     * usando o delimitador "," o start sendo 2 e  o end sendo 3 o resultado �
     * "b,c".
     *
     * @param delim o delimitador a ser utilizado. Comparado em mai�sculo.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     *
     * @return a parte do texto.
     */
    public String piece(String delim, int start, int end) {
        return piece(buffer.toString(), delim, start, end, false);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c,d -
     * usando o delimitador "," o start sendo 2 e  o end sendo 3 o resultado �
     * "b,c".
     *
     * @param text o texto a ser utilizado.
     * @param delim o delimitador a ser utilizado. Comparado em mai�sculo.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     *
     * @return a parte do texto.
     */
    public static String piece(String text, String delim, int start, int end) {
        return piece(text, delim, start, end, false);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c,d -
     * usando o delimitador "," o start sendo 2 e  o end sendo 3 o resultado �
     * "b,c".
     *
     * @param delim o delimitador a ser utilizado.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     * @param sensitive o delimitador deve ser comparado em mai�sculo ou
     *        exatamente.
     *
     * @return a parte do texto.
     */
    public String piece(String delim, int start, int end, boolean sensitive) {
        return piece(buffer.toString(), delim, start, end, sensitive);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador. Ex: a,b,c,d -
     * usando o delimitador "," o start sendo 2 e  o end sendo 3 o resultado �
     * "b,c".
     *
     * @param text o texto a ser utilizado.
     * @param delim o delimitador a ser utilizado.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     * @param sensitive o delimitador deve ser comparado em mai�sculo ou
     *        exatamente.
     *
     * @return a parte do texto.
     */
    public static String piece(String text, String delim, int start, int end,
        boolean sensitive) {
        if ((text == null) || (text.length() == 0)) {
            return "";
        }
        if ((delim == null) || (delim.length() == 0)) {
            return text;
        }
        start = (start < 1) ? 1
                            : start;
        end = (end < 0) ? 0
                        : end;
        if ((end != 0) && (end < start)) {
            return "";
        }
        int piece = 1;
        int ini = 0;
        int pos = 0;
        int fim = text.length();
        String auxtext = null;
        if (!sensitive) {
            auxtext = text.toUpperCase();
            delim = delim.toUpperCase();
        } else {
            auxtext = text;
        }
        pos = auxtext.indexOf(delim);
        while (((piece <= end) || (end == 0)) && (pos > -1)) {
            if ((end > 0) && (piece == end)) {
                fim = pos;
            }
            piece = piece + 1;
            if (piece == start) {
                ini = pos + delim.length();
            }
            pos = auxtext.indexOf(delim, pos + delim.length());
        }
        if (piece < start) {
            return "";
        }
        return text.substring(ini, fim);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador como uma lista.
     * Ex: a,b,c,d - usando o delimitador "," o start sendo 2 e  o end sendo 3
     * o resultado � "b,c".
     *
     * @param delim o delimitador a ser utilizado.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     * @param sensitive o delimitador deve ser comparado em mai�sculo ou
     *        exatamente.
     *
     * @return a parte do texto.
     */
    public List pieceAsList(String delim, int start, int end, boolean sensitive) {
        return pieceAsList(buffer.toString(), delim, start, end, sensitive);
    }

    /**
     * Retorna uma parte do texto separada por um delimitador como uma lista.
     * Ex: a,b,c,d - usando o delimitador "," o start sendo 2 e  o end sendo 3
     * o resultado � "b,c".
     *
     * @param text o texto a ser utilizado.
     * @param delim o delimitador a ser utilizado.
     * @param start a posi��o inicial. Come�a em 1.
     * @param end a posi��o final. Usando end=0 faz ir at� o final do texto.
     * @param sensitive o delimitador deve ser comparado em mai�sculo ou
     *        exatamente.
     *
     * @return a parte do texto.
     */
    public static List pieceAsList(String text, String delim, int start,
        int end, boolean sensitive) {
        List resp = new ArrayList();
        if (text != null) {
            String value = piece(text, delim, start, end, sensitive);
            if (!value.equals("")) {
                for (int i = 0; i <= count(value, delim, sensitive); i++) {
                    resp.add(piece(value, delim, i + 1, i + 1, sensitive));
                }
            }
        }
        return resp;
    }

    /**
     * Retorna a posi��o em que se encontra um subtexto no texto.
     *
     * @param text o texto a ser procurado.
     *
     * @return a posi��o encontrada. Retorna -1 caso n�o encontre.
     */
    public int indexOf(String text) {
        return indexOf(text, 0);
    }

    /**
     * Retorna a posi��o em que se encontra um subtexto no texto.
     *
     * @param text o texto a ser procurado.
     * @param from a partir de uma dada posi��o.
     *
     * @return a posi��o encontrada. Retorna -1 caso n�o encontre.
     */
    public int indexOf(String text, int from) {
        if ((text == null) || (buffer.length() == 0)) {
            return -1;
        }
        return buffer.toString().indexOf(text, from);
    }

    /**
     * Retorna a �ltima posi��o em que se encontra um subtexto no texto.
     *
     * @param text o texto a ser procurado.
     *
     * @return a �ltima posi��o encontrada. Retorna -1 caso n�o encontre.
     */
    public int lastIndexOf(String text) {
        return lastIndexOf(text, buffer.length());
    }

    /**
     * Retorna a �ltima posi��o em que se encontra um subtexto no texto.
     *
     * @param text o texto a ser procurado.
     * @param from a partir de uma dada posi��o.
     *
     * @return a �ltima posi��o encontrada. Retorna -1 caso n�o encontre.
     */
    public int lastIndexOf(String text, int from) {
        if ((text == null) || (buffer.length() == 0)) {
            return -1;
        }
        return buffer.toString().lastIndexOf(text, from);
    }

    /**
     * Efetua a troca de caracteres no texto. A troca � feita baseda na posi��o
     * equivalente do caracter nas strings. A n�o exist�ncia do equivalente
     * remove o caracter. Ex: Em "geraldo" changeChars("arg","AR") resulta em
     * "eRAldo"
     *
     * @param oldChars os caracteres existentes e que devem ser substitu�dos.
     * @param newChars os caracteres que ir�o substituir.
     *
     * @return o texto com os caracteres substitu�dos.
     */
    public String changeChars(String oldChars, String newChars) {
        return changeChars(buffer.toString(), oldChars, newChars, false);
    }

    /**
     * Efetua a troca de caracteres no texto. A troca � feita baseda na posi��o
     * equivalente do caracter nas strings. A n�o exist�ncia do equivalente
     * remove o caracter. Ex: Em "geraldo" changeChars("arg","AR") resulta em
     * "eRAldo"
     *
     * @param text o texto a ser processado.
     * @param oldChars os caracteres existentes e que devem ser substitu�dos.
     * @param newChars os caracteres que ir�o substituir.
     *
     * @return o texto com os caracteres substitu�dos.
     */
    public static String changeChars(String text, String oldChars,
        String newChars) {
        return changeChars(text, oldChars, newChars, false);
    }

    /**
     * Efetua a troca de caracteres no texto. A troca � feita baseda na posi��o
     * equivalente do caracter nas strings. A n�o exist�ncia do equivalente
     * remove o caracter. Ex: Em "geraldo" changeChars("arg","AR") resulta em
     * "eRAldo"
     *
     * @param oldChars os caracteres existentes e que devem ser substitu�dos.
     * @param newChars os caracteres que ir�o substituir.
     * @param sensitive indica se a compara��o deve ser feita em letra
     *        mai�scula.
     *
     * @return o texto com os caracteres substitu�dos.
     */
    public String changeChars(String oldChars, String newChars,
        boolean sensitive) {
        return changeChars(buffer.toString(), oldChars, newChars, sensitive);
    }

    /**
     * Efetua a troca de caracteres no texto. A troca � feita baseda na posi��o
     * equivalente do caracter nas strings. A n�o exist�ncia do equivalente
     * remove o caracter. Ex: Em "geraldo" changeChars("arg","AR") resulta em
     * "eRAldo"
     *
     * @param text o texto a ser processado.
     * @param oldChars os caracteres existentes e que devem ser substitu�dos.
     * @param newChars os caracteres que ir�o substituir.
     * @param sensitive indica se a compara��o deve ser feita em letra
     *        mai�scula.
     *
     * @return o texto com os caracteres substitu�dos.
     */
    public static String changeChars(String text, String oldChars,
        String newChars, boolean sensitive) {
        if (oldChars == null) {
            oldChars = "";
        }
        if (newChars == null) {
            newChars = "";
        }
        if ((text == null) || (oldChars.length() == 0)) {
            return text;
        }
        String auxtext = null;
        if (!sensitive) {
            auxtext = text.toUpperCase();
            oldChars = oldChars.toUpperCase();
            newChars = newChars.toUpperCase();
        } else {
            auxtext = text;
        }
        StringBuffer aux = new StringBuffer();
        char letn;
        char letu;
        for (int i = 0; i < text.length(); i++) {
            letn = text.charAt(i);
            letu = auxtext.charAt(i);
            if (sensitive) {
                letu = letn;
            }
            int pos = oldChars.indexOf(letu);
            if (pos == -1) {
                aux.append(letn);
            } else {
            	if (newChars.equalsIgnoreCase("ALL_TO_SPACE")) {
            		aux.append(" ");
            	} else if (newChars.length() > pos) {
            		aux.append(newChars.charAt(pos));
            	} 	
            	
            }
        }
        return aux.toString();
    }

    /**
     * Troca um caracter por outro no texto.
     *
     * @param oldChar o caracter antigo.
     * @param newChar o caracter novo.
     *
     * @return o texto com o caracter substitu�do.
     */
    public String change(char oldChar, char newChar) {
        return change(buffer.toString(), oldChar, newChar);
    }

    /**
     * Troca um caracter por outro no texto.
     *
     * @param text o texto a ser processado.
     * @param oldChar o caracter antigo.
     * @param newChar o caracter novo.
     *
     * @return o texto com o caracter substitu�do.
     */
    public static String change(String text, char oldChar, char newChar) {
        if ((text == null) || (text.length() == 0)) {
            return "";
        }
        return changeChars(text, "" + oldChar, "" + newChar, false);
    }

    /**
     * Troca um subtexto por outro no texto.
     *
     * @param oldTxt o subtexto antigo.
     * @param newTxt o subtexto novo.
     *
     * @return o texto processado.
     */
    public String change(String oldTxt, String newTxt) {
        return change(buffer.toString(), oldTxt, newTxt, false);
    }

    /**
     * Troca um subtexto por outro no texto.
     *
     * @param text o texto a ser processado.
     * @param oldTxt o subtexto antigo.
     * @param newTxt o subtexto novo.
     *
     * @return o texto processado.
     */
    public static String change(String text, String oldTxt, String newTxt) {
        return change(text, oldTxt, newTxt, false);
    }

    /**
     * Troca um subtexto por outro no texto.
     *
     * @param oldTxt o subtexto antigo.
     * @param newTxt o subtexto novo.
     * @param sensitive indica se o subtexto deve ser localizado em letra
     *        mai�scula.
     *
     * @return o texto processado.
     */
    public String change(String oldTxt, String newTxt, boolean sensitive) {
        return change(buffer.toString(), oldTxt, newTxt, sensitive);
    }

    /**
     * Troca um subtexto por outro no texto.
     *
     * @param text o texto a ser processado.
     * @param oldTxt o subtexto antigo.
     * @param newTxt o subtexto novo.
     * @param sensitive indica se o subtexto deve ser localizado em letra
     *        mai�scula.
     *
     * @return o texto processado.
     */
    public static String change(String text, String oldTxt, String newTxt,
        boolean sensitive) {
        if ((text == null) || (oldTxt == null) || (newTxt == null)
                    || oldTxt.equals("")) {
            return text;
        }
        if (text.length() == 0) {
            return "";
        }
        String dados;
        if (!sensitive) {
            dados = text.toUpperCase();
            oldTxt = oldTxt.toUpperCase();
        } else {
            dados = text;
        }
        StringBuffer response = new StringBuffer();
        int pos = 0;
        int lastpos = 0;
        while ((pos = dados.indexOf(oldTxt, lastpos)) > -1) {
            String ant = text.substring(lastpos, pos);
            response.append(ant + newTxt);
            lastpos = pos + oldTxt.length();
        }
        if (lastpos < text.length()) {
            response.append(text.substring(lastpos, text.length()));
        }
        return response.toString();
    }

    /**
     * Retorna a quantidade de ocorr�ncias de um caracter no texto.
     *
     * @param chr o caracter a ser procurado.
     *
     * @return a quantidade do caracter no texto.
     */
    public int count(char chr) {
        return count(buffer.toString(), chr + "", false);
    }

    /**
     * Retorna a quantidade de ocorr�ncias de um caracter no texto.
     *
     * @param text o texto a ser processado.
     * @param chr o caracter a ser procurado.
     *
     * @return a quantidade do caracter no texto.
     */
    public static int count(String text, char chr) {
        return count(text, chr + "", false);
    }

    /**
     * Retorna a quantidade de ocorr�ncias de um subtexto no texto.
     *
     * @param text o subtexto a ser procurado.
     * @param sensitive indica se o subtexto deve ser procurado em letra
     *        mai�scula.
     *
     * @return a quantidade do subtexto no texto.
     */
    public int count(String text, boolean sensitive) {
        return count(buffer.toString(), text, sensitive);
    }

    /**
     * Retorna a quantidade de ocorr�ncias de um subtexto no texto.
     *
     * @param text o texto a ser processado.
     * @param subText o subtexto a ser procurado.
     * @param sensitive indica se o subtexto deve ser procurado em letra
     *        mai�scula.
     *
     * @return a quantidade do subtexto no texto.
     */
    public static int count(String text, String subText, boolean sensitive) {
        if ((subText == null) || (text == null) || (text.length() == 0)) {
            return 0;
        }
        if (!sensitive) {
            subText = subText.toUpperCase();
            text = text.toUpperCase();
        }
        int cont = 0;
        int pos = text.indexOf(subText, 0);
        for (; pos > -1;) {
            cont = cont + 1;
            pos = text.indexOf(subText, pos + subText.length());
        }
        return cont;
    }

    /**
     * Limpar os espa�os iniciais e finais do texto.
     *
     * @return o texto sem os espa�os inciais e finais.
     */
    public String trim() {
        return buffer.toString().trim();
    }

    /**
     * Retorna o texto como subdiret�rio, com as barras \ transformadas em / e
     * sem a / inicial.
     *
     * @return o texto como subdiret�rio.
     */
    public String formatSubDir() {
        return formatSubDir(buffer.toString());
    }

    /**
     * Retorna o texto como subdiret�rio, com as barras \ transformadas em / e
     * sem a / inicial.
     *
     * @param text o texto a ser processado.
     *
     * @return o texto como subdiret�rio.
     */
    public static String formatSubDir(String text) {
        if (text == null) {
            return "";
        }
        text = text.trim();
        StringBuffer response = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char let = text.charAt(i);
            if (let == '\\') {
                let = '/';
            }
            if ((let == '/') && (i == 0)) {
                continue;
            }
            response.append(let);
        }
        return response.toString();
    }

    /**
     * Retorna o texto codificado como CGI.
     *
     * @param text a ser processado.
     *
     * @return o texto codificado como CGI.
     */
    public static String getCgi(String text) {
        if (text == null) {
            text = "";
        }
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException err) {
            // Nunca vai ocorrer.	
        }
        return text;
    }

    /**
     * Recebe o texto codificado como CGI e armazena no formato normal.
     *
     * @param text a ser processado.
     */
    public void setCgi(String text) {
        buffer.setLength(0);
        if (text == null) {
            text = "";
        }
        try {
            buffer.append(URLDecoder.decode(text, "UTF-8"));
        } catch (UnsupportedEncodingException err) {
            // Nunca vai ocorrer.	
        }
    }

    /**
     * Remove as quebras de linha \r\n substituindo por espa�o.
     *
     * @return o texto sem as quebras de linha.
     */
    public String clearLineBreak() {
        return changeLinebreak(buffer.toString(), " ");
    }

    /**
     * Remove as quebras de linha \r\n substituindo por espa�o.
     *
     * @param text o texto a ser processado.
     *
     * @return o texto sem as quebras de linha.
     */
    public static String clearLineBreak(String text) {
        return changeLinebreak(text, " ");
    }

    /**
     * Troca as quebras de linha por um subtexto. O \r\n no final do texto �
     * removido.
     *
     * @param newLinebreak o subtexto utilizado na substitui��o.
     *
     * @return o texto com os line breaks substitu�dos.
     */
    public String changeLinebreak(String newLinebreak) {
        return changeLinebreak(buffer.toString(), newLinebreak);
    }

    /**
     * Troca as quebras de linha por um subtexto. O \r\n no final do texto �
     * removido.
     *
     * @param text o texto a ser processado.
     * @param newLinebreak o subtexto utilizado na substitui��o.
     *
     * @return o texto com os line breaks substitu�dos.
     */
    public static String changeLinebreak(String text, String newLinebreak) {
        if ((text == null) || text.equals("")) {
            return "";
        }
        if (newLinebreak == null) {
            return text;
        }
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char let = text.charAt(i);
            if ((let == '\r') || (let == '\n')) {
                if ((let == '\n') && (i < (text.length() - 1))) {
                    resp.append(newLinebreak);
                }
            } else {
                resp.append(let);
            }
        }
        return resp.toString();
    }
    
    /**
     * Converte "\r" e "\n" no texto para char de \r e \n.
     *
     * @param text o texto a ser processado.
     *
     * @return o texto com os line breaks.
     */
    public static String showLineBreak(String text) {
        if ((text == null) || text.equals("")) {
            return "";
        }
        StringBuffer resp = new StringBuffer();
        boolean control = false;
        for (int i = 0; i < text.length(); i++) {
            if (control) {
                if (text.charAt(i) == 'r') {
                    resp.append("\r");
                } else if (text.charAt(i) == 'n') {
                    resp.append("\n");
                } else {
                    resp.append("\\" + text.charAt(i));
                }
                control = false;
            } else {
                if (text.charAt(i) == '\\') {
                    control = true;
                } else {
                    resp.append(text.charAt(i));
                }
            }
        }
        return resp.toString();
    }

    /**
     * Retorna o texto interno convertido para o formato XML.
     *
     * @return o texto convertido.
     */
    public String getXml() {
        return getXml(buffer.toString());
    }

    /**
     * Retorna um texto convertido para o formato XML.
     *
     * @param text o texto a ser convertido.
     *
     * @return o texto convertido.
     */
    public static String getXml(String text) {
        if (text == null) {
            text = "";
        }
        Map map = new HashMap();
        for (int i = 0; i < TEXT_SYMBOLS.length; i++) {
            map.put(TEXT_SYMBOLS[i], XML_SYMBOLS[i]);
        }
        return textToXmlEntity(text, map, false);
    }

    /**
     * Recebe um texto no formato XML converte e armazena-o.
     *
     * @param xml o texto a ser armazenado.
     */
    public void setXml(String xml) {
        buffer.setLength(0);
        if (xml != null) {
            Map map = new HashMap();
            for (int i = 0; i < XML_SYMBOLS.length; i++) {
                map.put(XML_SYMBOLS[i], TEXT_SYMBOLS[i]);
            }
            buffer.append(xmlEntityToText(xml, map));
        }
    }

    /**
     * Converte um texto normal num outro tipo de texto (XML, HTML).
     *
     * @param text o texto a ser processado.
     * @param map a cole��o que ser� utilizada na substitui��o.
     * @param allSpaces indica se todos os espa�os devem ser convertidos  ou
     *        apenas os do inicio e fim do texto.
     *
     * @return o texto convertido.
     */
    protected static String textToXmlEntity(String text, Map map,
        boolean allSpaces) {
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            String aux = text.charAt(i) + "";
            if (map.containsKey(aux)) {
                String value = (String) map.get(aux);
                if (aux.equals(" ") && !allSpaces && (i > 0)
                            && (i < (text.length() - 1))) {
                    value = " ";
                }
                resp.append(value);
            } else {
                resp.append(aux);
            }
        }
        return resp.toString();
    }

    /**
     * Converte um texto de outro tipo num texto normal (XML, HTML).
     *
     * @param text o texto a ser processado.
     * @param map a cole��o que ser� utilizada na substitui��o.
     *
     * @return o texto convertido.
     */
    protected static String xmlEntityToText(String text, Map map) {
        StringBuffer resp = new StringBuffer();
        StringBuffer seq = null;
        for (int i = 0; i < text.length(); i++) {
            char aux = text.charAt(i);
            if (aux == '&') {
                if (seq != null) {
                    resp.append(seq);
                    seq.setLength(0);
                } else {
                    seq = new StringBuffer();
                }
                seq.append("&");
            } else if (seq != null) {
                if (aux == ';') {
                    seq.append(";");
                    if (map.containsKey(seq.toString())) {
                        resp.append((String) map.get(seq.toString()));
                    } else {
                        resp.append(seq.toString());
                    }
                    seq.setLength(0);
                } else {
                    seq.append(aux);
                }
            } else {
                resp.append(aux);
            }
        }
        if (seq != null) {
            resp.append(seq.toString());
        }
        return resp.toString();
    }

    /**
     * Retorna o buffer interno.
     *
     * @return o buffer interno.
     */
    public StringBuffer getBuffer() {
        return buffer;
    }

    /**
     * Implementa o equals especializado.
     *
     * @param obj o objeto a ser comparado.
     *
     * @return indica se � igual.
     */
    public boolean equals(Object obj) {
        boolean ret = false;
        if (this == obj) {
            ret = true;
        } else if (obj instanceof StringA) {
            ret = toString().equals(obj.toString());
        }
        return ret;
    }

    /**
     * Implementa um hashCode especializado.
     *
     * @return indica o hashCode.
     */
    public int hashCode() {
        return toString().hashCode();
    }
    
    /**
     * Elimina acentua��o do texto
     * @param text
     * @return o texto sem os caracteres acentuados
     */
    public static String getUsAscii(String text) {
        return StringA.changeChars(text, FOREIGN_CHARS, US_CHARS);
    }

    public static String getForFile(String text) {
    	String resp = StringA.changeChars(text, "\r\n\\/:*?\"'<>|���@#$%&._", "ALL_TO_SPACE");
    	resp = resp.replaceAll("-+", "-"); // repeticao de -
    	resp = resp.replaceAll("^-", " "); // inicia com -
    	resp = resp.replaceAll("-$", " "); // termina com  -
    	resp = StringA.getUsAscii(resp.replaceAll("\\s+", " ").trim());
    	resp = StringA.changeChars(resp, " ", "_");
    	return resp.replace("_-_", "-");
    }
    
    public static String changeForHtmlTag(String text) {
        if ((text == null) || text.equals("")) {
            return "";
        }
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char let = text.charAt(i);
            if ((let == '\r') || (let == '\n')) {
                if ((let == '\n') && (i < (text.length() - 1))) {
                    resp.append("\\r\\n");
                }
            } else if (let == '<') {
            	resp.append("&lt;");
            } else if (let == '>') {
            	resp.append("&gt;");
            } else {
                resp.append(let);
            }
        }
        return resp.toString();
    }

    public static String left(String text, int size) {
        if ((text == null) || (size < 1)) return "";
        int Fin = text.length();
        if (size > Fin) return text;
        return mid(text, 0, size - 1);
    }
    
    public static String left(String text, int size, String complemento) {
        StringBuffer aux = new StringBuffer(left(text, size));
        int dif = size - aux.length();
        for (int i = 1; i <= dif; i++) {
            aux.append(complemento);
        }
        return aux.toString();
    }    
 
    public static String right(String text, int size) {
        if ((text == null) || (size < 1)) return "";
        int Fin = text.length();
        if (size > Fin) return text;
        return mid(text, Fin - size, Fin - 1);
    }
    
    public static String right(String text, int size, String complemento) {
        StringBuffer aux = new StringBuffer(right(text, size));
        int dif = size - aux.length();
        for (int i = 1; i <= dif; i++) {
            aux.insert(0, complemento);
        }
        return aux.toString();
    }
    
    public static int compareLevRatio(String s1, String s2) {
    	s1 = s1.toLowerCase().replaceAll("\\s+"," ").trim();
		s1 = Normalizer.normalize(s1, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		s2 = s2.toLowerCase().replaceAll("\\s+"," ").trim();
		s2 = Normalizer.normalize(s2, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		int lev = StringUtils.getLevenshteinDistance(s1, s2);
		int minLen = s1.length() < s2.length() ? s1.length() : s2.length();
		if (minLen == 0) return 100;
		if (s1.replace(" ", "").length() < 5 || s2.replace(" ", "").length() < 5) return 99;
		int diff = Math.abs(s1.length() - s2.length());
		return (lev-diff) * 100 / minLen;
    }
    
    public static void main(String[] args) {
    	System.out.println("{" + getForFile("---CONDOMINIO PEND�NCIAS_-_2021-") + "}");
    }
    
}
