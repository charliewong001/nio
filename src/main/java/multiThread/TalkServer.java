package multiThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TalkServer extends Thread {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private Socket socket;

    public TalkServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader clientReader = null;
        try {
            clientReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String msg = null;
            long start = System.currentTimeMillis();
            msg = clientReader.readLine();
            while (msg != null) {
                msg = clientReader.readLine();
            }
            long end = System.currentTimeMillis();

            System.out.println(Thread.currentThread().getId() + " spend :"
                    + (end - start));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientReader != null) {
                try {
                    clientReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(Const.PORT);
            while (true) {
                Socket socket = server.accept();
                executor.execute(new TalkServer(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
