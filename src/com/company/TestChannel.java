package com.company;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class TestChannel {

    /**
     * 1. 利用通道完成文件的复制(非直接缓冲区)
     */
    @Test
    public void test1() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            // 1. 获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            // 2. 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 3. 将通道中的数据存入缓冲区中
            while (inChannel.read(buf) != -1) {
                buf.flip();// 切换读数据的模式
                // 4. 将缓冲区中的数据写入通道中
                outChannel.write(buf);
                buf.clear(); // 清空缓冲区
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 2. 使用直接缓冲区完成文件的复制(内存映射文件)
     */
    @Test
    public void test2() {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            // 创建通道
            inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

            // 内存映射文件
            MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            // 直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMappedBuf.limit()];
            inMappedBuf.get(dst);
            outMappedBuf.put(dst);

            // 关闭流
            inChannel.close();
            outChannel.close();

        } catch (IOException e) {

        } finally {

        }
    }

    /**
     * 3. 通道之间的数据传输(直接缓冲区)
     */
    @Test
    public void test3() {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

            //inChannel.transferTo(0, inChannel.size(), outChannel);
            outChannel.transferFrom(inChannel, 0, inChannel.size());

            inChannel.close();
            outChannel.close();
        } catch (IOException e) {

        } finally {

        }
    }

    /**
     * 4. 分散和聚集
     * 分散读取
     * 聚集写入
     */
    @Test
    public void test4() {
        RandomAccessFile raf1 = null;
        RandomAccessFile raf2 = null;
        FileChannel channel1 = null;
        FileChannel channel2 = null;
        try {
            raf1 = new RandomAccessFile("1.txt", "rw");

            // 1. 获取通道
            channel1 = raf1.getChannel();

            // 2. 分配指定大小的缓冲区
            ByteBuffer buf1 = ByteBuffer.allocate(100);
            ByteBuffer buf2 = ByteBuffer.allocate(1024);

            // 3. 分散读取
            ByteBuffer[] bufs = {buf1, buf2};
            channel1.read(bufs);

            // 切换读模式
            for (ByteBuffer byteBuffer : bufs)
                byteBuffer.flip();

            System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
            System.out.println("-----------------------");
            System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

            // 4. 聚集写入
            raf2 = new RandomAccessFile("2.txt", "rw");
            channel2 = raf2.getChannel();
            channel2.write(bufs);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(raf1 != null) {
                try {
                    raf1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(raf2 != null) {
                try {
                    raf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(channel1 != null) {
                try {
                    channel1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(channel2 != null) {
                try {
                    channel2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 5. 遍历字符集
     */
    @Test
    public void test5() {
        Map<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> set = map.entrySet();

        for(Map.Entry<String, Charset> entry : set) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    /**
     * 6. 字符集 编码、解码
     */
    @Test
    public void test6() {
        Charset cs1 = Charset.forName("GBK");

        // 获取编码器
        CharsetEncoder ce = cs1.newEncoder();

        // 获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        // 创建Char缓冲区
        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("字符集测试! ");
        cBuf.flip();

        ByteBuffer bBuf = null;
        try {
            // 编码
            bBuf = ce.encode(cBuf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 12; i++)
            System.out.println(bBuf.get());

        // 解码
        bBuf.flip();
        CharBuffer cBuf2 = null;
        try {
            cBuf2 = cd.decode(bBuf);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        System.out.println(cBuf2.toString());

        System.out.println("--------------------------------");

//        Charset cs2 = Charset.forName("UTF-8");
        Charset cs2 = Charset.forName("GBK");
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3.toString());
    }
}
