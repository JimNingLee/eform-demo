package com.cabsoft.pdf.form;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SignPages {
	List<HashMap<String, SignPosition>> list = null;
	private int count = 0;
	HashMap<String, SignPosition> sign = null;
	
	public SignPages(){
		list = new ArrayList<HashMap<String, SignPosition>>();
	}
	
	public void newSignPosition(){
		sign = new HashMap<String, SignPosition>();
	}
	
	public void addSignPosition(SignPosition pos) throws Exception{
		count++;
		String key = "sign" + FormatNumber(count, "00");
		if(sign.containsKey(key)){
			throw new Exception("자필서명 이름이 중복되었습니다.");
		}else{
			sign.put(key, pos);
		}
	}
	
	public void addSignPages(){
		list.add(sign);
	}
	
	public List<HashMap<String, SignPosition>> getSignPages(){
		return list;
	}
	
	public String toXml(String charset){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"").append(charset).append("\"?>");
		sb.append("<sign>");
		for(int i=0; i<list.size(); i++){
			HashMap<String, SignPosition> ss = list.get(i);
			sb.append("<page>");
			sb.append("<num>").append(i+1).append("</num>");

			Set<String> set = ss.keySet();
			Object []hmKeys = set.toArray();
			Arrays.sort(hmKeys, keyStringSort);
			for(int j = 0; j < hmKeys.length; j++){
				sb.append("<pos>");
				String key = (String)hmKeys[j];
				SignPosition pos = ss.get(key);
				sb.append("<name>").append(pos.getId()).append("</name>");
				sb.append("<x1>").append(pos.getX1()).append("</x1>");
				sb.append("<y1>").append(pos.getY1()).append("</y1>");
				sb.append("<x2>").append(pos.getX2()).append("</x2>");
				sb.append("<y2>").append(pos.getY2()).append("</y2>");
				sb.append("</pos>");
			}
			sb.append("</page>");
		}
		sb.append("</sign>");
		return sb.toString();
	}
	
	private String FormatNumber(int dbl, String frm) {
		String ret = "";
		ret = new DecimalFormat(frm).format(dbl);
		return ret;
	}
	
    private Comparator<Object> keyStringSort = new Comparator<Object>() {
        public int compare(Object s1, Object s2) {
            String ss1 = (String)s1;
            String ss2 = (String)s2;
            return (-1) * ss2.compareTo(ss1);
        }
    };

}
