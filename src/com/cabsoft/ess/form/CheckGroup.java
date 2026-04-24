package com.cabsoft.ess.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;
import com.cabsoft.utils.StringUtils;

@SuppressWarnings("deprecation")
public class CheckGroup implements Serializable {

	private static final long serialVersionUID = -7833733319191651968L;
	private Log log =LogFactory.getLog(this.getClass().getName());
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_GROUP_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "group_id";
	public final String PROPERTY_HTML_GROUP = HTML_EXPORTER_PROPERTIES_PREFIX + "group";

	private final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";
	private final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
	
	private HashMap<String, List<CheckBox>> groups = new HashMap<String, List<CheckBox>>();
	private HashMap<String, String> label = new HashMap<String, String>();
	
	public List<CheckBox> getCheckboxGroup(String key) {
		if(groups.containsKey(key)){
			return groups.get(key);
		}else{
			List<CheckBox> tcheckbox =  new ArrayList<CheckBox>();
			groups.put(key, tcheckbox);
			return tcheckbox;
		}
	}
	
	public void addCheckboxGroup(RXPrintText text){
		CheckBox checkbox = new CheckBox();
		checkbox.setCheckbox(text);
		String key = RXProperties.getProperty(text, PROPERTY_HTML_GROUP_ID);
		if ( StringUtils.isNull(key) ) key = RXProperties.getProperty(text, PROPERTY_HTML_NAME);
		log.debug("addCheckboxGroup key = " + key+","+groups.containsKey(key));
		if(groups.containsKey(key)){
			groups.get(key).add(checkbox);
		}else{
			this.getCheckboxGroup(key).add(checkbox);
		}
	}
	
	public String getLabel(String key) {
		if(label.containsKey(key)){
			return label.get(key);
		}else{
			return "";
		}
	}

	public void setLabel(String key, String value) {
		if(!label.containsKey(key)){
			label.put(key, value);
		}
	}

	public int size(){
		return groups.keySet().size();
	}
	
	public Set<String> keySet() {
		return groups.keySet();
	}
	
	public void remove(String key){
		if(label.containsKey(key)){
			label.remove(key);
		}
		
		if(groups.containsKey(key)){
			groups.remove(key);
		}
	}
	
	public void remoiveAll(){
		label.clear();
		groups.clear();
	}
	
	public boolean containsKey(String key){
		return label.containsKey(key) && groups.containsKey(key);
	}
	
	
}
