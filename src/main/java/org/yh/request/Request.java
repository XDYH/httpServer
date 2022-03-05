package org.yh.request;

import org.yh.enumeration.Method;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {
    @Getter private Method method;
    @Getter private String url;
    private Map<String, String> heads = new HashMap<>();
    @Getter private String body;
    public Request(byte[] data){
        String[] lines;
        lines = new String(data, StandardCharsets.UTF_8).split("\r\n");//将数据解析为文本,按行分离

        String[] splitFirstLine = lines[0].split(" ");//分割起始行

        String stringMethod = splitFirstLine[0];//获取请求方法
        if (stringMethod.equals("GET")) {
            method = Method.GET;
        } else if (stringMethod.equals("POST")) {
            method = Method.POST;
        }

        url = splitFirstLine[1];//获取请求url


        for (int i = 2; i < lines.length; i++) {
            if (lines[i].equals("")) {
                break;
            }
            String[] line = lines[i].split(": ");//分割首部字段名称和参数
            heads.put(line[0], line[1]);
        }

        int lineNum = lines.length;
        body = lines[lineNum - 1];
    }
    public String getHead(String name) {
        return heads.get(name);
    }
}
