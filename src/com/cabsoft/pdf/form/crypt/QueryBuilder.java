package com.cabsoft.pdf.form.crypt;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class QueryBuilder {

    private HashMap<String, String> query = new HashMap<String, String>();

    public QueryBuilder() {
        query = new HashMap<String, String>();
    }

    public void addQuery(String name, String value) throws Exception {
        if (query.containsKey(name) == false) {
            query.put(name, value);
        } else {
            throw new Exception("[ " + name + " ]이 중복되었습니다.");
        }
    }

    public HashMap<String, String> getQuery() {
        return query;
    }

    @SuppressWarnings("deprecation")
	public String buildQuery() throws UnsupportedEncodingException {
        String ret = "";
        Iterator<String> it = query.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = query.get(key);
            value = value==null ? "" : value.trim();
            String s = key + "=" + java.net.URLEncoder.encode(value);
            if(ret.equalsIgnoreCase("")){
                ret = s;
            }else{
                ret = ret + "&" + s;
            }
        }
        return ret;
    }
    
    public String buildQuery(String charset) throws UnsupportedEncodingException {
        String ret = "";
        Iterator<String> it = query.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = query.get(key);
            value = value==null ? "" : value.trim();
            String s = key + "=" + java.net.URLEncoder.encode(value, charset);
            if(ret.equalsIgnoreCase("")){
                ret = s;
            }else{
                ret = ret + "&" + s;
            }
        }
        return ret;
    }
}
