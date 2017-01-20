package edu.spbau.master.java.torrent.client.network;

import edu.spbau.master.java.torrent.client.app.QuerySender;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.ClientInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
public final class FileLoader {

    private final PartiallyLoadedFile loadedFile;

    private final List<ClientInfo> clientInfoList;

    private final QuerySender querySender;

    private final Set<Integer> loadedPartsSet = new HashSet<>();

    private final Consumer<PartiallyLoadedFile> onPartLoadCallback;


    public void startLoad() {
        addLoadedParts();

        while (!loadedFile.isLoaded() && !clientInfoList.isEmpty()) {
            ClientWithParts nextSource = getNextSource();
            if (nextSource != null) {
                for (int partNum : nextSource.getPartNumArray()) {
                    if (!loadedPartsSet.contains(partNum)) {
                        try (
                                Socket socket = new Socket(nextSource.clientAddress, nextSource.clientPort);
                                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
                        ) {
                            querySender.handleGetQuery(
                                    loadedFile.getPath(),
                                    loadedFile.getFileInfo().getId(),
                                    partNum,
                                    dataInputStream,
                                    dataOutputStream
                            );
                            loadedFile.loadPart(partNum);
                            loadedPartsSet.add(partNum);
                            onPartLoadCallback.accept(loadedFile);
                        } catch (IOException e) {
                            log.error("Exception while loading part {} from  {}", partNum, nextSource.getClientAddress());
                        }
                    }
                }
            }
        }
    }


    private void addLoadedParts() {
        boolean[] parts = loadedFile.getParts();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i]) {
                loadedPartsSet.add(i);
            }
        }
    }

    private InetAddress getInetAddressFromInt(int ip) throws UnknownHostException {
        return InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(ip).array());
    }

    private ClientWithParts getNextSource() {
        int lastClient = clientInfoList.size() - 1;
        ClientInfo clientInfo = clientInfoList.get(lastClient);
        clientInfoList.remove(lastClient);
        try {
            InetAddress clientAddress = getInetAddressFromInt(clientInfo.getIp());

            try (
                    Socket socket = new Socket(clientAddress, clientInfo.getPort());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
            ) {
                int[] partsNum = querySender.handleStatQuery(loadedFile.getFileInfo().getId(), dataInputStream, dataOutputStream);
                return new ClientWithParts(clientAddress, clientInfo.getPort(), partsNum);
            } catch (IOException e) {
                log.error("Exception while update part to client map.");
            }

        } catch (UnknownHostException e) {
            log.error("Invalid client address.", e);
        }
        return null;
    }


    @Data
    private static final class ClientWithParts {
        private final InetAddress clientAddress;
        private final short clientPort;
        private final int[] partNumArray;
    }


}
