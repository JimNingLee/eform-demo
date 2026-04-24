package com.cabsoft.ess.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.RXPrintElement;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.utils.StringUtils;

@SuppressWarnings("deprecation")
public class FormPage implements Serializable {
	private static final long serialVersionUID = -2707697113974829932L;
	private Log log =LogFactory.getLog(this.getClass().getName());
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	public final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
	private final String PROPERTY_HTML_TAG_TYPE = HTML_EXPORTER_PROPERTIES_PREFIX + "type";
	public final String PROPERTY_HTML_GROUP_LABEL = HTML_EXPORTER_PROPERTIES_PREFIX + "group_label";
	public final String PROPERTY_HTML_GROUP_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "group_id";
	
	private List<RXPrintElement> elements = new ArrayList<RXPrintElement>();
	private RadioGroup radiogroup = new RadioGroup();
	private CheckGroup checkgroup = new CheckGroup();
	
	private int elementsCount = 0;
	
	public void addElement(RXPrintElement element){
		if (element instanceof RXPrintText) {
			RXPrintText printText = (RXPrintText)element;
			
			String text = printText.getFullText();
			
	        /*
	         * id 속성 가져오기
	         */
	       String id = RXProperties.getProperty(printText, PROPERTY_HTML_ID);
	       
	       if (id != null && !id.startsWith("scrollpoint")) {
	    	   elementsCount++;
		        /*
		         * tag 속성 가져오기
		         */
		       String tag_type = RXProperties.getProperty(printText, PROPERTY_HTML_TAG_TYPE);
		       String disable = RXProperties.getProperty(printText, "com.cabsoft.rx.export.html.disable");
		       
		       if(!"1".equals(disable)) {
			        String[] tag = null;
			        String type = "";
			        if(text.indexOf("|")>0){
			            tag = StringUtils.split(text, "|");
			            type = tag[0];
			        }else{
			            type = text;
			        }
			        
			        type = (tag_type!=null && !"".equals(tag_type)) ? tag_type : type;
			        if("radio".equalsIgnoreCase(type)){
			        	String key = RXProperties.getProperty(printText, PROPERTY_HTML_NAME);
			        	String group_label = RXProperties.getProperty(printText, PROPERTY_HTML_GROUP_LABEL);
			        	radiogroup.addRadioButton(printText);
			        	radiogroup.setLabel(key, group_label);
			        	
			        	elements.add(printText);
			        }else if("checkbox".equalsIgnoreCase(type)){
			        	String key = RXProperties.getProperty(printText, PROPERTY_HTML_GROUP_ID);
			        	if ( StringUtils.isNull(key) ) key = RXProperties.getProperty(printText, PROPERTY_HTML_NAME);
			        	String group_label = RXProperties.getProperty(printText, PROPERTY_HTML_GROUP_LABEL);
			        	
			        	checkgroup.addCheckboxGroup(printText);
			        	checkgroup.setLabel(key, group_label);
			        	
			        	elements.add(printText);
		//	        }else if("select".equalsIgnoreCase(type)){
			        	
			        }else{
			        	elements.add(printText);
			        }
		       }
	       }
		} else if (element instanceof RXPrintImage) {
			RXPrintImage printImage = (RXPrintImage)element;
			
	        /*
	         * id 속성 가져오기
	         */
	       String id = RXProperties.getProperty(printImage, PROPERTY_HTML_ID);
	       
	       if (id != null && !id.startsWith("scrollpoint")) {
	    	   elementsCount++;
	    	   elements.add(printImage);
	       }
		}
	}
	
	public List<RXPrintElement> getElements(){
		return elements;
	}
	
	public RXPrintElement getElement(int index) throws Exception{
		if(index<elements.size()){
			return elements.get(index);
		}else{
			throw new Exception("더이상 구성 요소가 없습니다.");
		}
	}
	
	public RadioGroup getRadioGroup(){
		return radiogroup;
	}
	
	public CheckGroup getCheckGroup(){
		return checkgroup;
	}
	
	public int size(){
		return elements.size();
	}

	public int getElementsCount() {
		return elementsCount;
	}
	
}
