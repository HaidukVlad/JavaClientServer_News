import creator_package.Creator;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        while(true)
        try (Creator creator = new Creator("127.0.0.1", 8000)) {
            System.out.println("Client connected");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите 'current' для текущих новостей или дату (YYYY-MM-DD) для новостей за указанный день:");
            String request = scanner.nextLine();

            System.out.println("Request: " + request);
            creator.writeLine(request);

            String response = creator.readLine();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
