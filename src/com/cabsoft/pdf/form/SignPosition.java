package com.cabsoft.pdf.form;

public class SignPosition {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private String id = "";
	
	public SignPosition(String id, int x1, int y1, int x2, int y2){
		this.id = id;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1 + x2;
		this.y2 = y1 + y2;
	}

	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the x1
	 */
	public int getX1() {
		return x1;
	}

	/**
	 * @return the y1
	 */
	public int getY1() {
		return y1;
	}

	/**
	 * @return the x2
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * @return the y2
	 */
	public int getY2() {
		return y2;
	}

	
	
}
