package com.cabsoft.ess;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.utils.Hash;
import com.cabsoft.utils.StringUtils;
import com.cabsoft.ecrypt.Crypt;

@SuppressWarnings({"deprecation", "unused"})
public class BuildForm {
	private final Log log = LogFactory.getLog(BuildForm.class);
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	
	/*
	 * html element의 id
	 */
	public final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";

	/*
	 * html element의 alt 속성 설정
	 */
	public final String PROPERTY_HTML_ALT = HTML_EXPORTER_PROPERTIES_PREFIX + "alt";

	/*
	 * html element의 설명을 위한 title 속성 설정
	 */
	public final String PROPERTY_HTML_TITLE = HTML_EXPORTER_PROPERTIES_PREFIX + "title";
	
    /*
     * html element의 tag 값 설정
     * select: <option selected="selected">충북</option> <option>충남</option> <option>강원</option>
     * checkbox: 0 또는 1, 기본값이 체크인 경우 1을 설정한다.
     * radio button: 0 또는 1, 기본적으로 선택되는 radio button에만 1을 설정한다.
     */
    public final String PROPERTY_HTML_TAGVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "tagValue";
    
    /*
     * html element의 onclick 등의 이벤트 발생시 호출되는 자바스크립트 설정
     * 
     * onclick="doCheck(this)"
     * 
     * 이때 doCheck(this) 등의 함수는 이미 정의 되어 있어야 한다.
     */
    public final String PROPERTY_HTML_SCRIPT = HTML_EXPORTER_PROPERTIES_PREFIX + "script";
    
    /*
     * 해당 radio button의 값을 설정한다.
     */
    public final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";
    
    public final String PROPERTY_HTML_DESCRIPTION = HTML_EXPORTER_PROPERTIES_PREFIX + "desc";
    public final String PROPERTY_HTML_TAG_TYPE = HTML_EXPORTER_PROPERTIES_PREFIX + "type";

    
	
	public BuildForm(){
		
	}
	
	public void build(Writer writer, String formName, String customer, String product, List<RXPrintText> formList, List<Integer> formListPage, HashMap<String, Object> Hmconfirm) throws Exception{
		//String mtd = "font-size:12px; font-family:\"굴림체,seoul,verdana\"; color:#737373; line-height:14px;";
		//String td = "border:1px solid rgb(205, 214, 227); padding: 5px 8px 5px 8px;";
		Hash md5 = new Hash();
		StringBuffer json = new StringBuffer();
		StringBuffer encData = new StringBuffer();
		json.setLength(0);
		encData.setLength(0);
       	json.append("[");
		writer.write("<script type=\"text/javascript\">");
		writer.write("var desc_map = new Map();");
		writer.write("var value_map = new Map();");
		writer.write("var page_map = new Map();");
		writer.write("var confirm_map = new Map();");
		writer.write("var text_map = new Map();");
		
//		writer.write("value_map.put(\"customer\", \"" + customer + "\");\n");
//		writer.write("value_map.put(\"product\", \"" + product + "\");\n");
    	json.append("{");
    	json.append("\"id\":\"customer\"");
    	json.append(",\"value_map\":\""+customer+"\"");
    	json.append("}");
    	json.append(",{");
    	json.append("\"id\":\"product\"");
    	json.append(",\"value_map\":\""+product+"\"");
    	json.append("}");

		for(int i=0; i<formList.size(); i++){
//		for(int i=formList.size()-1; i > -1; i--){
			RXPrintText printText = formList.get(i);
			Integer current_page = formListPage.get(i);
			String text = printText.getFullText();
			String id = RXProperties.getProperty(printText, PROPERTY_HTML_ID);
			String name = RXProperties.getProperty(printText, PROPERTY_HTML_NAME);
			String tag_type = RXProperties.getProperty(printText, PROPERTY_HTML_TAG_TYPE);
			String tagValue = RXProperties.getProperty(printText, PROPERTY_HTML_TAGVALUE);
			String radiovalue = RXProperties.getProperty(printText, PROPERTY_HTML_RADIOVALUE);
	        String tag_desc = RXProperties.getProperty(printText, PROPERTY_HTML_DESCRIPTION);
	        
	        if(!StringUtils.isNull(id)){
	            String[] tag = null;
	            String type = "";
				String value = "";
	            if(text.indexOf("|")>0){
	                tag = StringUtils.split(text, "|");
	                type = tag[0];
	                value = tag[1];
	            }else{
	                value = text;
	            }
//		        log.debug ("id:"+id+",name:"+name+",tag_type:"+tag_type+",type:"+type+",value:"+value+",tagvalue:"+tagValue+",radiovalue:"+radiovalue+",tag_desc:"+tag_desc+",tag:"+tag+",text:"+text);

            	json.append(",{");
            	json.append("\"id\":\""+id+"\"");
	            
	            /*
	             * 일반 테스트인 경우
	             */
	            if("text".equalsIgnoreCase(tag_type)){
//	            	writer.write("desc_map.put(\"" + id + "\", \"" + tag_desc + "\");\n");
//	            	writer.write("value_map.put(\"" + id + "\", \"\");\n");
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+text+"\"");
	            }
	            /*
	             * 확인 받는 컨트롤(confirm)
	             */
	            else if("textconfirm".equalsIgnoreCase(tag_type)){
	            	json.append(",\"desc_map\":\""+tag_desc +"\"");
	            	json.append(",\"value_map\":\"\"");
	            	encData.append(tag_desc+":"+"");
	            	try {
	            		String ss = new String(com.cabsoft.utils.Base64Util.encode(printText.getFullText().getBytes("utf-8"), false));
	            		ss = StringUtils.replaceAll(ss,  "\n", "");
//						writer.write("confirm_map.put(\"" + id + "\", \"" +  md5.encryptText(ss) + "\");\n");
		            	json.append(",\"confirm_map\":\""+md5.encryptText(ss)+"\"");
		            	json.append(",\"text_map\":\""+ss+"\"");
						// 확인 정보 세션에 넣음
						Hmconfirm.put(id, printText.getFullText());
				} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            else if("readconfirm".equalsIgnoreCase(tag_type)){
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+""+"\"");
	            }
	            else if("buttonconfirm".equalsIgnoreCase(tag_type)){
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+""+"\"");
	            }
	            /*
	             * 버튼인 경우
	             */
	            else if("button".equalsIgnoreCase(tag_type)){
	            	
	            }
	            /*
	             * radio button 인 경우
	             */
	            else if("radio".equalsIgnoreCase(tag_type)){
	            	json.append(",\"value_map\":\""+""+"\"");
	            	
	            	if (tag_desc.indexOf(";;") > 0) {
		            	String[] sDesc = tag_desc.split(";;"); //;;
		            	json.append(",\"desc_map\":\""+ sDesc[0] +"\"");
//		            	writer.write("desc_map.put(\"" + id + "\", \"" + sDesc[0] + "\");\n");
//	            		writer.write("desc_map.put(\"" + id + "_" + radiovalue + "\", \"" + sDesc[1] + "\");\n");
	            	} else {
		            	json.append(",\"desc_map\":\""+ tag_desc +"\"");
	            	}
	            	// radio 의 초기값은 windows.onload 시에 결정
//            		writer.write("value_map.put(\"" + id + "\", \"" + "" + "\");\n");
	            }
	            /*
	             * checkbox 인 경우
	             */
	            else if("checkbox".equalsIgnoreCase(tag_type)){
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+tagValue+"\"");
	            }
	            /*
	             * select 태그인 경우
	             */
	            else if("select".equalsIgnoreCase(tag_type)){
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+""+"\"");
	            }
	            /*
	             * 기타
	             */
	            else{
	            	json.append(",\"desc_map\":\""+tag_desc+"\"");
	            	json.append(",\"value_map\":\""+""+"\"");
	            }
            	json.append(",\"page_map\":\""+(current_page * 10000 + printText.getY()*2)+"\"");
               	json.append("}\n");
	        } 
		}
       	json.append("]");
		Hmconfirm.put("jsondata", json.toString());
//		log.debug( json.toString());
		writer.write("endData=\""+Crypt.eFormEncrypt(encData.toString(), "utf-8")+"\";\n");
		writer.write("</script>\n");
	}
}
