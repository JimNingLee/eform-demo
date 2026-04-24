package com.cabsoft.ess.form;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.UAgentInfo;
import com.cabsoft.ecrypt.Crypt;
import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.rx.engine.util.RXStringUtil;
import com.cabsoft.utils.Hash;
import com.cabsoft.utils.StringUtils;
import com.cabsoft.utils.sysinfo;

@SuppressWarnings("deprecation")
public class FormPages implements Serializable {
	private static final long serialVersionUID = -2544267594740588410L;
	private Log log =LogFactory.getLog(this.getClass().getName());		
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";

	private final String fontfamily = "나눔고딕코딩";
	
	/*
	 * html element의 id
	 */
	private final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	private final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";

	/*
	 * html element의 alt 속성 설정
	 */
	private final String PROPERTY_HTML_ALT = HTML_EXPORTER_PROPERTIES_PREFIX + "alt";

	/*
	 * html element의 설명을 위한 title 속성 설정
	 */
	private final String PROPERTY_HTML_TITLE = HTML_EXPORTER_PROPERTIES_PREFIX + "title";
	
    /*
     * html element의 tag 값 설정
     * select: <option selected="selected">충북</option> <option>충남</option> <option>강원</option>
     * checkbox: 0 또는 1, 기본값이 체크인 경우 1을 설정한다.
     * radio button: 0 또는 1, 기본적으로 선택되는 radio button에만 1을 설정한다.
     */
	private final String PROPERTY_HTML_TAGVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "tagValue";
    
    /*
     * html element의 onclick 등의 이벤트 발생시 호출되는 자바스크립트 설정
     * 
     * onclick="doCheck(this)"
     * 
     * 이때 doCheck(this) 등의 함수는 이미 정의 되어 있어야 한다.
     */
    private final String PROPERTY_HTML_SCRIPT = HTML_EXPORTER_PROPERTIES_PREFIX + "script";
    
    /*
     * 해당 radio button의 값을 설정한다.
     */
    private final String PROPERTY_HTML_RADIOVALUE = HTML_EXPORTER_PROPERTIES_PREFIX + "radiovalue";
    
    private final String PROPERTY_HTML_DESCRIPTION = HTML_EXPORTER_PROPERTIES_PREFIX + "desc";
    
    private final String PROPERTY_HTML_TAG_TYPE = HTML_EXPORTER_PROPERTIES_PREFIX + "type";
    
	/**
	 * CSS Class 설정
	 */
	private final String PROPERTY_HTML_CLASS = HTML_EXPORTER_PROPERTIES_PREFIX + "class";
	
	public final String PROPERTY_HTML_LABEL = HTML_EXPORTER_PROPERTIES_PREFIX + "label";
	
	public final String PROPERTY_HTML_TH = HTML_EXPORTER_PROPERTIES_PREFIX + "th";
	
	public final String PROPERTY_HTML_LAPPEND = HTML_EXPORTER_PROPERTIES_PREFIX + "lappend";
	
	public final String PROPERTY_HTML_RAPPEND = HTML_EXPORTER_PROPERTIES_PREFIX + "rappend";
	
	public final String PROPERTY_HTML_GROUP_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "group_id";
	public final String PROPERTY_HTML_GROUP = HTML_EXPORTER_PROPERTIES_PREFIX + "group";
	
	
	private String customerName = "";
	private String productName = "";
	private String productInfo = "";
	private String reportFile = "";
	
	private List<FormPage> pages = new ArrayList<FormPage>();
	
    private String encoding = "UTF-8";
    private String htmlHeader = "";
    private String htmlFooter = "";
    private String prevImgUrl = "";
    
    private String docStart = "";
    private String docEnd = "";
    private String pageStart = "";
    private String pageEnd = "";
    
    private int radioIndex = 0;
    private Map<String, Object> essParameters = new HashMap<String, Object>();
    
    private List<RXPrintElement> formList = new ArrayList<RXPrintElement>();
    private List<Integer> formListPage = new ArrayList<Integer>();
    
    private HashMap<String, Object>  exptHMap = new HashMap<String, Object>();
    
    private int current_page = 0;  // 현재 페이지 정보
    
    private transient  Writer writer;
    private String jobID = "";
    
    private String useragent = "";
    private String os = "ms";
    private String bw = "ie";
    
    private transient UAgentInfo uinfo;
    
    /*
     * IPAD CHECK
     */
//    private final int offsetHeight = 100;
    
    public FormPages(String useragent, String userAccept){
    	this.useragent = useragent;
    	os = sysinfo.os(this.useragent);
    	bw = sysinfo.bw(this.useragent);
    	uinfo = new UAgentInfo(useragent, userAccept);
    }
    
    public String getJobID() {
		return jobID;
	}



	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public void setPrevImgUrl(String prevImgUrl) {
		this.prevImgUrl = prevImgUrl;
	}

	public void setProductInfo(String productInfo){
		this.productInfo = productInfo;
	}
	
	public void setReportFile(String reportFile){
		this.reportFile = reportFile;
	}

	public void setOutputStream(OutputStream out, String encoding) throws Exception{
    	this.encoding = encoding;
    	writer = new OutputStreamWriter(out, encoding);
    }
    
	public void setHtmlHeader(String htmlHeader) {
		this.htmlHeader = htmlHeader;
	}

	public void setHtmlFooter(String htmlFooter) {
		this.htmlFooter = htmlFooter;
	}

	
	
	public void setDocStart(String docStart) {
		this.docStart = docStart;
	}

	public void setDocEnd(String docEnd) {
		this.docEnd = docEnd;
	}

	public void setPageStart(String pageStart) {
		this.pageStart = pageStart;
	}

	public void setPageEnd(String pageEnd) {
		this.pageEnd = pageEnd;
	}

	/*
     * 사용자 입력에 의해 변경되어야 할 데이터 맵핑 해시맵
     * 이 해시맵에 의해 ReportExpressPrint 개체가 수정된다.
     */
    public Map<String, Object> getEssParams(){
        return essParameters;
    }
    
    public HashMap<String, Object> getExptHMap() {
		return exptHMap;
	}
    
	public void addPage(FormPage page){
		pages.add(page);
	}
	
	public List<FormPage> getPages(){
		return pages;
	}
	
	public FormPage getPage(int index) throws Exception{
		if(index<pages.size()){
			return pages.get(index);
		}else{
			throw new Exception("페이지를 초과하였습니다.");
		}
	}
	
	public int size(){
		return pages.size();
	}
	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	/*
	 * 문서의 스타일을 정의한다.
	 */
	public String getDocStart() {
		StringBuffer doc_start = new StringBuffer();
		doc_start.append("");

		/* 문서 시작 */
		//doc_start.append("<div id=\"docs-editor-container\">");
		doc_start.append("<div id=\"paper\" class=\"paper\" style=\"height:100%;\">");
		doc_start.append("<div id=\"canvas\" style=\"overflow-y: scroll; height:100%;\" class=\"rxe-appview-editor\">");
		doc_start.append("<div id=\"doc\" style=\"left: 5px; top: 5px; width:100%; height:100%;\" class=\"rxe-paginateddocumentplugin\">");
		
		return doc_start.toString();
	}
	
	/*
	 * 문서 스타일 정의의 끝 및 HTML 문서의 끝
	 */
	public String getDocEnd() {
		StringBuffer doc_end = new StringBuffer();

		doc_end.append("");

		/* 문서 끝 */
		//doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");
		doc_end.append("</div>");
		
		return doc_end.toString();
	}
	
	/*
	 * 보고서 페이지의 시작 부분
	 */
	public String getPageStart() {
		StringBuffer page_start = new StringBuffer();
		page_start.append("");
		
		page_start.append("<div class=\"rxe-page rxe-page-paginated\">");

		return page_start.toString();
	}
	
	/*
	 * 보고서 페이지의 끝 부분
	 */
	public String getPageEnd() {
		StringBuffer page_end = new StringBuffer();

		page_end.append("");

		page_end.append("\n</div>");

		return page_end.toString();
	}
	
	private String getElementType(RXPrintText printText){
		String text = printText.getFullText();
		
        /*
         * tag 속성 가져오기
         */
       String tag_type = RXProperties.getProperty(printText, PROPERTY_HTML_TAG_TYPE);
       
        String[] tag = null;
        String type = "";
        if(text.indexOf("|")>0){
            tag = StringUtils.split(text, "|");
            type = tag[0];
        }else{
            type = text;
        }
        
        type = (tag_type!=null && !"".equals(tag_type)) ? tag_type : type;
        
		return type;
	}
	
	private void exportHeader(String title) throws Exception{
		writer.write("<!DOCTYPE html>\n");
		writer.write("<html lang=\"ko\">\n");
		writer.write("<head>\n");
		writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\" />\n");
		writer.write("<meta name=\"reportexpress\" content=\"notranslate\"/>\n");
		writer.write("<meta name=\"viewport\" content=\"user-scalable=yes;\"/>\n");
		writer.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8,IE=9\"/>\n");
		writer.write("<title>" + title + "</title>\n");
		writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"/eformbank/cdoc/eform/essviewer/css/ess.css\"/>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/jquery-1.9.1.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/jquery.blockUI.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/jquery-ui.js\"></script>\n");
		writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"/eformbank/cdoc/eform/essviewer/css/jquery-ui.css\"/>\n");
		writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"/eformbank/cdoc/eform/essviewer/window/dhtmlxwindows.css\"/>\n");
		writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"/eformbank/cdoc/eform/essviewer/window/skins/dhtmlxwindows_dhx_skyblue.css\"/>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/window/dhtmlxcommon.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/window/dhtmlxwindows.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/window/dhtmlxcontainer.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/form.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/map.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/ess.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/report.js\"></script>\n");
		writer.write("<script type=\"text/javascript\" src=\"/eformbank/cdoc/eform/essviewer/js/jquery.scrollIntoView.js\"></script>\n");
		writer.write("</head>\n");
		writer.write("<body style=\"font-family:'" + fontfamily + "'\">\n");
	}
	
	private void exportFooter() throws Exception{
		
		writer.write("</body>\n");
		writer.write("</html>\n");
	}
	
	public void exportForm() throws Exception{
		if("".equals(htmlHeader)){
			exportHeader(productName);
		}else{
			writer.write(htmlHeader);
		}
		if("".equals(docStart)){
			writer.write(getDocStart());
		}else{
			writer.write(docStart);
		}
		if("".equals(productInfo)){
			writer.write("<div id=\"rx_doc\" style=\"display:none;\">");
			writer.write("<br/><center>");
			writer.write("<table cellspacing='0' cellpadding='0' border='0' width='96%'>\n");
			writer.write("<tr>\n");
			writer.write("<td class=\"mtd\" style=\"\"></td>\n");
			writer.write("</tr>\n");
			writer.write("<tr style=\"background:rgb(240,240,240);\">\n");
			writer.write("<td align=\"left\" class=\"td\" style=\"height:24px;\">&nbsp;&nbsp;&nbsp;");
			writer.write("<span style=\"font-size:150%;\">");
			writer.write("<span style=\"color:#0000ff; font-weight:bold;\">");
			writer.write(customerName);
			writer.write("</span> 님의 ");
			writer.write("<span style=\"color:#0000ff; font-weight:bold;\">");
			writer.write(productName + "</span>에 대한 간편입력 정보입니다.</span>");
			writer.write("</td>\n");
			writer.write("</tr>\n");
			writer.write("<tr>\n");
			writer.write("<td class=\"mtd\" style=\"\"></td>\n");
			writer.write("</tr>\n");
			writer.write("</table><br/></center>\n");
		}else{
			writer.write(productInfo);
		}
		
		int page_count = 0;
		
		writer.write("<br/><center>");
		if("".equals(pageStart)){
				writer.write(getPageStart());
		}else{
			writer.write(pageStart);
		}
		
		for(int i=0; i<pages.size(); i++){
			current_page = i;
			List<RXPrintElement> elements = pages.get(i).getElements();
			RadioGroup radiogroup = pages.get(i).getRadioGroup();
			CheckGroup checkboxgroup = pages.get(i).getCheckGroup();

			if(elements.size()>0 || radiogroup.size()>0 || checkboxgroup.size()>0){
				int pg = i+1;
				page_count++;
				
				writer.write("<a name=\"RX_FORM_PAGE_ANCHOR_" + String.valueOf(page_count) + "\"></a>\n");
				
//				writer.write("<br/><center>");
//				if("".equals(pageStart)){
//						writer.write(getPageStart());
//				}else{
//					writer.write(pageStart);
//				}
				writer.write("<br/>");
				writer.write("<table class=\"table\">\n");
				writer.write("<colgroup>\n");
				writer.write("<col width='100%'/>\n");
				writer.write("</colgroup>\n");
				
				writer.write("<tr class=\"tr100\">\n");
				writer.write("<td align=\"left\">");
				writer.write("<span class=\"pageth pageInfo\">");
				writer.write("&nbsp;&nbsp;&nbsp;" + String.valueOf(pg) + " 페이지 입력");
				writer.write("</span>");
				writer.write("</td>\n");
				writer.write("</tr>\n");

				writer.write("<tr class=\"blanktr\">\n");
				writer.write("<td></td>");
				writer.write("</tr>\n");
				
				for(RXPrintElement element : elements){
					if (element instanceof RXPrintText) {
						RXPrintText text = (RXPrintText)element;
						
						String type = getElementType(text);
						
						
						
						if("radio".equalsIgnoreCase(type)){
							String key = RXProperties.getProperty(text, PROPERTY_HTML_NAME);
//							System.out.println("key = " + key);
							if(radiogroup.containsKey(key)){
								writer.write(exportRadio(radiogroup, key));
							}
						}else if("checkbox".equalsIgnoreCase(type)){
							String key = RXProperties.getProperty(text, PROPERTY_HTML_GROUP_ID);
							if ( StringUtils.isNull(key) ) key = RXProperties.getProperty(text, PROPERTY_HTML_NAME);
							//logger.debug("key = " + key+","+checkboxgroup.containsKey(key));
							if(checkboxgroup.containsKey(key)){
								writer.write(exportCheckbox(checkboxgroup, key));
							}
						}else{
							writer.write("<tr class=\"tr100\">\n");
							writer.write("<th align=\"left\" class=\"th-item-title\">");
							writer.write("<span class=\"item-title\">");
							writer.write(getTableLabel(text));
							writer.write("</span></td>");
							writer.write("</tr>\n");
							
							writer.write("<tr class=\"blanktr2\">\n");
							writer.write("<td></td>");
							writer.write("</tr>\n");
							
							writer.write("<tr>\n");
							writer.write("<td align=\"left\" class=\"td\">");
							writer.write(exportText(text));
							writer.write("</td>\n");
							writer.write("</tr>\n");
							
							writer.write("<tr class=\"blanktr\">\n");
							writer.write("<td></td>");
							writer.write("</tr>\n");
						}
					} else if (element instanceof RXPrintImage) {
						RXPrintImage image = (RXPrintImage)element;
						writer.write("<tr class=\"tr100\">\n");
						writer.write("<th align=\"left\" class=\"th-item-title\">");
						writer.write("<span class=\"item-title\">");
						writer.write(getTableLabel(image));
						writer.write("</span></td>");
						writer.write("</tr>\n");
						
						writer.write("<tr class=\"blanktr2\">\n");
						writer.write("<td></td>");
						writer.write("</tr>\n");
						
						writer.write("<tr>\n");
						writer.write("<td align=\"left\" class=\"td\">");
						writer.write(exportImage(image));
						writer.write("</td>");
						writer.write("</tr>\n");
						
						writer.write("<tr class=\"blanktr\">\n");
						writer.write("<td></td>");
						writer.write("</tr>\n");
					}
				}
				
				//writer.write(exportRadio(radiogroup));
				//writer.write(exportCheckbox(checkboxgroup));
				
				writer.write("</table>\n");
				writer.write("<br/>");
//				if("".equals(pageEnd)){
//					writer.write(getPageEnd());
//				}else{
//					writer.write(pageEnd);
//				}
//				writer.write("</center></div>\n");
			}
			
			writer.flush();
		}

		
		if("".equals(pageEnd)){
			writer.write(getPageEnd());
		}else{
			writer.write(pageEnd);
		}
		writer.write("</center></div>\n");
		
		writer.write(getImageDiv());
		
		if("".equals(docEnd)){
			writer.write(getDocEnd());
		}else{
			writer.write(docEnd);
		}

		buildJavascriptHashMap(customerName, productName, formList, formListPage, exptHMap, page_count);
		
		
		if("".equals(htmlFooter)){
			exportFooter();
		}else{
			writer.write(htmlFooter);
		}
		
		writer.flush();
	}

	/**
	 *	미리보기 영역.
	 *
	 * @return
	 */
	protected String getImageDiv()
	{
		StringBuffer sb = new StringBuffer();
	    
		sb.append("<center id=\"rx_img_center\" style=\"display:none;\" >");
		sb.append("<div class=\"rxe-page rxe-page-paginated\">");
		sb.append("<img id=\"rx_image_preview\" style=\"width: 100%;\" ");
		sb.append("src='' alt=\"" + this.customerName + " 님의 " + this.productName + " 상품 정보\" ");
		sb.append("title=\"" + this.customerName + " 님의 " + this.productName + " 상품 정보\">");
		sb.append("</div>");
		sb.append("</center>");
	
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	public void buildJavascriptHashMap(String customer, String product, List<RXPrintElement> formList, 
			List<Integer> formListPage, HashMap<String, Object> Hmconfirm, int max_page) throws Exception{
		Hash md5 = new Hash();
		StringBuffer json = new StringBuffer();
		StringBuffer encData = new StringBuffer();
		json.setLength(0);
		encData.setLength(0);
       	json.append("[");
		writer.write("<script type=\"text/javascript\">");
		writer.write("max_page=" + String.valueOf(max_page) + ";\n");
		writer.write("var desc_map = new Map();");
		writer.write("var value_map = new Map();");
		writer.write("var hwsign_map = new Map();");
		writer.write("var page_map = new Map();");
		writer.write("var confirm_map = new Map();");
		writer.write("var text_map = new Map();");
		
    	json.append("{");
    	json.append("\"id\":\"customer\"");
    	json.append(",\"value_map\":\""+customer+"\"");
    	json.append("}");
    	json.append(",{");
    	json.append("\"id\":\"product\"");
    	json.append(",\"value_map\":\""+product+"\"");
    	json.append("}");
    	json.append(",{");
    	json.append("\"id\":\"reportFile\"");
    	json.append(",\"value_map\":\""+reportFile+"\"");
    	json.append("}");

		for(int i=0; i<formList.size(); i++){
			RXPrintElement element = formList.get(i);
			//Integer current_page = formListPage.get(i);
			
			String text = "";
			
			if (element instanceof RXPrintText) {
				RXPrintText ele = (RXPrintText)element;
				text = ele.getFullText();
			}
			
			String id = RXProperties.getProperty(element, PROPERTY_HTML_ID);
			String tag_type = RXProperties.getProperty(element, PROPERTY_HTML_TAG_TYPE);
			String tagValue = RXProperties.getProperty(element, PROPERTY_HTML_TAGVALUE);
	        String tag_desc = RXProperties.getProperty(element, PROPERTY_HTML_DESCRIPTION);
	        
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

            	json.append(",{");
            	json.append("\"id\":\""+id+"\"");

            	/*
	             * 일반 테스트인 경우
	             */
	            if("text".equalsIgnoreCase(tag_type)){
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
	            		String ss = new String(com.cabsoft.utils.Base64Util.encode(text.getBytes("utf-8"), false));
	            		ss = StringUtils.replaceAll(ss,  "\n", "");
		            	json.append(",\"confirm_map\":\""+md5.encryptText(ss)+"\"");
		            	json.append(",\"text_map\":\""+ss+"\"");
						// 확인 정보 세션에 넣음
						Hmconfirm.put(id, text);
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
            	json.append(",\"page_map\":\""+(current_page * 10000 + element.getY()*2)+"\"");
               	json.append("}\n");
	        } 
		}
       	json.append("]");
		Hmconfirm.put("jsondata", json.toString());

		writer.write("endData=\""+Crypt.eFormEncrypt(encData.toString(), "utf-8")+"\";\n");
		writer.write("</script>\n");
	}

	/*
	private String exportRadio(RadioGroup radiogroup) {
		StringBuilder sb = new StringBuilder();
		
		Set<String> keyset = radiogroup.keySet();
		Iterator<String> it = keyset.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String gname = radiogroup.getLabel(key);
			List<RadioButton> radio = radiogroup.getRadioButtonGroup(key);
			
			sb.append("<tr class=\"tr100\">\n");
			sb.append("<th align=\"left\"> class=\"th-item-title\">");
			sb.append("<span class=\"item-title\">");
			sb.append(gname);
			sb.append("</span>");
			sb.append("</th>\n");
			sb.append("</tr>\n");
			
			sb.append("<tr class=\"blanktr\">\n");
			sb.append("<td></td>");
			sb.append("</tr>\n");
			
			sb.append("<tr>\n");
			sb.append("<td align=\"left\" class=\"td\">");
			sb.append("<div title='[").append(gname).append("](을)를 선택하십시오.'>");
			for(int j=0; j<radio.size(); j++){
				RXPrintText text = radio.get(j).getRadio();
				sb.append("<span class=\"item-title-noborder\">");
				sb.append(exportText(text));
				sb.append("</span>");
				if(radio.size()>1){
					sb.append("<div class=\"blankdiv\">&nbsp;</div>");
				}
			}
			sb.append("</div></td>\n");
			
			sb.append("</tr>\n");
			sb.append("<tr class=\"blanktr2\">\n");
			sb.append("<td></td>");
			sb.append("</tr>\n");
		}
		
		return sb.toString();
	}
	*/

	private String exportRadio(RadioGroup radiogroup, String key) {
		StringBuilder sb = new StringBuilder();
		
		String gname = radiogroup.getLabel(key);
		List<RadioButton> radio = radiogroup.getRadioButtonGroup(key);

		sb.append("<tr class=\"tr100\">\n");
		sb.append("<th align=\"left\" class=\"th-item-title\">");
		sb.append("<span class=\"item-title\">");
		sb.append(gname);
		sb.append("</span>");
		sb.append("</th>\n");
		sb.append("</tr>\n");

		sb.append("<tr class=\"blanktr\">\n");
		sb.append("<td></td>");
		sb.append("</tr>\n");

		sb.append("<tr>\n");
		sb.append("<td align=\"left\" class=\"td\">");
		sb.append("<div title='[").append(gname).append("](을)를 선택하십시오.'>");
		for (int j = 0; j < radio.size(); j++) {
			RXPrintText text = radio.get(j).getRadio();
			sb.append("<span class=\"item-title-noborder\">");
			sb.append(exportText(text));
			sb.append("</span>");
			if (radio.size() > 1) {
				sb.append("<div class=\"blankdiv\">&nbsp;</div>");
			}
		}
		sb.append("</div></td>\n");

		sb.append("</tr>\n");
		sb.append("<tr class=\"blanktr2\">\n");
		sb.append("<td></td>");
		sb.append("</tr>\n");
		
		radiogroup.remove(key);
		
		return sb.toString();
	}

	/*
	private String exportCheckbox(CheckGroup checkgroup){
		StringBuilder sb = new StringBuilder();
		
		Set<String> keyset = checkgroup.keySet();
		Iterator<String> it = keyset.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String gname = checkgroup.getLabel(key);
			List<CheckBox> checkbox = checkgroup.getCheckboxGroup(key);
			
			sb.append("<tr class=\"tr100\">\n");
			sb.append("<th align=\"left\" class=\"th-item-title\">");
			sb.append("<span class=\"item-title\">");
			sb.append(gname);
			sb.append("</span>");
			sb.append("</th>\n");
			sb.append("</tr>\n");
			
			sb.append("<tr>\n");
			sb.append("<td align=\"left\" class=\"td\">");
			sb.append("<div title='[").append(gname).append("](을)를 선택하십시오.'>");
			for(int j=0; j<checkbox.size(); j++){
				RXPrintText text = checkbox.get(j).getCheckbox();
				sb.append("<span class=\"item-title-noborder\">");
				sb.append(exportText(text));
				sb.append("</span>");
				if(checkbox.size()>1){
					sb.append("<div class=\"blankdiv\">&nbsp;</div>");
				}
			}
			sb.append("</div></td>\n");
			
			sb.append("</tr>\n");
		}
		
		return sb.toString();
	}
	*/
	
	private String exportCheckbox(CheckGroup checkgroup, String key){
		StringBuilder sb = new StringBuilder();
		
		String gname = checkgroup.getLabel(key);
		List<CheckBox> checkbox = checkgroup.getCheckboxGroup(key);

		sb.append("<tr class=\"tr100\">\n");
		sb.append("<th align=\"left\" class=\"th-item-title\">");
		sb.append("<span class=\"item-title\">");
		sb.append(gname);
		sb.append("</span>");
		sb.append("</th>\n");
		sb.append("</tr>\n");

		sb.append("<tr class=\"blanktr\">\n");
		sb.append("<td></td>");
		sb.append("</tr>\n");

		sb.append("<tr>\n");
		sb.append("<td align=\"left\" class=\"td\">");
		sb.append("<div title='[").append(gname).append("](을)를 선택하십시오.'>");
		for (int j = 0; j < checkbox.size(); j++) {
			RXPrintText text = checkbox.get(j).getCheckbox();
			sb.append("<span class=\"item-title-noborder\">");
			sb.append(exportText(text));
			sb.append("</span>");
			if (checkbox.size() > 1) {
				sb.append("<div class=\"blankdiv\">&nbsp;</div>");
			}
		}
		sb.append("</div></td>\n");

		sb.append("</tr>\n");

		sb.append("<tr class=\"blanktr2\">\n");
		sb.append("<td></td>");
		sb.append("</tr>\n");

		
		checkgroup.remove(key);
		
		return sb.toString();
	}
	
//	private String getLabel(RXPrintText printText){
//		String label = RXProperties.getProperty(printText, PROPERTY_HTML_LABEL);
//		if(label==null || "".equals(label)){
//			return RXProperties.getProperty(printText, PROPERTY_HTML_DESCRIPTION);
//		}else{
//			return label;
//		}
//	}
	
	private String getTableLabel(RXPrintElement element){
		String th = RXProperties.getProperty(element, PROPERTY_HTML_TH);
		if(th==null || "".equals(th)){
			th =  RXProperties.getProperty(element, PROPERTY_HTML_DESCRIPTION);
			th = th==null ? "" : th;
		}
		return th;
	}
	
	private String getLeftAppendString(RXPrintText printText){
		String lappend = RXProperties.getProperty(printText, PROPERTY_HTML_LAPPEND);
		lappend = lappend==null ? "" : lappend;
		return lappend;
	}
	
	private String getRightAppendString(RXPrintText printText){
		String rappend = RXProperties.getProperty(printText, PROPERTY_HTML_RAPPEND);
		rappend = rappend==null ? "" : rappend;
		return rappend;
	}
	
	private String exportImage(RXPrintImage image){
		StringBuilder sb = new StringBuilder();
		
		formList.add(image);
		
		String id = RXProperties.getProperty(image, PROPERTY_HTML_ID);
		String alt = RXProperties.getProperty(image, PROPERTY_HTML_ALT);
		String title = RXProperties.getProperty(image, PROPERTY_HTML_TITLE);
		String script = RXProperties.getProperty(image, PROPERTY_HTML_SCRIPT);
		
		alt = alt == null ? "" : alt;
		
		if (title == null || title.equalsIgnoreCase("")) {
			title = RXStringUtil.xmlEncode(image.getHyperlinkTooltip());
		}
		title = title == null ? "" : title;
		
		script = script==null ? "" : script;
		
		String imgKey = image.getKey();

		if(imgKey==null || "".equals(imgKey)){
			imgKey = "hwsigne.png";
		}
		
		int h = image.getHeight();
//		int w = image.getWidth();
		h = h>36 ? h : 36;
		
		sb.append("<span ").append(script).append(" class=\"hwsign\">");
		sb.append("<img id=\"").append(id).append("\" style=\"width:").append(String.valueOf(h*3.5)).append("px; height:").append(String.valueOf(h))
			.append("px; top:2px; position:relative;\" src=\"").append("/eformbank/cdoc/eform/images/report/").append(imgKey).append("\"/>");
		sb.append("</span>");
		
		return sb.toString();
	}
	
    private String exportText(RXPrintText printText) {
    	StringBuilder sb = new StringBuilder();
    	
        String id = RXProperties.getProperty(printText, PROPERTY_HTML_ID);
        String name = RXProperties.getProperty(printText, PROPERTY_HTML_NAME);
        name = (name==null || name == "") ? id : name;
        
    	String text = printText.getFullText();

        /* 테스트가 null이거나 공백인 경우 HTML에서 &nbsp로 바꾸기 위함 */
        if (text == null || text.equalsIgnoreCase("")) {
            printText.setText(" ");
        }
        
        String lappend = getLeftAppendString(printText);
        String rappend = getRightAppendString(printText);

        /*
         * alt 속성 가져오기
         */
        String alt = RXProperties.getProperty(printText, PROPERTY_HTML_ALT);

        /*
         * title 속성 가져오기
         */
        String title = RXProperties.getProperty(printText, PROPERTY_HTML_TITLE);
        
        /*
         * CSS Class 속성 가져오기
         */
        String css_class = RXProperties.getProperty(printText, PROPERTY_HTML_CLASS);

        /*
         * tag 속성 가져오기
         */
       String tag_type = RXProperties.getProperty(printText, PROPERTY_HTML_TAG_TYPE);
       
       String tagValue = RXProperties.getProperty(printText, PROPERTY_HTML_TAGVALUE);
       String script = RXProperties.getProperty(printText, PROPERTY_HTML_SCRIPT);
       String radiovalue = RXProperties.getProperty(printText, PROPERTY_HTML_RADIOVALUE);
//       String tag_desc = RXProperties.getProperty(printText, PROPERTY_HTML_DESCRIPTION);
       
       /*
        * 스크립트
        */
       script = (script==null) ? "" : script.trim();
       
       /*
        * 태그 값
        */
       tagValue = (tagValue==null) ? "" : tagValue;
       
       /*
        * radio button 값
        */
       radiovalue = (radiovalue==null) ? "" : radiovalue;
       
		/*
		 * alt 값
		 */
		alt = (alt == null) ? "" : alt;

		/*
		 * title 값
		 */
		title = (title == null) ? "" : title;
		
		/*
		 * CSS Class 값
		 */
		css_class = (css_class == null) ? "" : css_class;
		
        String[] tag = null;
        String type = "";
        String value = "";
        if(text.indexOf("|")>0){
            tag = StringUtils.split(text, "|");
            type = tag[0];
            value = tag[1];
        }else{
            type = text;
            value = "";
        }
        
        type = (tag_type!=null && !"".equals(tag_type)) ? tag_type : type;
        // "button"으로 표시하지 않을 id 값 입력
        if (id != null) {
        	if(id.startsWith("scrollpoint")){
	        	type = "";
        	}else{
	        	essParameters.put(id, null);
	        	formList.add(printText);
	        	formListPage.add(current_page);
        	}
         }
        
		String style = "";
		
		//폰트 이름
//		style += "font-family: '" + printText.getFontName() + "'; ";
//		style += "font-family: '" + fontfamily + "'; ";

//		Color forecolor = printText.getForecolor();
//		if (!Color.black.equals(forecolor)) {
//			if("textconfirm".equalsIgnoreCase(type) && !id.endsWith("_input") ){ 
//				// 칼러 표시 (class 명 : textconfirmfiled)
//				style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
//			} else if(!StringUtils.isNull(type)) {
//				//	css에서 color 수정
//				style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
//			} else {
//				style += "color: #" + RXColorUtil.getColorHexa(forecolor) + "; ";
//			} 
//		}

//		if ("textconfirm".equalsIgnoreCase(type) && id.endsWith("_input") ) {
//			text = "";
//			style += "background-color: transparent; color:#ff0000; ";
//		} else if ("textconfirm".equalsIgnoreCase(type) ){
////			// css에서 backcolor 수정
//			Color runBackcolor = printText.getBackcolor();
//			if (runBackcolor != null) {
//				style += "background-color: #" + RXColorUtil.getColorHexa(runBackcolor) + "; ";
//			}
//		} else {
//			Color runBackcolor = printText.getBackcolor();
//			if (runBackcolor != null) {
//				style += "background-color: #" + RXColorUtil.getColorHexa(runBackcolor) + "; ";
//			}
//		}

		/*
		 * alt와 title 속성 설정
		 */
		String desc = "";
		if (!title.equalsIgnoreCase("")) {
			desc = desc + "title=\"" + title + "\" ";
		}else if (!alt.equalsIgnoreCase("")) {
			desc = desc + "title=\"" + alt + "\" ";
		}
		
        /*
         * 일반 테스트인 경우
         */
        if("text".equalsIgnoreCase(type) ){
        	style += " width:100%;";
        	
        	//sb.append("<span " + desc + " style=\"width:100%; padding:5px 5px 5px 5px;\">");

            if(StringUtils.isNull(css_class)){
            	sb.append("<input class=\"textfiled\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script);
            }else{
            	sb.append("<input class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\"");
            }
            sb.append(" value=\"" + value + "\"/>");
            //sb.append("</span>");
        }
        /*
         * 확인 텍스트인 경우
         * 
         */
        else if("textconfirm".equalsIgnoreCase(type)){
        	sb.append("<span " + desc + " style=\"" + style + "\">");

        	style += " width:100%; ";
        	
            if(StringUtils.isNull(css_class)){
            		if (id.endsWith("_input")) {
            			sb.append("<input class=\"textfiled\" type=\"text\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\"");
            		} else {
            			/**
            			 * 따라 쓰기 기능
            			 */
            			sb.append("<div class=\"div_textconfirmfiled\">\n");
            			sb.append("\n<div class=\"div1_textconfirmfiled\">");
            			sb.append("<span>");
            			sb.append("<input class=\"textconfirmfiled\" type=\"text\" name=\"" + id + "\"id=\"" + id + "\"" + script + " style=\"")
            				.append(style).append(" disabled='true' readonly=\"readonly\"");
            			sb.append(" value=\"").append(text).append("\"/>");
            			sb.append("</span>");
            			sb.append("</div>");
         			
            			if("galexynote 10.1".equals(os)){
            				if("chrome".equals(bw)){
            					sb.append("<div class=\"div2_textconfirmfiled\">");
            				}else{
            					sb.append("<div class=\"divm_textconfirmfiled\">");
            				}
            			}else{
            				if(uinfo.detectIos()){
            					sb.append("<div class=\"div3_textconfirmfiled\">");
            				}else{
            					sb.append("<div class=\"div2_textconfirmfiled\">");
            				}
            			}
            			sb.append("<span>");
            			sb.append("<input class=\"textconfirmfiled\" type=\"text\" name=\"" + name + "_input\" id=\"" + id + "_input\" " + script + 
            					" style=\"").append(style)
            					.append("\"");
            			sb.append(" value=\"\"/>");
            			sb.append("</span>");
            			sb.append("</div>\n");
            			sb.append("</div>\n");
            		}
            }else{
            	if (id.endsWith("_input")) {
            		sb.append("<input class=\"text" + css_class  + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\"");
            	} else {
            		sb.append("<input class=\"confirm" + css_class  + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\" disabled='true'");
            	}
            	sb.append(" value=\"" + value + "\"/>");
            }
            sb.append("</span>");
        }
        /*
         * 버튼인 경우
         */
        else if( "buttonconfirm".equalsIgnoreCase(type)){
        	sb.append("<span>");
        	String label = RXProperties.getProperty(printText, PROPERTY_HTML_LABEL);
        	label = label==null ? "" : label;
        	label = "&nbsp;&nbsp;" + label;

            if(StringUtils.isNull(css_class)){
            	sb.append("<span class=\"buttonconfirm\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + "");
            }else{
            	sb.append("<span class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + "");
            }
            text = ("".equals(lappend) ? text : lappend + "&nbsp;&nbsp;" + text);
            text = ("".equals(rappend) ? text : text + "&nbsp;&nbsp;" + rappend);
            if("".equals(label)){
            	sb.append(">" + text.replaceAll("\n", "<br/>") + "</span>");
            }else{
            	sb.append(">" + label.replaceAll("\n", "<br/>") + "</span>");
            }
            sb.append("</span>");
        }
       /*
         * 버튼인 경우
         */
        else if("button".equalsIgnoreCase(type)){
        	sb.append("<span " + desc + " style=\"" + style + "\">");
        	
        	style += " width:100%; ";
        	
            if(StringUtils.isNull(css_class)){
            	sb.append("<input class=\"button\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\"");
            }else{
            	sb.append("<input class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " style=\"" + style + "\"");
            }
            sb.append(" value=\"" + value + "\"/>");
            sb.append("</span>");
        }
        /*
         * radio button 인 경우
         */
        else if("radio".equalsIgnoreCase(type) || "checkbox".equalsIgnoreCase(type)){
        	String checked = "";
        	
            /*
             * checkbox나 radio button의 경우 선택된 요소는 checked="checked"로 설정해야
             * 웹 표준에 맞음
             */
            if(tagValue.equalsIgnoreCase("1")){
                checked = "checked=\"checked\" ";
            }
            
            if("radio".equalsIgnoreCase(type) ){
            	String label = RXProperties.getProperty(printText, PROPERTY_HTML_LABEL);
            	label = label==null ? "" : label;
            	label = "&nbsp;" + label;
            	String radioID = id;
            	if ( StringUtils.isNull(id)) {
                	/*
                	 * radio button의 경우 id가 중복되면 안되기 때문에 id를 증가시키면서 추가하고
                	 * name은 같은 그룹 내에서는 같은 이름으로 설정한다.
                	 */
                	radioIndex++;
                	radioID = "radio" + String.valueOf(radioIndex);
            	}

            	if(StringUtils.isNull(css_class)){
            		sb.append("<input type=\"" + type + "\" name=\"" + name + "\" id=\"" + radioID + "\" " + script + " value=\"" + radiovalue + "\" " + checked + "/>");
            	}else{
            		sb.append("<input class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + radioID + "\" " + script + " value=\"" + radiovalue + "\" " + checked + "/>");
            	}
            	if(!"".equals(label)){
            		sb.append("<label id=\"" + radioID + "_label\" for='").append(radioID).append("'>").append(label).append("</label>");
            	}
            }else{
            	String label = RXProperties.getProperty(printText, PROPERTY_HTML_LABEL);
            	label = label==null ? "" : label;
            	label = "&nbsp;" + label;
            	if(StringUtils.isNull(css_class)){
            		sb.append("<input type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " value=\"" + tagValue + "\" " + checked + "/>");
            	}else{
            		sb.append("<input class=\"" + css_class + "\" type=\"" + type + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + " value=\"" + tagValue + "\" " + checked + "/>");
            	}
            	if(!"".equals(label)){
            		sb.append("<label id=\"" + id + "_label\" for='").append(id).append("'>").append(label).append("</label>");
            	}
            }
            if(value!=null && !value.equalsIgnoreCase("")){
            	//writer.write(RXStringUtil.htmlEncode(value));
            }
            
//            writer.write("</span>");
        }
        /*
         * select 태그인 경우
         */
        else if("select".equalsIgnoreCase(type)){
        	//sb.append("<span " + desc + " style=\"" + style + "\">");

        	//style += " width:100%; ";

            if(StringUtils.isNull(css_class)){
            	sb.append("<select class=\"styled-select\" name=\"" + name + "\" id=\"" + id + "\" " + script + ">");
            }else{
            	sb.append("<select class=\"" + css_class + "\" name=\"" + name + "\" id=\"" + id + "\" " + script + ">");
            }
            sb.append(tagValue);
            sb.append("</select>");
            if(value!=null && !value.equalsIgnoreCase("")){
            	sb.append(RXStringUtil.htmlEncode(value));
            }
            //sb.append("</span>");
        }
        /*
         * 기타
         */
        else{
        	String label = RXProperties.getProperty(printText, PROPERTY_HTML_LABEL);
        	label = label==null ? "" : label;
        	/*
        	 * 스크립트가 있는 경우
        	 */
			if (script != null && !"".equals(script)) {
				if (css_class == null || "".equals(css_class)) {
					sb.append("<span class=\"text-button\" id=\"" + id + "\" " + desc	+ "\" " + script + ">");
				} else {
					sb.append("<span class=\"" + css_class + "\" id=\"" + id + "\" " + desc + script + ">");
				}
			} else {
				sb.append("<span id=\"" + id + "\" " + desc + " class=\"text-button\">");
			}
			
			String s = "";
			if (text == null || "".equalsIgnoreCase(text)) {
				s = RXStringUtil.htmlEncode(" ");
			} else {
				s = RXStringUtil.htmlEncode(text);
			}
			if(!"".equals(label)){
				s = label;
			}
			sb.append(fixNbsp(s));
			sb.append("</span>");

        }
        
        return sb.toString();
    }
    
    private String fixNbsp(String text){
        char c = 160;
        String ss = Character.toString(c);
        String s = text;
        if(text!=null){
            if(text.indexOf(ss)>=0){
                try{
                    s = StringUtils.replaceAll(text, ss, "&nbsp;");
                }catch(Exception e){
                    s = text;
                }
            }
        }
        return s;
    }
}
