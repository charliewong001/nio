package multiThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class TalkClient extends Thread {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void run() {
        PrintWriter writer = null;
        Socket socket = null;
        try {
            socket = new Socket(Const.SERVER, Const.PORT);
            writer = new PrintWriter(socket.getOutputStream());
            writer.println("h");
            LockSupport.parkNanos(1000 * 1000 * 1000);
            writer.println("e");
            LockSupport.parkNanos(1000 * 1000 * 1000);
            writer.println("l");
            LockSupport.parkNanos(1000 * 1000 * 1000);
            writer.println("l");
            LockSupport.parkNanos(1000 * 1000 * 1000);
            writer.println("o");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            executor.submit(new TalkClient());
        }
    }

}
