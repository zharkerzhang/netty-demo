package com.zharker.netty.aio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AsyncTimeClient implements Runnable, CompletionHandler<Void,AsyncTimeClient> {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClient("127.0.0.1",port),"aio-AsyncTimeClient-001").start();
    }

    private String host;
    private int port;
    private CountDownLatch countDownLatch;
    private AsynchronousSocketChannel client;

    public AsyncTimeClient(String host,int port){
        this.host = host;
        this.port = port;
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host,port),this,this);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClient attachment) {
        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()){
                    client.write(buffer,buffer,this);
                }else{
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<>() {
                        @Override
                        public void completed(Integer result, ByteBuffer readBuffer) {
                            readBuffer.flip();
                            byte[] readBytes = new byte[readBuffer.remaining()];
                            readBuffer.get(readBytes);
                            try {
                                String body = new String(readBytes,"utf-8");
                                System.out.println("get time from TimeServer: "+body);
                                countDownLatch.countDown();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            failedHandle(exc);
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                failedHandle(exc);
            }
        });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClient attachment) {
        failedHandle(exc);
    }

    private void failedHandle(Throwable exc){
        exc.printStackTrace();
        try {
            client.close();
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
