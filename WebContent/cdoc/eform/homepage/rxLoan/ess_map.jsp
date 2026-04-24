<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>
<%@ page language="java" import="java.util.HashMap"%>
<%@ page language="java" import="com.cabsoft.utils.SimpleQuery"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKey"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKeyInstance"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSA"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.TEA"%>
<%
	response.setHeader("Cache-Control", "no-store");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("utf-8");

	String errUrl = "../error.jsp?msg=";
	String referer = request.getHeader("referer");
	if (!request.isRequestedSessionIdValid() || referer == null || referer.lastIndexOf("rxLoan_service.jsp")<0) {
		out.println("[{\"id\":\"errormsg\",\"msg\":\"(103)서버와의 연결(세션)이 종료되었습니다. \"}]");
	} else {

		//client  jobid 와 server jobid 체크 시작
		boolean ServieChk  = true;
		String jobID = (String) session.getAttribute("jobID");
		String reqjobID = "";
		if (!StringUtils.isEmpty(jobID)) {
			String __p = (String)request.getParameter("__p");
			String __q = (String)request.getParameter("__q");
			String __e = (String)request.getParameter("__e");

			TEA tea = null;
			String teaKey = null;
			if(!StringUtils.isNull(__p) && !StringUtils.isNull(__q)){
				RSAKeyInstance key = RSAKeyInstance.getInstance();
				RSAKey rsaKey = key.getKey();
				RSA rsa = new RSA(rsaKey);
				teaKey = rsa.decrypt(__q);
				tea = new TEA(teaKey);
				__p = tea.decrypt(__p);

				
				SimpleQuery qry = new SimpleQuery();
				qry.setQuery(__p);
				reqjobID = qry.getQuery("jobID");
				
				if(!jobID.equals(reqjobID)){
					out.println("[{\"id\":\"errormsg\",\"msg\":\"(102)[전자문서 생성]서버에 생성 정보가 일치하지 않습니다. \"}]");
					ServieChk = false;
				}
			}else{
				out.println("[{\"id\":\"errormsg\",\"msg\":\"(101)[전자문서 생성]요청 정보가 올바르지 않습니다.\"}]");
				ServieChk = false;
			}
			//client  jobid 와 server jobid 체크 끝
			
			
			if (ServieChk) {
				RXSession ss = (RXSession) session.getAttribute(jobID+"_session");
				HashMap<String,Object> exptmap = ss.getExptHMap();
				String json = (String)exptmap.get("jsondata");

				if ("1".equals(__e)){
					String ejson = new String(tea.encrypt(json));
					out.println("[{\"id\":\"encData\",\"msg\":\""+ejson+"\"}]");
				} else {
					out.println("[{\"id\":\"encData\",\"msg\":"+json+"}]");
				}
				
				exptmap.remove("jsondata");
			}
		} else {
			out.println("[{\"error\",\"(100)[전자문서 생성]요청 정보가 올바르지 않습니다.\"}]");
		}
	}
%>
