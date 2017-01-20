package edu.spbau.master.java.torrent.client.app;

import edu.spbau.master.java.torrent.client.fs.FilePartReader;
import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.ClientQueryType;
import edu.spbau.master.java.torrent.shared.ServerQueryType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public final class QuerySender {

    private final FilePartReader filePartReader;


    public List<FileInfo> handleListQuery(DataInputStream dataInputStream,
                                          DataOutputStream dataOutputStream) throws IOException {
        log.info("Send list query to server");
        dataOutputStream.writeInt(ServerQueryType.List.value);

        int fileInfoCount = dataInputStream.readInt();
        List<FileInfo> result = new ArrayList<>(fileInfoCount);
        for (int i = 0; i < fileInfoCount; i++) {
            int fileId = dataInputStream.readInt();
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            result.add(FileInfo.builder().id(fileId).name(fileName).size(fileSize).build());
        }
        log.info("Get {} file info from server", result.size());
        return result;
    }

    public FileInfo handleUploadQuery(String fileName,
                                      long fileSize,
                                      DataInputStream dataInputStream,
                                      DataOutputStream dataOutputStream) throws IOException {
        log.info("Send upload query to server");
        dataOutputStream.writeInt(ServerQueryType.Upload.value);
        dataOutputStream.writeUTF(fileName);
        dataOutputStream.writeLong(fileSize);

        int fileId = dataInputStream.readInt();
        return FileInfo.builder().id(fileId).name(fileName).size(fileSize).build();
    }

    public List<ClientInfo> handleSourcesQuery(int fileId,
                                               DataInputStream dataInputStream,
                                               DataOutputStream dataOutputStream) throws IOException {
        log.info("Send sources query to server");

        dataOutputStream.writeInt(ServerQueryType.Sources.value);
        dataOutputStream.writeInt(fileId);

        int clientInfoCount = dataInputStream.readInt();
        List<ClientInfo> result = new ArrayList<>(clientInfoCount);
        for (int i = 0; i < clientInfoCount; i++) {
            int ip = dataInputStream.readInt();
            short port = dataInputStream.readShort();
            result.add(new ClientInfo(ip, port));
        }
        return result;
    }

    public int[] handleStatQuery(int fileId,
                                 DataInputStream dataInputStream,
                                 DataOutputStream dataOutputStream) throws IOException {
        log.info("Send stat query to client");
        dataOutputStream.writeInt(ClientQueryType.Stat.value);
        dataOutputStream.writeInt(fileId);

        int partCount = dataInputStream.readInt();
        int[] parts = new int[partCount];
        for (int i = 0; i < partCount; i++) {
            parts[i] = dataInputStream.readInt();
        }
        return parts;
    }

    public void handleGetQuery(String filePath,
                               int fileId,
                               int partNum,
                               DataInputStream dataInputStream,
                               DataOutputStream dataOutputStream) throws IOException {
        log.info("Send get query to client.");
        dataOutputStream.writeInt(ClientQueryType.Get.value);
        dataOutputStream.writeInt(fileId);
        dataOutputStream.writeInt(partNum);

        filePartReader.readFilePartFromStream(filePath, partNum, dataInputStream);
    }


}
