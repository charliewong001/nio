package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ss = (ServerSocketChannel) key.channel();
        SocketChannel sc = (SocketChannel) ss.accept();
        if (sc != null) {
            sc.configureBlocking(false);
            sc.register(key.selector(), SelectionKey.OP_READ,
                    ByteBuffer.allocate(1024));

            System.out.println("accept");
        }
    }

    public static void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        long read = sc.read(buffer);
        while (read > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                char c = (char) buffer.get();
                System.out.print(c);
            }
            System.out.println();
            buffer.clear();
            read = sc.read(buffer);
        }
        if (read == -1) {
            sc.close();
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();
        while (buffer.hasRemaining()) {
            sc.write(buffer);
        }
        buffer.compact();
    }

    public static void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(1234));
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select(3000) == 0) {
                    System.out.println("==");
                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable() && key.isValid()) {
                        handleWrite(key);
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable = true");
                    }
                    keys.remove(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        Server.selector();
    }
}
