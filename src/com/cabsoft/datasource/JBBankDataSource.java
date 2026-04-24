package com.cabsoft.datasource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.cabsoft.rx.engine.RXDataSource;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXField;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class JBBankDataSource implements RXDataSource, Serializable {
	private static final long serialVersionUID = 20130507L;
	
	private JBBankData jbData = null;
	List<HashMap<String, Object>> list;
	
	private int index = -1;
	private int recordCount = -1;
	private int totalRecordCount = -1;
	private int startIndex = -1;
	private int endIndex = -1;
	
	/*
	 * JSON 데이터(문자열)를  JBBankData 클래스로 변환한다.
	 */
	public JBBankDataSource(String jsonData){
		jbData = new Gson().fromJson(jsonData, JBBankData.class);
		
		/*
		 * JBBankData의 반복 리스트를 구한다.
		 */
		list = jbData.getListData().getList();
		
		totalRecordCount = list.size();
		recordCount = list.size();
	}
	
	public int getTotalRecordCount(){
		return totalRecordCount;
	}
	
	public void setRecordList(int startIDX, int endIDX){
		this.startIndex = startIDX;
		this.endIndex = endIDX;
		
		if(startIDX>-1 && endIDX>-1){
			endIndex = endIDX>totalRecordCount ? totalRecordCount : endIDX;
			list = jbData.getListData().getList().subList(startIndex, endIndex);
		}else{
			list = jbData.getListData().getList();
		}
		recordCount = list.size();
	}
	
	/*
	 * HeaderColName을 HashMap으로 반환한다.
	 */
	public HashMap<String, Object> getHeaderColName(){
		return jbData.getHeaderColName();
	}
	
	/*
	 * HeaderData를 HashMap으로 반환한다.
	 */
	public HashMap<String, Object> getHeaderData(){
		return jbData.getHeaderData();
	}
	
	/*
	 * ListColName을 HashMap으로 반환한다.
	 */
	public HashMap<String, Object> getListColName(){
		return jbData.getListColName();
	}
	
	/*
	 * FooterColName을 HashMap으로 반환한다.
	 */
	public HashMap<String, Object> getFooterColName(){
		return jbData.getFooterColName();
	}
	
	/*
	 * FooterData를 HashMap으로 반환한다.
	 */
	public HashMap<String, Object> getFooterData(){
		return jbData.getFooterData();
	}
	
	/*
	 * title을 반환한다.
	 */
	public String getTitle(){
		return jbData.getTitle();
	}
	
	/*
	 * LangType을 반환한다.
	 */
	public String getLangType(){
		return jbData.getLangType();
	}
	
	/*
	 * Detail 밴드에서 반복 루프 이벤트 발생시 호출됨 
	 */
	public boolean next() throws RXException {
		/*
		 * 이미 처리된 데이터를 제거한다.
		 */
		if(index!=-1){
			list.remove(index);
		}
		
//		index++;
//		boolean ret = (index < list.size());
//		return ret;
//		
		index = 0;
		return (list.size()>0);
	}
	
	/*
	 * Detail 밴드에서 출력할 필드명에 대한 값을 반환한다.
	 * 테이터 형이 자동 변환 되지 않으므로 디자인할 때 주의한다.
	 */
	public Object getFieldValue(RXField field) throws RXException {
		Object value = null;

		String fieldName = field.getName();

		HashMap<String, Object> data = list.get(index);
		
		if(data.containsKey(fieldName)){
			value = data.get(fieldName);
		}
//		System.out.println(">>>>>>>>>>>>>>>>>> Index = " + index + "  size = " + list.size() + " recordCount = " + recordCount);

		return value;
	}
	
}
