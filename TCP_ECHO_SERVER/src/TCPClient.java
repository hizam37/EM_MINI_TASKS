import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {
        PrintWriter printWriter;
        Socket clientSocket;
        try {
            clientSocket  = new Socket("127.0.0.1", 6666);
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println(bufferedReader.readLine());
            String userInput = null;
            Scanner scanner = new Scanner(System.in);
            while (!"exit".equalsIgnoreCase(userInput)) {
                userInput = scanner.nextLine();
                printWriter.println(userInput);
                bufferedReader.readLine();
                System.out.println(bufferedReader.readLine());
            }
            printWriter.flush();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
