package com.cabsoft.ess.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;

@SuppressWarnings("deprecation")
public class RadioGroup implements Serializable {

	private static final long serialVersionUID = 1106295853590632130L;

	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_NAME = HTML_EXPORTER_PROPERTIES_PREFIX + "name";
	
	private HashMap<String, List<RadioButton>> groups = new HashMap<String, List<RadioButton>>();
	private HashMap<String, String> label = new HashMap<String, String>();
	
	public List<RadioButton> getRadioButtonGroup(String key) {
		if(groups.containsKey(key)){
			return groups.get(key);
		}else{
			List<RadioButton> tradoo =  new ArrayList<RadioButton>();
			groups.put(key, tradoo);
			return tradoo;
		}
	}
	
	public void addRadioButton(RXPrintText text){
		RadioButton radio = new RadioButton();
		radio.setRadio(text);
		String key = RXProperties.getProperty(text, PROPERTY_HTML_NAME);
		if(groups.containsKey(key)){
			groups.get(key).add(radio);
		}else{
			this.getRadioButtonGroup(key).add(radio);
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
	
	public void removeAll(){
		label.clear();
		groups.clear();
	}
	
	public boolean containsKey(String key){
		return label.containsKey(key) && groups.containsKey(key);
	}
}
