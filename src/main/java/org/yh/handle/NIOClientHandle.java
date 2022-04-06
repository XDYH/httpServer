package org.yh.handle;


import lombok.extern.log4j.Log4j;
import org.yh.Dispatcher;
import org.yh.request.Request;
import org.yh.response.Response;
import org.yh.util.ResponseToByte;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

@Log4j
public class NIOClientHandle extends Thread{
    ArrayList<Byte> buf;
    SocketChannel socketChannel;
    Response response = new Response();
    public NIOClientHandle(ArrayList<Byte> buf, SocketChannel socketChannel) {
        this.buf = buf;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {

        byte[] arrayBuff = new byte[buf.size()];
        for (int i = 0; i < arrayBuff.length; i++) {
            arrayBuff[i] = buf.get(i);
        }
        log.debug("请求内容:\n" + new String(arrayBuff));
        Request request = new Request(arrayBuff);
        Dispatcher dispatcher = new Dispatcher(request, response);
        dispatcher.doDispatch();
        flushResponse();

    }
    private void flushResponse(){
        try {
            byte[] responseBuff = ResponseToByte.responseToByte(response);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(responseBuff.length);
            byteBuffer.put(responseBuff);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            socketChannel.close();
            log.info("请求完成，连接已关闭");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
