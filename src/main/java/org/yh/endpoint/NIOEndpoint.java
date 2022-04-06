package org.yh.endpoint;

import lombok.extern.log4j.Log4j;
import org.yh.handle.NIOClientHandle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j
public class NIOEndpoint {
    private final ThreadPoolExecutor threadPoolExecutor;

    Selector selector;
    ServerSocketChannel serverSocketChannel;


    public NIOEndpoint(int port) {
        this.threadPoolExecutor =
                new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(5));
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            this.selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    private void start() {
       while (true) {
           try {
               if(selector.select(3000) == 0){
                   System.out.println("==");
                   continue;
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
           while (selectionKeyIterator.hasNext()) {
               SelectionKey selectionKey = selectionKeyIterator.next();
               selectionKeyIterator.remove();
               if (selectionKey.isAcceptable()) {
                   accept(selectionKey);
               }
               if (selectionKey.isReadable()) {
                   SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                   ArrayList<Byte> buf = new ArrayList<>(1024);
                   ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                   while (true) {
                       try {
                           if ((socketChannel.read(byteBuffer) < 1)) break;
                           byteBuffer.flip();
                           while (byteBuffer.hasRemaining()) {
                               buf.add(byteBuffer.get());
                           }
                           byteBuffer.clear();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   if (buf.size() < 1) {
                       continue;
                   }
                   Thread t = new NIOClientHandle(buf, socketChannel);
                   threadPoolExecutor.execute(t);
               }
               if (selectionKey.isWritable() && selectionKey.isValid()) {
                   ByteBuffer buf = (ByteBuffer)selectionKey.attachment();
                   buf.flip();
                   SocketChannel sc = (SocketChannel) selectionKey.channel();
                   while(buf.hasRemaining()){
                       try {
                           sc.write(buf);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   buf.compact();
               }
           }

       }
    }
    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            log.info(socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
