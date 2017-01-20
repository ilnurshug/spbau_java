package edu.spbau.master.java.torrent.client.network;

import edu.spbau.master.java.torrent.client.app.IncomingQueryHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class ClientServer {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private volatile ServerSocket serverSocket;

    private final IncomingQueryHandler incomingQueryHandler;

    private final int port;


    public ClientServer(IncomingQueryHandler incomingQueryHandler, int port) {
        this.incomingQueryHandler = incomingQueryHandler;
        this.port = port;
    }

    public void listen() throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            executorService.execute(() -> {
                try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream())) {
                    InetAddress inetAddress = clientSocket.getInetAddress();
                    log.info("Start handle query from {}.", inetAddress);
                    incomingQueryHandler.handle(dataInputStream,
                            dataOutputStream);
                    log.info("Complete handle query from {}.", inetAddress);
                } catch (IOException e) {
                    log.error("Exception during handling client query.", e);
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        log.error("Exception during closing client socket.", e1);
                    }
                }
            });
        }
    }

    public void stop() throws IOException {
        executorService.shutdown();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("Exception during closing server socket.", e);
                throw e;
            }
        }
    }


}
