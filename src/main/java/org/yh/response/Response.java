package org.yh.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Response {
    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private Map<String, String> heads = new HashMap<>();
    private byte[] body;
    public void addHead(String name, String parameter) {
        heads.put(name, parameter);
    }
}
