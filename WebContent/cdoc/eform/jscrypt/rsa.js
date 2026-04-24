//Depends on jsbn.js and rng.js

//Version 1.1: support utf-8 encoding in pkcs1pad2

//convert a (hex) string to a bignum object
function parseBigInt(str,r) {
	return new BigInteger(str,r);
}

function linebrk(s,n) {
	var ret = "";
	var i = 0;
	while(i + n < s.length) {
		ret += s.substring(i,i+n) + "\n";
		i += n;
	}
	return ret + s.substring(i,s.length);
}

function byte2Hex(b) {
	if(b < 0x10)
		return "0" + b.toString(16);
	else
		return b.toString(16);
}

//"empty" RSA key constructor
function RSAKey() {
	this.n = null;
	this.e = 0;
	this.d = null;
	this.p = null;
	this.q = null;
	this.dmp1 = null;
	this.dmq1 = null;
	this.coeff = null;
}

//Set the public key fields N and e from hex strings
function RSASetPublic(N,E) {
	if(N != null && E != null && N.length > 0 && E.length > 0) {
		this.n = parseBigInt(N,16);
		this.e = parseInt(E,16);
	}
	else
		alert("Invalid RSA public key");
}

//Perform raw public operation on "x": return x^e (mod n)
function RSADoPublic(x) {
	return x.modPowInt(this.e, this.n);
}

function RSAEncrypt(text) {
	var ba = new Array();	
	for(i=0; i<text.length; i++)
		ba[i] = text.charCodeAt(i);

	var m = new BigInteger(ba);

	var c = this.doPublic(m);
	if(c == null) return null;
	var h = c.toString(16);
	if((h.length & 1) == 0) return h; else return "0" + h;
}


//protected
RSAKey.prototype.doPublic = RSADoPublic;

//public
RSAKey.prototype.setPublic = RSASetPublic;
RSAKey.prototype.encrypt = RSAEncrypt;
