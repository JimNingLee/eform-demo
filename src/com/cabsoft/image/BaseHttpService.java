/*
 * ReportExpress - Free Java Reporting Library.
 * Copyright (C) 2001 - 2012 CABSOFTWARE.COM. All rights reserved.
 * http://www.cabsoftware.com
 *
 * Unless you have purchased a commercial license agreement from CABSOFTWARE,
 * the following license terms apply:
 *
 * This program is part of ReportExpress.
 *
 * ReportExpress is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ReportExpress is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReportExpress. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cabsoft.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.cabsoft.RXSession;
import com.cabsoft.rx.engine.DefaultRXReportsContext;
import com.cabsoft.rx.engine.RXConstants;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.util.RXLoader;
import com.cabsoft.utils.RXobjLoader;
import com.cabsoft.utils.SimpleQuery;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: BaseHttpServlet.java 5180 2012-03-29 13:23:12Z teodord $
 */
@SuppressWarnings("unchecked")
public abstract class BaseHttpService extends HttpServlet {
	private static final long serialVersionUID = RXConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	public static final String DEFAULT_REPORTEXPRESS_PRINT_LIST_SESSION_ATTRIBUTE = "com.cabsoft.rx.j2ee.rx_print_list";
	public static final String DEFAULT_REPORTEXPRESS_PRINT_SESSION_ATTRIBUTE = "com.cabsoft.rx.j2ee.rx_print";
	public static final String DEFAULT_REPORTEXPRESS_PRINT_FILE_SESSION_ATTRIBUTE = "com.cabsoft.rx.j2ee.rx_print_file";
	public static final String DEFAULT_REPORTEXPRESS_PRINT_COMPRESSED_SESSION_ATTRIBUTE = "com.cabsoft.rx.j2ee.rx_print_compressed";
	public static final String DEFAULT_REPORTEXPRESS_PRINT_LIST_COMPRESSED_SESSION_ATTRIBUTE = "com.cabsoft.rx.j2ee.rx_print_list_compressed";

	public static final String REPORTEXPRESS_PRINT_LIST_REQUEST_PARAMETER = "rxprintlist";
	public static final String REPORTEXPRESS_PRINT_REQUEST_PARAMETER = "rxprint";
	public static final String REPORTEXPRESS_PRINT_FILE_REQUEST_PARAMETER = "rxprint_file";
	public static final String REPORTEXPRESS_PRINT_COMPRESSED_REQUEST_PARAMETER = "rxprint_compressed";
	public static final String REPORTEXPRESS_PRINT_LIST_COMPRESSED_REQUEST_PARAMETER = "rxprintlist_compressed";

	public static final String BUFFERED_OUTPUT_REQUEST_PARAMETER = "buffered";

	/**
	 *
	 */
	public RXReportsContext getReportExpresssContext() {
		return DefaultRXReportsContext.getInstance();
	}

	/**
	 * @throws IOException
	 * 
	 */
	public static List<ReportExpressPrint> getReportExpressPrintList(HttpServletRequest request) throws ServletException {
	
		String p = (String) request.getParameter("__p");
		String jobID = "";
		if (p != null && !"".equals(p)) {

			String pp = new String(com.cabsoft.utils.Hex.parseHexaString(p));
			SimpleQuery qry = new SimpleQuery();
			qry.setQuery(pp);
			jobID = qry.getQuery("jobID");
		} else {
			jobID = (String) request.getParameter("jobID");
		}

		String rxPrintListSessionAttr = request.getParameter(REPORTEXPRESS_PRINT_LIST_REQUEST_PARAMETER);
		if (rxPrintListSessionAttr == null) {
			rxPrintListSessionAttr = DEFAULT_REPORTEXPRESS_PRINT_LIST_SESSION_ATTRIBUTE;
		}

		String rxPrintSessionAttr = request.getParameter(REPORTEXPRESS_PRINT_REQUEST_PARAMETER);
		if (rxPrintSessionAttr == null) {
			rxPrintSessionAttr = DEFAULT_REPORTEXPRESS_PRINT_SESSION_ATTRIBUTE;
		}

		String rxPrintFileSessionAttr = request.getParameter(REPORTEXPRESS_PRINT_FILE_REQUEST_PARAMETER);
		if (rxPrintFileSessionAttr == null) {
			rxPrintFileSessionAttr = DEFAULT_REPORTEXPRESS_PRINT_FILE_SESSION_ATTRIBUTE;
		}

		String rxPrintCompressedSessionAttr = request.getParameter(REPORTEXPRESS_PRINT_COMPRESSED_REQUEST_PARAMETER);
		if (rxPrintCompressedSessionAttr == null) {
			rxPrintCompressedSessionAttr = DEFAULT_REPORTEXPRESS_PRINT_COMPRESSED_SESSION_ATTRIBUTE;
		}

		String rxPrintListCompressedSessionAttr = request.getParameter(REPORTEXPRESS_PRINT_LIST_COMPRESSED_REQUEST_PARAMETER);
		if (rxPrintListCompressedSessionAttr == null) {
			rxPrintListCompressedSessionAttr = DEFAULT_REPORTEXPRESS_PRINT_LIST_COMPRESSED_SESSION_ATTRIBUTE;
		}

		List<ReportExpressPrint> rxPrintList = null;

		/**
		 * Request에 jobID가 있는 경우 세션 jobID에서 데이터를 가져온다.
		 */
		if (jobID != null && !jobID.equalsIgnoreCase("")) {
			String data = ((RXSession) request.getSession().getAttribute(jobID+"_session")).getRxprintData();
			if (data != null) {
				try {
					ReportExpressPrint rxprint = (ReportExpressPrint) RXobjLoader.loadObjectFromCompressed(data);
					if (rxprint != null) {
						rxPrintList = new ArrayList<ReportExpressPrint>();
						rxPrintList.add(rxprint);
					}
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		}

		if (rxPrintList == null) {
			String listData = (String) request.getSession().getAttribute(rxPrintListCompressedSessionAttr);
			if (listData == null) {
				rxPrintList = (List<ReportExpressPrint>) request.getSession().getAttribute(rxPrintListSessionAttr);
				if (rxPrintList == null) {
					ReportExpressPrint reportexpressPrint = (ReportExpressPrint) request.getSession().getAttribute(rxPrintSessionAttr);
					if (reportexpressPrint == null) {
						String fs = (String) request.getSession().getAttribute(rxPrintFileSessionAttr);
						if (fs != null) {
							try {
								reportexpressPrint = RXLoader.loadReportExpressPrintFromFile(fs, null);
							} catch (Exception e) {
								throw new ServletException(e);
							}
						} else {
							String data = (String) request.getSession().getAttribute(rxPrintCompressedSessionAttr);
							if (data != null) {
								try {
									reportexpressPrint = (ReportExpressPrint) RXobjLoader.loadObjectFromCompressed(data);
								} catch (Exception e) {
									throw new ServletException(e);
								}
							}
						}
					}
					if (reportexpressPrint != null) {
						rxPrintList = new ArrayList<ReportExpressPrint>();
						rxPrintList.add(reportexpressPrint);
					}
				}
			} else {
				try {
					rxPrintList = (List<ReportExpressPrint>) RXobjLoader.loadObjectFromCompressed(listData);
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		}

		return rxPrintList;
	}

}
