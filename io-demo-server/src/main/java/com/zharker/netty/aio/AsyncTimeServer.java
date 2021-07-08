package com.zharker.netty.aio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AsyncTimeServer implements Runnable{
    public static void main(String[] args) {
        int port = 8080;
        AsyncTimeServer asyncTimeServer = new AsyncTimeServer(port);
        new Thread(asyncTimeServer,"aio-AsyncTimeServer-001").start();
    }

    private int port;
    private CountDownLatch countDownLatch;
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServer(int port){
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        doAccept();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AsyncTimeServer attachment) {
                attachment.asynchronousServerSocketChannel.accept(attachment, this);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                result.read(buffer, buffer, new ReadCompletionHandler(result));
            }

            @Override
            public void failed(Throwable exc, AsyncTimeServer attachment) {
                exc.printStackTrace();
                attachment.countDownLatch.countDown();
            }
        });
    }
}
