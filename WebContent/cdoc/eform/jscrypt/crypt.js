function GenerateKey(){
	var k="";
	while(k.length<16){
		time=new Date().getTime();
		random=Math.floor(65536*Math.random());
		k+=(time*random).toString()
	}
	return k;
}

function Key(s,l){
	var m=Math.floor(s.length/l);
	var k=s.substring(m-1,m);
	var p=s.substring(0,m-1);
	for(i=2;i<=l;i++){
		k+=s.substring(i*m-1,i*m);
		p+=s.substring((i-1)*m,i*m-1)
	}
	p+=s.substring(m*l);
	return[k,p];
}

function str_replace(a,b,c){
	var d=a.split(b);
	return d.join(c);
}

function Decrypt(c){
	r=Key(c,32);
	return DecryptTEA(r[0],r[1]);
}

function DecryptTEA(k,c){
	var a=Tea.decrypt(c,k);
	a=str_replace(a,"+"," ");
	return decodeURIComponent(a);
}

function EncryptTEA(k,a){
	return Tea.encrypt(a,k);
}

function EncryptRSA(a,b,c){
	var d=new RSAKey();
	d.setPublic(b,c);
	return d.encrypt(a);
}

function encrypt(a,b,c){
	var d=GenerateKey();
	var e=EncryptRSA(d,b,c);
	var f=EncryptTEA(d,a);
	f=encodeURIComponent(f);
	var g=new Array(f,e,d);
	return g;
}

function encryptP(a,b,c){
	var d=GenerateKey();
	var e=EncryptRSA(d,b,c);
	var f=EncryptTEA(d,a);
	var g=new Array(f,e,d,d);
	return g;
}

function trim(a,b){
	return ltrim(rtrim(a,b),b);
}

function ltrim(a,b){
	b=b||"\\s";
	return a.replace(new RegExp("^["+b+"]+","g"),"");
}

function rtrim(a,b){
	b=b||"\\s";
	return a.replace(new RegExp("["+b+"]+$","g"),"");
}