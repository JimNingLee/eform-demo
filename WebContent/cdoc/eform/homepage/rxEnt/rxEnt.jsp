<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>
<%@ page import="org.json.XML" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page errorPage="error.jsp" %>
<%

	System.err.println(">>> call rxEnt.jsp");

	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("utf-8");

	String errUrl = "./error.jsp?msg=";
	if (!request.isRequestedSessionIdValid()) {
		response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
	} else {

		System.err.println(">>> rxEnt.jsp called!");

		// 모든 파라미터 가져오기
		java.util.Enumeration<String> paramNames = request.getParameterNames();

		// XML 문자열 만들기
		StringBuilder xml = new StringBuilder();
		//xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");

		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);

			// XML 특수문자 이스케이프 처리
			if (value != null) {
				value = value.replace("&", "&amp;")
						.replace("<", "&lt;")
						.replace(">", "&gt;")
						.replace("\"", "&quot;")
						.replace("'", "&apos;");
			}

			xml.append("<").append(name).append(">")
					.append(value == null ? "" : value)
					.append("</").append(name).append(">");
		}
		xml.append("</root>");

		// 결과 XML 문자열
		String xmlString = xml.toString();
		System.err.println(">>> xmlString: " + xmlString);


		// 이전 jobID 제거
		String jobID = (String) session.getAttribute("jobID");
		if (!StringUtils.isEmpty(jobID)) {
			session.setAttribute(jobID + "_session", null);
			session.removeAttribute(jobID + "_session");
			session.setAttribute("jobID", null);
			session.removeAttribute("jobID");
		}

		//jobid생성
		jobID = SystemUtils.GenerateID();
		request.getSession().setAttribute("jobID", jobID);

		String servlet = "rxEnt_service.jsp";

		// html 뷰어로 전달
		String filename = "solution_demo";
		String process = "PDF";
		String rptTitle = "솔루션 테스트";
		String certpasswd = "";
		String requesrpasswd = "";
		String toolbarType = "1";
		String plugin = "";

		toolbarType = (null==toolbarType) ? RXSession.TOOLBAR_DEFAULT : toolbarType;

		String xmlType = "xmlstring"; //xmlstring, 그외

		RXSession ss = new RXSession();
		ss.setJobID(jobID);
		ss.setRxprintData("");
		ss.setIssueID("");
		ss.setRptFile(filename);
		ss.setXmlData(xmlString);
		ss.setPdfData(null);
		ss.setToolbarType("ios");
		ss.setPluginMode((plugin==null ? false : ("1".equals(plugin) ? true : false)));

		if (requesrpasswd != null && "1".equals(requesrpasswd)) {
			certpasswd = "";
			// 사용자 암호 입력이 요구됨(미리보기 후에 결정)
			ss.setUserpwd("");
			ss.setReqpasswd(true);
		} else {
			//암호 자동입력(미리보기전에 결정)
			ss.setUserpwd(certpasswd);
			ss.setReqpasswd(false);
		}
		ss.setPageView(null);
		ss.setJson(null);
		session.setAttribute(jobID + "_session", ss);
%>
<html>
<head>
	<META http-equiv=Content-Type content="text/html; charset=utf-8" Cache-control="no-cache" Pragma="no-cache">
	<link href="../loading/jquery.loadmask.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="../loading/jquery-latest.pack.js"></script>
	<script type='text/javascript' src='../loading/jquery.loadmask.js'></script>
	<style>
		body {
			font-size: 11px;
			font-family: tahoma;
		}

		#content {
			padding: 5px;
			width: 200px;
		}

		#buttons {
			padding-left: 40px;
		}
	</style>
	<script language="javascript">
		$(document).ready(function() {
			$("#body").mask("<%=rptTitle%> 생성 중 ...");
		} );

		function makeForm(url){
			var f = document.createElement("form");
			f.setAttribute("method", "post");
			f.setAttribute("action", url);
			document.body.appendChild(f);

			return f;
		}

		function addData(name, value){
			var i = document.createElement("input");
			i.setAttribute("type","hidden");
			i.setAttribute("name",name);
			i.setAttribute("value",value);
			return i;
		}

		window.onload = function(){
			f = makeForm('<%=servlet%>');
			f.appendChild(addData('ReportFile', '<%=filename%>'));
			f.appendChild(addData('Process', '<%=process%>'));
			f.appendChild(addData('certpasswd', '<%=certpasswd%>'));
			f.appendChild(addData('rptTitle', '<%=rptTitle%>'));
			f.appendChild(addData('xmlType', '<%=xmlType%>'));
			f.submit();
		}

	</script>

</head>
<body id="body" leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0">
<table cellspacing="0" cellpadding="0" align="center" style="height: 100%;">
	<tr>
		<td></td>
	</tr>
</table>
</body>
</html>
<%
	}
%>