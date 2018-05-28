package com.company;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 缓冲区中的四个核心属性
 * capacity : 容量，表示缓冲区最大存储数据的容量。一旦声明不能改变。
 * limit : 界限，表示缓冲区中可以操作的数据的大小。（limit 后数据不能进行读写）
 * position : 位置，表示缓冲区正则操作数据的位置。
 *
 * mark : 标记，表示记录当前 position 的位置。可以通过reset() 恢复到mark 的位置
 *
 * 0 <= mark <= position <= limit <= capacity
 */
public class TestBuffer {
    @Test
    public void test3() {
        // 分配直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer.isDirect());
    }
    @Test
    public void test2() {
        String str = "abcde";

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());

        buffer.flip();
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst, 0, 2);
        System.out.println(new String(dst, 0, 2));

        System.out.println(buffer.position());

        // mark() : 标记
        buffer.mark();

        buffer.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));
        System.out.println(buffer.position());

        // reset() : 恢复到 mark 的位置
        buffer.reset();
        System.out.println(buffer.position());

        // 判断缓存区中是否还有剩余的数据
        if(buffer.hasRemaining()) {
            //获取缓冲区中可以操作的数据数量
            System.out.println(buffer.remaining());
        }
    }
    @Test
    public void test1() {
        String str = "abcde";

        // 1. 分配一个指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("-------------allocate()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 2. 利用put() 存入数据到缓冲区中
        buffer.put(str.getBytes());
        System.out.println("-------------put()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 3. 切换到读数据模式
        buffer.flip();
        System.out.println("-------------flip()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 4. 利用 get() 读取缓冲区中的数据
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        System.out.println(new String(dst, 0, dst.length));

        System.out.println("-------------put()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 5. rewind() : 可重复读
        buffer.rewind();
        System.out.println("-------------rewind()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        // 6. clear() : 清空缓冲区, 但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        buffer.clear();
        System.out.println("-------------clear()---------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println((char) buffer.get());
    }
}
