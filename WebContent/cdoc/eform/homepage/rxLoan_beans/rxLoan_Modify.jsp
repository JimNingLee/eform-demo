<%@ page language="java" contentType="text/html;charset=utf-8"%>

<%@ page language="java" import="java.net.URLEncoder"%>
<%@ page language="java" import="java.net.URLDecoder"%>
<%@ page language="java" import="java.util.HashMap"%>
<%@ page language="java" import="java.io.PrintWriter"%>

<%@ page language="java" import="javax.servlet.ServletContext"%>
<%@ page language="java" import="javax.servlet.ServletOutputStream"%>
<%@ page language="java" import="java.io.ByteArrayOutputStream"%>
<%@ page language="java" import="javax.servlet.http.HttpServlet"%>
<%@ page language="java" import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" import="javax.servlet.http.HttpServletResponse"%>

<%@ page language="java" import="org.apache.commons.logging.Log"%>
<%@ page language="java" import="org.apache.commons.logging.LogFactory"%>

<%@ page language="java" import="com.cabsoft.ess.ModifyReportExpressPrint"%>
<%@ page language="java" import="com.cabsoft.rx.engine.ReportExpressPrint"%>
<%@ page language="java" import="com.cabsoft.utils.StackTrace"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SimpleQuery"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKey"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKeyInstance"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSA"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.TEA"%>

<%@ page language="java" import="com.cabsoft.utils.objSerializer"%>
<%@ page language="java" import="com.cabsoft.ess.ExportSigningData"%>
<%@ page language="java" import="com.cabsoft.rx.engine.export.RXHtmlExporterParameter"%>
<%@ page language="java" import="com.cabsoft.rx.engine.RXExporterParameter"%>
<%@ page language="java" import="com.cabsoft.org.json.JSONObject"%>
<%@ page language="java" import="com.cabsoft.GlobalParams"%>
<%@ page language="java" import="com.cabsoft.SignCertFactory"%>
<%@ page language="java" import="com.cabsoft.pdfutils.Sign.SignCertUtil"%>
<%@ page language="java" import="com.cabsoft.sign.SignEmulator"%>
<%@ page language="java" import="com.cabsoft.utils.Compress"%>

<%!@SuppressWarnings("deprecation")
public class EssModify {
	private final Log log = LogFactory.getLog(EssModify.class);
	GlobalParams globalParams = null;
	public EssModify(){
		
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String error = "";
		String result = "OK";
		
		try{
			globalParams = GlobalParams.getInstance();
			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}
			HttpSession session = request.getSession();
			String jobID = (String) session.getAttribute("jobID");
			RXSession ss = (RXSession) session.getAttribute(jobID+"_session");
			
			if(!jobID.equalsIgnoreCase(ss.getJobID())){
				result = "FAIL";
				error = "세션이 일치하지 않습니다.";
			}else{
				String rxprintData = ss.getRxprintData();
				ReportExpressPrint rxPrint = (ReportExpressPrint) com.cabsoft.utils.objLoader.loadObjectFromCompressedBase64(rxprintData);
				
				if(rxPrint==null){
					result = "FAIL";
					error = "세션에 저장된 보고서가 없습니다.";
				}else{
					HashMap<String, String> params = getParams(request);
					String signeddata = params.get("signeddata");
					
					signeddata = StringUtils.nvl(signeddata,"");
					
					ModifyReportExpressPrint modifyPrint = new ModifyReportExpressPrint(rxPrint);
					rxPrint = modifyPrint.getReportExpressPrint(params, ss.getExptHMap());
					
// 					/*
// 					 * 전자서명 데이터가 없는 경우 rxprint 전체에 전자서명을 하기 위해
// 					 * rxprint를 HTML 문서로 변환한다. 
// 					 */
// 					if(StringUtils.isNull(signeddata)){
						
// 						ByteArrayOutputStream baos = new ByteArrayOutputStream();
// 						ExportSigningData exporter = new ExportSigningData();
// 			            exporter.setParameter(RXHtmlExporterParameter.ZOOM_RATIO, (float) 96.0f / 72.0f);
// 			            exporter.setParameter(RXHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
// 			            exporter.setParameter(RXHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, false);
// 			            exporter.setParameter(RXHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, false);

// 			            exporter.setParameter(RXExporterParameter.CHARACTER_ENCODING, "utf-8");
// 			            exporter.setParameter(RXExporterParameter.REPORTEXPRESS_PRINT, rxPrint);
// 			            exporter.setParameter(RXExporterParameter.OUTPUT_STREAM, baos);
// 			            exporter.exportReport();
			            
// 			            baos.flush();
// 			            baos.close();
			            
// 			            signeddata = Compress.ZipBase64(baos.toByteArray());
// 					}
					
					rxprintData = objSerializer.ObjectToCompress(rxPrint);
					ss.setRxprintData(rxprintData);
					ss.setSignedData(signeddata);
					session.setAttribute(jobID+"_session", ss);
				}
			}
		}catch(Exception e){
			result = "FAIL";
			log.error(e.getMessage());
			error = e.getMessage();
		}
		
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
		JSONObject json = new JSONObject();
		
		json.put("error", error);
		json.put("result", result);
		json.write(writer);
		writer.flush();
	}
	
	private HashMap<String, String> getParams(HttpServletRequest request) throws Exception {
		HashMap<String, String> params = null;
		String __p = (String)request.getParameter("__p");
		String __q = (String)request.getParameter("__q");

		__p = __p==null ? "" : __p;
		__q = __q == null ? "" : __q;
		if(!"".equals(__p) && !"".equals(__q)){
			try {
				__p = URLDecoder.decode(__p);
				RSAKeyInstance key = RSAKeyInstance.getInstance();
				RSAKey rsaKey = key.getKey();
				RSA rsa = new RSA(rsaKey);
				String teaKey = rsa.decrypt(__q);
				TEA tea = new TEA(teaKey);
				__p = tea.decrypt(__p);
				
				SimpleQuery qry = new SimpleQuery(true, "utf-8");
				qry.setQuery(__p);
				params = qry.getParams();
							
				return params;
			} catch (Exception e) {
				log.error("HashMap:"+e);
				throw new Exception(e);
			}
		}else{
			throw new Exception("데이터 요청이 잘못되었습니다(암호화)");
		}
	}
	

	private String getSignEmulate(String s) throws Exception{
		String org = URLEncoder.encode(s, "utf-8");
		
		SignCertUtil scert = null;
		
		if (SignCertFactory.getInstance().getPdfSignCert() == null) {
			if (globalParams.isPFX() == true) {
				scert = new SignCertUtil(globalParams.getSignCertFile(), globalParams.getSignCertPwd(), globalParams.isWithBC());
			}else{
				scert = new SignCertUtil(globalParams.getSignCertFile(), globalParams.getSignCertKeyFile(), globalParams.getSignCertPwd());
			}
			SignCertFactory.getInstance().setPdfSignCert(scert);
		}else{
			scert = SignCertFactory.getInstance().getPdfSignCert();
		}
		
		
		byte[] sd = SignEmulator.sign(scert.getX509Certificate(), scert.getPrivateKey(), org.getBytes());
		
		return Compress.ZipBase64(sd);
	}


}%>