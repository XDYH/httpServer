package org.yh.util;

import org.yh.response.Response;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class ResponseToByte {
    private static final String CRLF = "\r\n";
    public static byte[] responseToByte(Response response) {
        StringBuilder responseString = new StringBuilder();
        //起始行
        responseString.append("HTTP/1.0").append(" ")
                .append(response.getStatusCode()).append(" ")
                .append(response.getReasonPhrase()).append(CRLF).append(CRLF);
        //首部字段
        Set<Map.Entry<String, String>> heads = response.getHeads().entrySet();
        for (Map.Entry<String, String> head : heads) {
            responseString.append(head.getKey()).append(": ")
                    .append(head.getValue()).append(CRLF);
        }
        responseString.append(CRLF);

        //内容实体:将请求头转为byte数组后与body合并
        byte[] headByte = responseString.toString().getBytes(StandardCharsets.UTF_8);
        int headLength = headByte.length;
        int length = headLength + response.getBody().length;
        byte[] result = new byte[length];
        System.arraycopy(headByte, 0, result, 0, headLength);
        System.arraycopy(response.getBody(), 0, result, headLength, response.getBody().length);
        return result;
    }
}
