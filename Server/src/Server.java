import creator_package.Creator;
import java.io.*;
import java.net.ServerSocket;


public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000))
        {
            System.out.println("Server starting...");
            while (true)
                try( Creator creator = new Creator(server))
                {
                    String request = creator.readLine();
                    System.out.println("Request: " + request);
                    String response = "HELLO FROM VLAD'S SERVER! " + request.length();
                    creator.writeLine(response);
                    System.out.println("Response: " + response);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

        // При выходе перед catch будет автоматически закрыт сервер в любом случае
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
