package com.cabsoft.ess.form;

import java.io.Serializable;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;

@SuppressWarnings("deprecation")
public class CheckBox implements Serializable {

	private static final long serialVersionUID = 8705968217000245829L;
	
	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_LABEL = HTML_EXPORTER_PROPERTIES_PREFIX + "label";
	
	private RXPrintText checkbox = null;
	private String label = "";

	public RXPrintText getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(RXPrintText checkbox) {
		this.checkbox = checkbox;
		label = RXProperties.getProperty(checkbox, PROPERTY_HTML_LABEL);
		label = label==null ? "" : label;
	}

	public String getLabel() {
		return label;
	}
}
