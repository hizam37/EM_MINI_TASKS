package MultiThreadedTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        System.out.println("Thread "+Thread.currentThread().getName()+" started");
        try (PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            if (clientSocket.isConnected()) {
                printWriter.println(clientSocket.getInetAddress().getHostAddress()+" is connected to the server text anything and it will reply the text it received from you and if you want to shut off the client just press Enter)");
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    if ("".equalsIgnoreCase(inputLine)) {
                        printWriter.println("GoodBye");
                        break;
                    }
                    printWriter.println("Received text from the client saying " + inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
