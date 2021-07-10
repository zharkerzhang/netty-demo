package com.zharker.netty.serialize;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;
@Builder
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userID;
    private String userName;

    public byte[] code(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        return code_(buffer);
    }
    public byte[] code(ByteBuffer buffer){
        buffer.clear();
        return code_(buffer);
    }

    private byte[] code_(ByteBuffer buffer) {
        byte[] value = userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(userID);
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
