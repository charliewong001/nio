package aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AioFile {
    private static long size;// 总长度
    private static long count;// 已处理的长度

    public static void main(String[] args)
            throws IOException, InterruptedException, ExecutionException {
        AsynchronousFileChannel afc = AsynchronousFileChannel
                .open(Paths.get("E:/charlie/workspaces/default/nio/file"));
        ByteBuffer buff = ByteBuffer.allocate(20);
        future(afc, buff);
        // completionHandler(afc, buff);
    }

    public static void future(AsynchronousFileChannel afc, ByteBuffer buff)
            throws InterruptedException, ExecutionException, IOException {
        do {
            Future<Integer> result = afc.read(buff, count);
            while (!result.isDone()) {
                System.out.println("waiting file channel finished...");
                Thread.sleep(1);
            }
            System.out.println("Finished ? = " + result.isDone());
            System.out.println("byteBuffer = " + result.get());
            System.out.println(buff);
            if (size <= 0) {
                size = afc.size();
            }
            count += result.get();
            buff.flip();
            System.out.println(buff);
            while (buff.hasRemaining()) {
                char c = (char) buff.get();
                System.out.print(c);
            }
            buff.clear();
            System.out.println();
        } while (count < size);
        afc.close();
    }

    public static void completionHandler(AsynchronousFileChannel afc,
            ByteBuffer buff) throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        afc.read(buff, 0, null, new Handler(afc, buff));
        System.out.println("waiting for completion...");
        latch.await();
    }

    static class Handler implements CompletionHandler<Integer, Object> {

        private AsynchronousFileChannel afc;
        private ByteBuffer buff;
        private long size;// 总长度
        private long count;// 已处理的长度

        public Handler(AsynchronousFileChannel afc, ByteBuffer buff) {
            this.afc = afc;
            this.buff = buff;
            try {
                this.size = afc.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void completed(Integer result, Object param) {
            System.out.println("Bytes read = " + result);
            buff.flip();
            while (buff.hasRemaining()) {
                char c = (char) buff.get();
                System.out.print(c);
            }
            System.out.println();
            buff.clear();
            count += result;
            if (count < size) {
                afc.read(buff, count, null, this);
            }
        }

        @Override
        public void failed(Throwable paramThrowable, Object param) {
            System.out.println(paramThrowable.getCause());
        }

    }
}
