<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>
<%@ page errorPage="../../error.jsp" %>
<%
	System.err.println(">>> call rxLoan.jsp");

	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("utf-8");

	String errUrl = "../error.jsp?msg=";
	if (!request.isRequestedSessionIdValid()) {
		response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
	} else {
		// 모든 파라미터 가져오기
		java.util.Enumeration<String> paramNames = request.getParameterNames();

		// XML 문자열 만들기
		StringBuilder xml = new StringBuilder();
		//xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");

		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);

			if(name.equals("name") || name.equals("email")) {
				// XML 특수문자 이스케이프 처리
				if (value != null) {
					value = value.replace("&", "&amp;")
							.replace("<", "&lt;")
							.replace(">", "&gt;")
							.replace("\"", "&quot;")
							.replace("'", "&apos;");
				}

				xml.append("<").append(name).append(" length=\"100\" value=\"")
						.append(value == null ? "" : value)
						.append("\" valueType=\"X\"")
						.append("/>");
			}else {
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
		session.setAttribute("jobID", jobID);

		String servlet = "rxLoan_service.jsp";

		System.err.println(">>> call rxLoan.jsp 2");
		// html 뷰어로 전달
		String signtype = (String) request.getParameter("signtype");
		String filename = "solution_rxLoan_demo";
		String process = "PDF";
		String rptTitle = "솔루션 테스트";
		String xmlfile = "";
		String xmlType = "xmlstring"; //xmlstring, 그외

		System.err.println(">>> call rxLoan.jsp 3");
		// Layer 여부
		String n_isLayer	= StringUtils.nvl(request.getParameter("n_isLayer"), "false");

		System.err.println(">>> call rxLoan.jsp 4");
		signtype = signtype==null ? "" : signtype;

		System.err.println(">>> call rxLoan.jsp 5");
		// 보고서 설정값 설정
		RXSession ss = new RXSession();
		ss.setJobID(jobID);
		ss.setRxprintData("");
		ss.setIssueID("");
		ss.setRptFile(filename);
		ss.setXmlData(xmlString);
		ss.setPdfData(null);
		ss.setUserpwd("");
		ss.setPageView(null);
		ss.setJson(null);

		System.err.println(">>> call rxLoan.jsp 6");
		ss.setCustomer("홍길동");
		ss.setProduct("전자문서");
		ss.setSignType(signtype);

		System.err.println(">>> call rxLoan.jsp 7");
		session.setAttribute(jobID + "_session", ss);
%>
<html>
<head>
	<META http-equiv=Content-Type content="text/html; charset=utf-8" Cache-control="no-cache" Pragma="no-cache">
	<link href="../loading/jquery.loadmask.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="../loading/jquery-latest.pack.js"></script>
	<script type='text/javascript' src='../loading/jquery.loadmask.js'></script>
	<style type="text/css">
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
			f.appendChild(addData('rptTitle', '<%=rptTitle%>'));
			f.appendChild(addData('xmlType', '<%=xmlType%>'));
			f.appendChild(addData('xmlfile', '<%=xmlfile%>'));
			f.appendChild(addData('n_isLayer', '<%=n_isLayer%>'));
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
