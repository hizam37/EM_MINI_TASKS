import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("127.0.0.1", 6666);
             PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String userInput;
            String resp = bufferedReader.readLine();
            System.out.println(resp);
            Scanner scanner = new Scanner(System.in);
            while ((userInput = scanner.nextLine())!=null) {
                printWriter.println(userInput);
                resp = bufferedReader.readLine();
                System.out.println(resp);
                if("GoodBye".equalsIgnoreCase(resp))
                {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
