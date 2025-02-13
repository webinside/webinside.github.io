package br.com.webinside.runtime.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public abstract class ElasticUtil {
	
	private static boolean DEBUG = false;
	
	public static final String GET = "get";
	public static final String PUT = "put";
	public static final String POST = "post";
	public static final String DELETE = "delete";

	public static String INDEX = "/";
	
	public static JSONObject sendHttp(String type, String path, JSONObject json) throws Exception {
		URIBuilder uri = new URIBuilder("http://localhost:9200");
		if (path.equals("attachment")) uri.setPath("/_ingest/pipeline/attachment");
		else uri.setPath(INDEX + path);
		if (json != null && json.get("base64") != null) uri.addParameter("pipeline", "attachment");
		if (DEBUG) {
			uri.addParameter("pretty", "true");
			System.out.println("------------------------------ SEND HTTP ------------------------------");
			System.out.println("METHOD: " + type.toUpperCase());
			System.out.println("URI: " + uri.build());
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest httpReq = null;
		if (type.equals(GET)) {
			httpReq = new HttpGet(uri.build());
		} else if (type.equals(POST)) {
			HttpPost httpPost = new HttpPost(uri.build());
			HttpEntity stringEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
			httpPost.setEntity(stringEntity);
			httpReq = httpPost;
		} else if (type.equals(PUT)) {
			HttpPut httpPut = new HttpPut(uri.build());
			HttpEntity stringEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
			httpPut.setEntity(stringEntity);
			httpReq = httpPut;
		} else if (type.equals(DELETE)) {
			httpReq = new HttpDelete(uri.build());
		}

		String auth = "usuario:senha";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpReq.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        
		if (DEBUG) {
			System.out.println("--- REQUEST ---");
			System.out.println(prettyJson(json));
		}
		CloseableHttpResponse response = httpclient.execute(httpReq);
	    int status = response.getStatusLine().getStatusCode();
	    if (DEBUG) System.out.println("STATUS: " + status);
		InputStream in = response.getEntity().getContent();
	    Reader reader = new InputStreamReader(in, "UTF-8");
	    String resp = IOUtils.toString(reader).trim();
	    JSONObject respJson = (JSONObject) new JSONParser().parse(resp);
	    response.close();
	    httpclient.close();
		if (DEBUG) {
			System.out.println("--- RESPONSE ---");
			System.out.println(resp);
		}
	    return respJson;
	}
	
	public static JSONObject jsonObj(JSONObject parent, String name) {
		JSONObject aux = (JSONObject) parent.get(name);
		if (aux == null) {
			aux = new JSONObject();
			parent.put(name, aux);
		}
		return aux;
	}

	public static List jsonArr(JSONObject parent, String name, String key, Object value) {
		List list = (List) parent.get(name);
		if (list == null) {
			list = new ArrayList();
			parent.put(name, list);
		}
		if (value != null) {
			JSONObject aux = new JSONObject();
			aux.put(key, value);
			list.add(aux);
		} else {
			list.add(key);
		}
		return list;
	}
	
	public static String prettyJson(JSONObject json) {
		if (json == null) return "";
		String ident = "";
		boolean ignore = false;
		StringBuilder resp = new StringBuilder();
		String txt = json.toJSONString();
		for (int i = 0; i < txt.length(); i++) {
			char let = txt.charAt(i);
			if (ignore) {
				resp.append(let);
				if (let =='"' && txt.charAt(i-1) != '\\') ignore = false;
				continue;
			}
			if (let == '}' || let == ']') {
				ident = ident.substring(0, ident.length() - 4);
				resp.append("\r\n").append(ident);
			}
			resp.append(let);
			if (let == ':') resp.append(" ");
			if (let == ',') resp.append("\r\n").append(ident);
			if (let == '{' || let == '[') {
				ident += "    ";
				resp.append("\r\n").append(ident);
			}
			if (let =='"') ignore = true;
		}
		return resp.toString().trim();
	}
	
	public static void exportPdf(HttpServletResponse response, String id) throws Exception {
		JSONObject respJson = ElasticUtil.sendHttp(ElasticUtil.GET, "/_doc/" + id, null);
		JSONObject source = (JSONObject) respJson.get("_source");
		if (source == null) return;
        byte[] decoded = Base64.decodeBase64((String)source.get("base64"));
		String fname = source.get("ts_pdf_title") + " - pg " + source.get("id_pdf_page") + ".pdf";
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=\"" + fname + "\"");
		response.setContentLength((int) decoded.length);
		InputStream in = new ByteArrayInputStream(decoded);
		Function.copyStream(in, response.getOutputStream());
		in.close();
	}
	
}
