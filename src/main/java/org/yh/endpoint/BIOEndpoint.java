package org.yh.endpoint;

import org.yh.handle.BIOClientHandle;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j
public class BIOEndpoint {
    ThreadPoolExecutor threadPoolExecutor;
    public BIOEndpoint(int port) {
         this.threadPoolExecutor =
                new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                                        new ArrayBlockingQueue<Runnable>(5));

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

            if (client != null) {
                addNewClient(client);
            }
        }
    }
    public void addNewClient(Socket client){
        threadPoolExecutor.execute(new BIOClientHandle(client));
        log.info("对客户端 " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + "处理中...");
    }
}
