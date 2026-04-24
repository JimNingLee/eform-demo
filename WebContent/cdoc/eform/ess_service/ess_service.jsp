<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page errorPage="../error.jsp" %>
<%@ include file="../ess_beans/ess_HtmlView.jsp"%>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	String errUrl = "../error.jsp?msg=";
	String referer = request.getHeader("referer");
	if (!request.isRequestedSessionIdValid() || referer == null || referer.lastIndexOf("ess_run.jsp")<0) {
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

			essService service = new essService();
			service.setDevel(true);
			service.setUseOverlap(true);
			service.processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(errUrl + java.net.URLEncoder.encode(e.getMessage(), "utf-8"));
		}
	}
%>
