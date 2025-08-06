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
        PrintWriter printWriter;
        BufferedReader bufferedReader;
        try {
            printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
            if(clientSocket.isConnected())
            {
                printWriter.println(Thread.currentThread().getName() + "Welcome to the MultiThreaded TCP Server" +
                        " type hello to get response from the server or type exit to close the server");
            }
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if ("hello".equals(inputLine)) {
                    printWriter.println("hello client");
                    printWriter.flush();
                } else if
                ("exit".equalsIgnoreCase(inputLine)) {
                    printWriter.flush();
                    bufferedReader.close();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}