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

package br.com.webinside.runtime.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Classe com funções diversas do WI
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.13 $
 *
 * @since 3.0
 */
public class Function {
	
    private static Random random = new Random();
    private static String tmpDir;

    protected Function() { }
    
    /**
     * Define uma mensagem no nome da Thread 
     *
     * @param msg indica a mensagem que deve ser o nome da Thread
     */
    public static void setThreadName(String msg) {
        String name = Thread.currentThread().getName();
        name = StringA.piece(name, "^", 1).trim();
        String newMsg = StringA.piece(msg, "^", 2).trim();
        if (newMsg.equals("")) {
            newMsg = msg;
        }
        Thread.currentThread().setName(name + " ^ " + newMsg);
    }

    /**
     * Retorna a listagem de um diretório.
     *
     * @param dir o diretório a ser listado.
     * @param mask a máscara dos arquivos a serem procurados.
     * @param sensitive indica se deve ser comparado em maiúsculo.
     *
     * @return a listagem do diretório.
     */
    public static String[] listDir(String dir, String mask, boolean sensitive) {
        return listDir(dir, "", mask, sensitive, false, true);
    }

    /**
     * Retorna a listagem de um diretório.
     *
     * @param dir o diretório a ser listado.
     * @param mask a máscara dos arquivos a serem procurados.
     * @param sensitive indica se deve ser comparado em maiúsculo.
     * @param recursive indica se deve ser utilizada recursividade.
     *
     * @return a listagem do diretório.
     */
    public static String[] listDir(String dir, String mask, boolean sensitive,
        boolean recursive) {
        return listDir(dir, "", mask, sensitive, recursive, true);
    }

    /**
     * Retorna a listagem de um diretório.
     *
     * @param dir o diretório a ser listado.
     * @param mask a máscara dos arquivos a serem procurados.
     * @param sensitive indica se deve ser comparado em maiúsculo.
     * @param recursive indica se deve ser utilizada recursividade.
     * @param sort indica se o resultado deve ser ordenado.
     *
     * @return a listagem do diretório.
     */
    public static String[] listDir(String dir, String mask, boolean sensitive,
        boolean recursive, boolean sort) {
        return listDir(dir, "", mask, sensitive, recursive, sort);
    }

    private static String[] listDir(String dir, String child, String mask,
        boolean sensitive, boolean recursive, boolean sort) {
        if ((dir == null) || dir.equals("")) {
            return new String[0];
        }
        dir = StringA.change(dir, "\\", "/");
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        if (mask == null) {
            mask = "*";
        }
        mask = StringA.change(mask, "\\", "/");
        if (mask.startsWith("/")) {
            mask = StringA.mid(mask, 1, mask.length());
        }
        if (child == null) {
            child = "";
        }
        String listdir = dir;
        if (!child.trim().equals("")) {
            if (!child.endsWith("/")) {
                child = child + "/";
            }
            listdir = listdir + child;
        }
        File localdir = new File(listdir);
        String[] lista = localdir.list();
        List resp = new ArrayList();
        if (lista != null) {
            for (int i = 0; i <= (lista.length - 1); i++) {
                File sub = new File(dir + child, lista[i]);
                if ((recursive) && (sub.isDirectory())) {
                    String[] sublist =
                        listDir(dir, child + lista[i], mask, sensitive, true,
                            false);
                    for (int a = 0; a < sublist.length; a++) {
                        String item = lista[i] + "/" + sublist[a];
                        resp.add(item);
                    }
                } else { 
                    String usemask = child + mask;
                    if (mask.indexOf("/") > -1) {
                        usemask = mask;
                    }
                    if (validateMask(child + lista[i], usemask, sensitive)) {
                        resp.add(lista[i]);
                    }
                }
            }
        }
        if (sort) {
            if (sensitive) {
                Collections.sort(resp);
            } else {
                StringComparator sc = new StringComparator();
                sc.setIgnoreCase(true);
                Collections.sort(resp, sc);
            }
        }
        return (String[]) resp.toArray(new String[0]);
    }

    /**
     * Remove um diretório e todos os seus arquivos recursivamente.
     *
     * @param dir o diretório a ser removido.
     */
    public static void removeDir(String dir) {
        removeDir(dir, true);
    }

    /**
     * Remove um diretório recursivamente.
     *
     * @param dir o diretório a ser removido.
     * @param removeFiles indica se arquivos devem ser removidos.
     */
    public static void removeDir(String dir, boolean removeFiles) {
        if (dir != null) {
            String[] files = new File(dir).list();
            if (files == null) {
                files = new String[0];
            }
            for (int i = 0; i < files.length; i++) {
                File fname = new File(dir, files[i]);
                if (fname.isDirectory()) {
                    removeDir(fname.toString(), removeFiles);
                } else if (removeFiles) {
                    fname.delete();
                }
            }
            new File(dir).delete();
        }
    }

    /**
     * Remove arquivos de um diretório recursivamente.
     *
     * @param dir o diretório a ser processado.
     * @param mask a máscara para remoção dos arquivos. Utiliza o médoto
     *        validateMask.
     */
    public static void removeFiles(String dir, String mask) {
    	removeFiles(dir, mask, false);
    }
    
    /**
     * Remove arquivos de um diretório recursivamente.
     *
     * @param dir o diretório a ser processado.
     * @param mask a máscara para remoção dos arquivos. Utiliza o validateMask.
     * @param recursive indica se é para ser recursivo.
     */
    public static void removeFiles(String dir, String mask, boolean recursive) {
        if ((dir != null) && (mask != null)) {
            String[] files = new File(dir).list();
            if (files == null) {
                files = new String[0];
            }
            for (int i = 0; i < files.length; i++) {
                File fname = new File(dir, files[i]);
                if (fname.isDirectory()) {
                	if (recursive) {
                		removeFiles(fname.getAbsolutePath(), mask, recursive);
                	}
                    continue;
                }
                if (validateMask(files[i], mask, false)) {
                    fname.delete();
                }
            }
            if (recursive) {
            	new File(dir).delete();
            }
        }
    }

    /**
     * Efetua a validação de um nome de arquivo em uma máscara.
     *
     * @param file o nome do arquivo a ser processado.
     * @param mask a máscara a ser utilizada. Pode ser: nome exato, &#42;,
     *        prefixo&#42; e prefixo&#42;.ext
     * @param sensitive indica se a comparação deve ser feita em maiúsculo.
     *
     * @return indica se a máscara atenda ao nome de arquivo.
     */
    public static boolean validateMask(String file, String mask,
        boolean sensitive) {
        if ((file == null) || (mask == null)) {
            return false;
        }
        if (!sensitive) {
            file = file.toUpperCase();
            mask = mask.toUpperCase();
        }
        mask = StringA.change(mask, "*.*", "*");
        if (mask.equals("") || mask.equals("*.*")) {
            mask = "*";
        }
        if (mask.equals(file) || mask.equals("*")) {
            return true;
        }
        boolean resp = false;
        if (mask.indexOf("*") != -1) {
            if (mask.startsWith(file) && !mask.startsWith(file + "/")) {
                resp = true;
            } else {
                int pos = mask.indexOf("*", 0);
                String miaux = StringA.mid(mask, 0, pos - 1);
                String mfaux = StringA.mid(mask, pos + 1, mask.length() - 1);
                String fiaux = StringA.mid(file, 0, miaux.length() - 1);
                int len = file.length() - mfaux.length();
                String ffaux = StringA.mid(file, len, file.length() - 1);
                if ((miaux + mfaux).equals(fiaux + ffaux)) {
                    resp = true;
                }
            }
        }
        return resp;
    }

    /**
     * Copia os arquivos de um diretório para outro, que é criado se não
     * existir
     *
     * @param fromDir o diretório origem.
     * @param toDir o diretório destino.
     * @param recursive indica a recursividade.
     */
    public static void copyDir(String fromDir, String toDir, boolean recursive) {
        String[] files = listDir(fromDir, "*.*", false, recursive);
        for (int i = 0; i < files.length; i++) {
            copyFile(fromDir + "/" + files[i], toDir + "/" + files[i], true);
        }
    }

    /**
     * Efetua a cópia de um arquivo (binário).
     *
     * @param fromFile o arquivo de origem.
     * @param toFile o arquivo de destino.
     *
     * @return indica se foi possível.
     */
    public static boolean copyFile(String fromFile, String toFile) {
        return copyFile(fromFile, toFile, false);
    }

    /**
     * Efetua a cópia de um arquivo (binário).
     *
     * @param from o arquivo de origem.
     * @param to o arquivo de destino.
     * @param createDir indica se o diretório de destino deve ser criado.
     *
     * @return indica se foi possível.
     */
    public static boolean copyFile(String from, String to, boolean createDir) {
        if (from.equals(to)) {
            return true;
        }
        File fromFile = new File(from);
        File toFile = new File(to);
        toFile.delete();
        if (!fromFile.isFile() || toFile.exists()) {
            return false;
        }
        if ((createDir) && (!toFile.getParentFile().exists())) {
        	toFile.getParentFile().mkdirs();
            new File(toFile.getParent()).mkdirs();
        }
        try {
	        FileChannel inChannel = new FileInputStream(fromFile).getChannel();
	        FileChannel outChannel = new FileOutputStream(toFile).getChannel();
	        outChannel.transferFrom(inChannel, 0, inChannel.size());
	        Function.closeStream(inChannel);
	        Function.closeStream(outChannel);
        } catch (Exception err) {
            err.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    /**
     * Retorna uma chave alfanumérica randômica.
     *
     * @return uma chave alfanumérica de 20 bytes.
     */
    public static String randomKey() {
    	return randomKey(20, false);
    }	
    
    /**
     * Retorna uma chave alfanumérica randômica.
     *
     * @param size o tamanho da chave.
     *
     * @return uma chave alfanumérica.
     */
    public static String randomKey(int size) {
    	return randomKey(size, false);
    }

    /**
     * Retorna uma chave alfanumérica randômica.
     *
     * @param size o tamanho da chave.
     * @param onlynumber indica se é apenas numeros.
     *
     * @return uma chave alfanumérica.
     */
    // Alfa: 65 to 90 = 25 options    
    public static String randomKey(int size, boolean onlynumber) {
        String key = "";
        for (int i = 1; i <= size; i++) {
            if (onlynumber) {
                int num = (int) (random.nextDouble() * 10);
                key = key + num;
            } else {
                int num = (int) (random.nextDouble() * 35);
                if (num < 10) {
                	key = key + num;
                } else {
                	key = key + (char) (num + 55);
                }
            }
        }
        return key;
    }    
    
    /**
     * Gera um nome randomico para um arquivo temporário.
     *
     * @param prefix o prefixo a ser utilizado.
     * @param extension a extensão a ser utilizada.
     *
     * @return o caminho completo para o arquivo temporário.
     */
    public static String rndTmpFile(String prefix, String extension) {
        return rndTmpFile(20, prefix, extension);
    }

    /**
     * Gera um nome randomico para um arquivo temporário.
     *
     * @param size a quantidade de digitos randomicos.
     * @param prefix o prefixo a ser utilizado.
     * @param extension a extensão a ser utilizada.
     *
     * @return o caminho completo para o arquivo temporário.
     */
    public static String rndTmpFile(int size, String prefix, String extension) {
        String tmp = tmpDir();
        while (true) {
            StringBuffer file = new StringBuffer();
            file.append(tmp).append(prefix);
            for (int i = 0; i < size; i++) {
                int num = (int) (random.nextDouble() * 10);
                file.append(num);
            }
            if (!extension.equals("")) {
            	file.append(".").append(extension);
            }	
            File fl = new File(file.toString());
            if (!fl.exists()) {
                return file.toString();
            }
        }
    }
    
    public static String rndTmpFolder(String prefix) {
    	String tmp = tmpDir();
        if (!tmp.endsWith("/"))  tmp += "/";
    	return tmp + prefix + "-" + randomKey().toLowerCase();
    }

    /**
     * Força um diretório temporário
     *
     * @param tmpDir indica o diretório temporário
     */
    public static void setTmpDir(String tmpDir) {
    	if (tmpDir != null && !tmpDir.equals("")) {
	        String aux = StringA.change(tmpDir, "\\", "/");
	        if (!aux.endsWith("/")) {
	            aux = aux + "/";
	        }
	        Function.tmpDir = aux;
    	}    
    }
    
    /**
     * Retorna o diretório temporário.
     *
     * @return o diretório temporário.
     */
    public static String tmpDir() {
    	if (tmpDir != null) {
    		return tmpDir;
    	}
        String tmp = System.getProperty("java.io.tmpdir", "/tmp");
        File dir = new File(tmp);
        if (!dir.exists()) {
            dir = new File("/temp");
        }
        String resp = "/";
        if (dir.exists()) {
            resp = StringA.change(dir.toString(), "\\", "/");
            if (!resp.endsWith("/")) {
                resp = resp + "/";
            }
        }
        return resp;
    }
    
    /**
     * Retorna a data atual formatada com uma máscara.
     *
     * @param mask a máscara a ser utilizada. Ex: "dd,MM,yyyy,HH,mm,ss"
     *
     * @return a data atual usando a máscara.
     */
    public static String getDate(String mask) {
        return getDate(new Date(), mask, null, null);
    }

    /**
     * Retorna uma data formatada com uma máscara.
     *
     * @param date a data a ser utilizada.
     * @param mask a máscara a ser utilizada. Ex: "dd,MM,yyyy,HH,mm,ss"
     * @param locale um locale a ser utilizado.
     *
     * @return a data usando a máscara.
     */
    public static String getDate(Date date, String mask, Locale locale) {
    	return getDate(date, mask, locale, null);
    }

    /**
     * Retorna uma data formatada com uma máscara.
     *
     * @param date a data a ser utilizada.
     * @param mask a máscara a ser utilizada. Ex: "dd,MM,yyyy,HH,mm,ss"
     * @param locale um locale a ser utilizado.
     * @param timezone um timezone a ser utilizado.
     *
     * @return a data usando a máscara.
     */
    public static String getDate(Date date, String mask, 
    		Locale locale, TimeZone timezone) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat frmdata = new SimpleDateFormat(mask);
        if (locale != null) {
            frmdata = new SimpleDateFormat(mask, locale);
        }
        if (timezone != null) {
        	frmdata.setTimeZone(timezone);
        }
        return frmdata.format(date);
    }

    /**
     * Descompacta um arquivo. ATENÇÃO: Em caso de erro verifique se o arquivo
     * ZIP não foi gerado contendo a saída da página que fez o download.
     *
     * @param zipFileName o arquivo a ser descompactado.
     * @param rootDir o diretório aonde descompactar.
     *
     * @return uma mensagem de erro ou vazio se ocorreu corretamente.
     */
    public static String unzip(String zipFileName, String rootDir) {
        File f = new File(zipFileName);
        if (!f.exists()) {
            return "File not found: " + zipFileName;
        }
        try {
            ZipFile zf = new ZipFile(f);
            for (Enumeration i = zf.entries(); i.hasMoreElements();) {
                ZipEntry item = (ZipEntry) i.nextElement();
                if (item.isDirectory()) {
                    File dir = new File(rootDir, item.getName());
                    dir.mkdirs();
                    continue;
                }
                InputStream fis = zf.getInputStream(item);
                File file = new File(rootDir, item.getName());
                if (file.exists()) {
                    file.delete();
                }
                String path = file.getAbsolutePath();
                path = path.replace('\\', '/');
                path = path.substring(0, path.lastIndexOf("/"));
                File fPath = new File(path);
                if (!fPath.exists() && !fPath.mkdirs()) {
                    return "Cannot create dir: " + path + ".";
                }
                FileOutputStream fout = new FileOutputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                copyStream(fis, baos);
                fis.close();
                fout.write(baos.toByteArray());
                baos.close();
                fout.close();
            }
            zf.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
        return "";
    }

    /**
     * Transforma uma string num número inteiro.
     *
     * @param number o número como string.
     *
     * @return o número inteiro. Retorna zero em caso de excessão.
     */
    public static int parseInt(String number) {
        int ret = 0;
        try {
            ret = Integer.parseInt(number);
        } catch (Exception e) {
            // É desconsiderada.
        }
        return ret;
    }

    /**
     * Transforma uma string num número longo.
     *
     * @param number o número como string.
     *
     * @return o número longo. Retorna zero em caso de excessão.
     */
    public static long parseLong(String number) {
        long ret = 0;
        try {
            ret = Long.parseLong(number);
        } catch (Exception e) {
            // É desconsiderada.
        }
        return ret;
    }

    /**
     * Transforma uma string num número float.
     *
     * @param number o número como string.
     *
     * @return o número float. Retorna zero em caso de excessão.
     */
    public static float parseFloat(String number) {
        float ret = 0;
        try {
            ret = Float.parseFloat(number);
        } catch (Exception e) {
            // É desconsiderada.
        }
        return ret;
    }

    /**
     * Transforma uma string num número double.
     *
     * @param number o número como string.
     *
     * @return o número double. Retorna zero em caso de excessão.
     */
    public static double parseDouble(String number) {
        double ret = 0;
        try {
            ret = Double.parseDouble(number);
        } catch (Exception e) {
            // É desconsiderada.
        }
        return ret;
    }

    /**
     * Retorna o classpath do Execute.class
     *
     * @param cl o classloader a ser utilizado.
     *
     * @return o classpath.
     */
    public static String getWIClassPath(ClassLoader cl) {
    	String name = "br/com/webinside/runtime/core/Execute.class";
        URL url = cl.getResource(name);
        String ret = "";
        if (url != null) {
            ret = url.getPath();
            ret = StringA.change(ret, "/C:", "C:");
            ret = StringA.piece(ret, "/" + name, 1);
        }
        return ret;
    }

    /**
     * Localiza a posição que o pipe se encerra.
     *
     * @param text indica o texto a ser processado.
     * @param pos indica a posição de inicio onde o pipe foi encontrado.
     *
     * @return a posição final que encerra o pipe.
     */
    public static int lastPipePos(String text, int pos) {
        int end = -1;
        boolean isFunc = StringA.mid(text, pos, pos + 1).equals("|$");
        if (isFunc) {
            int subFunc = pos + 2;
            int ct1 = 0;
            int ct2 = 0;
            do {
                end = text.indexOf("$|", subFunc);
                if (end > -1) {
                    String aux = StringA.mid(text, pos, end + 1);
                    ct1 = StringA.count(aux, "|$", true);
                    ct2 = StringA.count(aux, "$|", true);
                    subFunc = end + 2;
                }
            } while ((end > -1) && (ct1 != ct2));
            if (end > -1) {
            	end = end + 1;
            }
        } else {
            end = text.indexOf("|", pos + 1);
            int spc = text.indexOf(" ", pos + 1);
            int cr = text.indexOf("\r", pos + 1);
            int lf = text.indexOf("\n", pos + 1);
            if ((end == -1) || ((spc > -1) && (spc < end))) {
                end = spc;
            }
            if ((end == -1) || ((cr > -1) && (cr < end))) {
                end = cr;
            }
            if ((end == -1) || ((lf > -1) && (lf < end))) {
                end = lf;
            }
        }
        if (end == -1) {
            end = text.length();
        }
        return end;
    }

    /**
     * Cria um arquivo zip
     * @param base é o caminho para o diretório a ser zipado
     * @param file
     * @param zos 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void doZIPFile(String base, File file, ZipOutputStream zos,
            List exclude)
    throws FileNotFoundException, IOException {
        if (!file.exists()) {
            return;
        }
        base = StringA.change(base, "\\", "/");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                doZIPFile(base, files[i], zos, exclude);
            }
        } else {
            if (exclude == null || exclude.indexOf(file.getName()) == -1) {
                String entryName = file.getAbsolutePath();
                entryName = StringA.change(entryName, "\\", "/");
                entryName = entryName.substring(base.length() + 1);
                ZipEntry ze = new ZipEntry(entryName);
                
                // Armazena os arquivos com compactacao
                zos.setMethod(ZipOutputStream.DEFLATED);
                // Usa a taxa de compressao maxima
                zos.setLevel(9);
                zos.putNextEntry(ze);
                zos.write(getByteArrayFromFile(file));
                zos.closeEntry();
            }
        }
    }

    private static byte[] getByteArrayFromFile(File file)
    throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(fis, baos);
        fis.close();
        baos.close();
        return baos.toByteArray();
    }

    /**
     * Copia os dados de um inputStream para um outputStream
     * @param is inputStream a ser copiado
     * @param os outputStrem
     * retorna true 
     * @throws IOException
     */
	public static long copyStream(InputStream in, OutputStream out) throws IOException {
		int BUFFER_SIZE = 8 * 1024;
	    long transferred = 0;
	    int read;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while ((read = in.read(buffer, 0, BUFFER_SIZE)) >= 0) {
	        out.write(buffer, 0, read);
	        transferred += read;
	    }
		out.flush();
	    return transferred;
	}
	
	public static void closeStream(Closeable stream) {
		try {
			if (stream != null) stream.close();
		} catch (Exception e) {
			// ignorado
		}
	}
	
	public static void sendFile(File file, OutputStream out) {
		try {
			InputStream in = new FileInputStream(file);
			copyStream(in, out);
			in.close();
		} catch (Exception e) {
			// ignorado
		}
	}
	
	public static int getArraySize(WIMap wiMap, String prefix) {
		prefix = StringA.piece(prefix, "[]", 1).trim();
		if (prefix.equals("")) return 0;
		Set keys = wiMap.getAsMap().keySet();
		int size = 0;
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.startsWith(prefix + "[") && key.endsWith("].")) {
				int from = prefix.length() + 1;
				int to = key.indexOf("]", from);
				String actual = StringA.mid(key, from, to - 1);
				int value = Function.parseInt(actual);
				if (value > 0) {
					size = (size < value ? value : size);
				}	
			}
		}
        return size;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignorado
		}
	}
	
	public static String getStackTrace() {
		StringBuilder str = new StringBuilder();
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) str.append("\n");
			str.append(elements[i]);
		}
		return str.toString();
	}
	
	public static void decodeJSON(WIMap wiMap, String json, String prefix) {
		if (json == null || json.trim().equals("") || json.trim().equals("null")) return;
		Object obj = JSONValue.parse(json.trim());
		if (obj != null) {
			parseDecodeJSON(wiMap, obj, prefix);
		}
	}
	
	private static void parseDecodeJSON(WIMap wiMap, Object obj, String prefix) {
		if (obj instanceof JSONObject) {
			JSONObject jobj = (JSONObject) obj;
			for (Object key : jobj.keySet()) {
				Object value = jobj.get(key);
				String auxPrefix = (String) key;
				if (!prefix.equals("")) auxPrefix = prefix + "." + key;
				parseDecodeJSON(wiMap, value, auxPrefix);
			}
		} else if (obj instanceof JSONArray) {
			JSONArray jarr = (JSONArray)obj;
			int count = 1;
			for (Iterator iterator = jarr.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				if (prefix.equals("")) prefix = "tmp.json";
				if (prefix.endsWith("]")) prefix += ".json";
				parseDecodeJSON(wiMap, object, prefix + "[" + count + "]");
				count++;
			}
		} else {
			if (!prefix.equals("")) {
				wiMap.put(prefix, obj.toString());
			}
		}
	}
		
}
