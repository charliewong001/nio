package singleThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 首先启动TalServer，TalkClient首先输入
 * 
 * @author Charlie
 *
 */
public class TalkClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Const.SERVER, Const.PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String msg = clientReader.readLine();
            String serverMsg = null;
            while (msg != null && !"".equals(msg)) {
                writer.println(msg);
                writer.flush();
                if ("bye".equalsIgnoreCase(msg)) {
                    break;
                }
                serverMsg = serverReader.readLine();
                System.out.println("\t\t server:" + serverMsg);
                if ("bye".equalsIgnoreCase(serverMsg)) {
                    break;
                }
                msg = clientReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
