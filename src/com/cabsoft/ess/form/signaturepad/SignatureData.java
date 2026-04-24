package com.cabsoft.ess.form.signaturepad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignatureData implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3174143626413611124L;
	private String name = "";
    private String product = "";
    private String desc = "";
    private String color = "";
    private boolean variableStrokeWidth = true;
    float penWidth;
    float padWidth;
    float padHeight;
    
    private SignatureLine[] data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isVariableStrokeWidth() {
        return variableStrokeWidth;
    }

    public void setVariableStrokeWidth(boolean variableStrokeWidth) {
        this.variableStrokeWidth = variableStrokeWidth;
    }

    public SignatureLine[] getData() {
        return data;
    }

    public void setData(SignatureLine[] data) {
        this.data = data;
    }

    public float getPenWidth() {
        return penWidth;
    }

    public void setPenWidth(float penWidth) {
        this.penWidth = penWidth;
    }

    public float getPadWidth() {
        return padWidth;
    }

    public void setPadWidth(float padWidth) {
        this.padWidth = padWidth;
    }

    public float getPadHeight() {
        return padHeight;
    }

    public void setPadHeight(float padHeight) {
        this.padHeight = padHeight;
    }
    
    @Override
    public String toString(){
        List<SignatureLine> list = new ArrayList<SignatureLine>(Arrays.asList(data));
        
        return list.toString();
    }
}
