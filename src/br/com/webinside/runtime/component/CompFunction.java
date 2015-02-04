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

package br.com.webinside.runtime.component;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;

import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.xml.Inputter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class CompFunction {
    
	// Caminho definido no login para o builder/conf onde estão os templates.
    private static String templatesDir = "";
    private static ThreadLocal thisProject = new ThreadLocal();
    public static String WEBAPP_VERSION = "3.0";

    private CompFunction() { } 
    
    /**
     * DOCUMENT ME!
     */ 
	public static void setProject(AbstractProject project) {
		thisProject.set(project);
	}
    
    /**
     * DOCUMENT ME!
     *
     * @param resp DOCUMENT ME!
     * @param objName DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param ident DOCUMENT ME!
     */
    public static void setProperty(StringBuffer resp, String objName,
        String property, String value) {
        if ((resp == null) || value.equals("")) {
            return;
        }
        value = filterTagAttribute(value);
        resp.append("<jsp:setProperty\n");
    	resp.append("  name=\"" + objName + "\" property=\"" + property + "\"");
    	resp.append(" value=\"" + value + "\"\n");
        resp.append("/>");
    }

    /**
     * DOCUMENT ME!
     *
     * @param txt DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String filterTagAttribute(String txt) {
        if (txt == null) {
            return "";
        }
        AbstractProject proj = (AbstractProject) thisProject.get();
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < txt.length(); i++) {
            char let = txt.charAt(i);
            if (proj != null && proj.isMakeCompatible()) {
            	if (let == '\n') let = ' ';
            	else if (let == '\r') continue;
            }
            if (let == '"') {
                resp.append("\\\"");
            } else if (let == '\\') {
                resp.append("\\\\");
            } else if (let == '$' && WEBAPP_VERSION.equals("2.4")) {
            	// usado quando tem funcao num gravar 
            	// ou num objeto no tomcat 5
                resp.append("\\$");
            } else {
                resp.append(let);
            }
        }
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ele DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String toJSP(AbstractActionElement ele) {
        return toJSP(ele, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param ele DOCUMENT ME!
     * @param withCore DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String toJSP(AbstractActionElement ele, boolean withCore) {
        if (ele == null) {
            return "";
        }
        String seq = ele.getSeq();
        String cName = ele.getClass().getSimpleName();
        // Nome curto
        String shortName = StringA.piece(cName, "Ref", 1);
        if (shortName.startsWith("Redir")) {
            shortName = "Redir";
        }
        if (shortName.startsWith("Cookie")) {
            shortName = "Cookie";
        }
        if (shortName.startsWith("FileList")) {
            shortName = "FileList";
        }
        if (shortName.startsWith("FileRemove")) {
            shortName = "FileRemove";
        }

        // Gerando elemento		
        String varName = shortName.toLowerCase() + seq;
        StringBuffer resp = new StringBuffer();
        resp.append("<w:setPageElement\n");
    	resp.append("  type=\"" + cName + "\" name=\"" + varName + "\"\n");
        resp.append("/><jsp:useBean\n  id=\"" + varName + "\" ");
        resp.append("type=\"br.com.webinside.runtime.component." + cName + "\"\n");
        resp.append("/>");        
        resp.append(setProperties(ele, varName));
        if (withCore) {
        	resp.append(jspCore(shortName, varName));
        }
        return resp.toString();
    }

	public static String jspCore(String type, String id) {
		StringBuffer resp = new StringBuffer();
		resp.append("<w:executeCore\n");
		resp.append("  type=\"" + type + "\" name=\"" + id + "\"");
		resp.append("\n/>");
		return resp.toString();
	}

	/**
     * DOCUMENT ME!
     *
     * @param ele DOCUMENT ME!
     * @param varName DOCUMENT ME!
     * @param ident DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String setProperties(Object ele, String varName) {
        StringBuffer resp = new StringBuffer();
        List names = new ArrayList();
        Method[] methods = ele.getClass().getMethods();
        if (methods != null) {
			for (int i=0;i<methods.length;i++) {
				names.add(methods[i].getName());
			}        	
        }
        Collections.sort(names);
        for (int i = 0; i < names.size(); i++) {
            String mName = (String)names.get(i);
            if (!mName.startsWith("set")) {
                continue;
            }
            String mComp = StringA.mid(mName, 3, mName.length());

            // sets desnecessarios
            if (mComp.equals("Seq")) {
                continue;
            }

            // elementos gerados pelo wizard
            if (mComp.equals("FromObject")) {
                continue;
            }

            // existe no Update e no EventUpdate e no TransactionElement
            boolean transaction = 
            	ele.getClass().isAssignableFrom(TransactionElement.class);
            if (mComp.equals("MessageFalse") && !transaction) {
                continue;
            }

            // existe no WebServiceMethod
            if (mComp.equals("FaultOrder")) {
                continue;
            }

            // existe em PageBuilder
            if (mComp.equals("AsGridTemplate")) {
                continue;
            }

            // existe no Connector e no JavaGrid
            if (mComp.equals("PluginName") || mComp.equals("PluginNamespace")) {
                continue;
            }

            // existe no Grid
            if (mComp.equals("ContentSharedGrid")
                        || mComp.equals("ContentUnique")) {
                continue;
            }

            // existe na Página e no Grid
            if (mComp.startsWith("Content")) {
                if ((ele instanceof Page) || 
                		(ele instanceof AbstractGridLinear)) {
                    continue;
                }
            }
            
            // existe na Página 
            if (mComp.equals("UsePersistOnWizard")) {
                continue;
            }

            // recuperando o get correspondente	
            String mValue = "";
            try {
                String mGetName = "get" + mComp;
                
                // Existe no ObjectElement
                if (ele.getClass().isAssignableFrom(ObjectElement.class)) {
                    if (mGetName.equals("getMultiple")) {
                        mGetName = "isMultiple";
                    }
                    if (mGetName.equals("getUsejson")) {
                        mGetName = "isUsejson";
                    }
                }

                // Existe no GridSql
                if (ele.getClass().isAssignableFrom(GridSql.class)) {
                    if (mGetName.equals("getRecursive")) {
                        mGetName = "isRecursive";
                    }
                }

                // Existe no Redir
                if (mGetName.equals("getReturn")) {
                    mGetName = "hasReturn";
                }

                // Existe no GridRef
                if (mGetName.equals("getGenerateInPage")) {
                    mGetName = "isGenerateInPage";
                }
                if (mGetName.equals("getReturnEmpty")) {
                    mGetName = "isReturnEmpty";
                }

                // Existe no WIObjectGrid
                if (mGetName.equals("getWithNavigator")) {
                    mGetName = "isWithNavigator";
                }

                // Existe no FileOut
                if (mGetName.equals("getAppend")) {
                    mGetName = "isAppend";
                }

                // Existe no TransactionElement
                if (ele.getClass().isAssignableFrom(TransactionElement.class)) {
                    if (mGetName.equals("getStart")) {
                        mGetName = "isStart";
                    }
                }

                // Existe no Page
                if (mGetName.equals("getErrorPage")) {
                    mGetName = "isErrorPage";
                }
                if (mGetName.equals("getSyncPre")) {
                    mGetName = "isSyncPre";
                }
                if (mGetName.equals("getSyncPos")) {
                    mGetName = "isSyncPos";
                }
                if (mGetName.equals("getCheckPosToken")) {
                    mGetName = "isCheckPosToken";
                }

                // Existe no Conector
                if (mGetName.equals("getInPrePage")) {
                    mGetName = "isInPrePage";
                }

                // Existe no Download e Upload
                if (mGetName.equals("getZip")) {
                    mGetName = "isZip";
                }
                if (mGetName.equals("getBase64")) {
                    mGetName = "isBase64";
                }

                // Existe no Gravar
                if (mGetName.equals("getDisableProduce")) {
                    mGetName = "isDisableProduce";
                }

                Method mRef = ele.getClass().getMethod(mGetName, new Class[0]);
                if (mRef.getReturnType().isAssignableFrom(boolean.class)) {
                    mValue =
                        ((Boolean) mRef.invoke(ele, new Object[0]))
                                .booleanValue() + "";
                } else {
                    mValue = (String) mRef.invoke(ele, new Object[0]);
                }
            } catch (NoSuchMethodError err) {
                System.err.println(CompFunction.class.getName() + ": " + err);
                continue;
            } catch (Exception err) {
                if (!(err instanceof ClassCastException)) {
                    System.err.println(CompFunction.class.getName() + ": " + err);
                }
                continue;
            }
            if ((mValue == null) || mValue.equals("")) {
                continue;
            }
            if (!mComp.startsWith("WI")) {
	            String let = (mComp.charAt(0) + "").toLowerCase();
	            mComp = let + mComp.substring(1, mComp.length());
            }
			setProperty(resp, varName, mComp, mValue);
        }
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static String jspJavaParameters(JavaParameter[] parameters, 
    		String condition) {
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < parameters.length; i++) {
            JavaParameter param = parameters[i];
            String id = CompFunction.filterTagAttribute(param.getVarId());
            String test = CompFunction.filterTagAttribute(condition);
            String value = 
            	CompFunction.filterTagAttribute(param.getValue().trim());
            resp.append("<wi:set\n  var=\"" + id + "\" test=\"" + test + "\"");
            resp.append(" value=\"" + value + "\" scope=\"wi\" validation=\"true\"\n/>");
        }
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param msgs DOCUMENT ME!
     * @param varName DOCUMENT ME!
     * @param method DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String jspErrorMessages(List msgs, String varName) {
        StringBuffer resp = new StringBuffer();
        if (msgs != null) {
	        for (int i = 0; i < msgs.size(); i++) {
	            String msg = (String) msgs.get(i);
	            String cod = StringA.piece(msg, "[", 2);
	            cod = StringA.piece(cod, "]", 1);
	            String value = StringA.piece(msg, "] -", 2, 0).trim();
	            value = StringA.change(value, "\"", "\\\"");
	            resp.append("<w:setPropertyByMethod\n");
	            resp.append("  name=\"" + varName + "\"");
	            resp.append(" method=\"setMessageFalse\"");
	            resp.append(" arg1=\"" + cod + "\" arg2=\"" + value + "\"\n/>");
	        }
        }
        return resp.toString();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param fileName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static Document getTemplate(String fileName) {
        File tfl = new File(templatesDir, fileName);
        return new Inputter().input(tfl);
    }

    /**
     * @param builderDef The builderDef to set.
     */
    public static void setTemplatesDir(String builderConf) {
        CompFunction.templatesDir = builderConf;
    }

}
