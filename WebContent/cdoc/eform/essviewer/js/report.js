/**
 * 서식 고유 함수 
 */

// 반드시 존재하여야 함
function reportStart(docname) {
	var msg = "";
	if (docname.indexOf("4-202-0190")>-1) {
		msg =docname + "\" 을(를) 확인하셨습니까?";
		report42020190_radio("","radio101");
		doCheck("","checkbox3");
	}

	reportEvent();
	if(isHelpPopup)
	{
		EssHelp();	
	}

}

//반드시 존재하여야 함
function reportEvent() {
	if (document.domain.indexOf("woori.com")>-1){
		document.domain = "woori.com";
		opener.refreshTime();
	}
}

//반드시 존재하여야 함
function reportFormValidateSend(obj) {
	var chkValid = false;
	var docname = document.title;
	if (obj != null){
		if (docname.indexOf("4-202-0190")>-1) {
			if ("text45" == obj.id ) {
				if (document.getElementById("checkbox3").disabled || !document.getElementById("checkbox3").checked) {
					chkValid = true; //pass
				}
			} else if ("sign100" == obj.id) {
				if (document.getElementById("radio102").checked) {
					chkValid = true; //pass
				}
			}else if ("text100" == obj.id) {
				if (document.getElementById("checkbox1").disabled || !document.getElementById("checkbox1").checked) {
					chkValid = true; //pass
				}
			}else if ("text200" == obj.id) {
				if (document.getElementById("checkbox2").disabled || !document.getElementById("checkbox2").checked) {
					chkValid = true; //pass
				}
			}
		}
	}
	return chkValid;
}

//반드시 존재하여야 함
function reportdo(obj) {
	var docname = document.title;
	if (obj != null){
		if (docname.indexOf("4-202-0190")>-1) {
			if ("checkbox3" == obj.id ) {
				if (document.getElementById("checkbox3").disabled || !document.getElementById("checkbox3").checked) {
					document.getElementById("text45").value = "  ";
					ctl_enable("","text45",false);
				} else {
					ctl_enable("","text45",true);
				}
			} 
		}
	}
}


//반드시 존재하여야 함
function reportFormValidate2(obj) {
	var chkValid = true;
	var docname = document.title;
	if (docname.indexOf("4-202-0190")>-1) {
		if ("sign100" == obj.id ) {
			if (document.getElementById("radio102").checked) {
				chkValid = false;
			}
		}
	}
	return chkValid;
}

//반드시 존재하여야 함
function setjobProcess(val2){
	if (val2) {
		//가능
		//opener.setvalid("0");
	} else {
		//불가능
		//opener.setvalid("1");
	}
}

var predhxWins, prehelp_win;

function EssHelp() {
	var url = "./help/esshelp.htm";
	var imgUrl = "../img/efdoc/window/";
	var bodyId = $("body").attr("id");

	if(bodyId == null || bodyId == undefined)
	{
		$("body").attr("id", "essViewer");
		bodyId = "essViewer";
	}

	if(isLayerContent)
	{
		url = "viewer/help/esshelp.htm";
		imgUrl = "img/efdoc/window/";
	}

	dhxWins = new dhtmlXWindows();
	dhxWins.enableAutoViewport(false);
	dhxWins.attachViewportTo(bodyId);
	dhxWins.setImagePath(imgUrl);
	help_win = dhxWins.createWindow("w1", 0, 0, 650, 540);
	help_win.setText("스마트대출서류접수서비스 도움말");
	help_win.button("park").hide();
	help_win.button("minmax1").hide();
	dhxWins.window("w1").centerOnScreen();
	dhxWins.window("w1").attachURL(url, true);
	dhxWins.window("w1").setModal(true);

	// dhtmlXWindows Scroll 생성
	dhtmlXWinScroll();
}

// dhtmlXWindows Scroll 생성
function dhtmlXWinScroll()
{
	var len = $("body").find(".dhtmlx_wins_body_inner").length;
	var winsBody = null;

	for(var i = 0; i < len; i++)
	{
		winsBody = $("body").find(".dhtmlx_wins_body_inner")[i];

		if($(winsBody).children().length > 0)
		{
			$($(winsBody).children()[0]).css("overflow", "auto");
		}
	}
}


function preView() {
	var url = "./help/preview.htm";
	predhxWins = new dhtmlXWindows();
	predhxWins.enableAutoViewport(false);
	predhxWins.attachViewportTo("essViewer");
	predhxWins.setImagePath("../img/efdoc/window/");
	prehelp_win = predhxWins.createWindow("w2", 0, 0, 1200, 800);
	prehelp_win.setText("스마트대출서류접수서비스 견양 미리 보기");
	prehelp_win.button("park").hide();
	prehelp_win.button("minmax1").hide();
	predhxWins.window("w2").centerOnScreen();
	predhxWins.window("w2").attachURL(url, true);
	predhxWins.window("w2").setModal(true);

	// dhtmlXWindows Scroll 생성
	dhtmlXWinScroll();
}

function report42020190_radio(obj,id,val) {
	var chkArray = new Array("sign100","radio103","radio104","radio105","radio106","radio107",
			"text45","text100","text200","checkbox1","checkbox2","checkbox3");
	if (id != null) {
		obj = document.getElementById(id);
	}
	if(obj != null && obj.id == "radio101" && obj.checked){
		for (var i=0, maxC=chkArray.length; i<maxC; i++){
			$("#"+chkArray[i]).attr('class','confirmcss');
			$("#"+chkArray[i]).attr('disabled',false);
			$("#"+chkArray[i]).css('background',"#FFFF00");
//			document.getElementById(chkArray[i]).style.background="#FFFF00";
		}
		$("#sign100").attr('onClick','doSign(this);');
		$("#nosign_check_name").html(desc_map.get("nosign_check_name")); 
	}else {   //radio102
		$("#sign100").html( "(인)"); 
		$("#nosign_check_name").html(""); 
		for (var i=0, maxC=chkArray.length; i<maxC; i++){
			$("#"+chkArray[i]).attr('class','clearcss');
			$("#"+chkArray[i]).attr('disabled',true);
			$("#"+chkArray[i]).css('background',"none");
//			document.getElementById(chkArray[i]).style.background="none";
		}
		$("#signimg_sign100").css('display',"none");
		$("#sign100").attr('onClick','');
		value_map.put("sign100", "");
	}
	
	if(obj != null) doRadio(obj);

}

function ctl_enable(obj,id,val) {

	if (id != null) {
		obj = document.getElementById(id);
	}
	if(obj != null ){
		if (val) {
			$("#"+obj.id).attr('class','confirmcss');
			$("#"+obj.id).attr('disabled',false);
			$("#"+obj.id).css('background',"#FFFF00");
		}else {   
			$("#"+obj.id).attr('class','clearcss');
			$("#"+obj.id).attr('disabled',true);
			$("#"+obj.id).css('background',"none");
		}
	}
}

/**
 * 서식별 이벤트 적용 최초 실행 시 한번 실행.
 */
function rx_event()
{
	if(isLayerContent)
	{

		var keys = value_map.keys();
		
		for (var i = 0; i < keys.length; i++) {
			var key = keys[i];

			if(key.indexOf("textconfirm") > -1)
			{
				$("#" + key).css("color", "#cccccc");
				$("#" + key).attr("disabled", false);
				$("#" + key).attr("readonly", true);
				$("#" + key).on("click", doConfirm);
			}
		}
	}

	if(value_map.get("product").indexOf("1203003") > -1)
	{
		$("#checkbox01").on("click", rx_1203003);
		$("#checkbox05").on("click", rx_1203003);
		$("#checkbox06").on("click", rx_1203003);
		$("#checkbox09").on("click", rx_1203003);
		$("#checkbox12").on("click", rx_1203003);
	}
}

/**
 * 서식별 초기화 최초 실행 시 한번 실행.
 */
function rx_init()
{
	$("#docs-editor-container").find("input").attr("mask", "@-.");

	if(value_map.get("product").indexOf("1101003") > -1)
	{
		var keys = value_map.keys();
		var id = "rdAgree";
		
		for (var i = 0; i < keys.length; i++) {
			var key = keys[i];
	
			if(!(key == "yesnoRdAgree_01" || key == "yesnoRdAgree_02"))
			{
				if(key.indexOf(id) > -1)
				{
					if(key.indexOf("textconfirm") > -1)
					{
						$("#" + key + "_input").attr("disabled", true);
						$("#" + key).off("click");
						$("#" + key).removeClass("textfiled");
					}
					else
					{
						$("#" + key).attr("disabled", true);
						$("#" + key).removeClass("radiobutton");
					}
				}
			}
		}
	}
	else if(value_map.get("product").indexOf("1202007") > -1)
	{
		if(!$("#ness_checkboxfree").is(":checked"))
		{
			$("#notchecktext1").css("border", "0px");
			$("#notchecktext1")[0].style.setProperty("background", "rgb(255, 255, 255)", "important");
			$("#notchecktext1").attr("disabled", true);
		}

		$("#ness_checkboxfree").on("click", function()
		{
			if(this.checked)
			{
				$("#notchecktext1").css("border", "2px solid #EF9F1B");
				$("#notchecktext1")[0].style.setProperty("background", "rgb(255, 255, 0)", "important");
				$("#notchecktext1").attr("disabled", false);
				$("#notchecktext1").focus();
			}
			else
			{
				$("#notchecktext1").css("border", "0px");
				//$("#notchecktext1").css("background", "rgb(255, 255, 255)", "important");
				$("#notchecktext1")[0].style.setProperty("background", "rgb(255, 255, 255)", "important");
				$("#notchecktext1").attr("disabled", true);
				$("#notchecktext1").val("");
			}
		});
	}
	else if(value_map.get("product").indexOf("1203003") > -1)
	{
		// 실행 통지
		rx_disabled_check("radioAgree01_1", true);
		rx_disabled_check("radioAgree01_2", true);
		rx_disabled_check("radioAgree01_3", true);
		
		// 이자납기일
		rx_disabled_check("radioAgree02_1", true);
		rx_disabled_check("radioAgree02_2", true);

		// 이자납기일 및 변동금리 사전고지
		rx_disabled_check("radioAgree03_1", true);
		rx_disabled_check("radioAgree03_2", true);

		// 변동금리부대출
		rx_disabled_check("radioAgree04_1", true);
		rx_disabled_check("radioAgree04_2", true);
	}
}

function rx_1201001()
{
	var o = $("#inpDiv").find("input");
	var len = o.length;

	for(var i = 0; i < len; i++)
	{
		if(o[i].id.indexOf("checkbox") == -1)
		{
			if(o[i].id == "noColorId01_inp" || o[i].id == "noColorId02_inp")
			{
				$("#" + o[i].id.replace("_inp", "")).html(o[i].value + "%");
			}
			else
			{
				$("#" + o[i].id.replace("_inp", "")).html(o[i].value);
			}
		}
		else
		{
			if(o[i].checked)
			{
				$("#" + o[i].id.replace("_inp", "")).attr("checked", o[i].checked);
			}
		}
	}

	$("#inpDiv").remove();
}

function rx_1203003()
{
	if(this.id == "checkbox01")
	{
		// 실행 통지
		rx_disabled_check("radioAgree01_1", !this.checked);
		rx_disabled_check("radioAgree01_2", !this.checked);
		rx_disabled_check("radioAgree01_3", !this.checked);
		
	}
	if(this.id == "checkbox05" || this.id == "checkbox06")
	{
		// 이자납기일
		rx_disabled_check("radioAgree02_1", !($("#checkbox05")[0].checked || $("#checkbox06")[0].checked));
		rx_disabled_check("radioAgree02_2", !($("#checkbox05")[0].checked || $("#checkbox06")[0].checked));
	}
	if(this.id == "checkbox09")
	{
		// 이자납기일 및 변동금리 사전고지
		rx_disabled_check("radioAgree03_1", !this.checked);
		rx_disabled_check("radioAgree03_2", !this.checked);
	}
	if(this.id == "checkbox12")
	{
		// 변동금리부대출
		rx_disabled_check("radioAgree04_1", !this.checked);
		rx_disabled_check("radioAgree04_2", !this.checked);
	}
}

function rx_disabled_check(id, isDisabled)
{
	if(isDisabled)
	{
		$("#" + id).attr("checked", false);
		$("#" + id).attr("disabled", true);
		$("#" + id).css("background", "rgb(255, 255, 255)");
		$("#" + id).css("border", "0px");
	}
	else
	{
		$("#" + id).attr("disabled", false);
		$("#" + id).css("background", "rgb(255, 255, 0)");
		$("#" + id).css("border", "#ef9f1b 1px solid");
	}
}

/**
 * 선택에 따라 하위 컴포넌트 diabled 처리
 * @param obj
 */
function doRadioAgree(obj)
{
	hidePopbox();
	reportEvent();

    var docOption = document.getElementsByName(obj.name);
    for (var i = 0; i < docOption.length; i++) {
        if (docOption[i].checked) {
        	value_map.put(docOption[i].id, docOption[i].value);
        }
    }

	var keys = value_map.keys();
	var id = obj.id;
	var groupId = id.substring(0, id.indexOf("_"));
	id = id.replace("yesnoR", "r");
	groupId = groupId.replace("yesnoR", "r");
	
	for (var i = 0; i < keys.length; i++) {
		var key = keys[i];

		if(key.indexOf(groupId) > -1)
		{
			if(key.indexOf(id) > -1)
			{
				if(key.indexOf("textconfirm") > -1)
				{
					$("#" + key + "_input").attr("disabled", false);
					$("#" + key).addClass("textfiled");
					$("#" + key).on("click", doConfirm);
				}
				else
				{
					$("#" + key).attr("disabled", false);
					$("#" + key).addClass("radiobutton");
				}
			}
			else if(key.length != id.length)
			{
				if(key.indexOf("textconfirm") > -1)
				{
					$("#" + key + "_input").attr("disabled", true);
					$("#" + key).removeClass("textfiled");
					$("#" + key).off("click");
				}
				else
				{
					$("#" + key).attr("disabled", true);
					$("#" + key).removeClass("radiobutton");
				}
			}
		}
	}
}

/**
 * 서식별 validate
 * @param obj
 * @param chkValid
 * @returns {Boolean}
 */

var desc_map_copy = null;

function rx_validate(obj, chkValid)
{
	//금융소비자불이익사항우선설명확인서
	if(value_map.get("product").indexOf("1101003") > -1)
	{
		if(document.getElementsByName("rdAgree0")[0].checked && obj.id == "rdAgree_0101_radio")
		{
			var rdos	= document.getElementsByName("rdAgree1");
			var len		= rdos.length;

			chkValid	= false;
			
			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("rdAgree1")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "하나의 항목");
			}
			else
			{
				desc_map.put(obj.id, "동의 - 만 65세 이상");
			}

			value_map.put("textconfirm_rdAgree_02", $("#textconfirm_rdAgree_02").val());
		}
		else if(document.getElementsByName("rdAgree0")[1].checked)
		{
			value_map.put("textconfirm_rdAgree_01", $("#textconfirm_rdAgree_01").val());
		}
		else if(!(document.getElementsByName("rdAgree0")[0].checked || document.getElementsByName("rdAgree0")[1].checked) && obj.id == "yesnoRdAgree_01")
		{
			chkValid = false;
		}

		// 동의 또는 동의하지 않음 선택 시 선택 하지 않은 자필기재 삭제.
		if((document.getElementsByName("rdAgree0")[0].checked || document.getElementsByName("rdAgree0")[1].checked) &&
				!(desc_map.get("sign1") == null || desc_map.get("sign1") == ""))
		{
			if(desc_map_copy == null)
			{
				desc_map_copy =
				{
					textconfirm_rdAgree_01 : desc_map.get("textconfirm_rdAgree_01")
					, textconfirm_rdAgree_02 : desc_map.get("textconfirm_rdAgree_02")
				};
			}

			if(document.getElementsByName("rdAgree0")[0].checked)
			{
				desc_map.put("textconfirm_rdAgree_01", desc_map_copy.textconfirm_rdAgree_01);
				desc_map.put("textconfirm_rdAgree_02", "");
				value_map.put("textconfirm_rdAgree_02", " ");
			}
			else
			{
				desc_map.put("textconfirm_rdAgree_01", "");
				desc_map.put("textconfirm_rdAgree_02", desc_map_copy.textconfirm_rdAgree_02);
				value_map.put("textconfirm_rdAgree_01", " ");
			}
		}
	}
	// SMS/E-MAIL/DM 통지서비스 이용 신청서
	else if(value_map.get("product").indexOf("1203003") > -1)
	{
		if(obj.id == "radioAgree01_1" && document.getElementById("checkbox01").checked)
		{
			var rdos	= document.getElementsByName("rdAgree01");
			var len		= rdos.length;

			chkValid	= false;
			
			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("rdAgree01")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "하나의 항목");
			}
			else
			{
				desc_map.put(obj.id, "실행통지-SMS");
			}
		}
		else if(obj.id == "radioAgree02_1" && (document.getElementById("checkbox05").checked || document.getElementById("checkbox06").checked))
		{
			var rdos	= document.getElementsByName("rdAgree02");
			var len		= rdos.length;

			chkValid	= false;

			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("rdAgree02")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "하나의 항목");
			}
			else
			{
				desc_map.put(obj.id, "이자납입기일-SMS");
			}
		}
		else if(obj.id == "radioAgree03_1" && document.getElementById("checkbox09").checked)
		{
			var rdos	= document.getElementsByName("rdAgree03");
			var len		= rdos.length;

			chkValid	= false;
			
			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("rdAgree03")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "하나의 항목");
			}
			else
			{
				desc_map.put(obj.id, "이자납입기일 및 변동금리 사전고지-SMS");
			}
		}
		else if(obj.id == "radioAgree04_1" && document.getElementById("checkbox12").checked)
		{
			var rdos	= document.getElementsByName("rdAgree04");
			var len		= rdos.length;

			chkValid	= false;
			
			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("rdAgree04")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "하나의 항목");
			}
			else
			{
				desc_map.put(obj.id, "변동금리부대출 금리변동시 SMS 익영업일 통지(TRACKING 포함)-SMS");
			}		}
	}
	// 대출거래약정서2
	else if(value_map.get("product").indexOf("1202002") > -1)
	{
		if(obj.id == "ness_radio_1")
		{
			var rdos	= document.getElementsByName("radio01");
			var len		= rdos.length;

			chkValid	= false;
			
			for(var i = 0; i < len; i++)
			{
				if(document.getElementsByName("radio01")[i].checked)
				{
					chkValid = true;
				}
			}

			if(!chkValid)
			{
				desc_map.put(obj.id, "이자계산일");
			}
			else
			{
				desc_map.put(obj.id, "이자계산일-매월 셋째주 토요일");
			}
		}
	}

	return chkValid;
}