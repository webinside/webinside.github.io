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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe para ler e gravar arquivos texto.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 *
 * @since 3.0
 */
public class FileIO {
    private static String RET = System.getProperty("line.separator");
    /** Utilizar o arquivo para leitura. */
    public static final char READ = 'R';
    /** Utilizar o arquivo para gravação. */
    public static final char WRITE = 'W';
    private File file;
    private char type;
    private int line = 0;
    private BufferedWriter out;
    private BufferedReader in;
    private boolean append = false;

    /**
     * Cria um novo FileIO.
     *
     * @param f o arquivo a ser utilizado.
     * @param t o tipo de uso. READ ou WRITE.
     */
    public FileIO(String f, char t) {
        type = ' ';
        type = Character.toUpperCase(t);
        if ((t == READ) || (t == WRITE)) {
            type = t;
        }
        file = new File(f);
    }

    /**
     * Libera o arquivo do uso.
     */
    public void close() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
        	e.printStackTrace(System.err);
        }
    }

    /**
     * Define se deve concatenar ao conteúdo já existente.
     *
     * @param a define se deve concatenar.
     */
    public void setAppend(boolean a) {
        this.append = a;
    }

    /**
     * Define o arquivo a ser utilizado.
     *
     * @param f o caminho para o arquivo.
     */
    public void changeFilename(String f) {
        if (f != "") {
            close();
            file = new File(f);
        }
    }

    /**
     * Altera o tipo de uso do arquivo.
     *
     * @param t define o tipo.
     */
    public void changeType(char t) {
        t = Character.toUpperCase(t);
        if ((t == READ) || (t == WRITE)) {
            close();
            type = t;
        }
    }

    /**
     * Retorna o File interno para o arquivo.
     *
     * @return o File interno para o arquivo.
     */
    public File getFile() {
        return file;
    }

    /**
     * Retorna o caminho completo para o arquivo.
     *
     * @return o caminho para o arquivo.
     */
    public String toString() {
        return StringA.change(file.getAbsolutePath(), '\\', '/');
    }

    /**
     * Retorna o texto na forma de uma lista das linhas.
     *
     * @return a lista das linhas do texto.
     */
    public List readTextList() {
        List response = new ArrayList();
        readText(response, "");
        return response;
    }

    /**
     * Retorna o texto completo.
     *
     * @return o texto completo.
     */
    public String readText() {
    	return readText("");
    }

    /**
     * Retorna o texto completo.
     *
     * @return o texto completo.
     */
    public String readText(String charset) {
        StringBuffer response = new StringBuffer();
        readText(response, charset);
        return response.toString();
    }

    private void readText(Object obj, String charset) {
        if (((file != null) && file.exists() && (type == READ))
            && (in == null)) {
            try {
            	if (charset.equals("")) {
            		in = new BufferedReader(new FileReader(file));
            	} else {
            		FileInputStream fis = new FileInputStream(file);
                    in = new BufferedReader(new InputStreamReader(fis, charset));
            	}
                String l = null;
                while ((l = in.readLine()) != null) {
                    if (obj instanceof List) {
                        ((List) obj).add(l);
                    } else if (obj instanceof StringBuffer) {
                        ((StringBuffer) obj).append(l).append(RET);
                    }
                }
                in.close();
                in = null;
            } catch (IOException err) {
            	err.printStackTrace(System.err);
            }
        }
    }

    /**
     * Lê uma linha do texto.
     *
     * @param l o conteúdo da linha lida.
     *
     * @return a posição da linha.
     */
    public int readLine(StringA l) {
        if ((file == null) || (l == null) || (type != READ)) {
            return -1;
        }
        if (in == null) {
            try {
                in = new BufferedReader(new FileReader(file));
                line = 0;
            } catch (IOException e) {
                line = -1;
            }
        }
        if (line >= 0) {
            try {
                String aux = in.readLine();
                l.set(aux);
                if (aux == null) {
                    line = -1;
                    close();
                }
                line++;
            } catch (IOException err) {
            	err.printStackTrace(System.err);
            }
        }
        return line;
    }

    /**
     * Escreve o texto completo e fecha o arquivo.
     *
     * @param t o texto a ser escrito.
     */
    public void writeText(String t) {
        if ((file != null) && (type == WRITE) && (out == null)) {
            try {
                out = new BufferedWriter(new FileWriter(file, append));
                out.write(t, 0, t.length());
                out.flush();
                out.close();
                out = null;
            } catch (IOException err) {
            	err.printStackTrace(System.err);
            }
        }
    }

    /**
     * Escreve um texto no arquivo seguido do terminador de linha.
     *
     * @param t a linha a ser escrita.
     */
    public void writeln(String t) {
        write(t, true);
    }

    /**
     * Escreve um texto no arquivo.
     *
     * @param t o texto a ser escrito.
     */
    public void write(String t) {
        write(t, false);
    }

    private void write(String l, boolean rn) {
        if ((file != null) && (type == WRITE)) {
            try {
                if (out == null) {
                    out = new BufferedWriter(new FileWriter(file, append));
                }
                if (out != null) {
                    if (rn) {
                        l = l + RET;
                    }
                    out.write(l, 0, l.length());
                    out.flush();
                }
            } catch (IOException err) {
            	err.printStackTrace(System.err);
            }
        }
    }
}
