import MultiThreadedTCP.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPSERVER {
    public static void main(String[] args){
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}


