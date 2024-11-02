import creator_package.Creator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Map<LocalDate, List<String>> newsStorage = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server starting...");
            initializeNews();
            while (true) {
                Socket clientSocket = server.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeNews() {
        newsStorage.put(LocalDate.now(), Arrays.asList("Breaking News 1", "Breaking News 2"));
        newsStorage.put(LocalDate.now().minusDays(1), Arrays.asList("Old News 1", "Old News 2"));
    }

    // Обработчик запросов от клиентов
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (Creator creator = new Creator(clientSocket)) {
                String request = creator.readLine();
                System.out.println("Request: " + request);

                String response;
                if (request.equalsIgnoreCase("current")) {
                    response = getCurrentNews();
                } else {
                    response = getNewsForDate(request);
                }

                creator.writeLine(response);
                System.out.println("Response: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //текущиЕ новости
        private String getCurrentNews() {
            LocalDate today = LocalDate.now();
            return String.join("\n", newsStorage.getOrDefault(today, Collections.singletonList("No news for today")));
        }

        // новости за опред дату
        private String getNewsForDate(String dateStr) {
            try {
                LocalDate date = LocalDate.parse(dateStr);
                List<String> news = newsStorage.getOrDefault(date, Collections.singletonList("No news for this date"));
                return String.join("\n", news);
            } catch (Exception e) {
                return "Invalid date format. Please use YYYY-MM-DD.";
            }
        }
    }
}
