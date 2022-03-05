package org.yh.handle;

import org.yh.Dispatcher;
import org.yh.request.Request;
import org.yh.response.Response;
import lombok.extern.log4j.Log4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

@Log4j
public class ClientHandle extends Thread{
    Socket client;
    public ClientHandle(Socket client) {
        this.client = client;

    }
    @Override
    public void run() {
        int available = 0;
        BufferedInputStream bufferedInputStream = null;
        try {
            log.info("客户端 " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + "请求连接" );
            bufferedInputStream  = new BufferedInputStream(client.getInputStream());
            available = bufferedInputStream.available();
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
            bufferedInputStream.read(buff, 0, available);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("请求内容:\n" + new String(buff));
        Request request = new Request(buff);//处理请求
        log.info(client.getPort());
        Dispatcher dispatcher = new Dispatcher(request, new Response(), client);
        dispatcher.doDispatch();
    }
}
