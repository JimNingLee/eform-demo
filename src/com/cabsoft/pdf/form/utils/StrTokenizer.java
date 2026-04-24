package com.cabsoft.pdf.form.utils;

/**
 * <p>StrTokenizer 클래스를 사용하면, 문자열을 구분자로 분할 할 수 있습니다.</p>
 * <pre> 구분자 사이에 아무것도 없으면, 공백문자를 반환한다.
 * ex) 1||3 -> 1, 공백, 3 이렇게 3개의 토큰을 반환한다.
 * </pre>
 */
public class StrTokenizer {

    private int currentPosition;
    private int maxPosition;
    private String str;
    private String delimiter;
    private int delimiterSize;
    private int countTokens = -1;	// Token의 갯수

    /**
     * 대상 문자열에 대한 Tokenizer를 생성합니다. 구분자는 기본값(" ")으로 사용합니다.
     *
     * @param str 대상 문자열
     */
    public StrTokenizer(String str) {
        this(str, " ");
    }

    /**
     * 대상 문자열에 대한 Tokenizer를 생성합니다. delim는 구분자이니다.
     *
     *
     * @param str 대상 문자열
     * @param delim 구분자
     * @throws NullPointerException - str 가 null 경우
     */
    public StrTokenizer(String str, String delim) {
        this.str = str;
        this.delimiter = delim;
        currentPosition = 0;
        maxPosition = this.str.length();
        delimiterSize = this.delimiter.length();
    }

    /**
     * 이용할 수 있는 토큰이 더 존재하지를 판단합니다. 이 메서드가 <code>true</code<를 돌려주는 경우, nextToken 메소드를 호출해 다음 토큰을 가져갈 수 있습니다.
     *
     * @return 현 위치의 뒤로, 1개 이상의 토큰이 존재할 경우 true, 그렇지 않을 경우 false
     */
    public boolean hasMoreTokens() {
        return ((currentPosition <= maxPosition) || (str.indexOf(delimiter, currentPosition) > -1));
    }

    /**
     * 다음 토큰을 반환한다.
     * 반환할 토큰이 없으면 null을 돌려준다.
     *
     * @return 다음 토큰
     */
    public String nextToken() {
        String result = null;
        int index = str.indexOf(delimiter, currentPosition);
        if (index > -1) {
            result = str.substring(currentPosition, index);
            currentPosition = index + delimiterSize;
        } else {
            result = str.substring(currentPosition);
            currentPosition = maxPosition + delimiterSize;
        }
        return result;
    }

    /**
     * 토큰의 총 갯수를 반환한다.
     *
     * @return 토큰의 총 갯수
     */
    public int countTokens() {
        if (countTokens < 0) {
            int tCountTokens = 1;
            int cPos = 0;
            int index = 0;
            while ((index = str.indexOf(delimiter, cPos)) > -1) {
                tCountTokens++;
                cPos = index + delimiterSize;
            }
            countTokens = tCountTokens;
        }
        return countTokens;
    }

    /**
     * 배열 크기 만큼의 문자열배열을 만든후, 토큰 값을 채워넣은 후 반환한다.
     * 토큰 갯수가 배열 크기보다 작을 경우는, 나머지 값을 null로 설정한고,
     * 토큰 갯수가 배열 크기보다 클 경우는, 배열 크기만큼만 값을 설정한다.
     *
     * @param arraySize 배열 크기
     * @return
     */
    @SuppressWarnings("javadoc")
	public String[] toArray(int arraySize) {
        String[] array = new String[arraySize];
        for (int i = 0; i < arraySize && hasMoreTokens(); i++) {
            array[i] = nextToken();
        }
        return array;
    }

    /**
     * 토큰들을 문자열배열로 만든다.
     * 문자열배열의 크기는 토큰의 크기로 만들어진다.
     *
     * @return 문자열배열
     */
    public String[] toArray() {
        return toArray(countTokens());
    }
}
