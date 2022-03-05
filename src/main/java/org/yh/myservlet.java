package org.yh;

import org.yh.request.Request;
import org.yh.response.Response;
import org.yh.servlet.HttpServlet;

import java.nio.charset.StandardCharsets;

public class myservlet extends HttpServlet {
    @Override
    public void doGet(Request req, Response resp) {
        resp.setBody("123".getBytes(StandardCharsets.UTF_8));
    }
}
