package server;


import org.apache.commons.io.FileUtils;
import utils.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private final ServerSocket socket;

    public Server(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            processConnection();
        }
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processConnection() {
        try {
            new Thread(new ClientHandler(socket.accept())).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        while (!socket.isClosed() && handleRequest()) {}

        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private boolean handleRequest() {
        try {
            Request req = Request.fromInt(inputStream.readInt());

            switch (req) {
                case DISCONNECT:
                    return false;
                case LIST:
                    executeList();
                    break;
                case GET:
                    executeGet();
                    break;
            }
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            return false;
        }

        return true;
    }

    private void executeList() throws IOException {
        final String path = System.getProperty("user.dir") + "/" + inputStream.readUTF();

        List<File> items = new ArrayList<File>();
        List<Boolean> isDir = new ArrayList<Boolean>();

        try {
            listAllFiles(path, items, isDir);
        } catch (IOException e) {
            outputStream.writeInt(0);
            return;
        }

        outputStream.writeInt(items.size());
        for (int i = 0; i < items.size(); i++) {
            outputStream.writeUTF(items.get(i).getName());
            outputStream.writeBoolean(isDir.get(i));
        }

        outputStream.flush();
    }

    private void executeGet() throws IOException {
        String path = System.getProperty("user.dir") + "/" + inputStream.readUTF();
        byte[] bytes;

        try {
            bytes = FileUtils.readFileToByteArray(new File(path));
        } catch (IOException error) {
            outputStream.writeInt(0);
            return;
        }

        outputStream.writeInt(bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            outputStream.writeByte(bytes[i]);
        }

        outputStream.flush();
    }

    private static void listAllFiles(String directoryName, List<File> items, List<Boolean> isDir) throws IOException {
        File directory = new File(directoryName);

        File[] itemList = directory.listFiles();

        if (itemList == null) {
            throw new IOException();
        }

        for (File item : itemList) {
            items.add(item);
            isDir.add(item.isDirectory());
        }
    }

}
