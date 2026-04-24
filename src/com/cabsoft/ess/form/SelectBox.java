package com.cabsoft.ess.form;

import java.io.Serializable;

import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.util.RXProperties;

@SuppressWarnings("deprecation")
public class SelectBox implements Serializable {

	private static final long serialVersionUID = 3396262592257476065L;

	private final String HTML_EXPORTER_PROPERTIES_PREFIX = RXProperties.PROPERTY_PREFIX + "export.html.";
	public final String PROPERTY_HTML_LABEL = HTML_EXPORTER_PROPERTIES_PREFIX + "label";
	
	private RXPrintText selectbox = null;
	private String label = "";
	
	public RXPrintText getSelectbox() {
		return selectbox;
	}
	
	public void setSelectbox(RXPrintText selectbox) {
		this.selectbox = selectbox;
		label = RXProperties.getProperty(selectbox, PROPERTY_HTML_LABEL);
		label = label==null ? "" : label;
	}
	
	public String getLabel() {
		return label;
	}
	
	
	
}
