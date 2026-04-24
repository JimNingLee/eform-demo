<%@ page language="java" contentType="text/html;charset=utf-8"%>

<%@ page language="java" import="java.net.URLEncoder"%>
<%@ page language="java" import="java.net.URLDecoder"%>
<%@ page language="java" import="java.text.SimpleDateFormat"%>
<%@ page language="java" import="java.util.Date"%>
<%@ page language="java" import="java.util.HashMap"%>
<%@ page language="java" import="java.util.Locale"%>
<%@ page language="java" import="java.util.Map"%>
<%@ page language="java" import="java.util.Iterator"%>
<%@ page language="java" import="java.io.ByteArrayOutputStream"%>

<%@ page language="java" import="javax.servlet.ServletContext"%>
<%@ page language="java" import="javax.servlet.ServletOutputStream"%>
<%@ page language="java" import="javax.servlet.http.HttpServlet"%>
<%@ page language="java" import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" import="javax.servlet.http.HttpServletResponse"%>

<%@ page language="java" import="org.apache.commons.logging.Log"%>
<%@ page language="java" import="org.apache.commons.logging.LogFactory"%>

<%@ page language="java" import="com.cabsoft.rx.engine.ReportExpressPrint"%>
<%@ page language="java" import="com.cabsoft.utils.StackTrace"%>
<%@ page language="java" import="com.cabsoft.utils.StringUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SystemUtils"%>
<%@ page language="java" import="com.cabsoft.utils.SimpleQuery"%>
<%@ page language="java" import="com.cabsoft.RXSession"%>

<%@ page language="java" import="com.cabsoft.GlobalParams"%>
<%@ page language="java" import="com.cabsoft.ess.essAttachments"%>
<%@ page language="java" import="com.cabsoft.sign.service.PdfSignService"%>
<%@ page language="java" import="com.cabsoft.exporters.service.ExportPdfService"%>
<%@ page language="java" import="com.cabsoft.SignCertFactory"%>
<%@ page language="java" import="com.cabsoft.pdfutils.Sign.SignCertUtil"%>
<%@ page language="java" import="com.cabsoft.sign.SignEmulator"%>
<%@ page language="java" import="com.cabsoft.utils.Compress"%>
<%@ page language="java" import="com.cabsoft.utils.sysinfo"%>

<%@ page language="java" import="com.cabsoft.utils.IssuerID"%>
<%@ page language="java" import="com.cabsoft.utils.Hash"%>

<%@ page language="java" import="com.cabsoft.text.BaseColor"%>
<%@ page language="java" import="com.cabsoft.sign.AdditionStamp"%>
<%@ page language="java" import="com.cabsoft.utils.Base64Util"%>

<%!@SuppressWarnings("deprecation")
	public class EssPdfExport {
		private final Log log = LogFactory.getLog(EssPdfExport.class);
		GlobalParams globalParams = null;

		public EssPdfExport() {

		}

		public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
			globalParams = GlobalParams.getInstance();
			if (globalParams == null) {
				globalParams = GlobalParams.getInstance(request.getRealPath("/WEB-INF/"));
			}
			HttpSession session = request.getSession();
			String jobID = (String) session.getAttribute("jobID");
			RXSession ss = (RXSession) session.getAttribute(jobID + "_session");
			String exptFileName = StringUtils.nvl(ss.getRptFile(), SystemUtils.getDateTime("yyyyMMdd_hhmmss"));

			if (!jobID.equalsIgnoreCase(ss.getJobID())) {
				throw new Exception("세션이 일치하지 않습니다.");
			}

			String rxprintData = ss.getRxprintData();
			ReportExpressPrint rxPrint = (ReportExpressPrint) com.cabsoft.utils.objLoader
					.loadObjectFromCompressedBase64(rxprintData);

			if (rxPrint == null) {
				throw new Exception("세션에 저장된 보고서가 없습니다.");
			}

			response.setHeader("Set-Cookie", "essDownload=true; path=/");

			String signedData = ss.getSignedData();

			if (!StringUtils.isNull(signedData)) {
				signedData = getSignEmulate(signedData);
			}

			String sdData[] = { signedData };

			Map<String, Object> parameters = new HashMap<String, Object>();
			ServletOutputStream out = response.getOutputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ByteArrayOutputStream att_baos = new ByteArrayOutputStream();
			String webinf = request.getRealPath("/WEB-INF/");
			parameters.put("SignOnly", "0");
			parameters.put("Process", "pdf");
			ExportPdfService pdf = new ExportPdfService();
			pdf.exportPdf(webinf, baos, rxPrint, parameters, "");
			baos.flush();
			baos.close();

			// 서식안에서 결정
			String additionInfo = rxPrint.getProperty("com.cabsoft.rx.export.additional.info.visible");
			// 추가정보
			//com.cabsoft.rx.export.additional.info.x
			//com.cabsoft.rx.export.additional.info.y
			//com.cabsoft.rx.export.additional.info.w
			//com.cabsoft.rx.export.additional.info.h

			if ("true".equals(additionInfo) && !StringUtils.isNull(signedData)) {
				ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
				AdditionStamp.stamp(baos, baos2, signedData, 4f, new BaseColor(0, 0, 255), new BaseColor(0, 0, 0), 460,
						20, 80, 50);

				essAttachments att = new essAttachments();
				att.buildAttach(baos2, att_baos, null, null, sdData);
				att_baos.flush();
				att_baos.close();
			} else {
				essAttachments att = new essAttachments();
				att.buildAttach(baos, att_baos, null, null, sdData);
				att_baos.flush();
				att_baos.close();
			}

			// 		Hash hash = new Hash();
			// 		hash.setAlgorithm("MD5");
			// 		System.out.println("att_baos:"+hash.encrypt(att_baos.toByteArray()));

			//		Adobe PDF Writer에서 저장 안됨
			// 		if(sysinfo.osVersion(ua)>=6.0f){
			// 			response.addHeader("Content-Disposition", "attachment;filename=" + exptFileName + ".pdf;");
			// 		}else{
			// 			response.addHeader("Content-Disposition", "inline;filename=" + exptFileName + ".pdf;");
			// 		}

			ByteArrayOutputStream pdf_out = new ByteArrayOutputStream();

			PdfSignService.signPdfSelf(pdf_out, att_baos, webinf, "");

			//PdfSignTsaService_api.signPdf(pdf_out, att_baos, webinf, "");

			// 		System.out.println("pdf_out:"+hash.encrypt(pdf_out.toByteArray()));
			pdf_out.flush();

			// 		ss.setUsePdf(true);
			// 		ss.setPdfData(pdf_out.toByteArray());
			// 		ss.setTiffServer(globalParams.getProperty("com.cabsoft.tiff.server"));
			// 		ss.setReceiveUrl(globalParams.getProperty("com.cabsoft.tiff.receive.url"));
			// 		sendImageServer(ss, rxPrint.getPages().size());

			String ua = request.getHeader("User-Agent");
			response.setContentType("application/pdf;charset=utf-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + exptFileName + ".pdf;");
			pdf_out.writeTo(out);
		}

		private String getSignEmulate(String s) throws Exception{
// 			String org = URLEncoder.encode(s, "utf-8");
			String org = s;
			if(org.startsWith("MI")){
				byte[] b = Base64Util.decode(org.getBytes());
				return Compress.ZipBase64(b);
			}else{
			
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
		}

	}%>