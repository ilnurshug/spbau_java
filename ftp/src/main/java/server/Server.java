package server;


import utils.Requests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean handleRequest() {
        try {
            int req = inputStream.readInt();

            if (!Requests.validRequest(req)) {
                throw new Exception("invalid request");
            }

            switch (req) {
                case Requests.DISCONNECT:
                    return false;
                case Requests.LIST:
                    server.requests.List.execute(inputStream, outputStream);
                    break;
                case Requests.GET:
                    server.requests.Get.execute(inputStream, outputStream);
                    break;
            }
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            return false;
        }

        return true;
    }

}
