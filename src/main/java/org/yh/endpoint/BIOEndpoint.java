package org.yh.endpoint;

import org.yh.handle.ClientHandle;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j
public class BIOEndpoint {

    public BIOEndpoint(int port) {
        //TODO: 线程池管理
        startListing(port);

    }
    private void startListing(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            log.error("端口监听失败,请重试或更换端口");
            return;
        }
        log.info("正在监听端口" + port);
        while (true) {
            log.info("开始扫描");
            Socket client = null;

            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            addNewClient(client);
        }
    }
    public void addNewClient(Socket client){
        Thread t = new ClientHandle(client);
        t.start();
        log.info("对客户端 " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + "处理中...");
    }
}
