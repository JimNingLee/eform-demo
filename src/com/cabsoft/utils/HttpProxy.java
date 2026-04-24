package com.cabsoft.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpProxy {
	
	static final String GET = "get";
	static final String POST = "post";
	
	private Map<String, String> params = new HashMap<String, String>();
	private String url = "";
	private String method = "";
	private String encoding = "";
	
	public HttpProxy(){
		params.clear();
		url = "";
		method = "";
		encoding = "utf-8";
	}
	
	public HttpProxy(String purl, Map<String, String> pparams, String pmethod, String pencoding){
		params.clear();
		params = pparams;
		url = purl;
		method = pmethod;
		encoding = pencoding;
	}
	
	public HttpProxy(String purl, Map<String, String> pparams, String pmethod){
		params.clear();
		params = pparams;
		url = purl;
		method = pmethod;
		encoding = "utf-8";
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void addParams(String name, String value){
		params.put(name, value);
	}
	
	public void removeParams(String name){
		params.remove(name);
	}
	
	private String post(){
		HttpClient client = new DefaultHttpClient();
		
		try{
			HttpPost post = new HttpPost(url);
			System.out.println("POST : " + post.getURI());
			
			List<NameValuePair> paramList = convertParam(params);
			post.setEntity(new UrlEncodedFormEntity(paramList, encoding));
			HttpResponse hp = client.execute(post);
			return EntityUtils.toString(hp.getEntity());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		
		return "error";
	}
	
	private String get(){
		HttpClient client = new DefaultHttpClient();

		try{
			List<NameValuePair> paramList = convertParam(params);
			HttpGet get = new HttpGet(url+"?"+URLEncodedUtils.format(paramList, encoding));
			System.out.println("GET : " + get.getURI());
			
			ResponseHandler<String> rh = new BasicResponseHandler();
			
			return client.execute(get, rh);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		
		return "error";
	}
	
	private List<NameValuePair> convertParam(Map<String, String> params){
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		Iterator<String> keys = params.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
		}
		
		return paramList;
	}
	
	public String execute(){
		if("get".equalsIgnoreCase(method)){
			return get();
		}else if("post".equalsIgnoreCase(method)){
			return post();
		}else{
			return "설정값을 확인하시기 바랍니다.(method)";
		}
	}
	
	public static void main(String[] args) {
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("w", "tot");
		m.put("q", "한예슬");
		
		HttpProxy p1 = new HttpProxy("http://dev.cabsoftware.com:8080/rxcert/", m, HttpProxy.POST, "utf-8");
		System.out.println(p1.execute());
		
		HttpProxy p = new HttpProxy();
		p.setUrl("http://dev.cabsoftware.com:8080/rxcert/");
		p.setEncoding("utf-8");
		p.setMethod(HttpProxy.POST);
		p.addParams("w", "tot");
		p.addParams("q", "김태희");
		
		System.out.println(p.execute());
		
		p.setMethod(HttpProxy.GET);
		System.out.println(p.execute());

	}


}
