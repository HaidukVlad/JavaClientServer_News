import creator_package.Creator;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Creator creator = new Creator("127.0.0.1", 8000)) {
            System.out.println("Client connected");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите 'current' для текущих новостей или дату (YYYY-MM-DD) для новостей за указанный день:");
            String request = scanner.nextLine();

            System.out.println("Request: " + request);
            creator.writeLine(request);

            // Чтение всех строк ответа от сервера
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = creator.readLine()) != null) {
                response.append(line).append("\n");
            }

            System.out.println("Response:\n" + response.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
