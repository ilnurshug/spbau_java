package edu.spbau.master.java.torrent.tracker.network;

import edu.spbau.master.java.torrent.tracker.app.QueryHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class TrackerServer {
    public static final int SERVER_PORT = 8081;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private volatile ServerSocket serverSocket;

    private final QueryHandler queryHandler;

    public TrackerServer(QueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public void listen() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            executorService.execute(() -> {
                try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream())) {
                    InetAddress inetAddress = clientSocket.getInetAddress();
                    log.info("Start handle query from {}.", inetAddress);
                    queryHandler.handle(dataInputStream,
                            dataOutputStream,
                            ByteBuffer.wrap(inetAddress.getAddress()).getInt());
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
