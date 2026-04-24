package com.cabsoft.pdf.form;

/**
 * 동일 그룹의 라디오 버튼을 위해 해시맵에 그룹 ID로 라디오 버튼을 추가하기 위한 클래스
 */
import java.util.HashMap;

public class RadioButtonFactory {
	private HashMap<String, RadioButtons> factory = null;
	
	public RadioButtonFactory(){
		factory = new HashMap<String, RadioButtons>();
	}
	
	public RadioButtons getRadioButtons(String group){
		RadioButtons buttons = null;
		if(factory.isEmpty() || factory.containsKey(group)==false){
			buttons = new RadioButtons();
			factory.put(group, buttons);
		}else{
			buttons = factory.get(group);
		}
		return buttons;
	}
	
	public void addRadioButtons(String group, RadioButtons buttons){
		if(factory.containsKey(group)){
			factory.remove(group);
		}
		factory.put(group, buttons);
	}
	
	public HashMap<String, RadioButtons> getButtonFactory(){
		return factory;
	}
	
	public void FactoryClear(){
		if(factory!=null && factory.isEmpty()!=false){
			factory.clear();
		}
		factory = null;
	}
}
