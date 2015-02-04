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

package br.com.webinside.modules.parser;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class TokenMgrError extends Error {

	private static final long serialVersionUID = 1L;
	
    /*
     * Ordinals for various reasons why an Error of this type can be thrown.
     */
    /** Lexical error occured. */
    static final int LEXICAL_ERROR = 0;
    /**
     * An attempt wass made to create a second instance of a static token
     * manager.
     */
    static final int STATIC_LEXER_ERROR = 1;
    /** Tried to change to an invalid lexical state. */
    static final int INVALID_LEXICAL_STATE = 2;
    /** Detected (and bailed out of) an infinite loop in the token manager. */
    static final int LOOP_DETECTED = 3;
    /**
     * Indicates the reason why the exception is thrown. It will have one of
     * the above 4 values.
     */
    int errorCode;

    /*
     * Constructors of various flavors follow.
     */
    public TokenMgrError() {
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param message DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(String message, int reason) {
        super(message);
        errorCode = reason;
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param EOFSeen DOCUMENT ME!
     * @param errorLine DOCUMENT ME!
     * @param errorColumn DOCUMENT ME!
     * @param errorAfter DOCUMENT ME!
     * @param curChar DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(boolean EOFSeen, int errorLine, int errorColumn,
        String errorAfter, char curChar, int reason) {
        this(LexicalError(EOFSeen, errorLine, errorColumn, errorAfter, curChar),
            reason);
    }

    /**
     * Replaces unprintable characters by their espaced (or unicode escaped)
     * equivalents in the given string
     *
     * @param str DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static final String addEscapes(String str) {
        StringBuffer retval = new StringBuffer();
        char ch;
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case 0:
                continue;

            case '\b':
                retval.append("\\b");
                continue;

            case '\t':
                retval.append("\\t");
                continue;

            case '\n':
                retval.append("\\n");
                continue;

            case '\f':
                retval.append("\\f");
                continue;

            case '\r':
                retval.append("\\r");
                continue;

            case '\"':
                retval.append("\\\"");
                continue;

            case '\'':
                retval.append("\\\'");
                continue;

            case '\\':
                retval.append("\\\\");
                continue;

            default:
                if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u"
                        + s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }
                continue;
            }
        }
        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the token
     * manager to indicate a lexical error. Parameters : EOFSeen     :
     * indicates if EOF caused the lexicl error curLexState : lexical state in
     * which this error occured errorLine   : line number when the error
     * occured errorColumn : column number when the error occured errorAfter :
     * prefix that was seen before this error occured curchar     : the
     * offending character Note: You can customize the lexical error message
     * by modifying this method.
     *
     * @param EOFSeen DOCUMENT ME!
     * @param errorLine DOCUMENT ME!
     * @param errorColumn DOCUMENT ME!
     * @param errorAfter DOCUMENT ME!
     * @param curChar DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static final String LexicalError(boolean EOFSeen, int errorLine,
        int errorColumn, String errorAfter, char curChar) {
        return ("Lexical error at line " + errorLine + ", column "
        + errorColumn + ".  Encountered: "
        + (
            EOFSeen ? "<EOF> "
                    : (
                ("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " ("
                + (int) curChar + "), "
            )
        ) + "after : \"" + addEscapes(errorAfter) + "\"");
    }

    /**
     * You can also modify the body of this method to customize your error
     * messages. For example, cases like LOOP_DETECTED and
     * INVALID_LEXICAL_STATE are not of end-users concern, so you can return
     * something like : "Internal Error : Please file a bug report .... " from
     * this method for such cases in the release version of your parser.
     *
     * @return DOCUMENT ME!
     */
    public String getMessage() {
        return super.getMessage();
    }
}
