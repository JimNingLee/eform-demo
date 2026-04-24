package com.cabsoft.datasource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class JBBankData implements Serializable {
    private static final long serialVersionUID = 20130507L;
    
    HashMap<String, Object> HeaderColName;
    HashMap<String, Object> HeaderData;
    HashMap<String, Object> ListColName;
    HashMap<String, Object> FooterColName;
    HashMap<String, Object> FooterData;
    
    JBListData ListData;
    
    String title;
    String LangType;

    public HashMap<String, Object> getHeaderColName() {
        return HeaderColName;
    }

    public void setHeaderColName(HashMap<String, Object> HeaderColName) {
        this.HeaderColName = HeaderColName;
    }

    public HashMap<String, Object> getHeaderData() {
        return HeaderData;
    }

    public void setHeaderData(HashMap<String, Object> HeaderData) {
        this.HeaderData = HeaderData;
    }

    public HashMap<String, Object> getListColName() {
        return ListColName;
    }

    public void setListColName(HashMap<String, Object> ListColName) {
        this.ListColName = ListColName;
    }

    public HashMap<String, Object> getFooterColName() {
        return FooterColName;
    }

    public void setFooterColName(HashMap<String, Object> FooterColName) {
        this.FooterColName = FooterColName;
    }

    public HashMap<String, Object> getFooterData() {
        return FooterData;
    }

    public void setFooterData(HashMap<String, Object> FooterData) {
        this.FooterData = FooterData;
    }

    public JBListData getListData() {
        return ListData;
    }

    public void setListData(JBListData ListData) {
        this.ListData = ListData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLangType() {
        return LangType;
    }

    public void setLangType(String LangType) {
        this.LangType = LangType;
    }
    
    /*
     * 
     */
    public class JBListData implements Serializable {
    	private static final long serialVersionUID = 20130507L;
    	
    	List<HashMap<String, Object>> list;

        public List<HashMap<String, Object>> getList() {
            return list;
        }

        public void setList(List<HashMap<String, Object>> list) {
            this.list = list;
        }
    }
    
}
