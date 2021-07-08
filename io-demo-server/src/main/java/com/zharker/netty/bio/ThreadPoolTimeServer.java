package com.zharker.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPoolTimeServer {

    public static void main(String[] args) {
        int port = 8080;
        ServerSocket server = null;
        TimeServerHandlerExecutePool executor = null;
        try {
            server = new ServerSocket(port);
            Socket socket = null;
            executor = new TimeServerHandlerExecutePool(50,10000);
            while(true){
                socket = server.accept();
                executor.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server = null;
            }
            if (executor != null){
                executor.close();
                executor = null;
            }
        }
    }
}
