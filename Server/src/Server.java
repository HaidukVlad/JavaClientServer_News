import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000))
        {
            System.out.println("Server starting...");

            try(
                    Socket socket = server.accept();
                    BufferedWriter writer =
                                    new BufferedWriter(
                                            new OutputStreamWriter(
                                                    socket.getOutputStream())))
            {
                writer.write("HELLO FROM VLAD'S SERVER!");
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
