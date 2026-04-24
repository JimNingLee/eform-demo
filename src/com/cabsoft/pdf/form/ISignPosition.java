package com.cabsoft.pdf.form;

public interface ISignPosition {
	public ISignPosition newInstance();
	public void setSignPosition(String id, int x1, int y1, int x2, int y2);
	public String getId();
	public void setId(String id);
	public String getGroupName();
	public void setGroupName(String groupName);
	public int getX1();
	public int getY1();
	public int getX2();
	public int getY2();
}
