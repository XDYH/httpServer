package org.yh;

import org.yh.enumeration.Method;
import org.yh.request.Request;
import org.yh.response.Response;
import org.yh.servlet.HttpServlet;
import org.yh.util.ResponseToByte;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Log4j
@AllArgsConstructor
public class Dispatcher {
    private Request request;
    private Response response;
    private Socket client;

    public void doDispatch() {

        HttpServlet servlet = null;
        //通过url获得servlet-name，再通过servlet-name获得servlet-class
        String servletName = WebConfig.getServletName(request.getUrl());
        if (servletName == null) {
            return;
        }
        String servletClass = WebConfig.getServletClass(servletName);
        if (servletClass == null) {
            return;
        }
        try {

            servlet = (HttpServlet) Class.forName(servletClass).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (servlet != null) {
            if (request.getMethod() == Method.GET) {
                servlet.doGet(request, response);
            }else if (request.getMethod() == Method.POST) {
                servlet.doPost(request, response);
            }
            log.info("请求servlet处理完毕");
        }
        flushResponse();
    }

    private void flushResponse(){
        try {

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(ResponseToByte.responseToByte(response));
            outputStream.close();
            client.close();
            log.info("请求完成，连接已关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
