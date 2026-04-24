 function getDate(){
	 var date = new Date();
	 var year = date.getFullYear();
	 var month = date.getMonth()+1;
	 var day = date.getDate();
	 var hour = date.getHours();
	 var minute = date.getMinutes();
	 var sec = date.getSeconds();
	 
	 return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + sec;
}

 
function doSign(obj,type){
	
	hidePopbox();
	var date = getDate();
	var chkdo = true;
	var msg = "내용";
	var jobj = $('#'+obj.id);
	
	if (type == 'popup') {
		msg = "\""+desc_map.get(obj.id)+ "\" 을(를) 확인하셨습니까?";
		chkdo = confirm(msg);
	}
	
	if (chkdo) { 
		value_map.put(obj.id, date);


		obj.innerHTML = date.replace(/ /g, "<br/>");

		viewConfirm(obj,'sign');
		var obj2 = document.getElementById("signimg_"+obj.id);
		if (obj2 != null){
			obj2.style.display = "";
		}
	}

	$("#"+obj.id).removeClass("button");
	obj.style.background="#FFFFFF";
	obj.style.color="#FF0000";
	obj.disabled=true;	
	
	netxtTabObject();
}

function doTextconfirm(obj){
	hidePopbox();
	var args = new Array();
	args[0]="정말로 설명입니다.<br/>입력값:";
	args[1]=obj.value;
    var reValue = window.showModalDialog('ess_prompt.jsp', args,'status:no;;help:no;dialogWidth:300px; dialogHeight:200px;center:yes;scroll:no');
    if (reValue != null) {
        obj.value = reValue;
    }
    netxtTabObject();
}


function doCheck(obj){
	hidePopbox();
	if(obj.checked){
		value_map.put(obj.id, "1");
	}else{
		value_map.put(obj.id, "0");
	}
	$("#span_"+obj.id).removeClass("radiocheckspan");
	netxtTabObject();
}

function doRadio(obj){
	hidePopbox();

    var docOption = document.getElementsByName(obj.name);

    for (var i = 0; i < docOption.length; i++) {
        if (docOption[i].checked) {
        	value_map.put(docOption[i].id, docOption[i].value);
        } else {
        	value_map.put(docOption[i].id, "");
        }
    }
    $("#span_"+obj.id).removeClass("radiocheckspan");
    netxtTabObject();
}

function doClick(obj,type){
	var chkdo = true;
	var msg = "내용";
	
	if (type == 'popup') {
		msg = "\""+desc_map.get(obj.id)+ "\" 을(를) 확인하셨습니까?";
		chkdo = confirm(msg);
	}
	if (chkdo) { 
		obj.style.background="none";
		obj.style.fontWeight="bold";
		value_map.put(obj.id, "내용 확인 완료");
		value_map.put(obj.id, "1");

		viewConfirm(obj);
	}
	
	$("#"+obj.id).removeClass("button");
	obj.style.background="#FFFFFF";
	obj.style.color="#FF0000";
	obj.disabled=true;
	
	netxtTabObject();
}

function doBtnClick(obj){
	hidePopbox();

	obj.style.color="#FF0000";
	obj.style.background="none";
	obj.style.fontWeight="bold";
	viewConfirm(obj);
	netxtTabObject();
}

function doSelect(obj){
	hidePopbox();
	value_map.put(obj.name, obj.options[obj.selectedIndex].value);
	netxtTabObject();
}

function checkFromTag(tag, instr){
	if(tag.indexOf(instr)!=-1){
		return true;
	}else{
		return false;
	}
}

function checkFormValidateSend(_chkindex){
	var keys = value_map.keys();

	for (i = 0; i < keys.length; i++) {
		var key = keys[i];
		var chkValid = true;
		var type = 	$("#"+key).attr('type');
		
		if (type == null)type= "";
		
		if(type=="" || type=="text"){
			type = key;
		}
		
		// alert( "i:" + i +","+ _chkindex + "/" +
		// keys.length+","+(_chkindex%keys.length)+","+key);

		if (key != "customer" && key != "product" && key != "undefined") {
			var value = value_map.get(key);
			if (key.indexOf("sign") == 0) {
				// 이미 입력된값을 건드리지 않음
				if (value == null || value == "") {
					chkValid = false;
				}
			} else {
				value = "";
				value_map.put(key, ""); // 초기화

				var obj = document.getElementById(key);
				if (type.indexOf("select") == 0) {
					value = obj.options[obj.selectedIndex].value;
				} else if (type.indexOf("textconfirm") == 0) {
					var obj2 = document.getElementById(key + "_input");
//					alert(MD5(Base64.encode(Utf8.encode(obj2.value)))+"\n"+confirm_map.get(key));
					if (MD5(Base64.encode(Utf8.encode(obj2.value))) != confirm_map.get(key)) {
						chkValid = false;
					} else {
						value = obj.value;
					}
				} else if (type.indexOf("buttonconfirm") == 0) {
					if (obj.disabled == true) {
						value = obj.disabled; // true
					} else {
						chkValid = false;
					}
				} else if (key.indexOf("readconfirm") == 0) {
					if (obj.disabled == true) {
						value = obj.disabled; // true
					} else {
						chkValid = false;
					}
				} else if (key.indexOf("yesnoconfirm") == 0) { // radio 보다 상위에
					// 있어야 함
					if (obj.checked) {
						if (obj.value == "1") {
							value = obj.value;
						} else {
							chkValid = false;
						}
					} else {
						value = "";
					}
				} else if (type.indexOf("checkbox") == 0) {
					if (obj.checked) {
						value = "1";
					} else {
						value = "0";
					}
				} else if (type.indexOf("radio") == 0) {
					if (obj.checked) {
						value = obj.value;
					} else {
						value = "";
					}
				} else {
					value = obj.value;
					if (value == null || value == "") {
						chkValid = false;
					}
				}
			}
			if (chkValid) {
				document.getElementById(key).style.backgroundColor = "#ffffff";
				value_map.put(key, value);
			} else {
				chkValid = reportFormValidateSend(obj);
				if (!chkValid) return key;
			}
		}
	}
	return "";
}

function beforeFormValidate(_chkindex){
	var keys = value_map.keys();
//	alert( _chkindex + "/" + keys.length+","+(_chkindex%keys.length));
	if  (_chkindex >= keys.length || _chkindex < 0)  _chkindex =  0;
	for(i=_chkindex; i>-1; i--){
		var key = keys[i];
//		alert( "i:" + i +","+ _chkindex + "/" + keys.length+","+(_chkindex%keys.length)+","+key);
			if( key!= "customer" && key!= "product" && key != "undefined" ){
					if ( FormValidate2(key) ) {
						chkindex = i;
						return key;
					}
			}
	}
	return nextFormValidate(0); 
}

function nextFormValidate(_chkindex){
	var keys = value_map.keys();
//	alert( _chkindex + "/" + keys.length+","+(_chkindex%keys.length));
	if  (_chkindex >= keys.length || _chkindex < 0)  _chkindex =  keys.length-1;
	for(i=_chkindex; i<keys.length; i++){
		var key = keys[i];
//		alert( "i:" + i +","+ _chkindex + "/" + keys.length+","+(_chkindex%keys.length)+","+key);
			if( key!= "customer" && key!= "product" && key != "undefined" ){
					if ( FormValidate2(key) ) {
						chkindex = i;
						return key;
					}
			}
	}
}

function FormValidate2(key) {
		var chkValid = true;
	
	  	var obj = document.getElementById(key);
		var type = 	$("#"+key).attr('type');
		if (type == null)type= "";
		if(type =="checkbox" || type == "textfiled" ){
			if (obj.disabled==true) {
				chkValid = false;
			}
		} else if ( type == "radiobutton"  ) {
			if (obj.disabled==true) {
				chkValid = false;
			} else if ( obj.id != obj.name ) {
				chkValid = false;
			}
		}
		// 각각의 보고서에서 한번더 확인
		if (chkValid) {
			chkValid = reportFormValidate2(obj);
		}
	
	return chkValid;
}

function initFormValidate(){
	var keys = desc_map.keys();

	for(i=0; i<keys.length; i++){
		var key = keys[i];
		if (key.indexOf("sign")==0 || key.indexOf("yesnoconfirm") == 0 || key.indexOf("radio") == 0) {
			var obj = document.getElementById(key);
//			alert( "i:" + i +","+ keys.length+","+key+","+obj.checked+","+obj.name+","+obj.value);

			if (obj != null && obj.checked) {
				value_map.put(obj.id, obj.value);
			}
		}else {
			value_map.put(key, "");
		}
	}
}

function makeForm(id, url){
	var f = document.createElement("form");
	f.setAttribute("id", id);
	f.setAttribute("name", id);
    f.setAttribute("method", "post");
    f.setAttribute("enctype", "application/x-www-form-urlencoded");
    if(url!=""){
    	f.setAttribute("action", url);
    }
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

function buildForm(){
	var s = "";
	
	var keys = value_map.keys();
	for(i=0; i<keys.length; i++){
		var key = keys[i];
		
		if(key!="customer" && key!="product" &&  key.indexOf("buttonconfirm") < 0 && key.indexOf("span_")!=0){
			s += key + "=" + encodeURIComponent(value_map.get(key)) + "&";
		}
	}
	return s;
}

function buildSignContents(){
	var mtd = "font-size:12px; font-family:\"굴림체,seoul,verdana\"; color:#737373; line-height:14px;";
	var td = "border:1px solid rgb(205, 214, 227); padding: 5px 8px 5px 8px;";
	
	var s = "<br/><br/><center>\n";
	s += "<table cellspacing='0' cellpadding='0' border='0' width='620' style='" + td + "'>\n";
	s += "<tr>\n";
	s += "<td style=\"padding:2;" + mtd + "\">\n";
	s += "<table cellspacing='0' cellpadding='0' border='0'>\n";
	s += "<tr>\n";
	s += "<td style=\"" + mtd + "\"><br/></td>\n";
	s += "</tr>\n";
	s += "<tr>\n";
	s += "<td align=\"left\" style=\"" + mtd + "\">&nbsp;&nbsp;&nbsp;";
	s += "<span style=\"color:#000000; font-size:14; font-weight:bold;\">";
	s += value_map.get("customer");
	s += "</span>";
	s += " 님의 ";
	s += "<span style=\"color:#000000; font-size:14; font-weight:bold;\">";
	s += value_map.get("product");
	s += "</span>에 대한 전자서명 정보입니다.";
	s += "<br/><br/><center>";
	s += "<table cellspacing='0' cellpadding='0' width='600' style='BORDER-COLLAPSE: collapse;" + mtd + "'>\n";
	s += "<colgroup>\n";
	s += "<col width='250px'/>\n";
	s += "<col width='350px'/>\n";
	s += "</colgroup>\n";
	
	var keys = value_map.keys();

	for(i=0; i<keys.length; i++){
		var key = keys[i];
		var isValid = true;
		var isFix = false;

		if(key!="customer" && key!="product" && key!="boldText" &&  key.indexOf("buttonconfirm") < 0 && key.indexOf("span_")!=0){
			var value = value_map.get(key);
			if(checkFromTag(key, "checkbox")){
				if (document.getElementById(key).disabled == true) {
					isValid = false;
				}
				if(value=="1"){
					value = "선택함";
				}else{
					value = "선택안함";
				}
			}else if(checkFromTag(key, "radio")){
				if (document.getElementById(key).checked) {
					value = "동의함";
				} else {
					value = "동의하지 않음";
				}
				 if(checkFromTag(key, "yesnoconfirm")) isFix = true;
			}else if(checkFromTag(key, "yesno")){
				if (document.getElementById(key).checked) {
					if(value=="1"){
						value = "동의함";
					}else{
						value = "동의안함";
					}
				} else {
					isValid = false;
				}
			}else if(checkFromTag(key, "readconfirm")){
				value = "내용을 확인 하였습니다.";
			}
			
			if (isValid) {
				s += "<tr>\n";
				s += "<td align=\"left\" style='" + td + "'>";
				s += desc_map.get(key);
				s += "</td>\n";
				s += "<td align=\"left\" style='" + td + "'>";
				s += value;
				s += "</td>\n";
				s += "</tr>\n";
			}
		}
	}
	
	s += "</table></center></td>\n";
	s += "</tr>\n";
	s += "</table>\n";
	s += "</td>\n";
	s += "</tr>\n";
	s += "</table>\n";
	s += "</center>";
	
	return s;
}