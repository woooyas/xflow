package com.nhnacademy.aiot.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP 메세지를 파싱하는 유틸리티 클래스입니다.
 */
public class HTTPMessages {

    private static final String CRLF = "\r\n\r\n";

    private HTTPMessages() {}

    /**
     * HTTP 메세지에 헤더가 있는지 확인합니다.
     * 
     * @param byteData 확인할 HTTP 메세지입니다.
     * @return HTTP 메세지에 헤더가 있으면 <code>true</code>를 반환합니다.
     */
    public static boolean hasHeaders(byte[] byteData) {
        return new String(byteData).contains(CRLF);
    }

    /**
     * HTTP 메세지에 특정 헤더가 있는지 확인합니다.
     * 
     * @param byteData 확인할 HTTP 메세지입니다.
     * @param header 헤더의 이름입니다.
     * @return HTTP 메세지에 특정 헤더가 있으면 <code>true</code>를 반환합니다.
     */
    public static boolean hasHeader(byte[] byteData, String header) {
        return new String(byteData).contains(header);
    }

    /**
     * HTTP 메세지에서 바디를 가져옵니다.
     * 
     * @param byteData HTTP 메세지입니다.
     * @return HTTP 메세지에서 바디를 문자열로 반환합니다.
     */
    public static String getBody(byte[] byteData) {
        return new String(byteData).split(CRLF)[1];
    }

    /**
     * HTTP 메세지에서 헤더의 값을 가져옵니다.
     * 
     * @param byteData HTTP 메세지입니다.
     * @param header 헤더의 이름입니다.
     * @return HTTP 메세지에서 헤더의 값을 문자열로 반환합니다.
     */
    public static String getHeaderValue(byte[] byteData, String header) {
        String requestString = new String(byteData);
        Pattern pattern = Pattern.compile(header + ": .+\r\n");
        Matcher matcher = pattern.matcher(requestString);

        matcher.find();

        return requestString.substring(matcher.start() + header.length() + 2, matcher.end() - 2);
    }
}
