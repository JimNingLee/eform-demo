package com.cabsoft.pdf.form;

/**
 * 라디오 버튼 생성을 위한 클래스
 */
import com.cabsoft.rx.engine.RXPrintText;

public class RadioButton {
	private RXPrintText text = null;
	private String onValue = "";
	private boolean checked = false;
	private int frameTop = 0;
	private int frameLeft = 0;
	
	/**
	 * 
	 * @param text RXPrintText 클래스
	 * @param onValue 선택된 경우 리턴되는 값
	 * @param checked 선택된 버튼인지 여주
	 */
	public RadioButton(RXPrintText text, String onValue, boolean checked, int frameLeft, int frameTop){
		this.text = text;
		this.onValue = onValue;
		this.checked = checked;
		this.frameLeft = frameLeft;
		this.frameTop = frameTop;
	}

	/**
	 * 
	 * @return RXPrintText
	 */
	public RXPrintText getRXPrintText() {
		return text;
	}

	/**
	 * 
	 * @return
	 */
	public String getOnValue() {
		return onValue;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return checked;
	}

	public int getFrameTop() {
		return frameTop;
	}

	public int getFrameLeft() {
		return frameLeft;
	}
}
