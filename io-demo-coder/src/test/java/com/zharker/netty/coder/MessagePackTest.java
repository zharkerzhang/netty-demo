package com.zharker.netty.coder;

import org.junit.Assert;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.List;

public class MessagePackTest {

    @Test
    public void test() throws IOException {
        List<String> src = List.of("a","b","c");
        MessagePack messagePack = new MessagePack();
        byte[] raw = messagePack.write(src);
        List<String> dst = messagePack.read(raw, Templates.tList(Templates.TString));
        for (int i = 0; i < dst.size(); i++) {
            Assert.assertEquals(dst.get(i),src.get(i));
        }
    }
}
