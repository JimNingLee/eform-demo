package com.cabsoft.html;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportSinglePage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	ServletContext context = null;

	public ExportSinglePage() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		context = config.getServletContext();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		// try {
		// String pageIndex = (String) request.getParameter("PageIndex");
		// if (pageIndex == null || StringUtils.isNumber(pageIndex) == false) {
		// throw new ServletException("페이지 번호가 잘못되었습니다.\n페이지 번호: " + (pageIndex
		// == null ? "null" : pageIndex));
		// }
		// ExportHtml.Html(Integer.valueOf(pageIndex), request, response);
		// } catch (Exception e) {
		// throw new ServletException(e);
		// }
	}

}
