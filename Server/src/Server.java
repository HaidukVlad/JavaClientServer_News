import creator_package.Creator;
import java.io.*;
import java.net.ServerSocket;


public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server starting...");
            while (true) {
                Creator creator = new Creator(server);
                new Thread(() -> {
                    String request = creator.readLine();
                    System.out.println("Request: " + request);
                    String response = "HELLO FROM VLAD'S SERVER! " + request.length();

                    try {Thread.sleep(2000);} catch (InterruptedException e) { }

                    creator.writeLine(response);
                    System.out.println("Response: " + response);
                    try {creator.close();} catch (IOException e) { }
                }).start();
            }
        // При выходе перед catch будет автоматически закрыт сервер в любом случае
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
