package creator_package;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Creator implements Closeable {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public Creator(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Creator(ServerSocket server) throws IOException {
        this(server.accept());
    }

    public Creator(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = createReader();
        this.writer = createWriter();
    }

    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
