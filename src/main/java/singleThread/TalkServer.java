package singleThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer {

    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(Const.PORT);
            Socket socket = server.accept();
            System.out.println("client has connected!");
            System.out.println("-------------------------");
            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedReader serverReader = new BufferedReader(
                    new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            String msg = clientReader.readLine();
            String serverMsg = null;
            while (msg != null && !"".equals(msg)) {
                System.out.println("\t\t client : " + msg);
                if ("bye".equalsIgnoreCase(msg)) {
                    break;
                }
                serverMsg = serverReader.readLine();
                writer.println(serverMsg);
                writer.flush();
                if ("bye".equalsIgnoreCase(serverMsg)) {
                    break;
                }
                msg = clientReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
