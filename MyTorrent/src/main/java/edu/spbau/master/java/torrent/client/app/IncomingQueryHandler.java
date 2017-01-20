package edu.spbau.master.java.torrent.client.app;


import edu.spbau.master.java.torrent.client.data.ClientServerAPI;
import edu.spbau.master.java.torrent.client.fs.FilePartWriter;
import edu.spbau.master.java.torrent.shared.ClientQueryType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@AllArgsConstructor
@Slf4j
public final class IncomingQueryHandler {

    private final ClientServerAPI clientServerAPI;

    private final FilePartWriter filePartWriter;


    public void handle(DataInputStream inputStream, DataOutputStream outputStream) throws IOException, FileNotFoundException {
        int queryType = inputStream.readInt();
        if (queryType > 0 && queryType <= ClientQueryType.values().length) {
            ClientQueryType clientQueryType = ClientQueryType.values()[queryType - 1];
            switch (clientQueryType) {
                case Stat:
                    handleStatQuery(inputStream, outputStream);
                    break;
                case Get:
                    handleGetQuery(inputStream, outputStream);
                    break;
            }


        } else {
            log.error("Invalid query type {}", queryType);
        }
    }

    private void handleStatQuery(DataInputStream inputStream, DataOutputStream outputStream) throws IOException, FileNotFoundException {
        int fileId = inputStream.readInt();
        log.info("Handle stat query for file {}", fileId);

        int[] loadedFilePartNums = clientServerAPI.getLoadedFilePartNums(fileId);

        outputStream.writeInt(loadedFilePartNums.length);
        for (int loadedFilePartNum : loadedFilePartNums) {
            outputStream.writeInt(loadedFilePartNum);
        }
    }

    private void handleGetQuery(DataInputStream inputStream, DataOutputStream outputStream) throws IOException, FileNotFoundException {
        int fileId = inputStream.readInt();
        int partNum = inputStream.readInt();

        log.info("Handle get query for file {} and part {}");

        String filePath = clientServerAPI.getFilePath(fileId);
        filePartWriter.writePartToStream(filePath, partNum, outputStream);
    }

}
