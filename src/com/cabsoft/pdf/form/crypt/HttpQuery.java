package com.cabsoft.pdf.form.crypt;

import java.util.HashMap;
import java.util.StringTokenizer;

public class HttpQuery {

	private String charSet = "";
    private HashMap<String, String> params = new HashMap<String, String>();

    public HttpQuery() {
        params = new HashMap<String, String>();
        charSet = "UTF-8";
    }

    public void setQuery(String data) throws Exception {
        String qry = new String(Crypt.decryptFromHex(data)).trim();
        parseQuery(qry);
    }

    public void setQuery(String data, String enc) throws Exception {
        String qry = "";
        if(enc.equalsIgnoreCase("hex")){
            qry = new String(Crypt.decryptFromHex(data)).trim();
        }else if(enc.equalsIgnoreCase("cryptebase64")){
        	qry = new String(Crypt.decryptFromBase64(data)).trim();
        }else if(enc.equalsIgnoreCase("base64")){
            qry = Base64.decode(data, "UTF-8").trim();
        }
        parseQuery(qry);
    }
    
    public void parseQuery(String qry) {
        StringTokenizer st = new StringTokenizer(qry, "&");
        StringTokenizer st1;

        try{
            while (st.hasMoreTokens()) {
                String nv = st.nextToken().trim();
                st1 = new StringTokenizer(nv, "=");
                String p = st1.nextToken().trim();
                String q = "";
                if (st1.hasMoreTokens()) {
                    q = st1.nextToken().trim();
                }
                if (!q.equalsIgnoreCase("")) {
                	q = java.net.URLDecoder.decode(q, charSet);
                }
                params.put(p, q);
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    public String getQuery(String name) {
    	if(params.containsKey(name))
    		return params.get(name);
    	else
    		return "";
    }

    public HashMap<String, String> getParams() {
        return params;
    }

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
}
