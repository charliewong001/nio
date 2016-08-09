package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server {
    public static void main(String[] args)
            throws IOException, InterruptedException, ExecutionException {
        AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel
                .open();
        assc.bind(new InetSocketAddress("127.0.0.1", 1234));
        ByteBuffer buff = ByteBuffer.allocate(1024);
        // future(assc, buff);
        completeHandler(assc, buff);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    public static void future(AsynchronousServerSocketChannel assc,
            ByteBuffer buff) throws InterruptedException, ExecutionException {
        Future<AsynchronousSocketChannel> future = assc.accept();
        while (!future.isDone()) {
            System.out.println("waiting for accepting...");
            Thread.sleep(1000);
        }
        AsynchronousSocketChannel asc = future.get();
        Future<Integer> result = asc.read(buff);
        while (!result.isDone()) {
            System.out.println("waiting for handling...");
            Thread.sleep(1000);
        }
        buff.flip();
        while (buff.hasRemaining()) {
            char c = (char) buff.get();
            System.out.print(c);
        }
        System.out.println();
    }

    public static void completeHandler(AsynchronousServerSocketChannel assc,
            final ByteBuffer buff) {
        assc.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Object>() {

                    @Override
                    public void completed(AsynchronousSocketChannel result,
                            Object attachment) {
                        result.read(buff, null,
                                new CompletionHandler<Integer, Object>() {

                            @Override
                            public void completed(Integer result,
                                    Object attachment) {
                                buff.flip();
                                while (buff.hasRemaining()) {
                                    char c = (char) buff.get();
                                    System.out.print(c);
                                }
                                System.out.println();
                            }

                            @Override
                            public void failed(Throwable exc,
                                    Object attachment) {
                                // TODO Auto-generated method stub

                            }
                        });
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        // TODO Auto-generated method stub

                    }
                });
    }
}
