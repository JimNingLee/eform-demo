package com.cabsoft.pdf.form.utils;

import java.text.DecimalFormat;

/**
 *
 * @author Administrator
 */
@SuppressWarnings("javadoc")
public class StringUtils {

    /**
     *
     */
    public static final String EMPTY = "";
    /**
     *
     */
    public static final String NULL = "null";
    /**
     *
     */
    public static final char[] WORD_SEPARATORS = {'_', '-', '@', '$', '#', ' '};
    /**
     *
     */
    public static final int INDEX_NOT_FOUND = -1;

    private StringUtils() {
    }

    /**
     *
     * @param str
     * @param pattern
     * @param replace
     * @return
     */
    public static String replaceAll(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;

        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * <p>문자열을 구분자로 나누어서, 문자열 배열로 만든다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")도 포함한다.</p>
     *
     * <pre>
     * StringUtils.split("h-a-n", '-') = ["h", "a", "n"]
     * StringUtils.split("h--n", '-')  = ["h", "", "n"]
     * StringUtils.split(null, *)      = null
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    public static String[] split(String str, char separator) {
        return split(str, new String(new char[]{separator}));
    }

    /**
     * <p>문자열을 구분자로 나누어서, 문자열 배열로 만든다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")도 포함한다.</p>
     *
     * <pre>
     * StringUtils.split("h-a-n", "-") = ["h", "a", "n"]
     * StringUtils.split("h--n", "-")  = ["h", "", "n"]
     * StringUtils.split(null, *)      = null
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    public static String[] split(String str, String separator) {
        if (str == null) {
            return null;
        }
        StrTokenizer tokenizer = new StrTokenizer(str, separator);
        return tokenizer.toArray();
    }

    /**
     *
     * @param dbl
     * @param frm
     * @return
     */
	public static String FormatNumber(int dbl, String frm) {
        String ret = "";
        try {
            ret = new DecimalFormat(frm).format(dbl);
        } catch (Exception e) {
            ret = String.valueOf(dbl);
        }
        return ret;
    }

    /**
     *
     * @param dbl
     * @param frm
     * @return
     */
    public static String FormatNumber(double dbl, String frm) {
        String ret = "";
        try {
            ret = new DecimalFormat(frm).format(dbl);
        } catch (Exception e) {
            ret = String.valueOf(dbl);
        }
        return ret;
    }

    /**
     *
     * @param dbl
     * @param frm
     * @return
     */
    public static String FormatNumber(String dbl, String frm) {
        String ret = "";
        String tdbl = (dbl == null || dbl.trim().equalsIgnoreCase("")) ? "" : dbl.trim();
        try {
            if (!tdbl.equalsIgnoreCase("")) {
                ret = FormatNumber(Double.parseDouble(tdbl), frm);
            } else {
                ret = tdbl;
            }
        } catch (Exception e) {
            ret = dbl;
        }
        return ret;
    }

    /**
     *
     * @param buf
     * @param n
     */
    public static void indent(StringBuffer buf, int n) {
        for (int i = 0; i < n; i++) {
            buf.append("    ");
        }
    }

    /**
     * <p>문자(char)가 단어 구분자('_', '-', '
     *
     * @', '$', '#', ' ')인지 판단한다.</p>
     *
     * @param c 문자(char)
     * @return 단어 구분자이면 true, 아니면 false를 반환한다.
     */
    public static boolean isWordSeparator(char c) {
        for (int i = 0; i < WORD_SEPARATORS.length; i++) {
            if (WORD_SEPARATORS[i] == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>문자(char)가 단어 구분자(wordSeparators)인지 판단한다.</p> <p>단어 구분자가
     * <code>null</code>이면 false를 반환한다.</p>
     *
     * @param c 문자(char)
     * @param wordSeparators 단어 구분자
     * @return 단어 구분자이면 true, 아니면 false를 반환한다.
     */
    public static boolean isWordSeparator(char c, char[] wordSeparators) {
        if (wordSeparators == null) {
            return false;
        }
        for (int i = 0; i < wordSeparators.length; i++) {
            if (wordSeparators[i] == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>문자열(String)을 카멜표기법으로 표현한다.</p>
     *
     * <pre>
     * StringUtils.camelString("ITEM_CODE", true)  = "ItemCode"
     * StringUtils.camelString("ITEM_CODE", false) = "itemCode"
     * </pre>
     *
     * @param str 문자열
     * @param firstCharacterUppercase 첫문자열을 대문자로 할지 여부
     * @return 카멜표기법으로 표현환 문자열
     */
    public static String camelString(String str, boolean firstCharacterUppercase) {
        if (str == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        boolean nextUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (isWordSeparator(c)) {
                if (sb.length() > 0) {
                    nextUpperCase = true;
                }
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------
    // 공백/여백문자  검사, 제거, 치환
    // ----------------------------------------------------------------------
    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나
     * <code>null</code>인 검사한다.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)    = true
     * StringUtils.isBlank("")      = true
     * StringUtils.isBlank("   ")   = true
     * StringUtils.isBlank("han")   = false
     * StringUtils.isBlank(" han ") = false
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int strLen = str.length();
        if (strLen > 0) {
            for (int i = 0; i < strLen; i++) {
                if (Character.isWhitespace(str.charAt(i)) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이 아니거나
     * <code>null</code>이 아닌지 검사한다.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)    = false
     * StringUtils.isNotBlank("")      = false
     * StringUtils.isNotBlank("   ")   = false
     * StringUtils.isNotBlank("han")   = true
     * StringUtils.isNotBlank(" han ") = false
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * <p>문자열(String)이 공백("")이거나
     * <code>null</code>인 검사한다.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)    = true
     * StringUtils.isEmpty("")      = true
     * StringUtils.isEmpty("   ")   = false
     * StringUtils.isEmpty("han")   = false
     * StringUtils.isEmpty(" han ") = false
     * </pre>
     *
     *
     * @param str 검사할 문자열
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>문자열(String)이 공백("")이 아니거나
     * <code>null</code>이 아닌지 검사한다.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)    = false
     * StringUtils.isNotEmpty("")      = false
     * StringUtils.isNotEmpty("   ")   = true
     * StringUtils.isNotEmpty("han")   = true
     * StringUtils.isNotEmpty(" han ") = true
     * </pre>
     *
     * @param str 검사할 문자열
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한다.</p>
     *
     * <pre>
     * StringUtils.trim(null)    = null
     * StringUtils.trim("")      = ""
     * StringUtils.trim("   ")   = ""
     * StringUtils.trim("han")   = "han"
     * StringUtils.trim(" han ") = "han"
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나
     * <code>null</code>이면
     * <code>null</code>을 반환한다.</p>
     *
     * <pre>
     * StringUtils.trimToNull(null)    = null
     * StringUtils.trimToNull("")      = null
     * StringUtils.trimToNull("   ")   = null
     * StringUtils.trimToNull("han")   = "han"
     * StringUtils.trimToNull(" han ") = "han"
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String trimToNull(String str) {
        return isBlank(str) ? null : trim(str);
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나
     * <code>null</code>이면 공백("")을 반환한다.</p>
     *
     * <pre>
     * StringUtils.trimToNull(null)    = ""
     * StringUtils.trimToNull("")      = ""
     * StringUtils.trimToNull("   ")   = ""
     * StringUtils.trimToNull("han")   = "han"
     * StringUtils.trimToNull(" han ") = "han"
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String trimToEmpty(String str) {
        return isBlank(str) ? EMPTY : trim(str);
    }

    /**
     * <p>문자열(String)이
     * <code>null</code>이면 기본문자열을 반환한다.</p>
     *
     * <pre>
     * StringUtils.defaultIfNull(null, "")    = ""
     * StringUtils.defaultIfNull("", "")      = ""
     * StringUtils.defaultIfNull("   ", "")   = "   "
     * StringUtils.defaultIfNull("han", "")   = "han"
     * StringUtils.defaultIfNull(" han ", "") = " han "
     * </pre>
     *
     * @param str 문자열
     * @param defaultStr 기본문자열
     * @return
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * <p>문자열(String)이
     * <code>null</code>이면 공백문자열을 반환한다.</p>
     *
     * <pre>
     * StringUtils.defaultIfNull(null)    = ""
     * StringUtils.defaultIfNull("")      = ""
     * StringUtils.defaultIfNull("   ")   = "   "
     * StringUtils.defaultIfNull("han")   = "han"
     * StringUtils.defaultIfNull(" han ") = " han "
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String defaultIfNull(String str) {
        return defaultIfNull(str, EMPTY);
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나
     * <code>null</code>이면, 기본문자열을 반환한다.</p>
     *
     * <pre>
     * StringUtils.defaultIfBlank(null, "")    = ""
     * StringUtils.defaultIfBlank("", "")      = ""
     * StringUtils.defaultIfBlank("   ", "")   = ""
     * StringUtils.defaultIfBlank("han", "")   = "han"
     * StringUtils.defaultIfBlank(" han ", "") = " han "
     * </pre>
     *
     * @param str 문자열
     * @param defaultStr 기본문자열
     * @return
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나
     * <code>null</code>이면, 공백문자열을 반환한다.</p>
     *
     * <pre>
     * StringUtils.defaultIfBlank(null)    = ""
     * StringUtils.defaultIfBlank("")      = ""
     * StringUtils.defaultIfBlank("   ")   = ""
     * StringUtils.defaultIfBlank("han")   = "han"
     * StringUtils.defaultIfBlank(" han ") = " han "
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String defaultIfBlank(String str) {
        return defaultIfBlank(str, EMPTY);
    }

    // ----------------------------------------------------------------------
    // 문자열 비교
    // ----------------------------------------------------------------------
    /**
     * <p>두 문자열(String)이 일치하면
     * <code>true</code>을 반환한다.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "")     = false
     * StringUtils.equals("", null)     = false
     * StringUtils.equals(null, "han")  = false
     * StringUtils.equals("han", null)  = false
     * StringUtils.equals("han", "han") = true
     * StringUtils.equals("han", "HAN") = false
     * </pre>
     *
     * @see String#equals(Object)
     * @param str1 첫번째 문자열
     * @param str2 두번째 문자열
     * @return 문자열(String)이 일치하면 <code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * <p>대소문자를 무시한, 두 문자열(String)이 일치하면
     * <code>true</code>을 반환한다.</p>
     *
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "")     = false
     * StringUtils.equalsIgnoreCase("", null)     = false
     * StringUtils.equalsIgnoreCase(null, "han")  = false
     * StringUtils.equalsIgnoreCase("han", null)  = false
     * StringUtils.equalsIgnoreCase("han", "han") = true
     * StringUtils.equalsIgnoreCase("han", "HAN") = true
     * </pre>
     *
     * @see String#equalsIgnoreCase(String)
     * @param str1 첫번째 문자열
     * @param str2 두번째 문자열
     * @return 대소문자를 무시한 문자열(String)이 일치하면 <code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    /**
     * <p>문자열이 접두사로 시작하는지를 판단한다.</p>
     *
     * <pre>
     * StringUtils.startsWith(null, *)    = false
     * StringUtils.startsWith(*, null)    = false
     * StringUtils.startsWith("han", "h") = true
     * StringUtils.startsWith("han", "a") = false
     * </pre>
     *
     * @param str 문자열
     * @param prefix 접두사
     * @return
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * <p>문자열 offset 위치부터 접두사로 시작하는지를 판단한다.</p>
     *
     * <pre>
     * StringUtils.startsWith(null, *, 0)    = false
     * StringUtils.startsWith(*, null, 0)    = false
     * StringUtils.startsWith("han", "h", 0) = true
     * StringUtils.startsWith("han", "a", 0) = false
     * StringUtils.startsWith("han", "a", 1) = true
     * </pre>
     *
     * @param str 문자열
     * @param prefix 접두사
     * @param offset 비교 시작 위치
     * @return
     */
    public static boolean startsWith(String str, String prefix, int offset) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix, offset);
    }

    /**
     * <p>문자열이 접미사로 끝나는지를 판단한다.</p>
     *
     * <pre>
     * StringUtils.endsWith(null, *)    = false
     * StringUtils.endsWith(*, null)    = false
     * StringUtils.endsWith("han", "h") = false
     * StringUtils.endsWith("han", "n") = true
     * </pre>
     *
     * @param str 문자열
     * @param suffix
     * @return
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * <p>문자열(String)에 검색문자열(String)이 포함되어 있는지 검사한다.</p>
     *
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains(*, null)    = false
     * StringUtils.contains("han", "")  = true
     * StringUtils.contains("han", "h") = true
     * StringUtils.contains("han", "H") = false
     * </pre>
     *
     * @see String#indexOf(String)
     * @param str 문자열
     * @param searchStr 검색문자열
     * @return 문자열(String)에 검색 문자열이 포함되어 있을때 <code>true</code>, 문자열(String)에 검색
     * 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 <code>null</code>일때 <code>false</code>
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) > INDEX_NOT_FOUND;
    }

    // ----------------------------------------------------------------------
    // 대/소문자 변환
    // ----------------------------------------------------------------------
    /**
     * <p>문자열(String)을 대문자로 변환한다.</p>
     *
     * <pre>
     * StringUtils.toUpperCase(null)  = null
     * StringUtils.toUpperCase("han") = "HAN"
     * StringUtils.toUpperCase("hAn") = "HAN"
     * </pre>
     *
     * @param str 문자열
     * @return 대문자로 변환한 문자열
     */
    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>문자열(String)을 소문자로 변환한다.</p>
     *
     * <pre>
     * StringUtils.toLowerCase(null)  = null
     * StringUtils.toLowerCase("han") = "han"
     * StringUtils.toLowerCase("hAn") = "han"
     * </pre>
     *
     * @param str 문자열
     * @return 소문자로 변환한 문자열
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * <p>대문자는 소문자로 변환하고 소문자는 대문자로 변환한다.</p>
     *
     * <pre>
     * StringUtils.swapCase(null)  = null
     * StringUtils.swapCase("Han") = "hAN"
     * StringUtils.swapCase("hAn") = "HaN"
     * </pre>
     *
     * @param str 문자열
     * @return
     */
    public static String swapCase(String str) {
        if (str == null) {
            return null;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLowerCase(charArray[i])) {
                charArray[i] = Character.toUpperCase(charArray[i]);
            } else {
                charArray[i] = Character.toLowerCase(charArray[i]);
            }
        }

        return new String(charArray);
    }

    /**
     * 문자열(String)의 첫번째 문자를 대문자로 변환한다.
     *
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("Han") = "Han"
     * StringUtils.capitalize("han") = "Han"
     * </pre>
     *
     * @param str 문자열
     * @return 첫번째 문자를 대문자로 변환한 문자열
     */
    public static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        char[] charArray = str.toCharArray();
        if (charArray.length > 0) {
            charArray[0] = Character.toUpperCase(charArray[0]);
        }
        return new String(charArray);
    }

    /**
     * 문자열(String)의 첫번째 문자를 소문자로 변환한다.
     *
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("han") = "han"
     * StringUtils.uncapitalize("HAN") = "hAN"
     * </pre>
     *
     * @param str 문자열
     * @return 첫번째 문자를 대문자로 변환한 문자열
     */
    public static String uncapitalize(String str) {
        if (str == null) {
            return null;
        }
        char[] charArray = str.toCharArray();
        if (charArray.length > 0) {
            charArray[0] = Character.toLowerCase(charArray[0]);
        }
        return new String(charArray);
    }

    // ----------------------------------------------------------------------
    // 문자열 배열 결합/분리
    // ----------------------------------------------------------------------
    /**
     * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")은 무시한다.</p>
     *
     * <pre>
     * StringUtils.compose(null, *)               = ""
     * StringUtils.compose(["h", "a", "n"], ".")  = "h.a.n"
     * StringUtils.compose([null, "a", "n"], ".") = "a.n"
     * StringUtils.compose(["", "a", "n"], ".")   = "a.n"
     * StringUtils.compose(["h", "", "n"], ".")   = "h.n"
     * StringUtils.compose(["  ", "a", "n"], ".") = "  .a.n"
     * </pre>
     *
     * @param strArray 문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    public static String compose(String[] strArray, char separator) {
        StringBuffer sb = new StringBuffer();
        if (strArray != null) {
            for (int i = 0; i < strArray.length; i++) {
                if (StringUtils.isEmpty(strArray[i])) {
                    sb.append(EMPTY);
                } else {
                    if (sb.length() > 0) {
                        sb.append(separator);
                    }
                    sb.append(strArray[i]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")은 무시한다.</p>
     *
     * <pre>
     * StringUtils.compose(null, *)               = ""
     * StringUtils.compose(["h", "a", "n"], ".")  = "h.a.n"
     * StringUtils.compose([null, "a", "n"], ".") = "a.n"
     * StringUtils.compose(["", "a", "n"], ".")   = "a.n"
     * StringUtils.compose(["h", "", "n"], ".")   = "h.n"
     * StringUtils.compose(["  ", "a", "n"], ".") = "  .a.n"
     * </pre>
     *
     * @param strArray 문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    public static String compose(String[] strArray, String separator) {
        StringBuffer sb = new StringBuffer();
        if (strArray != null) {
            for (int i = 0; i < strArray.length; i++) {
                if (StringUtils.isEmpty(strArray[i])) {
                    sb.append(EMPTY);
                } else {
                    if (sb.length() > 0) {
                        sb.append(separator);
                    }
                    sb.append(strArray[i]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")도 포함한다.</p>
     *
     * <pre>
     * StringUtils.join(null, *)               = ""
     * StringUtils.join(["h", "a", "n"], '-')  = "h-a-n"
     * StringUtils.join([null, "a", "n"], '-') = "-a-n"
     * StringUtils.join(["", "a", "n"], '-')   = "-a-n"
     * StringUtils.join(["h", "", "n"], '-')   = "h--n"
     * StringUtils.join(["  ", "a", "n"], '-') = "  -a-n"
     * </pre>
     *
     * @param strArray 문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    public static String join(String[] strArray, char separator) {
        StringBuffer sb = new StringBuffer();
        if (strArray != null) {
            boolean isFirst = true;
            for (int i = 0; i < strArray.length; i++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(separator);
                }
                if (StringUtils.isEmpty(strArray[i])) {
                    sb.append(EMPTY);
                } else {
                    sb.append(strArray[i]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p> <p>배열의 문자열 중에
     * <code>null</code>과 공백("")도 포함한다.</p>
     *
     * <pre>
     * StringUtils.join(null, *)               = ""
     * StringUtils.join(["h", "a", "n"], "-")  = "h-a-n"
     * StringUtils.join([null, "a", "n"], "-") = "-a-n"
     * StringUtils.join(["", "a", "n"], "-")   = "-a-n"
     * StringUtils.join(["h", "", "n"], "-")   = "h--n"
     * StringUtils.join(["  ", "a", "n"], "-") = "  -a-n"
     * </pre>
     *
     * @param strArray 문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    public static String join(String[] strArray, String separator) {
        StringBuffer sb = new StringBuffer();
        if (strArray != null) {
            boolean isFirst = true;
            for (int i = 0; i < strArray.length; i++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(separator);
                }
                if (StringUtils.isEmpty(strArray[i])) {
                    sb.append(EMPTY);
                } else {
                    sb.append(strArray[i]);
                }
            }
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------
    // 문자열 자르기
    // ----------------------------------------------------------------------
    /**
     * <p>문자열(String)을 해당 길이(
     * <code>length</code>) 만큼, 왼쪽부터 자른다.</p>
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -length) = ""
     * StringUtils.left("", *)      = *
     * StringUtils.left("han", 0)   = ""
     * StringUtils.left("han", 1)   = "h"
     * StringUtils.left("han", 11)  = "han"
     * </pre>
     *
     * @param str 문자열
     * @param length 길이
     * @return
     */
    public static String left(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length < 0) {
            return EMPTY;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    /**
     * <p>문자열(String)을 해당 길이(
     * <code>length</code>) 만큼, 오른쪽부터 자른다.</p>
     *
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -length) = ""
     * StringUtils.right("", *)      = *
     * StringUtils.right("han", 0)   = ""
     * StringUtils.right("han", 1)   = "n"
     * StringUtils.right("han", 11)  = "han"
     * </pre>
     *
     * @param str 문자열
     * @param length 길이
     * @return
     */
    public static String right(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length < 0) {
            return EMPTY;
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(str.length() - length);
    }

    /**
     * <p>문자열(String)을 시작 위치(
     * <code>beginIndex</code>)부터 길이(
     * <code>length</code>) 만큼 자른다.</p>
     *
     * <p>시작 위치(
     * <code>beginIndex</code>)가 음수일 경우는 0으로 자동 변환된다.</p>
     *
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -length) = ""
     * StringUtils.mid("han", 0, 1)   = "h"
     * StringUtils.mid("han", 0, 11)  = "han"
     * StringUtils.mid("han", 2, 3)   = "n"
     * StringUtils.mid("han", -2, 3)  = "han"
     * </pre>
     *
     * @param str 문자열
     * @param beginIndex 위치(음수일 경우는 0으로 자동 변환된다.)
     * @param length 길이
     * @return
     */
    public static String mid(String str, int beginIndex, int length) {
        if (str == null) {
            return null;
        }
        if (length < 0 || beginIndex > str.length()) {
            return EMPTY;
        }
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (str.length() <= (beginIndex + length)) {
            return str.substring(beginIndex);
        }
        return str.substring(beginIndex, beginIndex + length);
    }

    /**
     * <p>시작 인덱스부터 문자열을 자는다.</p> <p>시작 인덱스가 0보다 작거나, 문자열의 총길이보다 크면 공백("")을
     * 반환한다.</p>
     *
     * <pre>
     * StringUtils.substring(null, *)    = null
     * StringUtils.substring("", *)      = ""
     * StringUtils.substring("han", 1)   = "an"
     * StringUtils.substring("han", 615) = ""
     * StringUtils.substring("han", -1)  = ""
     * </pre>
     *
     * @param str
     * @param beginIndex 시작 인덱스(0부터 시작)
     * @return
     */
    public static String substring(String str, int beginIndex) {
        if (str == null) {
            return null;
        }

        if (beginIndex < 0) {
            return EMPTY;
        }

        if (beginIndex > str.length()) {
            return EMPTY;
        }

        return str.substring(beginIndex);
    }

    /**
     * <p>시작 인덱스부터 끝 인덱스까지 문자열을 자는다.</p> <p>시작 인덱스또는 끝 인덱스가 0보다 작으면 공백("")을
     * 반환한다.</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", *, *)      = ""
     * StringUtils.substring("han", 1, 2)   = "an"
     * StringUtils.substring("han", 1, 615) = "an"
     * StringUtils.substring("han", -1, *)  = ""
     * StringUtils.substring("han", *, -1)  = ""
     * </pre>
     *
     * @param str
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public static String substring(String str, int beginIndex, int endIndex) {
        if (str == null) {
            return null;
        }

        if (beginIndex < 0 || endIndex < 0) {
            return EMPTY;
        }

        if (endIndex > str.length()) {
            endIndex = str.length();
        }

        if (beginIndex > endIndex || beginIndex > str.length()) {
            return EMPTY;
        }

        return str.substring(beginIndex, endIndex);
    }

    /**
     * <p>처음 발견한 구분자의 위치까지 문자열을 자른다.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)       = null
     * StringUtils.substringBefore("", *)         = ""
     * StringUtils.substringBefore("han", null)   = "han"
     * StringUtils.substringBefore("han", "")     = ""
     * StringUtils.substringBefore("hanhan", "a") = "h"
     * StringUtils.substringBefore("hanhan", "g") = "hanhan"
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return
     */
    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int endIndex = str.indexOf(separator);
        if (endIndex == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, endIndex);
    }

    /**
     * <p>마지막으로 발견한 구분자의 위치까지 문자열을 자른다.</p>
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)       = null
     * StringUtils.substringBeforeLast("", *)         = ""
     * StringUtils.substringBeforeLast("han", null)   = "han"
     * StringUtils.substringBeforeLast("han", "")     = "han"
     * StringUtils.substringBeforeLast("hanhan", "a") = "hanh"
     * StringUtils.substringBeforeLast("hanhan", "g") = "hanhan"
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return
     */
    public static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        int endIndex = str.lastIndexOf(separator);
        if (endIndex == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, endIndex);
    }

    /**
     * <p>처음 발견한 구분자의 위치 다음부터 문자열을 자른다.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)       = null
     * StringUtils.substringAfter("", *)         = ""
     * StringUtils.substringAfter("han", null)   = ""
     * StringUtils.substringAfter("han", "")     = "han"
     * StringUtils.substringAfter("hanhan", "a") = "nhan"
     * StringUtils.substringAfter("hanhan", "g") = ""
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return
     */
    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int beginIndex = str.indexOf(separator);
        if (beginIndex == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        beginIndex = beginIndex + separator.length();
        if (beginIndex == str.length()) {
            return EMPTY;
        }
        return str.substring(beginIndex);
    }

    /**
     * <p>마지막으로 발견한 구분자의 위치 다음부터 문자열을 자른다.</p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)       = null
     * StringUtils.substringAfterLast("", *)         = ""
     * StringUtils.substringAfterLast("han", null)   = ""
     * StringUtils.substringAfterLast("han",     "") = ""
     * StringUtils.substringAfterLast("hanhan", "a") = "n"
     * StringUtils.substringAfterLast("hanhan", "g") = ""
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return
     */
    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        int beginIndex = str.lastIndexOf(separator);
        if (beginIndex == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        beginIndex = beginIndex + separator.length();
        if (beginIndex == str.length()) {
            return EMPTY;
        }
        return str.substring(beginIndex);
    }

    /**
     * <p>문자열이 해당 길이보다 크면, 자른 후 줄임말을 붙여준다.</p> <p>길이는 기본문자들(영어/숫자등)이 1으로,
     * 다국어(한글등)이면 2로 계산한다.</p>
     *
     * <pre>
     * StringUtils.curtail(null, *, *) = null
     * StringUtils.curtail("abcdefghijklmnopqr", 10, null) = "abcdefghij"
     * StringUtils.curtail("abcdefghijklmnopqr", 10, "..") = "abcdefgh.."
     * StringUtils.curtail("한글을 사랑합시다.", 10, null)   = "한글을 사랑"
     * StringUtils.curtail("한글을 사랑합시다.", 10, "..")   = "한글을 사.."
     * </pre>
     *
     *
     * @param str 문자열
     * @param size 길이(byte 길이)
     * @param tail 줄임말
     * @return
     */
    public static String curtail(String str, int size, String tail) {
        int strLen = str.length();
        int tailLen = (tail != null) ? tail.length() : 0;
        int maxLen = size - tailLen;
        int curLen = 0;
        int index = 0;
        for (; index < strLen && curLen < maxLen; index++) {
            if (Character.getType(str.charAt(index)) == Character.OTHER_LETTER) {
                curLen++;
            }
            curLen++;
        }

        if (index == strLen) {
            return str;
        } else {
            StringBuffer result = new StringBuffer();
            result.append(str.substring(0, index));
            if (tail != null) {
                result.append(tail);
            }
            return result.toString();
        }
    }

    // ----------------------------------------------------------------------
    // 패딩
    // ----------------------------------------------------------------------
    /**
     * <p>왼쪽부터 크기만큼 패딩문자로 채운다.</p>
     *
     * <pre>
     * StringUtils.leftPad("han", 5, " ")    = "  han"
     * StringUtils.leftPad("han", 5, "123")  = "12han"
     * StringUtils.leftPad("han", 10, "123") = "1231231han"
     * StringUtils.leftPad("han", -1, " ")   = "han"
     * </pre>
     *
     * @param str
     * @param size 크기
     * @param padStr 패딩문자
     * @return
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int padStrLen = padStr.length();
        int padLen = size - strLen;
        if (padLen <= 0) {
            // 패딩할 필요가 없음
            return str;
        }

        StringBuffer result = new StringBuffer();
        if (padLen == padStrLen) {
            result.append(padStr);
            result.append(str);
        } else if (padLen < padStrLen) {
            result.append(padStr.substring(0, padLen));
            result.append(str);
        } else {
            char[] padding = padStr.toCharArray();
            for (int i = 0; i < padLen; i++) {
                result.append(padding[i % padStrLen]);
            }
            result.append(str);
        }
        return result.toString();
    }

    /**
     * <p>오른쪽부터 크기만큼 패딩문자로 채운다.</p>
     *
     * <pre>
     * StringUtils.rightPad("han", 5, " ")    = "han  "
     * StringUtils.rightPad("han", 5, "123")  = "han12"
     * StringUtils.rightPad("han", 10, "123") = "han1231231"
     * StringUtils.rightPad("han", -1, " ")   = "han"
     * </pre>
     *
     * @param str
     * @param size 크기
     * @param padStr 패딩문자
     * @return
     */
    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int padStrLen = padStr.length();
        int padLen = size - strLen;
        if (padLen <= 0) {
            // 패딩할 필요가 없음
            return str;
        }

        StringBuffer result = new StringBuffer();
        if (padLen == padStrLen) {
            result.append(str);
            result.append(padStr);
        } else if (padLen < padStrLen) {
            result.append(str);
            result.append(padStr.substring(0, padLen));
        } else {
            result.append(str);
            char[] padding = padStr.toCharArray();
            for (int i = 0; i < padLen; i++) {
                result.append(padding[i % padStrLen]);
            }
        }
        return result.toString();
    }

    /**
     *
     * @param str
     * @param code
     * @param codeLen
     * @param flag
     * @return
     */
    public static String strEncode(String str, String code, int codeLen,
            int flag) {
        if (str == null) {
            return "";
        }

        if ("".equals(str)) {
            return str;
        }

        int strLen = str.length();

        if (strLen < codeLen) {
            return str;
        }

        String temp1 = "";
        String temp2 = "";
        String temp3 = "";
        int start = 0;
        int end = 0;

        if (flag == 0) {
            temp1 = str.substring(0, codeLen);
            start = 0;
            end = strLen - codeLen;
        } else if (flag == 1) {
            temp1 = str.substring(0, strLen - codeLen);
            start = strLen - codeLen;
            end = strLen;
        } else {
            return str;
        }

        for (int i = start; i < end; i++) {
            temp2 += "*";
        }

        temp3 = temp1 + temp2;

        return temp3;
    }

    // 계좌번호
    /**
     *
     * @param str
     * @return
     */
    public static String accountNumber(String str) {
        str = str.trim();
        int len = str.length();
        String temp = "";
        String strResult = "";
        for (int i = 5; i < len - 3; i++) {
            temp += "*";
        }

        if (len > 5) {
            strResult = str.substring(0, 5) + temp + str.substring(len - 3, len);
        }
        // return "*****"+ str.substring(5,str.trim().length()-2) + "**";
        // System.out.println("변환전 계좌번호 : " + str);
        // System.out.println("변환된 계좌번호 : " + strResult);
        return strResult;
        // return strEncode(str, "*", 7, 0);
    }

    // 휴대폰번호
    /**
     *
     * @param str
     * @return
     */
    public static String hpNumber(String str) {

        return strEncode(str, "*", 4, 1);
    }

    // 전화번호
    /**
     *
     * @param str
     * @return
     */
    public static String phoneNumber(String str) {

        return strEncode(str, "*", 4, 1);
    }

    // ID
    /**
     *
     * @param str
     * @return
     */
    public static String idNumber(String str) {

        return strEncode(str, "*", 2, 1);
    }

    // 이메일
    /**
     *
     * @param str
     * @return
     */
    public static String emailNumber(String str) {
        String[] strArr = null;
        String reStr = null;

        if (str != null) {
            strArr = str.split("@");

            if (strArr.length == 2) {
                reStr = strEncode(strArr[0], "*", 2, 1);
                reStr = reStr + "@" + strArr[1];
            } else {
                reStr = null;
            }

        }

        return reStr;
    }

    // 주민번호
    /**
     *
     * @param str
     * @return
     */
    public static String juminNumber(String str) {

        return strEncode(str, "*", 6, 1);
    }

    // 주민번호
    /**
     *
     * @param str
     * @return
     */
    public static String juminNumberD(String str) {
        String r = StringUtils.left(str, 6) + "-" + StringUtils.right(str, 7);
        return strEncode(r, "*", 6, 1);
    }

    // 비밀번호
    /**
     *
     * @param str
     * @return
     */
    public static String passNumber(String str) {
        return strEncode(str, "*", (str == null ? "" : str).length(), 1);
    }

    /**
     *
     * @param byteArray
     * @return
     */
    public static String prettyDump(byte byteArray[]) {
        return prettyDump(byteArray, 0, byteArray.length, 80, '\0');
    }

    /**
     *
     * @param byteArray
     * @param len
     * @return
     */
    public static String prettyDump(byte byteArray[], int len) {
        return prettyDump(byteArray, 0, byteArray.length, len, '\0');
    }

    /**
     *
     * @param sb
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     * @param indent
     */
    public static void prettyDump(StringBuffer sb, byte byteArray[], int offset, int length, int column, char separator, int indent) {
        int blockSize;
        if (separator == 0) {
            blockSize = 2 * column / 2;
        } else {
            blockSize = 2 * column / 3;
        }
        int leftOvers = length % blockSize;
        int lastBlockOffset = length - leftOvers;
        for (int i = 0; i < lastBlockOffset; i += blockSize) {
            if (indent > 0) {
                StringUtils.indent(sb, indent);
            }
            dumpString(sb, byteArray, offset + i, blockSize, separator);
            sb.append("\n");
        }
        if (indent > 0) {
            StringUtils.indent(sb, indent);
        }
        dumpString(sb, byteArray, lastBlockOffset, leftOvers, separator);
    }

    /**
     *
     * @param byteArray
     * @param offset
     * @param length
     * @param column
     * @param separator
     * @return
     */
    public static String prettyDump(byte byteArray[], int offset, int length, int column, char separator) {
        StringBuffer sb = new StringBuffer();
        prettyDump(sb, byteArray, offset, length, column, separator, 0);
        return sb.toString();
    }

    /**
     *
     * @param strBuffer
     * @param byteArray
     * @param offset
     * @param length
     * @param separator
     */
    public static void dumpString(StringBuffer strBuffer, byte byteArray[], int offset, int length, char separator) {
        if (byteArray == null || byteArray.length == 0) {
            return;
        }
        for (int i = offset; i < offset + length; i++) {
            appendHex(byteArray[i], strBuffer);
            if (separator != 0 && i != (offset + length) - 1) {
                strBuffer.append(separator);
            }
        }
    }

    /**
     *
     * @param hex
     * @param str
     */
    public static void appendHex(byte hex, StringBuffer str) {
        str.append((char) (hex));
    }

    /**
     *
     * @param dbl
     * @param frm
     * @return
     */
    public static String FormatNumber(long dbl, String frm) {
        String ret = "";
        try {
            ret = new DecimalFormat(frm).format(dbl);
        } catch (Exception e) {
            ret = String.valueOf(dbl);
        }
        return ret;
    }

    public static String repeat(String string, int count) {
        checkNotNull(string);  // eager for GWT.

        if (count <= 1) {
            checkArgument(count >= 0, "invalid count: %s", count);
            return (count == 0) ? "" : string;
        }

        // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark
        final int len = string.length();
        final long longSize = (long) len * (long) count;
        final int size = (int) longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required array size too large: "
                    + String.valueOf(longSize));
        }

        final char[] array = new char[size];
        string.getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(
            boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression,
            String errorMessageTemplate,
            Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
    }

    static String format(String template, Object... args) {
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(
                template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }
}
