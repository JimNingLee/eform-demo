
/**
 * 체크박스와 라디오 버튼의 경우 span 태그에 
 * 자동으로 추가된 keypress 이벤트 핸들러 
 * @param event
 * @param id
 * @returns {Boolean}
 */
function doEvent(event, id){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 var checked = document.getElementById(id).checked;
			 document.getElementById(id).checked = !checked;
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * 버튼 타입 keypress 이벤트 핸들러
 * doBtnClick 이벤트와 동일
 * @param event
 * @param obj
 * @returns {Boolean}
 */
function doBtnKeyPress(event, obj){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 doBtnClick(obj);
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * type이 sign인 경우 span 태그에 자동으로 추가되는
 * keypress 이벤트 핸들러로 doSign과 동일
 * @param event
 * @param obj
 * @returns {Boolean}
 */
function doSignKeyPress(event, obj){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 doSign(obj);
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * type이 sign인 경우 span 태그에 자동으로 추가되는
 * keypress 이벤트 핸들러로 doSign과 동일
 * @param event
 * @param obj
 * @param popup
 * @returns {Boolean}
 */
function doSignKeyPress(event, obj, popup){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 doSign(obj, popup);
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * 버튼으로 처리되는 span 태그에 자동으로 추가되는
 * keypress 이벤트 핸들러로 doClick와 동일
 * @param event
 * @param obj
 * @param popup
 * @returns {Boolean}
 */
function doClickKeyPress(event, obj,popup){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 doClick(obj, popup);
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * 버튼으로 처리되는 span 태그에 자동으로 추가되는
 * keypress 이벤트 핸들러로 doClick과 동일
 * @param event
 * @param obj
 * @param id
 * @returns {Boolean}
 */
function doClickKrePress(event, obj, id){
	try{
		 var keyCode = event.keyCode;
		 var chCode = event.charCode;
		 if(keyCode==13 || keyCode==32 || chCode==32){
			 event.preventDefault ? event.preventDefault() : event.returnValue = false;
			 doClick(obj, id);
		 }
		 return false;
	}catch(e){
		alert(e.description);
	}
}

/**
 * input type=text인 경우 keypress 이벤트 핸들러로 엔터키 입력시
 * 다음 미입력 항목으로 이동
 * @param event
 * @param obj
 */
function modifyText(event, obj){
	var keyCode = event.keyCode;
	if(keyCode==13){
		$("#"+obj.id).removeClass("textfiled");
		netxtTabObject(obj);
	}
}

/**
 * input type=text인 경우 keypress 이벤트 리스너 추가
 */
function addEventText(){
	var keys = value_map.keys();
	for(i=0; i<keys.length; i++){
		var key = keys[i];
		var obj = document.getElementById(key);
		
		var type = 	$("#"+key).attr('type');
		if(type=="text"){
			if (obj.addEventListener) {
				obj.addEventListener("keypress", function(){modifyText(event,this)}, false);
			} else if (obj.attachEvent)  {
				obj.attachEvent("onkeypress", function(){modifyText(event,this)});
			}else{
				alert("이벤트 리스너를 지원하지 않습니다.");
			}
			if(key.indexOf("textconfirm") == 0){
				var obj2 = document.getElementById(key + "_input");
				if (obj2.addEventListener) {
					obj2.addEventListener("keypress", function(){modifyText(event,this)}, false);
				} else if (obj2.attachEvent)  {
					obj2.attachEvent("onkeypress", function(){modifyText(event,this)});
				}else{
					alert("이벤트 리스너를 지원하지 않습니다.");
				}
			}
		}
	}
}

/**
 * 엔진에서 체크박스와 라디오버튼의 경우 자동으로 추가되는
 * onfocus 이벤트 핸들러
 * @param obj
 */
function myRadioCheckFocus(obj){
	var spanID = "span_" + obj.id;
	var obj = document.getElementById(spanID);
	obj.focus();
}

/**
 * window.onload 이벤트에서 처음으로 호출되는 다음 미입력 항목 이동 함수
 * @returns
 */
function netxtTabObjectFirst(){
	var chkFrm = checkFormValidateSend();
	if(chkFrm!=""){
		ChkStyle(chkFrm);
		showMouseOverlay(chkFrm)
		return chkFrm;
	}
}

/**
 * 다음 미입력 항목으로 이동
 * @returns
 */
function netxtTabObject(){
	var chkFrm = checkFormValidateSend();
	if(chkFrm!="" && chkFrm.indexOf("span_")!=0){
		ChkStyle(chkFrm);
		showMouseOverlay(chkFrm)
		return chkFrm;
	}else{
		showMouseOverlay("__end__");
	}
}

/**
 * 마우스 포인터를 따라 입력 도움말을 보여주기 위한 이벤트
 */
function mouseOverlay(){
	$('.bodyTip').mouseover(function(e) {

		$(this).mousemove(function(e) {
			if(mouseOverlayEnable==false){
				e.preventDefault ? e.preventDefault() : e.returnValue = false;
				return false;
			}
			var top = e.pageY;
			if(top>topMax){
				if(($(window).width()/2) <= e.pageX){
					var t = e.pageY - 15;			
					var l = e.pageX - $(".tooltiplayer").width()-40; 
					
					$('.tooltiplayer')
						.removeClass("tooltiplayerL")
						.removeClass("tooltiplayerR")
						.addClass("tooltiplayerR");			
				}		
				if(($(window).width()/2) > e.pageX){
					var t = e.pageY - 15;			
					var l = e.pageX + 25; 
					
					$('.tooltiplayer')
						.removeClass("tooltiplayerL")
						.removeClass("tooltiplayerR")
						.addClass("tooltiplayerL");
				}
				$('.tooltiplayer').css({ "top": t, "left": l }).show();
				e.preventDefault ? e.preventDefault() : e.returnValue = false;
				return false;
			}else{
				$('.tooltiplayer').hide(); //레이어 숨기기
			}
		});
		e.preventDefault ? e.preventDefault() : e.returnValue = false;
		return false;
	}); 
	$('.bodyTip').mouseout(function(e) {
		$('.tooltiplayer').hide(); //레이어 숨기기
		e.preventDefault ? e.preventDefault() : e.returnValue = false;
		return false;
   	});
}

/**
 * 마우스 포인터를 따라 이동하는 레이어 숨기기
 * 이 함수는 입력항목이 없는 경우에 호출
 */
function hideMouseOverlay(){
	$('.bodyTip').mouseout(function() {
		$('.tooltiplayer').hide(); //레이어 숨기기
		event.preventDefault ? event.preventDefault() : event.returnValue = false;
		return false;
   	});
}

/**
 * idkey에 해당하는 입력 도움말을 보여준다
 * @param idkey
 * @returns {Boolean}
 */
function showMouseOverlay(idkey){
	var id = "";
	if(idkey==""){
		var keys = value_map.keys();
		for (i = 0; i < keys.length; i++) {
			key = keys[i];
			if (key != "customer" && key != "product" && key != "undefined") {
				id = key;
				break;
			}
		}
	}else{
		id = idkey;
	}

	if(id=="" || id=="__end__"){
		mouseOverlayEnable = false;
		hideMouseOverlay();
		return false;
	}
	mouseOverlayEnable = true;
	if(id.indexOf("span_")==0){
		id = id.substring(5);
	}
	var type = 	$("#"+id).attr('type');
	if (type == null)type= "";
	
	var obj = document.getElementById(id);
	var msg = "";

	if (type.indexOf("checkbox") == 0 || type.indexOf("radio") == 0) {
		if(id.indexOf("yesnoconfirm")==0){
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 동의 하셔야 합니다.";
		} else {
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 입력하셔야 합니다.";
		}
	}else{
		if(id.indexOf("textconfirm")==0){
			msg = "<font color='red'><b>[" + Utf8.decode(Base64.decode(text_map.get(id))) + "]</b></font><br/>반드시 보이는 글자와 똑같이 타이핑하셔서 입력하셔야 합니다.";
		}else if(id.indexOf("buttonconfirm")==0 || id.indexOf("readconfirm")==0  || id.indexOf("sign")==0 ){
				msg = "<font color='red'><b>[" + obj.innerHTML + "]</b></font><br/>반드시 확인(클릭)하셔야 합니다.";
		}else{
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 입력하셔야 합니다.";
		}
	}

	msg = "<br/>" + msg;
	
	var tooltipobj = document.getElementById("mtooltip");
	tooltipobj.innerHTML = msg;
	
//	var sobj = document.getElementById("marrow");
//	sobj.innerHTML = "<img src='../essviewer/images/ess_input.png'/>&nbsp;<font color='red'><b>[" + desc_map.get(id) + "]</b></font>";
}

/**
 * idKey에 대한 도움말을 생성한다.
 * @param idkey
 * @returns {String}
 */
function getTooltipMessage(idkey){
	var id = "";
	if(idkey==""){
		var keys = value_map.keys();
		for (i = 0; i < keys.length; i++) {
			key = keys[i];
			if (key != "customer" && key != "product" && key != "undefined") {
				id = key;
				break;
			}
		}
	}else{
		id = idkey;
	}

	var type = 	$("#"+id).attr('type');
	if (type == null)type= "";
	
	var obj = document.getElementById(id);
	var msg = "";

	if (type.indexOf("checkbox") == 0 || type.indexOf("radio") == 0) {
		if(id.indexOf("yesnoconfirm")==0){
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 동의 하셔야 합니다.";
		} else {
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 입력하셔야 합니다.";
		}
	}else{
		if(id.indexOf("textconfirm")==0){
			msg = "<font color='red'><b>[" + Utf8.decode(Base64.decode(text_map.get(id))) + "]</b></font><br/>반드시 보이는 글자와 똑같이 타이핑하셔서 입력하셔야 합니다.";
		}else if(id.indexOf("buttonconfirm")==0 || id.indexOf("readconfirm")==0  || id.indexOf("sign")==0 ){
				msg = "<font color='red'><b>[" + obj.innerHTML + "]</b></font><br/>반드시 확인(클릭)하셔야 합니다.";
		}else{
			msg = "<font color='red'><b>[" + desc_map.get(id) + "]</b></font><br/>반드시 입력하셔야 합니다.";
		}
	}

	return msg;
}

/**
 * onmouseover 이벤트 핸들러
 * @param id
 */
function MouseOver(id){
	console.log("MouseOver [" + id + "]");
	console.log(document.activeElement.tabIndex);
	var sid = "";
	if(id.indexOf("_input")>-1){
		sid = id.substring(0, id.indexOf("_input"));
	}else{
		sid = id;
	}
	var obj = document.getElementById(sid);
	var msg = getTooltipMessage(sid);
	showPopbox(obj, msg);
}

/**
 * onmouseout 이벤트 핸들러
 * @param id
 */
function MouseOut(id){
	console.log("MouseOut [" + id + "]");
	if($('.box').css('display') == 'block'){
		$('.box').css('display','none');
		$('.box').fadeOut("slow");
	}
}

/**
 * onfocus 이벤트 핸들러
 * @param sid
 */
function Focus(sid){
	console.log("Focus [" + sid + "]");
	var id = "";
	if(sid.indexOf("span_")>-1){
		id = sid.substring(sid.indexOf("span_")+"span_".length);
	}else{
		id = sid;
	}

	var obj = document.getElementById(sid);
	if(obj==undefined){
		obj = document.getElementById(id);
	}
	var msg = getTooltipMessage(id);
	
	showPopbox2(id, msg);
}

/**
 * mouse over나 focus 시에 툴팁을 보여줌
 * @param id
 * @param htmlDes
 * @returns {Boolean}
 */
function showPopbox2(id, htmlDes){
	if(mouseOverlayStart==true) return false;
	var obj =  document.getElementById(id);

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