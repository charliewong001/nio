package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    public static void main(String[] args)
            throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel asc = AsynchronousSocketChannel.open();
        ByteBuffer buff = ByteBuffer.allocate(1024);
        // future(asc, buff);
        completeHandler(asc, buff);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    public static void future(AsynchronousSocketChannel asc, ByteBuffer buff)
            throws InterruptedException, ExecutionException {
        Future<?> connect = asc
                .connect(new InetSocketAddress("127.0.0.1", 1234));

        connect.get();

        buff.clear();
        buff.put("hello world".getBytes());
        buff.flip();
        Future<Integer> result = asc.write(buff);
        System.out.println(result.get());

    }

    public static void completeHandler(AsynchronousSocketChannel asc,
            ByteBuffer buff) {
        asc.connect(new InetSocketAddress("127.0.0.1", 1234), null,
                new CompletionHandler<Void, Object>() {

                    @Override
                    public void completed(Void result, Object attachment) {

                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {

                    }
                });
        buff.clear();
        buff.put("hello world".getBytes());
        buff.flip();
        asc.write(buff, null, new CompletionHandler<Integer, Object>() {

            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("send content length = " + result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {

            }
        });
    }
}
