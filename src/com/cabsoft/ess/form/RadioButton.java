package com.cabsoft.ess.form;

import java.io.Serializable;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;

@SuppressWarnings("deprecation")
public class RadioButton implements Serializable {

	private static final long serialVersionUID = 3064524578234865839L;
	
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_LABEL = HTML_EXPORTER_PROPERTIES_PREFIX + "label";
	
	private RXPrintText radio = null;
	private String label = "";
	
	public RXPrintText getRadio() {
		return radio;
	}
	
	public void setRadio(RXPrintText radio) {
		this.radio = radio;
		label = RXProperties.getProperty(radio, PROPERTY_HTML_LABEL);
		label = label==null ? "" : label;
	}
	public String getLabel() {
		return label;
	}
	
	

}
