<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKey"%>
<%@ page language="java" import="com.cabsoft.jscrypt.cipher.RSAKeyInstance"%>

<%!public class EssHeaderBeans {
		private boolean useAbsolutePosition = false;
		private boolean devel = false;
		private float ie_version;
		private String bw = "";
		private HttpSession session = null;
		
		private int tabindex = 0;
		
		public EssHeaderBeans(){
			devel = false;
			bw = "";
		}
		
		public void setUseAbsolutePosition(boolean useAbsolutePosition){
			this.useAbsolutePosition = useAbsolutePosition;
		}
		
		public void setDevel(boolean dev){
			this.devel = dev;
		}
		
		public void setIEVersion(float ie_version){
			this.ie_version = ie_version;
		}
		
		public void setBroswer(String bw){
			this.bw = bw;
		}
		
		public void setSession(HttpSession session){
			this.session = session;
		}
		
		public String downloadForm(String jobID, String exportfileName, String pwd) {
			StringBuffer sb = new StringBuffer();

			sb.append("<form action=\"post\" name=\"frmcert\">\n");
			sb.append("<input type=\"hidden\" name=\"jobID\" value=\"")
					.append(jobID).append("\"></input>\n");
			sb.append("<input type=\"hidden\" name=\"exportFileName\" value=\"")
					.append(exportfileName).append("\"></input>\n");
			sb.append("<input type=\"hidden\" name=\"userpwd\" value=\"").append(pwd).append("\"></input>\n");
			sb.append("</form>\n");
			return sb.toString();
		}

		public String addMenu(String id, String caption, String tooltip, String css_class) {
			StringBuffer sb = new StringBuffer();
			tabindex++;
			sb.append(
					"<li><a tabindex='").append(tabindex).append("' id=\"" + id + "\" href=\"javascript:void(0);\" onclick=\"doMenu('" + id + "');\" title=\"" + tooltip + "\" class=\"" + css_class + "\">" + caption + "<span></span></a></li>\n");
			return sb.toString();
		}
		
		public String addCurrentPage(String id, String id1, String caption, String tooltip, String css_class){
			StringBuffer sb = new StringBuffer();

			sb.append("<li>");
			sb.append("<a id=\"").append(id).append("\" hred=\"#\" title=\"").append(tooltip).append("\" ");
			sb.append("class=\"").append(css_class).append("\">").append(caption);
			sb.append("<span id=\"").append(id1).append("\" style=\"cursor:default\">-</span></a>");
			sb.append("</li>");
			
			return sb.toString();
		}

		public String addCertKey(String path){
			StringBuffer sb = new StringBuffer();
			String time = String.valueOf(System.currentTimeMillis());
			
			RSAKeyInstance key = RSAKeyInstance.getInstance();
			String e = RSAKey.toHex(key.getKey().getPublicExponent());
			String m = RSAKey.toHex(key.getKey().getModulus());
			
			if(session!=null){
				session.setAttribute("__RSAKEY__", key.getKey());
			}
			
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
				.append("jscrypt/tea-block.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
			.append("jscrypt/base64.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
			.append("jscrypt/md5.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
				.append("jscrypt/utf8.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
				.append("jscrypt/jsbn.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
				.append("jscrypt/rsa.js?").append(time).append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
				.append("jscrypt/crypt.js?").append(time).append("\"></script>\n");
			
			sb.append("<script type=\"text/javascript\" >\n");
			sb.append("mm=\"").append(m).append("\";ee=\"").append(e).append("\";");
			sb.append("</script>\n");
			
			return sb.toString();
		}
		
		public String MenuStart(String bodyID, String path, String title,
				String charset, String jobID, String exportFileName, String rxe, String form, 
				String signType, String sessionId, String layerYn) {
			StringBuffer sb = new StringBuffer();
			String time = String.valueOf(System.currentTimeMillis());

			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			sb.append("<html lang=\"ko\">\n");
			sb.append("<head>\n");
			sb.append("<title>").append(title).append("</title>\n");
			sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n");
			sb.append("<meta name=\"reportexpress\" content=\"notranslate\"/>\n");
			sb.append("<meta name=\"viewport\" content=\"user-scalable=yes;\"/>\n");
			sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=7;IE=8;IE=9;\"/>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
					.append(path).append("css/rxe.css?").append(time)
					.append("\"/>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
				.append(path).append("css/print.css?").append(time)
				.append("\" media=\"print\"/>\n");
			
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
				.append(path).append("css/navi.css?").append(time)
				.append("\" media=\"screen\"/>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
				.append(path).append("css/ess.css?").append(time)
				.append("\" media=\"screen\"/>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
				.append(path).append("css/popbox.css?").append(time)
				.append("\" media=\"screen\"/>\n");
			
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
			.append(path).append("css/fonts/fonts.css?").append(time)
			.append("\" media=\"screen\"/>\n");
			
			/*
			 * 전자서명 형태
			 * 0 - 입력값만 전자서명
			 * 1 - 문서 전체에 전자서명
			 */
			sb.append("<script type=\"text/javascript\" >\n");
			sb.append("var signType=\"" + signType + "\";\n");
			sb.append("var isHelpPopup=false;\n");
			sb.append("var isLayerContent=true;\n");
			sb.append("var isMenuShow=true;\n");
			//sb.append("var initPath='ess_map.jsp';\n");
			sb.append("var initPath='essMapClient.jsp';\n");
			sb.append("var sessionId = \"" + sessionId + "\";\n");
			sb.append("var rptTitle = \"" + title + "\";\n");
			sb.append("var layerYn = \"" + layerYn + "\";\n");
			sb.append("</script>\n");			

			if(!devel){
				/*
				 * 마우스 우클릭, 선택방지 , 드래그 방지
				 */
				sb.append("<script type=\"text/javascript\" >\n");
				sb.append("document.oncontextmenu=new Function('return false');\n");
				sb.append("document.ondragstart=new Function('return false'); \n");
				sb.append("document.onselectstart=new Function('return false');\n");
				sb.append("</script>\n");

				/*
				 * 키보드 이벤트
				 */
				sb.append("<script type=\"text/javascript\" >\n");

				// CTRL
				sb.append("var isCtrl = false;\n");
				sb.append("document.onkeyup=function(e){ \n");
				sb.append("var keyCode = e.keyCode || window.event.keyCode;\n");
				sb.append("if(keyCode == 17) isCtrl=false;\n");
				sb.append("};\n");
				
				// ALT
				sb.append("var isAlt = false;\n");
				sb.append("document.onkeyup=function(e){ \n");
				sb.append("var event = window.event || e;\n");
				sb.append("if(event.keyCode == 18) isAlt=false;\n");
				sb.append("};\n");

				sb.append("document.onkeydown=function(e){\n");
				sb.append("var event = window.event || e;\n");

// 				// F5 및 F12 키 -- 사파리에서 동작 안함 ㅠ.ㅠ
				sb.append("if(event.keyCode == 116 || event.keyCode == 123){\n");
				sb.append("event.keyCode = 0;\n");
				sb.append("event.returnValue=false;\n");
				sb.append("return false;\n");
				sb.append("}\n");
						
				sb.append("if(event.keyCode == 17) isCtrl=true;\n");
				sb.append("if(event.keyCode == 18) isAlt=true;\n");
				
				//run code for ALT+F4 -- ie, open!
				sb.append("if(event.keyCode == 115 && isAlt == true) {\n");
				sb.append("event.Handled = true;");
				sb.append("event.returnValue=false;\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+S -- ie, save!
				sb.append("if(event.keyCode == 83 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+O -- ie, open!
				sb.append("if(event.keyCode == 79 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");
				
				//run code for CTRL+P -- ie, open!
				sb.append("if(event.keyCode == 80 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+T -- ie, new tab!  
				sb.append("if(event.keyCode == 84 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+N -- ie 새창
				sb.append("if(event.keyCode == 78 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+A
				sb.append("if(event.keyCode == 65 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");

				//run code for CTRL+C
				sb.append("if(event.keyCode == 67 && isCtrl == true) {\n");
				sb.append("return false;\n");
				sb.append("}\n");
				sb.append("};\n");

				sb.append("</script>\n");

			}

			/*
			 * JQUERY
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/jquery-1.9.1.js?").append(time)
					.append("\"> </script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/jquery.blockUI.js?").append(time)
					.append("\"> </script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/jquery.fileDownload.js?").append(time)
					.append("\"> </script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/jquery-ui.js?").append(time)
					.append("\"> </script>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
					.append(path).append("css/jquery-ui.css?").append(time)
					.append("\" media=\"screen\"/>\n");



			/*
			 * MODAL WINDOW
			 */
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
					.append(path).append("window/dhtmlxwindows.css\"/>\n");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
					.append(path)
					.append("window/skins/dhtmlxwindows_dhx_skyblue.css\"/>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("window/dhtmlxcommon.js\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("window/dhtmlxwindows.js\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("window/dhtmlxcontainer.js\"></script>\n");
			
			/*
			 * map.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/map.js?").append(time)
					.append("\"></script>\n");

			/*
			 * rxe.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/").append(rxe).append("?").append(time)
					.append("\"></script>\n");
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
			.append("js/").append("ess_input.js").append("?").append(time)
			.append("\"></script>\n");
			/*
			 * form.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append(path)
					.append("js/").append(form).append("?").append(time)
					.append("\"></script>\n");
			
			/*
			 * popbox.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append("").append(path)
					.append("js/popbox.js").append("?").append(time)
					.append("\"></script>\n");
			
			/*
			 * report.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append("").append(path)
					.append("js/report.js").append("?").append(time)
					.append("\"></script>\n");
			
			
			/*
			 * jquery.scrollIntoView.js
			 */
			sb.append("<script type=\"text/javascript\" src=\"").append("").append(path)
					.append("js/jquery.scrollIntoView.js").append("?").append(time)
					.append("\"></script>\n");
			
			/*
			 * MENU STYLE
			 */
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
					.append(path).append("css/gray-blue.css\" media=\"screen\"/>\n");
			sb.append("</head>\n");

			if(useAbsolutePosition==false){
				sb.append("<body id=\"").append(bodyID)
						.append("\" bgcolor=\"#eeeeee\" class='bodyTip'>\n");
			}else{
				sb.append("<body id=\"").append(bodyID)
				.append("\" bgcolor=\"#ffffff\" class='bodyTip'>\n");
			}

			/*
			 * AJAX
			 */
			 sb.append("<div id=\"ajax_indicator\" style=\"display: none;\">\n");
			 sb.append("<div class=\"barMain\">\n");
			 sb.append("<div class=\"msg\" id=\"msg\">작업 처리 중입니다.</div>\n");
			 sb.append("<div class=\"captions\">작업이 완료되는 동안 잠시만 기다려 주십시오</div>\n");
			 sb.append("</div>\n");
			 sb.append("</div>\n");
			 
			/*
			 * 말풍선
			 */
			sb.append("<div class='popbox' id='popbox'>\n");
			sb.append("<div class='collapse'>\n");
			sb.append("<div class='box'>\n");
			sb.append("<div class='arrow'></div>\n");
			sb.append("<div class='arrow-border'></div>\n");
			sb.append("<p id='popboxDes'>\n");
			sb.append("</p>\n");
			sb.append("</div>\n");
			sb.append("</div>\n");
			sb.append("</div>\n");
			
			/*
			 * 마우스 오버레이 툴팁
			 */
			 sb.append("<div class='tooltiplayer' style='top: 263px; left: 1076px; display: none;'>");
			 sb.append("<div id='marrow' class='arrow'><img src='" + path + "/images/ess_input.png'/>&nbsp;<b>필수 입력 항목</b></div>");
			 sb.append("<div id='mtooltip'>.....</div>");
// 			sb.append("<div id='marrow'>.....</div>");
			 sb.append("</div>");

			sb.append("<ul class=\"gray-blue\">\n");

			return sb.toString();
		}

		public String MenuEnd(String path) {
			StringBuffer sb = new StringBuffer();
			String stab = "";
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("id=\"pdf\" href=\"javascript:void(0);\" onclick=\"doMenu('pdf');\" title=\"작성 완료\" class=\"tool-compl\">작성완료<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('first');\" title=\"첫 페이지로 이동\" class=\"tool-first\">첫 페이지<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('before');\" title=\"이전 페이지로 이동\" class=\"tool-before\">이전 페이지<span></span></a></li>\n"
			);
			
			tabindex++;
			sb.append(
				"<li><a").append(stab).append("id=\"hpageno\" href=\"#\" title=\"현재 페이지\" class=\"page\">현재 페이지<span id=\"pageno\" style=\"cursor:default\">-</span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('next');\" title=\"다음 페이지로 이동\" class=\"tool-next\">다음 페이지<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('last');\" title=\"마지막 페이지로 이동\" class=\"tool-last\">마지막 페이지<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('zoomout');\" title=\"축소하기\" class=\"tool-zoomout\">축소<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('zoomin');\" title=\"확대하기\" class=\"tool-zoomin\">확대<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('beforechk');\" title=\"이전 입력 항목 가기\" class=\"tool-beforechk\">이전<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('nextchk');\" title=\"다음 입력 항목 가기\" class=\"tool-nextchk\">다음<span></span></a></li>\n"
			);
			
			tabindex++;
			stab = " tabindex='" + String.valueOf(tabindex) + "' ";
			sb.append(
				"<li><a").append(stab).append("href=\"javascript:void(0);\" onclick=\"doMenu('close');\" title=\"뷰어 닫기\" class=\"tool-close\">닫기<span></span></a></li>\n"
			);

			sb.append("</ul>\n");
			
			sb.append("<div class=\"tool-notice\"> 모든 항목을 작성 후 <strong>'작성완료'</strong> 버튼을 클릭하여 제출하여 주시기 바랍니다.</div>\n");

			return sb.toString();
		}
	}%>