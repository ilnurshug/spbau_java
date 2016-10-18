package client;

import utils.Request;

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
        outputStream.writeInt(Request.GET.type);
        outputStream.writeUTF(path);
        outputStream.flush();

        final int size = inputStream.readInt();
        byte[] data = new byte[size];
        int read = 0;
        while (read < size) {
            read += inputStream.read(data, read, size - read);
        }

        return data;
    }

    public int executeList(String path, List<String> items, List<Boolean> isDir) throws IOException {
        outputStream.writeInt(Request.LIST.type);
        outputStream.writeUTF(path);
        outputStream.flush();

        final int size = inputStream.readInt();

        for (int i = 0; i < size; i++) {
            String item = inputStream.readUTF();
            Boolean isDirectory = inputStream.readBoolean();

            items.add(item);
            isDir.add(isDirectory);
        }

        return size;
    }

    public void closeConnection() throws IOException {
        outputStream.writeInt(Request.DISCONNECT.type);
        outputStream.flush();

        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
