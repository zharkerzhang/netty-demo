package com.zharker.netty.codec.protobuf;

import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqId(1);
        builder.setUserName("test user name req");
        builder.setProductName("test product name req");
        List<String> address = Lists.newArrayList("nanjing yuhuatai","beijing liulicha","shenzhen hongshulin");
        builder.addAllAddress(address);
        return builder.build();
    }

    @Test
    public void test() throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("before encode: "+req.toString());
        SubscribeReqProto.SubscribeReq reqDecodeEncode = decode(encode(req));
        System.out.println("after decode encode: "+reqDecodeEncode.toString());
        Assert.assertEquals(reqDecodeEncode,req);
    }
}
