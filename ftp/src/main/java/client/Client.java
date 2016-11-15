package client;

import utils.Requests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public byte[] executeGet(String path) throws IOException {
        return client.requests.Get.execute(inputStream, outputStream, path);
    }

    public int executeList(String path, List<String> items, List<Boolean> isDir) throws IOException {
        return client.requests.List.execute(inputStream, outputStream, path, items, isDir);
    }

    public void closeConnection() throws IOException {
        outputStream.writeInt(Requests.DISCONNECT);
        outputStream.flush();

        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
