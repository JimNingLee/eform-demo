package com.cabsoft.pdf.form;

/**
 * 동일 그룹의 라디오 버튼을 리스트에 담기 위한 클래스
 */
import java.util.ArrayList;

import com.cabsoft.rx.engine.RXPrintText;

public class RadioButtons {
	private ArrayList<RadioButton> list = null;
	
	public RadioButtons(){
		list = new ArrayList<RadioButton>(); 
	}
	
	public void addRadioButton(RadioButton button){
		if(list==null){
			list = new ArrayList<RadioButton>(); 
		}
		list.add(button);
	}
	
	public void addRadioButton(RXPrintText text, String onValue, boolean checked, int frameLeft, int frameTop){
		if(list==null){
			list = new ArrayList<RadioButton>(); 
		}
		RadioButton button = new RadioButton(text, onValue, checked, frameLeft, frameTop);
		list.add(button);
	}
	
	public ArrayList<RadioButton> getRadioButtonList(){
		if(list==null){
			list = new ArrayList<RadioButton>(); 
		}
		return list;
	}
}
