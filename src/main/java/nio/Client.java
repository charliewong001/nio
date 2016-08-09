package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class Client extends Thread {

    private String name;

    public Client(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new Client("t1"));
        Thread t2 = new Thread(new Client("t2"));
        Thread t3 = new Thread(new Client("t3"));
        t1.start();
        Thread.sleep(2000);
        t2.start();
        Thread.sleep(2000);
        t3.start();
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress("127.0.0.1", 1234));
            if (sc.finishConnect()) {
                int i = 0;
                while (i <= 3) {
                    TimeUnit.SECONDS.sleep(1);
                    String info = "thread name = " + name + ";i am " + ++i
                            + "-th information from client";
                    buffer.clear();
                    buffer.put(info.getBytes("utf-8"));
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        sc.write(buffer);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
