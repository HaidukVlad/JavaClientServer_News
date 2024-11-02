import creator_package.Creator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final String NEWS_FILE = "news.txt";
    private static final Map<LocalDate, List<String>> newsStorage = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Server starting...");
            loadNewsFromFile();
            while (true) {
                Socket clientSocket = server.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server encountered an error", e);
        }
    }

    private static void loadNewsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NEWS_FILE))) {
            String line;
            LocalDate currentDate = null;
            List<String> currentNewsList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.matches("\\d{4}-\\d{2}-\\d{2}:")) {
                    if (currentDate != null) {
                        newsStorage.put(currentDate, new ArrayList<>(currentNewsList));
                    }
                    currentDate = LocalDate.parse(line.replace(":", "").trim());
                    currentNewsList.clear();
                } else {
                    currentNewsList.add(line.trim());
                }
            }

            if (currentDate != null) {
                newsStorage.put(currentDate, currentNewsList);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading news file", e);
        }
    }

    // Сохранение новостей обратно в файл
    private static void saveNewsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NEWS_FILE))) {
            for (Map.Entry<LocalDate, List<String>> entry : newsStorage.entrySet()) {
                writer.write(entry.getKey() + ":");
                writer.newLine();
                for (String news : entry.getValue()) {
                    writer.write(news);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error writing news file", e);
        }
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
                    saveNewsToFile(); // Сохраняем текущие новости при запросе "current"
                } else {
                    response = getNewsForDate(request);
                }

                creator.writeLine(response);
                System.out.println("Response: " + response);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error handling client request", e);
            }
        }

        // текущие новости
        private String getCurrentNews() {
            LocalDate today = LocalDate.now();
            List<String> newsToday = newsStorage.getOrDefault(today, Collections.singletonList("No news for today"));
            return String.join("\n", newsToday);
        }

        // новости за указанную дату
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
