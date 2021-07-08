package com.zharker.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeClient implements Runnable{
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeClient timeClient = new MultiplexerTimeClient("127.0.0.1",port);
        new Thread(timeClient,"nio-MultiplexerTimeClient-001").start();
    }

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public MultiplexerTimeClient(String host,int port){
        this.host = host==null?"127.0.0.1":host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        while(!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while (it.hasNext()){
                    selectionKey = it.next();
                    it.remove();
                    try {
                        handleInput(selectionKey);
                    }catch (Exception e){
                        if(selectionKey != null){
                            selectionKey.cancel();
                            if(selectionKey.channel() != null){
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        if(key.isValid()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(socketChannel.finishConnect()){
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    doWrite(socketChannel);
                }else {
                    System.exit(1);
                }
            }else if(key.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if(readBytes>0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String response = new String(bytes,"utf-8");
                    System.out.println("get time from TimeServer: "+response);
                    stop = true;
                }else if(readBytes<0){
                    key.cancel();
                    socketChannel.close();
                }else{}
            }
        }
    }

    private void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector,SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else {
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] requestBytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(requestBytes.length);
        writeBuffer.put(requestBytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
        if(!writeBuffer.hasRemaining()){
            System.out.println("has send order to server");
        }
    }
}
