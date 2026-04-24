package com.cabsoft.massive;

import java.io.Serializable;

public class PageView  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7739740635556964626L;
	private int startIndex = -1;
	private int endIndex = -1;
	
	public PageView(){
		startIndex = -1;
		endIndex = -1;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
}
