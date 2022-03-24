package org.yh.endpoint;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
@Log4j
public class NIOEndpoint {
    int port;
    Selector selector;
    ServerSocketChannel serverSocketChannel;


    public NIOEndpoint(int port) {
        this.port = port;
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    private void start() {
       while (true) {
           Iterator<SelectionKey> selectionKeyIterator = selector.keys().iterator();
           while (selectionKeyIterator.hasNext()) {
               SelectionKey selectionKey = selectionKeyIterator.next();
               selectionKeyIterator.remove();
               if (selectionKey.isValid()) {
                   continue;
               }
               if (selectionKey.isAcceptable()) {
                   accept(selectionKey);
               }
           }

       }
    }
    private void accept(SelectionKey key) {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.info(socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
