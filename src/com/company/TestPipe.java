package com.company;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {
    @Test
    public void test1() {

        Pipe pipe = null;
        Pipe.SourceChannel sourceChannel = null;
        Pipe.SinkChannel sinkChannel = null;
        try {
            // 1. 获取管道
            pipe = Pipe.open();

            // 2. 将缓冲区中的数据写入管道
            ByteBuffer buf  = ByteBuffer.allocate(1024);

            sinkChannel = pipe.sink();
            buf.put("通过单项管道发送数据".getBytes());
            buf.flip();
            sinkChannel.write(buf);


            // 3. 读取缓冲区中的数据
            sourceChannel = pipe.source();
            buf.flip();
            int len = sourceChannel.read(buf);
            System.out.println(new String(buf.array(), 0, len));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(sourceChannel != null) {
                try {
                    sourceChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(sinkChannel != null) {
                try {
                    sinkChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
