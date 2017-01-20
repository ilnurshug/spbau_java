package edu.spbau.master.java.torrent.client.network;

import edu.spbau.master.java.torrent.client.app.QuerySender;
import edu.spbau.master.java.torrent.client.data.ClientUserAPI;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static edu.spbau.master.java.torrent.tracker.network.TrackerServer.SERVER_PORT;

@AllArgsConstructor
@Slf4j
public final class LoadingManager {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final InetAddress serverAddress;

    private final QuerySender querySender;

    private final ClientUserAPI clientUserAPI;

    private final Consumer<PartiallyLoadedFile> onCompleteLoadCallback;

    private final Consumer<PartiallyLoadedFile> onPartLoadCallback;

    public void addToLoadQueue(PartiallyLoadedFile loadedFile) {
        executorService.execute(() -> {
            if (loadedFile.getIsLoaded().compareAndSet(false, true)) {
                while (!loadedFile.isLoaded() && !executorService.isShutdown()) {

                    try {
                        Thread.sleep(1000);
                        FileLoader fileLoader = new FileLoader(
                                loadedFile,
                                getSourceList(loadedFile.getFileInfo()),
                                querySender,
                                onPartLoadCallback
                        );
                        fileLoader.startLoad();
                    } catch (IOException | InterruptedException e) {
                        log.error("Exception while file loading", e);
                    }
                }
                if (loadedFile.isLoaded()) {
                    clientUserAPI.loadCompleted(loadedFile.getFileInfo().getId());
                    onCompleteLoadCallback.accept(loadedFile);
                }
            }
        });
    }

    private List<ClientInfo> getSourceList(FileInfo fileInfo) throws IOException {
        try (
                Socket socket = new Socket(serverAddress, SERVER_PORT);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            List<ClientInfo> result = querySender.handleSourcesQuery(fileInfo.getId(), dataInputStream, dataOutputStream);
            Collections.shuffle(result);
            return result;
        }
    }


    public void stop() {
        executorService.shutdown();
    }


}
