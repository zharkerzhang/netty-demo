package com.zharker.netty.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class TestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = UserInfo.builder().userID(100).userName("jojo hansel").build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();
        byte[] bytes = bos.toByteArray();
        bos.close();

        System.out.println("jdk serializable length : "+bytes.length);
        bytes = userInfo.code();
        System.out.println("byte array serializable length : "+bytes.length);

        System.out.println("=====================");
        int loop = 1000_000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(userInfo);
            os.flush();
            os.close();
            bytes = bos.toByteArray();
            bos.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("jdk serializable cost time : " + (end-start) + "ms");

        start = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < loop; i++) {
            bytes = userInfo.code(buffer);
        }
        end = System.currentTimeMillis();
        System.out.println("byte array serializable cost time : " + (end-start) + "ms");



    }
}
