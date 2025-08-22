import MultiThreadedTCP.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TcpServer {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}


