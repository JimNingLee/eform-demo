package com.cabsoft.utils;

import com.cabsoft.rx.engine.ReportExpressPrint;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;








public class PdocUtil
{
	public Map<String, Object> streamTomap(HttpServletRequest request){
		
		String jstr = "";
		StringBuffer paramData = new StringBuffer();
		BufferedReader br = null;
		
		try{
			br =  new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			while((jstr = br.readLine()) != null){
				paramData.append(jstr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> mapObj = null;
		try {
			mapObj = mapper.readValue(paramData.toString(), new TypeReference<Map<String, Object>>() {});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("---------------------------mapObj : "+mapObj);		
		
		return mapObj;
		
	}

  
  public JSONObject streamJasonArray(HttpServletRequest request) throws JsonIOException, JsonSyntaxException, UnsupportedEncodingException, IOException, JSONException {
    JsonParser jsonParser = new JsonParser();
    
    JsonObject paramJson = null;
    
    paramJson = new JsonObject();
    
    try {
      paramJson = (JsonObject)jsonParser.parse(new InputStreamReader(request.getInputStream(), "UTF-8"));
      System.out.println(paramJson);
      //입력된 값을 JSON으로 변경
    }
    catch (Exception e) {
      e.printStackTrace();
    } 

    
    JSONObject rsltJson = new JSONObject(paramJson.toString());
    //json형식의 paramJson의 string값으로 JSON객체를 생성
    
    System.out.println("############# rsltJson:" + rsltJson.toString());
    
    return rsltJson;
  }



  
  public String getDocInfo(ReportExpressPrint rxPrint, String pdfEncode) {
    String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"2000\"}, \"data\" : {\"title\":\"" + rxPrint.getName() + "\", \"pageNum\":\"" + rxPrint.getPages().size() + "\", \"pageWidth\":\"" + rxPrint.getPageWidth() + "\", \"pdfEncode\":\"" + pdfEncode + "\", \"pageHeight\":\"" + rxPrint.getPageHeight() + "\"} }";
  }



  
  public String getBankaDocInfo(String pageNum, String pageWidth, String pageHeight, String pdfEncode) {
	String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"2000\"}, \"data\" : {\"title\":\"전자문서\", \"pageNum\":\"" + pageNum + "\", \"pageWidth\":\"" + pageWidth + "\", \"pdfEncode\":\"" + pdfEncode + "\", \"pageHeight\":\"" + pageHeight + "\"} }";
  }



  
  public String getPageImg(String base64PageImgStr) {
	  String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"2000\"}, \"data\" : {\"pageImg\":\"" + base64PageImgStr + "\", \"pageIdx\":\"1\"} }";
  }





  
  public String exportPdfSuccess(String resultmsg, String pdfEn) {
	  String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"2000\",\"extMsg\" : \"" + resultmsg + "\",\"pdfEn\" : \"" + pdfEn + "\"}}";
  }





  
  public String processSuccess(String rsltList) {
	  String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"2000\",\"rsltList\" : \"" + rsltList + "\"}}";
  }





  
  public String exportPdfError(String resultmsg) {
	  String retStr = "";
    
    return "{ \"head\" : {\"cd\" : \"3000\",\"extMsg\" : \"" + resultmsg + "\"}}";
  }
}



