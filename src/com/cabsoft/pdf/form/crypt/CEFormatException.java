/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cabsoft.pdf.form.crypt;

import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class CEFormatException extends IOException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -606951826816808981L;

	/**
     *
     * @param s
     */
    public CEFormatException(String s) {
        super(s);
    }
}
