package edu.spbau.master.java.torrent.client.network;

import edu.spbau.master.java.torrent.client.data.ClientSchedulerAPI;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.ServerQueryType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static edu.spbau.master.java.torrent.tracker.network.TrackerServer.SERVER_PORT;

@AllArgsConstructor
@Slf4j
public final class ClientScheduler {

    private static final int UPDATE_DELAY = 30;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final InetAddress serverAddress;

    private final int clientPort;

    private final ClientSchedulerAPI clientSchedulerAPI;

    public void start() {
        executorService
                .scheduleWithFixedDelay(() -> {
                            try (
                                    Socket socket = new Socket(serverAddress, SERVER_PORT);
                                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
                            ) {
                                List<FileInfo> availableFiles = clientSchedulerAPI.getAvailableFiles();
                                log.info("Send update query to server with {} files", availableFiles.size());
                                dataOutputStream.writeInt(ServerQueryType.Update.value);
                                dataOutputStream.writeShort(clientPort);
                                dataOutputStream.writeInt(availableFiles.size());
                                for (FileInfo availableFile : availableFiles) {
                                    dataOutputStream.writeInt(availableFile.getId());
                                }

                                dataInputStream.readInt();
                            } catch (IOException e) {
                                log.error("Exception during send update query to server.", e);
                            }
                        },
                        0,
                        UPDATE_DELAY,
                        TimeUnit.SECONDS);
    }


    public void stop() {
        executorService.shutdown();
    }


}
