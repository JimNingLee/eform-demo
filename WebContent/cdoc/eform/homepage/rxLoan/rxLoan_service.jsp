<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page errorPage="../../error.jsp" %>
<%@ include file="../rxLoan_beans/rxLoan_HtmlView.jsp"%>
<%

	System.err.println(">>> call rxLoan_service.jsp");

	response.setHeader("Cache-Control", "no-store");
	System.err.println(">>> call rxLoan_service.jsp 2");
	response.setHeader("Pragma", "no-cache");
	System.err.println(">>> call rxLoan_service.jsp 3");
	response.setDateHeader("Expires", 0);
	System.err.println(">>> call rxLoan_service.jsp 4");
	String errUrl = "../error.jsp?msg=";
	System.err.println(">>> call rxLoan_service.jsp 5");
	String referer = request.getHeader("referer");
	System.err.println(">>> call rxLoan_service.jsp 6");

	if (!request.isRequestedSessionIdValid() || referer == null || referer.lastIndexOf("rxLoan.jsp")<0) {
		System.err.println(">>> call rxLoan_service.jsp session ERROR");
		response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
	} else {

// 		//client  jobid 와 server jobid 체크 시작
// 		boolean ServieChk  = true;
// 		String jobID = (String) session.getAttribute("jobID");
// 		String reqjobID = "";
// 		if (!StringUtils.isEmpty(jobID)) {
// 			String __p = (String)request.getParameter("__p");
// 			String __q = (String)request.getParameter("__q");

// 			if(!StringUtils.isNull(__p) && !StringUtils.isNull(__q)){
// 				RSAKeyInstance key = RSAKeyInstance.getInstance();
// 				RSAKey rsaKey = key.getKey();
// 				RSA rsa = new RSA(rsaKey);
// 				String teaKey = rsa.decrypt(__q);
// 				TEA tea = new TEA(teaKey);
// 				__p = tea.decrypt(__p);


// 				SimpleQuery qry = new SimpleQuery();
// 				qry.setQuery(__p);
// 				reqjobID = qry.getQuery("jobID");

// 				if(!jobID.equals(reqjobID)){
// 					out.println("[{\"id\":\"errormsg\",\"msg\":\"(102)[전자문서 생성]서버에 생성 정보가 일치하지 않습니다. \"}]");
// 					ServieChk = false;
// 				}
// 			}else{
// 				out.println("[{\"id\":\"errormsg\",\"msg\":\"(101)[전자문서 생성]요청 정보가 올바르지 않습니다.\"}]");
// 				ServieChk = false;
// 			}
// 			//client  jobid 와 server jobid 체크 끝		

		try {
			out.clear();
			out = pageContext.pushBody();
			System.err.println(">>> call rxLoan_service.jsp 6");

			essService service = new essService();
			System.err.println(">>> call rxLoan_service.jsp 7");
			service.setDevel(true);
			System.err.println(">>> call rxLoan_service.jsp 8");
			service.setUseOverlap(true);
			System.err.println(">>> call rxLoan_service.jsp 9");
			service.processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(errUrl + java.net.URLEncoder.encode(e.getMessage(), "utf-8"));
		}
	}
%>
