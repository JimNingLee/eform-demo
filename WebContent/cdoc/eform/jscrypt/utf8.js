// UTF-8 encode/decode  (compatible with Java's String.getBytes("UTF-8"))
var Utf8 = (function() {
    function encode(str) {
        var bytes = [];
        for (var i = 0; i < str.length; i++) {
            var c = str.charCodeAt(i);
            if (c < 0x80) {
                bytes.push(c);
            } else if (c < 0x800) {
                bytes.push(0xC0 | (c >> 6));
                bytes.push(0x80 | (c & 0x3F));
            } else if (c >= 0xD800 && c <= 0xDBFF && i + 1 < str.length) {
                var c2 = str.charCodeAt(++i);
                var cp = 0x10000 + ((c - 0xD800) << 10) + (c2 - 0xDC00);
                bytes.push(0xF0 | (cp >> 18));
                bytes.push(0x80 | ((cp >> 12) & 0x3F));
                bytes.push(0x80 | ((cp >> 6) & 0x3F));
                bytes.push(0x80 | (cp & 0x3F));
            } else {
                bytes.push(0xE0 | (c >> 12));
                bytes.push(0x80 | ((c >> 6) & 0x3F));
                bytes.push(0x80 | (c & 0x3F));
            }
        }
        return bytes;
    }

    function decode(bytes) {
        var str = '', i = 0, b, b2, b3;
        while (i < bytes.length) {
            b = bytes[i++];
            if (b < 0x80) {
                str += String.fromCharCode(b);
            } else if ((b & 0xE0) === 0xC0) {
                str += String.fromCharCode(((b & 0x1F) << 6) | (bytes[i++] & 0x3F));
            } else if ((b & 0xF0) === 0xE0) {
                b2 = bytes[i++]; b3 = bytes[i++];
                str += String.fromCharCode(((b & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
            } else if ((b & 0xF8) === 0xF0) {
                b2 = bytes[i++]; b3 = bytes[i++];
                var b4 = bytes[i++];
                var cp = ((b & 7) << 18) | ((b2 & 0x3F) << 12) | ((b3 & 0x3F) << 6) | (b4 & 0x3F);
                cp -= 0x10000;
                str += String.fromCharCode(0xD800 + (cp >> 10), 0xDC00 + (cp & 0x3FF));
            }
        }
        return str;
    }

    return { encode: encode, decode: decode };
})();
