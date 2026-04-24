package com.cabsoft.datasource;

import java.util.HashMap;
import java.util.List;


public class PSBankData {

	private String title = "";
	private String TO_DAY = "";
	List<HashMap<String, Object>> list;
	
    /**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the tO_DAY
	 */
	public String getTO_DAY() {
		return TO_DAY;
	}

	/**
	 * @param tO_DAY the tO_DAY to set
	 */
	public void setTO_DAY(String tO_DAY) {
		TO_DAY = tO_DAY;
	}

	/**
	 * @return the listData
	 */
    public List<HashMap<String, Object>> getList() {
        return list;
    }

	/**
	 * @param listData the listData to set
	 */
    public void setList(List<HashMap<String, Object>> list) {
        this.list = list;
    }

}
