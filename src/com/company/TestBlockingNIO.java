package com.company;


import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingNIO {

    // 客户端
    @Test
    public void client() {
        FileChannel inChannel = null;
        SocketChannel sChannel = null;
        try {
            // 1. 获取通道
            sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));


            inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);


            // 2. 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 3. 读取本地文件，并发送到服务端
            while(inChannel.read(buf) != -1) {
                buf.flip();
                sChannel.write(buf);
                buf.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭通道
            if(inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(sChannel != null) {
                try {
                    sChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // 服务端
    @Test
    public void server() {

        ServerSocketChannel ssChannel = null;
        FileChannel outChannel = null;
        SocketChannel sChannel = null;
        try {
            // 1. 获取通道
            ssChannel = ServerSocketChannel.open();

            outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // 2. 绑定连接
            ssChannel.bind(new InetSocketAddress(9898));

            // 3. 获取客户端连接的通道
            sChannel = ssChannel.accept();

            // 4. 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 5. 接收客户端的数据，并保存到本地
            while(sChannel.read(buf) != -1) {
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
