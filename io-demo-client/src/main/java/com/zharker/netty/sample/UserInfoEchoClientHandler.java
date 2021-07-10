package com.zharker.netty.sample;

import com.zharker.netty.serialize.UserInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
public class UserInfoEchoClientHandler extends ChannelHandlerAdapter {
    private int sendNumber;
    private static final String ECHO_REQ = "hi, welcome to netty.$_";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo[] infos = getUserInfos();
        for (int i = 0; i < infos.length; i++) {
            ctx.write(infos[i]);
        }
        ctx.flush();
    }

    private UserInfo[] getUserInfos() {
        UserInfo[] infos = new UserInfo[sendNumber];
        UserInfo userInfo = null;
        for (int i = 0; i < sendNumber; i++) {
            userInfo = UserInfo.builder().userID(i).userName("name_"+(sendNumber-i)).build();
            infos[i] = userInfo;
        }
        return infos;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("receive msgpackage message from server: ["+msg+"]");
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
