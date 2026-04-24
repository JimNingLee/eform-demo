package com.cabsoft.pdf.form;

import java.util.HashMap;
import java.util.List;

public interface ISignPages {
	public void newSignPosition();
	public void addSignPosition(ISignPosition pos) throws Exception;
	public void addTextPosition(ISignPosition pos) throws Exception;
	public void addCheckboxPosition(ISignPosition pos) throws Exception;
	public void addRadiobuttonPosition(ISignPosition pos) throws Exception;
	public void addValidation(String id) throws Exception;
	public void addSignPages();
	public List<HashMap<String, ISignPosition>> getSignPages();
	public String toXml(String charset);
	public String toJSON(String charset);
	public HashMap<String, String> toJSONasMap(String charset);
}
