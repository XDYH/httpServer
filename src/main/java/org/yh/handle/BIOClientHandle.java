package org.yh.handle;

import org.yh.Dispatcher;
import org.yh.request.Request;
import org.yh.response.Response;
import lombok.extern.log4j.Log4j;
import org.yh.util.ResponseToByte;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Log4j
public class BIOClientHandle extends Thread{
    Socket client;
    InputStream inputStream;
    Response response = new Response();
    public BIOClientHandle(Socket client) {
        this.client = client;

    }
    @Override
    public void run() {
        int available = 0;

        try {
            log.info("客户端 " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + "请求连接" );
            this.inputStream = client.getInputStream();

            available = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (available < 1){
            log.warn("无效的连接");
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        byte[] buff = new byte[available];
        try {
            inputStream.read(buff, 0, available);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("请求内容:\n" + new String(buff));
        Request request = new Request(buff);//处理请求
        log.info(client.getPort());
        Dispatcher dispatcher = new Dispatcher(request, response, client);
        dispatcher.doDispatch();
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
