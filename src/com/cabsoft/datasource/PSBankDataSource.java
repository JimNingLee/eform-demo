package com.cabsoft.datasource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.cabsoft.rx.engine.RXDataSource;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXField;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PSBankDataSource implements RXDataSource, Serializable {

	private static final long serialVersionUID = 1983395779128520276L;
	private PSBankData psData = null;
	List<HashMap<String, Object>> list;
	
	private int index = -1;
	private int recordCount = -1;
	private int totalRecordCount = -1;
	private int startIndex = -1;
	private int endIndex = -1;
	
	public PSBankDataSource(String jsonData, String rootEl){
		JsonObject json = new JsonParser().parse(jsonData).getAsJsonObject();
	    JsonElement je = json.get(rootEl);
	    
		psData = new Gson().fromJson(je, PSBankData.class);
		
		list = psData.getList();
		
		totalRecordCount = list.size();
		setRecordCount(list.size());
	}
	
	public int getTotalRecordCount(){
		return totalRecordCount;
	}
	
	public void setRecordList(int startIDX, int endIDX){
		this.startIndex = startIDX;
		this.endIndex = endIDX;
		
		if(startIDX>-1 && endIDX>-1){
			endIndex = endIDX>totalRecordCount ? totalRecordCount : endIDX;
			list = psData.getList().subList(startIndex, endIndex);
		}else{
			list = psData.getList();
		}
		setRecordCount(list.size());
	}
	
	public String getTitle(){
		return psData.getTitle();
	}
	
	public String getToDAY(){
		return psData.getTO_DAY();
	}
	
	/*
	 * Detail ��忡�� �ݺ� ���� �̺�Ʈ �߻��� ȣ��� 
	 */
	public boolean next() throws RXException {
		/*
		 * �̹� ó���� �����͸� �����Ѵ�.
		 */
		if(index!=-1){
			list.remove(index);
		}
		
		index = 0;
		return (list.size()>0);
	}
	
	/*
	 * Detail ��忡�� ����� �ʵ�� ���� ���� ��ȯ�Ѵ�.
	 * ������ ���� �ڵ� ��ȯ ���� �����Ƿ� �������� �� �����Ѵ�.
	 */
	public Object getFieldValue(RXField field) throws RXException {
		Object value = null;

		String fieldName = field.getName();

		HashMap<String, Object> data = list.get(index);
		
		if(data.containsKey(fieldName)){
			value = data.get(fieldName);
		}
		return value;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	/**
	 * @return the list
	 */
	public List<HashMap<String, Object>> getList() {
		return list;
	}

}
