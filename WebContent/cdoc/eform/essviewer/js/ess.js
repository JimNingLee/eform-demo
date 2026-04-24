/******************************************
 * MS WINDOWS용
 ******************************************/

/*******************************************************************************
 * A4 용지: 755px X 1084px 1inch = 25.4mm = 96pixel 1mm = 3.78px
 ******************************************************************************/

var ver = "전자청약 뷰어 3.5";
var menu_offset = 98;
var dhxWins, help_win;
var currheight;
var move_page = false;
var max_page = 4;
var page_no = 1;
var scr_width;
var scr_height;
var pageWidth;
var pageHeight;
var jobID = "";
var mm = "";
var ee = "";
var zoom = 1;
var signHtml = "";
var dpath = "./";
var agent = navigator.userAgent.toLowerCase();
var chkindex = -1;
var chkscroll = true;
var ctlscrollnm = "";

/**
 * 이미 서명 및 전송이 완료 되었는지 확인용
 */
var submitted = false;

var isLayerContent = true;

/**
 * 마우스 이동시 툴팁이 사라지게 하는 메뉴의 높이
 */
var topMax = 90;

/**
 * 마우스를 따라다니는 툴팁을 사용할 경우
 * mouseOverlayStart = true이면 이전에 사용되는
 * 툴팁은 무시된다.
 */
var mouseOverlayStart = false;
var mouseOverlayEnable = true;



Browser = {
	os : function() {
		if (agent.indexOf("windows") > -1) {
			return "ms";
		} else if (agent.indexOf("android") > -1) {
			if (agent.indexOf("shw-m380w") > -1) {
				return "galexytab";
			} else if ((agent.indexOf("shw-m480w") > -1)
					|| (agent.indexOf("tablet") > -1)) {
				os = "galexytabnote";
			} else {
				return "android";
			}
		} else if (agent.indexOf("linux") > -1) {
			return "linux";
		} else if (agent.indexOf("iphone") > -1) {
			return "iphone";
		} else if (agent.indexOf("ipad") > -1) {
			return "ipad";
		} else if (agent.indexOf("mac") > -1) {
			return "mac";
		} else {
			return "unknown";
		}
	},

	bw : function() {
		if ((agent.indexOf("msie") > -1) || agent.indexOf("trident") > -1) {
			return "ie";
		} else if ((agent.indexOf("opera") > -1) || (agent.indexOf("opr") > -1)){
			return "opera";
		} else if (agent.indexOf("chrome") > -1) {
			return "chrome";
		} else if (agent.indexOf("safari") > -1) {
			return "safari";
		} else if (agent.indexOf("firefox") > -1) {
			return "firefox";
		} else {
			return "unknown";
		}
	},

	version : function() {
		if (this.os() == "ms" && this.bw() == "ie") {
			var start = agent.indexOf("msie");
			if(start>-1){
				var tmp = agent.substring(start);
				var end = tmp.indexOf(";");
				tmp = tmp.substring(0, end);
				tmp = tmp.substring(tmp.lastIndexOf(" "));
				return tmp;
			}else{
				start = agent.indexOf("rv:");
				var tmp = agent.substring(start);
				var end = tmp.indexOf(")");
				tmp = tmp.substring("rv:".length, end);
				return tmp;
			}
		} else {
			return 99999;
		}
	}
};

var os = Browser.os();
var bw = Browser.bw();
var version = Browser.version();

desc_map = new Map();
value_map = new Map();
hwsign_map = new Map();
page_map = new Map();
confirm_map = new Map();
text_map = new Map();

window.onresize = function() {
	ScreenSize();
	currheight = document.documentElement.clientHeight;
};

/**
 * 툴팁 텍스스를 레이어로 올릴 때 사용한다.
 */
/*****************************************************
$(function() {
	$( document ).tooltip();
});
*****************************************************/

$(document).ajaxStart(function(){
    $("#preloader").css("display", "block");
});

$(document).ajaxComplete(function(){
    $("#preloader").css("display", "none");
});

$(document).ready(function() {
	var method = "POST";
	if(os!="ms") method = "GET";

	// Popup 방식을 경우에만 적용
	essRun();

	$(document).on(
		"click",
		".rxFileDown",
		function() {
			var p = "Type=" + $(this).attr("href") + "&p=&SignOnly=1&curPageView=" + curPageView + "&jobID=" + jobID;
			p = encryptP(p, mm, ee);
			return false;
			$.fileDownload(
				exportSrc,
				{
					httpMethod : method,
					data : {
						__p: p[0],
						__q: p[1]
					},
					successCallback : function() {
						hideMask();
					},
					failCallback : function(responseHtml, url) {
						hideMask();
					},
					checkInterval : 10
				}
		);
		return false;
	});
});
	
function essRun() {
	showMask();
	var w = pageWidth + 50;
	var h0 = window.screen.availHeight;
	var h = 297 * 96 / 25.4;
	if (h > h0)
		h = h0;

	if (bw == "opera")
		h = h - 100;

	// iframe일 경우나 layer일 경우에는 rezie를 하지 않음
	if(window.frameElement == null && !isLayerContent)
	{
		window.top.resizeTo(w, h);
	}

	var canvas = document.getElementById("canvas");
	canvas.setAttribute('onscroll', "movescroll();");

	document.getElementById("canvas").style.overflowY = "scroll";

	setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");

	if(!(document.getElementById("version") == null || document.getElementById("version") == undefined))
	{
		document.getElementById("version").innerHTML = ver;
	}

	if(!(document.getElementById("hversion") == null || document.getElementById("hversion") == undefined))
	{
		document.getElementById("hversion").title = "ReportExpress Enterprise " + ver;
	}

	ScreenSize();
	
	var key = "";
	var _enc = "1"; // 0,1,2
	var p = "Type=" + $(this).attr("href") + "&jobID=" + jobID;
	p = encryptP(p, mm, ee);
	
	var essMapUrl = "ess_map.jsp";
	
	var edata;
	var data;
	var key;
	var jdata;
try{
	$.ajax({
		type: "POST",
		url: essMapUrl,
		data:{__p: p[0],__q: p[1],__e:_enc,isLayerContent:isLayerContent,sessionId:sessionId},
		contentType: "application/x-www-form-urlencoded; charset=utf-8",  //application/json
		dataType: "json",
		success: function (edata) {
			$.each(edata,function(j){
				key = edata[j].id;
				if (key == "errormsg") {
					hideMask();
					alert(edata[j].msg);
					self.close();
					return "-1";
				} else if(key == "encData") { 
					jdata = null;
					data = null;
					if (_enc == "1")  {
						jdata = DecryptTEA(p[2], trim(edata[j].msg));
						data = jQuery.parseJSON(jdata);
						jdata = null;
						
					} else {
						data = edata[j].msg;
					}
					edata[j] = null;
					$.each(data,function(i){
						key = data[i].id;
						var obj = document.getElementById(key);
						if (obj != null) {
							if (!key.indexOf("textconfirm")==0)
							{
								if(key.indexOf("noColor") == -1)
								{
									obj.style.color = "#FF0000";
								}
							} else {
								obj.style.color = "#252525";
							}
							/*
							if (!(key.indexOf("textconfirm")==0 && key.indexOf("_input")>0)){
								obj.style.background = "#FFFF00";
							}
							 */
						}
						if (data[i].confirm_map != null ) {
							confirm_map.put(key, data[i].confirm_map);
						}
						if (data[i].desc_map != null ) {
							desc_map.put(key, data[i].desc_map);
						}
						if (data[i].text_map != null ) {
							text_map.put(key, data[i].text_map);
						}
						if (data[i].value_map != null ) {
							value_map.put(key, data[i].value_map);
							if (key.indexOf("yesnoconfirm") == 0 || key.indexOf("radio") == 0) {
								if (obj != null && obj.checked) {
									value_map.put(key, obj.value);
								} else {
									value_map.put(key, "");
								}
							}else {
								value_map.put(key, data[i].value_map);
							}
							if (key.indexOf("textconfirm") == 0) {
								document.getElementById(key).value =Utf8.decode(Base64.decode(data[i].text_map));
							}
						}
						if (data[i].page_map != null ) {
							page_map.put(key, data[i].page_map);
						}
						data[i] = null;
					});
					data = null;
				}
			});

			reportStart(document.title);
			hideMask();

			rx_event();
			rx_init();
		},
		error: function (xhr, textStatus, errorThrown) {
			hideMask();
			alert("["+xhr+"]"+textStatus+"\n"+errorThrown);
		}
	});
	
}finally{
//	p = null;
	edata = null;
	data = null;
	key = null;
	jdata = null;
}
	

	/**
	 * ajax가 로딩되는 시간에 따라 타이머의 시간을 조절한다.
	 */
	var timeout = 100;
	self.setTimeout(function(){
		Initialize();
	},timeout);
	
	return false;
}

/**
 * 초기 입력항목 등을 설정한다.
 */
function Initialize(){
	/**
	 * input 타입이 text인 경우 이벤트 리스너 추가
	 */
	addEventText();
	
	if(mouseOverlayStart) mouseOverlay();
	
	/**
	 * 첫번째 입력 항목 풍선 도움말 표시
	 */
	showMouseOverlay("");
	
	/**
	 * 로딩과 동시에 첫 입력항목으로 이동
	 * 해당 위치로 스크롤되기 때문에 선택사항
	 */
	netxtTabObjectFirst();
	chkindex=1;
//	viewHighlight("button");
}

function viewHighlight(className){
	$('.'+className).each(function(index, item){
		$(item).css('background-image', 'url("../essviewer/css/essHighlight.png")');
		$(item).css('background-repeat', 'no-repeat');
		$(item).css('-webkit-background-size', 'contain');
		$(item).css('-moz-background-size', 'contain');
		$(item).css('-o-background-size', 'contain');
		$(item).css('background-size', 'contain');
		$(item).css('background-position', ($(item).css('text-align') != null) ? $(item).css('text-align') : 'center');
		$(item).css('behavior', 'url(../essviewer/pie/PIE.htc)');
	});
}

function movescroll() {
	try {
		var scrollTop = document.getElementById("canvas").scrollTop;
		page_no = Math.ceil(scrollTop / pageHeight + 1 / 3);
		page_no = page_no < 1 ? 1 : page_no;
		page_no = page_no > max_page ? max_page : page_no;
		setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");
		
		hidePopbox();
		chkscroll = true;
		
	} catch (e) {
		//alert("movescroll:"+e);
	}
}

function ScreenSize(){
	try{
		var doc_left;
		
		//  미리보기화면 위치
		doc_left = 6;
		scr_width = window.document.body.offsetWidth;
		scr_height = window.document.body.offsetHeight;
		doc_left = (scr_width - pageWidth) / 2 - 10;
		doc_left = (doc_left < 0) ? 6 : doc_left;

		//$("#doc").children().css("left", (($("#docs-editor-container").parent().width() - $($("#doc").children()[0]).width() - 18) / 2) + "px");
		document.getElementById("canvas").style.height = (document.documentElement.clientHeight-menu_offset) + "px";

		document.getElementById("doc").style.left = doc_left + "px";

		if(version<=7.0) document.getElementById("paper").style.overflowY  = "visible";
		document.getElementById("canvas").style.overflowY  = "scroll";

		if(!(parent == null || parent == undefined))
		{
			if(!(parent.rxInit == null || parent == undefined))
			{
				parent.rxInit(this);
				document.getElementById("canvas").style.height = (document.documentElement.clientHeight-menu_offset) + "px";
			}
		}

		pageWidth = $("#doc")[0].getBoundingClientRect().width;
		if(pageWidth == undefined)
		{
			pageWidth = $("#doc").width();
		}
	}catch(e){
		alert(e.description);
	}
}

function setToolbarText(id, text) {
	try {
		document.getElementById(id).innerHTML = text;
		setToolbarTitle("hpageno");
	} catch (e) {
	}
}

function setToolbarTitle(id) {
	try {
		document.getElementById(id).title = "현재 페이지는 " + max_page + " 페이지 중 " + page_no + " 페이지입니다.";
	} catch (e) {
	}
}

function Help() {
	var w, h;
	var base_url = dpath + "/essviewer/help/";
	var help_url = "";
	var help_caption = "ReportExpress™ Enterprise 페이지 설정 도움말";

	if (os == "android" || os == "galexytab" || version <= 6.0) {
		alert("해당 운영체제 또는 브라우저는 인쇄를 지원하지 않습니다.\n페이지 인쇄를 선택하시면 PDF로 저장됩니다.");
		return false;
	}

	if (bw == "ie" || bw == "opera") {
		dhxWins = new dhtmlXWindows();
		dhxWins.enableAutoViewport(false);
		dhxWins.attachViewportTo("essViewer");
		dhxWins.setImagePath(dpath + "/essviewer/window/imgs/");
	}

	if (bw == "ie") {
		help_caption = "Internet Explorer 페이지 설정 도움말";
		h = 720;
		w = 620;
		help_url = base_url + "ie_help.htm";
		showHelpWin(help_url, help_caption, w, h);
	} else if (bw == "firefox") {
		help_caption = "Firefox 페이지 설정 도움말";
		h = 680;
		w = 620;
		help_url = base_url + "firefox_help.htm";

		alert("Firefox의 경우 페이지 설정\n\n" + "용지 및 배경: 페이지 폭에 맞춤 설정\n"
				+ "여백 및 머리글/바닥글:\n"
				+ "     모든 여백은 0으로 설정하며 머리글/바닥글은 모두 [공백]으로 설정");
	} else if (bw == "opera") {
		h = 655;
		w = 620;
		help_caption = "Opera페이지 설정 도움말";
		help_url = base_url + "opera_help.htm";
		showHelpWin(help_url, help_caption, w, h);
	} else if (bw == "chrome") {
		h = 400;
		w = 560;
		help_caption = "Chrome 페이지 설정 도움말";
		help_url = base_url + "chrome_help.htm";
		alert("Chrome 페이지 설정\n\n" + "Chrome의 경우는 페이지 설정 옵션이 없습니다.");
	} else if (bw == "safari") {
		h = 400;
		w = 560;
		help_caption = "Safari 페이지 설정 도움말";
		help_url = base_url + "safari_help.htm";
		alert("Safari 페이지 설정\n\n"
				+ "Safari의 경우 브라우저 자체에서 페이지 설정 옵션이 적용되지 않습니다.");
	} else {
		h = 680;
		w = 620;
		help_caption = "Internet Explorer 페이지 설정 도움말";
		help_url = base_url + "ie_help.htm";
		showHelpWin(help_url, help_caption, w, h);
	}
}

function showHelpWin(url, caption, w, h) {
	help_win = dhxWins.createWindow("w1", 0, 0, w, h);
	help_win.setText(caption);
	help_win.button("park").hide();
	help_win.button("minmax1").hide();
	dhxWins.window("w1").centerOnScreen();
	dhxWins.window("w1").attachURL(url);
	dhxWins.window("w1").setModal(true);
}

function RXHelp() {
	var url = dpath + "/essviewer/help/help.htm";
	dhxWins = new dhtmlXWindows();
	dhxWins.enableAutoViewport(false);
	dhxWins.attachViewportTo("essViewer");
	dhxWins.setImagePath(dpath + "/essviewer/toolbar/imgs/");
	help_win = dhxWins.createWindow("w1", 0, 0, 620, 420);
	help_win.setText(ver + " 도움말");
	help_win.button("park").hide();
	help_win.button("minmax1").hide();
	dhxWins.window("w1").centerOnScreen();
	dhxWins.window("w1").attachURL(url);
	dhxWins.window("w1").setModal(true);
}

function Close() {
	top.window.opener = top;
	top.window.open('', '_parent', '');
	top.window.close();
}

function IssueHelp() {
	var url = dpath + "/essviewer/help/pdfhelp.htm";
	dhxWins = new dhtmlXWindows();
	dhxWins.enableAutoViewport(false);
	dhxWins.attachViewportTo("essViewer");
	dhxWins.setImagePath(dpath + "/smartcertviewer/toolbar/imgs/");
	help_win = dhxWins.createWindow("w1", 0, 0, 650, 420);
	help_win.setText("ReportExpress<sup>TM</sup> Enterprise eForm PDF 저장");
	help_win.button("park").hide();
	help_win.button("minmax1").hide();
	dhxWins.window("w1").centerOnScreen();
	dhxWins.window("w1").attachURL(url);

	dhxWins.window("w1").setModal(true);
}

function Issue(pwd) {
	dhxWins.window("w1").close();

	download("signedpdf", pwd);
}

function doMenu(mnu_id) {
	if (mnu_id == "rxhtml_help") {
		RXHelp();
	} else if (mnu_id == "close") {
		Close();
	} else if (mnu_id == "next") {
		page_no++;
		if (page_no > max_page) {
			alert("마지막 페이지입니다.");
			page_no = max_page;
		} else {
			move_page = true;
			window.location.href = "#RX_PAGE_ANCHOR_" + page_no;
			setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");
		}
	} else if (mnu_id == "before") {
		page_no--;
		if (page_no < 1) {
			alert("첫 페이지입니다.");
			page_no = 1;
		} else {
			move_page = true;
			window.location.href = "#RX_PAGE_ANCHOR_" + page_no;
			setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");
		}
	} else if (mnu_id == "first") {
		page_no = 1;
		move_page = true;
		window.location.href = "#RX_PAGE_ANCHOR_" + page_no;
		setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");
	} else if (mnu_id == "last") {
		page_no = max_page;
		move_page = true;
		window.location.href = "#RX_PAGE_ANCHOR_" + page_no;
		setToolbarText("pageno", page_no + "/" + max_page + "&nbsp;");
	} else if (mnu_id == "pdf") {
		if(submitted==false){
			download("pdf", '');
		}else{
			alert("이미 완료하셨습니다.");
		}
    } else if (mnu_id == "zoomin") {
		zoomin();
	} else if (mnu_id == "zoomout") {
		zoomout();
	} else if (mnu_id == "beforechk") {
		chkindex -- ;
		var chkFrm = beforeFormValidate(chkindex);
		if(chkFrm!=""){
			ChkStyle(chkFrm);
		}
	} else if (mnu_id == "nextchk") {
		chkindex ++ ;
		var chkFrm = nextFormValidate(chkindex);
		if(chkFrm!=""){
			ChkStyle(chkFrm);
		}
	}
//	else if (mnu_id == "essinput") {
//		signHtml = buildSignContents();
//		ShowContent(signHtml);
//	}
}

function viewInput(id){
//	hidePopbox();
	var obj = document.getElementById(id);
	var msg = "";

	if(id.indexOf("textconfirm")==0){
		msg = "반드시 <b>[" + Utf8.decode(Base64.decode(text_map.get(id))) + "]</b><br/>을(를) 보이는 글자와 똑같이 타이핑하셔서 입력하셔야 합니다.";
	}else	if(id.indexOf("buttonconfirm")==0 || id.indexOf("readconfirm")==0  || id.indexOf("sign")==0 ){
			msg = "반드시 <b>[" + obj.innerHTML + "]</b><br/>을(를) 확인(클릭)하셔야 합니다.";
	} else {
		msg = "<b>[" + desc_map.get(id) + "]</b><br/>을(를) 입력하셔야 합니다.";
	}

	msg	= replaceNewLine(msg);

//	self.setTimeout(function(){
		showPopbox(obj,msg);
//	}, 100);
}

//라이오버튼에 id 에 _1 일 들어가면 리턴값 반환을 위한 메소드 추가.
function viewRadioInput(id){
	hidePopbox();
	
	var obj = document.getElementById(id);
	var msg = "";
	
	if(id.indexOf("yesnoconfirm")==0){
		msg = "반드시 <b>[" + desc_map.get(id) + "]</b>br/>을(를) 동의 하셔야 합니다.";
	} else {
		msg = "<b>[" + desc_map.get(id) + "]</b><br/>을(를) 선택 하셔야 합니다.";
	}
	
//	self.setTimeout(function(){
		showPopbox(obj,msg);
//	}, 100);
}

function viewConfirm(obj,type){
	hidePopbox();
	var msg  = "";
	
	if(type == "sign"){
		msg = "<b>[" +obj.innerHTML + "]</b><br/>에 전자 서명";	
	}else{
		msg = "<b>[" +obj.innerHTML + "]</b><br/>을(를) 확인하셨습니다.";	
	}

	msg	= replaceNewLine(msg);
	
//	self.setTimeout(function(){
		showPopbox(obj,msg);
//	}, 100);
//	obj.disabled=true;
}

function hidePopbox(){
//	if($('.box').css('display') == 'block'){
//		$('.box').css('display','none');
//		$('.box').fadeOut("slow");
//	}																											
}

function showPopbox(obj, htmlDes, id){
	if(id=="" || id==undefined){
		id = obj.id;
	}
	showMouseOverlay(id);

	
	if (id != null) obj =  document.getElementById(id);

	var t2 = obj.getBoundingClientRect().top;
	var l2 = obj.getBoundingClientRect().left;
	var b2 = obj.getBoundingClientRect().bottom;
	var r2 = obj.getBoundingClientRect().right;
	var ctlwidth = 250;

	if(layerYn=="Y" || layerYn=="y")
	{
		b2 -= $("#docs-editor-container").parent()[0].getBoundingClientRect().top + menu_offset;
	}

	if(bw == "chrome")
	{
		b2 += 10;
	}

	if($("#doc").children()[0] != null)
	{
		l2 = l2 - $("#doc").children()[0].getBoundingClientRect().left;
	}

	obj.focus();
	
	if(mouseOverlayStart==true) return false;
	
	if($('.box').css('display') == 'block'){
	$('.box').css('display','none');
	$('.box').fadeOut("slow");
}
	
	if((l2+ctlwidth) > pageWidth){
		l2 = pageWidth - ctlwidth;
	}

	if($('.box').css('display') == 'none'){
		var th = '<table border=1><tr width="100%"><td style="text-align: center;"><b>&nbsp;&nbsp;&nbsp;미입력 사항</b></td>';
		    th += '<td style="text-align: right;"><img src="../essviewer/images/cross.png" valign="top" width="12"/>&nbsp;&nbsp;&nbsp;</td></tr>';
		    th += '<tr height="1px" bgcolor="#ffffff"><td colspan="2"></td></tr><tr>';
		    th += '<tr><td colspan="2" style="text-align: center;">';
		var tf = '</td></tr></table>';
		th = '';
		tf = '';

		htmlDes = getTooltipMessage(id);

		var opt = {
				targetId	: obj.id,
				width		: ctlwidth,
				left 		: l2-20,
				top 		: b2,
				arrowpos	: 20,
				color		: "#ffffa8",
//				autoClose	: 1000,
				html		: th + htmlDes + tf
		};
		$('.popbox').popbox(opt);
	}

}


function ChkStyle(_chkctlname) {
	if(_chkctlname.indexOf("span__")==0){
		return false;
	}
	//alert("______________"+_chkctlname+","+page_no);
	var obj = null;
	var scrollobj = null;
	var ctlt1 = 0;
	var ctlpageno = 0;

	if(!(undefined == _chkctlname || "" == _chkctlname))
	{
		obj = document.getElementById(_chkctlname);

		if (obj == null )
		{
			alert("객체가 존재하지 않습니다.(" +_chkctlname +")"); 
		}
		else
		{
			// 새로운 페이지이면 무조건 Scroll
			ctlpageno = parseInt(parseInt(page_map.get(_chkctlname),10)/10000);
			if (page_no != ctlpageno) {
				chkscroll = true;
				obj.scrollIntoView(true);
				scrollobj = document.getElementById("scrollpoint"+ctlpageno+"_1");
			}  
			ctlt1 = parseInt(page_map.get(_chkctlname),10) % 10000; //obj.getBoundingClientRect().top; //parseInt(scroll_map.get(_chkctlname),10);
			if (ctlt1 < pageHeight/2 ){
				if ("scrollpoint"+page_no+"_1" != ctlscrollnm) chkscroll = true;
				scrollobj = document.getElementById("scrollpoint"+ctlpageno+"_1");
			} else {
				if ("scrollpoint"+page_no+"_2" != ctlscrollnm) chkscroll = true;
				scrollobj = document.getElementById("scrollpoint"+ctlpageno+"_2");
			}
			
			if (chkscroll) {
				if (!(scrollobj ==null)){
					scrollobj.scrollIntoView(true);
					if(version<9) scrollobj.scrollIntoView();  ///$("#"+_chkctlname+"_1").scrollIntoView();
					ctlscrollnm = scrollobj.id;
				} else {
					if(window.frameElement != null && bw == "ie")
					{
						parentObj = parent;
						if(version<9)
						{
							obj.scrollIntoView(false);
						}
						parent.document.documentElement.scrollTop = $(obj).offset().top;
					}
					else
					{
						obj.scrollIntoView(true);
						if(version<9) obj.scrollIntoView();  ///$("#"+_chkctlname+"_1").scrollIntoView();
					}
					ctlscrollnm = obj.id;
				}
				chkscroll = false;
			}
			if(_chkctlname.indexOf("yesno") == 0 || _chkctlname.indexOf("radio") > -1)
			{
				viewRadioInput(_chkctlname);
			}
			else
			{
				viewInput(_chkctlname);
				
				if(_chkctlname.indexOf("textconfirm")==0){ 
					document.getElementById(_chkctlname+"_input").focus();
				} else {
					if (!obj.disabled ) obj.focus();
				}
			}
		}
	}
	else
	{
		alert("입력할 항목이 없습니다.");
	}
}

function download(type, pwd) {
	var chkFrm = checkFormValidateSend();

	if(chkFrm!="" && chkFrm.indexOf("span_")!=0){
		ChkStyle(chkFrm);
	}else{
		hidePopbox();
		document.getElementById("pdf").style.display = "none";
		submitted = true;
		if(signType=="0"){
			signHtml = buildSignContents();
			ShowContent(signHtml);
		}else{
			contentSign();
		}
	}
}

function contentCancel(){
	 dhxWins.window("w1").close();
	 document.getElementById("pdf").style.display = "";
	 submitted = false;
}
/*
 * 변수 signHtml을 전자서명 한다.
 */
function contentSign(){
	if(signType=="0") dhxWins.window("w1").close();
	showMask();

	var p = buildForm();
	
	if(signType=="0"){
		/*
		 * 입력 값에만 전자서명 하는 경우
		 * signHtml에 전자서명한다.
		 */
		p += "signeddata=" + encodeURIComponent(signHtml);
	}else{
		p += "signeddata=";
	}

	var pp = encrypt(p, mm, ee);
	
	var actionUrl = dpath + "ess_rxprint.jsp";
	f = makeForm("essForm", "");
	f.appendChild(addData('__p', pp[0]));
	f.appendChild(addData('__q', pp[1]));
	
	$.ajax({
		type: "POST",
		url: actionUrl,
		dataType: "json",
		data: $('#essForm').serialize(),
		success: function(msg){
			var result = msg.result;
			var error = msg.error;
			var signingdata = msg.signingdata;
			if(result=="OK"){
				if(signingdata==""){
					getPDF("");
				}else{
					/*
					 * 문서 전체에 전자서명하는 경우
					 * 서버에서 전달받은 signingdata에 전자서명한다.
					 */
					getPDF(signingdata);
				}
			}else{
				alert(error);
				hideMask();
			}
		}
	});
}

function getPDF(data){
	setInterval(checkDownload,500);
	var actionUrl = dpath + "ess_pdfservice.jsp";
	f = makeForm("essPdf", actionUrl);
	f.appendChild(addData('signeddata', data));
	f.submit();
}

function ShowContent(content) {
	var ss = "<div style=\"margin: 10px; align: right;\">";
	ss = "<p align=\"right\">";
	ss += "<input type=\"button\" value=\"확 인\" onclick=\"contentSign();\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	ss += "<input type=\"button\" value=\"취 소\" onclick=\"contentCancel();\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>";

	content = "<div style=\"height: 100%; overflow: auto;\">" + content + ss + "</div>";
	
	dhxWins = new dhtmlXWindows();
	dhxWins.enableAutoViewport(false);
	dhxWins.attachViewportTo("essViewer");
	dhxWins.setImagePath(dpath + "../essviewer/window/imgs/");
	help_win = dhxWins.createWindow("w1", 0, 0, 700, 450);
	help_win.setText("전자서명 내용 확인");
	help_win.button("park").hide();
	help_win.button("minmax1").hide();
	help_win.button("close").hide();
	dhxWins.window("w1").centerOnScreen();
	dhxWins.window("w1").attachHTMLString(content);

	dhxWins.window("w1").setModal(true);
}

function str_replace(src, needle, replacement) {  
    var temp = src.split(needle);  
    return temp.join(replacement);  
}

var setCssForFullWindowPdf = function (){
	
	var html = document.getElementsByTagName("html");
	if(!html){ return false; }
	
	var html_style = html[0].style,
		body_style = document.body.style;
	
	html_style.height = "100%";
	html_style.overflow = "hidden";	
	body_style.margin = "0";
	body_style.padding = "0";
	body_style.height = "100%";
	body_style.overflow = "hidden";
	
};

function checkDownload(){
	if (document.cookie.indexOf("essDownload=true") != -1) {
		document.cookie = "essDownload=; expires=" + new Date(1000).toUTCString() + "; path=/";
		hideMask();
	}
}

function ClearSession() {
	window.location.href = "/eForm/servlet/ExportPrint?Type=Clear";
}

function openDrillDown(hyperlink) {
	var f = "width=1050,height=800,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,copyhistory=no,resizable=yes,left=0,top=0";
	window.open("/eForm/" + hyperlink, "DrillDown", f);
}

$(function() {
	//$( document ).tooltip();
});

String.prototype.trim = function(chars) {
	if (chars) {
		var str = "[" + chars + "\\s]+";
		return this.replace(new RegExp(str, "g"), "");
	}

	return this.replace(/^\s+|\s+$/g, "");

}
String.prototype.ltrim = function(chars) {
	if (chars) {
		var str = "^[" + chars + "\\s]+";
		return this.replace(new RegExp(str, "g"), "");
	}

	return this.replace(/^\s+/, "");
}
String.prototype.rtrim = function(chars) {
	if (chars) {
		var str = "[" + chars + "\\s]+$";
		return this.replace(new RegExp(str, "g"), "");
	}

	return this.replace(/\s+$/, "");
}

function showMask() {
	$.blockUI({
		message : $('#ajax_indicator'),
		fadeIn : 700,
		fadeOut : 700,
		showOverlay : true,
		centerY : true,
		css : {
			width : '300px',
			border : 'none',
			height : '60px'
		},
		overlayCSS : {
			backgroundColor : '#B3B3B3'
		}
	});
}

function hideMask() {
	$.unblockUI();
}

//F5, ctrl  새로고침 막기
//$(document).keydown(function (e) {
//            if (e.which === 116) {
//                if (typeof event == "object") {
//                    event.keyCode = 0;
//                }
//                return false;
//            } else if ( e.ctrlKey) {
//                return false;
//            } 
//}); 

//	$(document).ready(function() {
//		//document.body.style.zoom = 1.00;
//		//document.body.style.zoom = "100%";
//		//document.body.style.zoom = "normal";
//	});
	
	function fnczoom(){
		try {
			var tag = "doc";
			var size = zoom;
			if (jQuery.browser.msie) {
				var _body = document.getElementById(tag); //document.body;
				_body.style.zoom = size;
			} else {
				var _body = $('#' + tag);
				
				_body.css('-webkit-transform','scale(' + (size) + ')');
				_body.css('-webkit-transform-origin','0 0');
				_body.css('-moz-transform','scale(' + (size) + ')');
				_body.css('-moz-transform-origin','0 0');
				_body.css('-o-transform','scale(' + (size) + ')');
				_body.css('-o-transform-origin','0 0');
			}
		}
		catch(e) {
			//alert("fnczoom ==> " + e.message);
		}
	}

	function zoomout(){
		try {
			var size = zoom;

			if(size <= 1.00) zoom=1;
			else zoom -= 0.25;

			fnczoom();
		}
		catch(e) {
			//alert("zoomout ==> " + e.message);
		}
	}
	
	function zoomin(){
		try {
			var size = zoom;

			if(size >= 2.00) zoom=2;
			else zoom += 0.25;

			fnczoom();
		}
		catch(e) {
			//alert("zoomin ==> " + e.message);
		}
	}

	function replaceNewLine(msg)
	{
		msg	= msg.replace(/<BR>/g, "");
		msg	= msg.replace(/<br>/g, "");
		msg	= msg.replace(/<BR\/>/g, "");
		msg	= msg.replace(/<br\/>/g, "");
		
		return msg;
	}
