package edu.spbau.master.java.torrent.client.network;

import edu.spbau.master.java.torrent.client.app.QuerySender;
import edu.spbau.master.java.torrent.client.data.ClientUserAPI;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import static edu.spbau.master.java.torrent.tracker.network.TrackerServer.SERVER_PORT;

@AllArgsConstructor
@Slf4j
public final class ClientUser {

    private final InetAddress serverAddress;

    private final ClientUserAPI clientUserAPI;

    private final LoadingManager loadingManager;

    private final QuerySender querySender;

    public List<FileInfo> getAllTorrentFiles() throws IOException {
        try (
                Socket socket = new Socket(serverAddress, SERVER_PORT);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            return querySender.handleListQuery(dataInputStream, dataOutputStream);
        } catch (IOException e) {
            log.error("Exception during getting all torrent files", e);
            throw e;
        }
    }

    public List<FileInfo> getAllClientFiles() {
        return clientUserAPI.getAvailableFiles();
    }

    public List<PartiallyLoadedFile> getAllPartiallyLoadedFiles() {
        return clientUserAPI.getPartiallyLoadedFiles();
    }

    public void addFile(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }

        try (
                Socket socket = new Socket(serverAddress, SERVER_PORT);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            long fileLength = file.length();
            String name = file.getName();

            FileInfo fileInfo = querySender.handleUploadQuery(name, fileLength, dataInputStream, dataOutputStream);
            clientUserAPI.addFile(fileInfo, file.toPath().toString());
        } catch (IOException e) {
            log.error("Exception during adding file to torrent", e);
            throw e;
        }
    }

    public void addToLoadingQueue(String path, int fileId) throws IOException {
        List<PartiallyLoadedFile> partiallyLoadedFiles = clientUserAPI.getPartiallyLoadedFiles();
        for (PartiallyLoadedFile loadedFile : partiallyLoadedFiles) {
            if (loadedFile.getFileInfo().getId() == fileId) {
                log.info("Load partially loaded file.");
                loadingManager.addToLoadQueue(loadedFile);
                return;
            }
        }

        List<FileInfo> availableFiles = clientUserAPI.getAvailableFiles();
        for (FileInfo availableFile : availableFiles) {
            if (availableFile.getId() == fileId) {
                log.info("Such file already downloaded. Nothing to do.");
                return;
            }
        }

        List<FileInfo> allTorrentFiles = getAllTorrentFiles();
        for (FileInfo torrentFile : allTorrentFiles) {
            if (torrentFile.getId() == fileId) {
                createFile(path, torrentFile.getSize());
                PartiallyLoadedFile loadedFile = clientUserAPI.addLoadingFile(torrentFile, path);
                loadingManager.addToLoadQueue(loadedFile);
                log.info("Add new file to loading queue.");
                return;
            }
        }

        throw new FileNotFoundException();
    }

    private void createFile(String path, long size) throws IOException {
        RandomAccessFile rw = new RandomAccessFile(path, "rw");
        rw.setLength(size);
    }


}
