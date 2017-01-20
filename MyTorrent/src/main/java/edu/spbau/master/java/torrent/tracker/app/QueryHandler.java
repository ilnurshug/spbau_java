package edu.spbau.master.java.torrent.tracker.app;

import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.ServerQueryType;
import edu.spbau.master.java.torrent.tracker.data.TrackerDataHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Slf4j
public final class QueryHandler {

    private final TrackerDataHolder trackerDataHolder;

    public void handle(DataInputStream inputStream, DataOutputStream outputStream, int clientIp) throws IOException {
        int queryType = inputStream.readInt();
        if (queryType > 0 && queryType <= ServerQueryType.values().length) {
            ServerQueryType serverQueryType = ServerQueryType.values()[queryType - 1];
            switch (serverQueryType) {
                case List:
                    handleListQuery(outputStream);
                    break;
                case Upload:
                    handleUploadQuery(inputStream, outputStream);
                    break;
                case Sources:
                    handleSourcesQuery(inputStream, outputStream);
                    break;
                case Update:
                    handleUpdateQuery(inputStream, outputStream, clientIp);
                    break;
            }

        } else {
            log.error("Invalid query type {}", queryType);
        }
    }


    private void handleListQuery(DataOutputStream outputStream) throws IOException {
        log.info("Handle list query");
        List<FileInfo> fileList = trackerDataHolder.getFileList();
        outputStream.writeInt(fileList.size());
        for (FileInfo fileInfo : fileList) {
            outputStream.writeInt(fileInfo.getId());
            outputStream.writeUTF(fileInfo.getName());
            outputStream.writeLong(fileInfo.getSize());
        }
    }


    private void handleUploadQuery(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        log.info("Handle upload query");

        String name = inputStream.readUTF();
        long fileSize = inputStream.readLong();
        int fileId = trackerDataHolder.addFileInfo(name, fileSize);

        outputStream.writeInt(fileId);
    }

    private void handleSourcesQuery(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        log.info("Handle sources query");

        int fileId = inputStream.readInt();
        List<ClientInfo> fileSources = trackerDataHolder.getFileSources(fileId);

        outputStream.writeInt(fileSources.size());
        for (ClientInfo fileSource : fileSources) {
            outputStream.writeInt(fileSource.getIp());
            outputStream.writeShort(fileSource.getPort());
        }
    }

    private void handleUpdateQuery(DataInputStream inputStream, DataOutputStream outputStream, int clientIp) throws IOException {
        log.info("Handle update query");

        short clientPort = inputStream.readShort();
        int fileCount = inputStream.readInt();

        int[] fileIds = new int[fileCount];

        for (int i = 0; i < fileCount; i++) {
            fileIds[i] = inputStream.readInt();
        }

        trackerDataHolder.updateClientFileList(fileIds, new ClientInfo(clientIp, clientPort));
        outputStream.writeInt(1);
    }

}
