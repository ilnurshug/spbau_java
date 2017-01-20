package edu.spbau.master.java.torrent.tracker.data;

import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.exception.InvalidDataStreamException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class TrackerDataHolderImpl implements TrackerDataHolder {

    private static final long EXPIRED_TIME = 60_000;

    private final AtomicInteger maxFileId;

    private final ConcurrentMap<Integer, Set<ClientInfoWithTimestamp>> fileIdToClientInfoSetMap;

    private final ConcurrentMap<Integer, FileInfo> fileIdToFileInfoMap;


    public static TrackerDataHolderImpl newInstance() {
        return new TrackerDataHolderImpl(
                new AtomicInteger(),
                new ConcurrentHashMap<>()
        );
    }


    public static TrackerDataHolderImpl load(InputStream inputStream) throws IOException, InvalidDataStreamException {
        log.info("Start loading tracker's files info.");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            ConcurrentMap<Integer, FileInfo> fileIdToFileInfoMap = (ConcurrentMap<Integer, FileInfo>) objectInputStream.readObject();
            AtomicInteger maxFileId = (AtomicInteger) objectInputStream.readObject();
            log.info("Loading tracker's files info is completed.");
            return new TrackerDataHolderImpl(maxFileId, fileIdToFileInfoMap);
        } catch (IOException e) {
            log.error("IOException while loading tracker files info", e);
            throw e;
        } catch (ClassNotFoundException | ClassCastException e) {
            log.error("Invalid input stream", e);
            throw new InvalidDataStreamException();
        }
    }

    public void persist(OutputStream outputStream) throws IOException {
        log.info("Start saving tracker's files info.");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(fileIdToFileInfoMap);
            objectOutputStream.writeObject(maxFileId);
            log.info("Saving tracker's files info is completed.");
        } catch (IOException e) {
            log.error("IOException while saving tracker files info", e);
            throw e;
        }
    }

    private TrackerDataHolderImpl(AtomicInteger maxFileId,
                                  ConcurrentMap<Integer, FileInfo> fileIdToFileInfoMap) {
        this.maxFileId = maxFileId;
        this.fileIdToClientInfoSetMap = new ConcurrentHashMap<>();
        this.fileIdToFileInfoMap = fileIdToFileInfoMap;
    }

    public List<FileInfo> getFileList() {
        return new ArrayList<>(fileIdToFileInfoMap.values());
    }

    public int addFileInfo(String fileName, long fileSize) {
        int id = maxFileId.incrementAndGet();
        FileInfo fileInfo = FileInfo.builder()
                .name(fileName)
                .size(fileSize)
                .id(id)
                .build();
        fileIdToFileInfoMap.putIfAbsent(id, fileInfo);

        log.info("Add new file {} with size {} and id {}.", fileName, fileSize, id);

        return id;
    }

    public List<ClientInfo> getFileSources(int fileId) {
        List<ClientInfo> result = new ArrayList<>();

        long curTime = System.currentTimeMillis();

        log.info("Get source of file {}", fileId);

        int deletedCount = 0;

        Set<ClientInfoWithTimestamp> clientInfoSet = fileIdToClientInfoSetMap.getOrDefault(fileId, Collections.emptySet());
        Iterator<ClientInfoWithTimestamp> iterator = clientInfoSet.iterator();
        while (iterator.hasNext()) {
            ClientInfoWithTimestamp clientInfoWithTimestamp = iterator.next();
            if (clientInfoWithTimestamp.getTimestamp() + EXPIRED_TIME < curTime) {
                deletedCount++;
                iterator.remove();
            } else {
                result.add(clientInfoWithTimestamp.clientInfo);
            }
        }
        log.info("Found {} source of file {}, {} expired", result.size(), fileId, deletedCount);
        return result;
    }

    public void updateClientFileList(int[] fileIds, ClientInfo clientInfo) {
        ClientInfoWithTimestamp curInfo = new ClientInfoWithTimestamp(clientInfo, System.currentTimeMillis());
        for (Integer fileId : fileIds) {
            fileIdToClientInfoSetMap.compute(fileId, (id, clientInfoSet) -> {
                if (clientInfoSet == null) {
                    Set<ClientInfoWithTimestamp> result = ConcurrentHashMap.newKeySet();
                    result.add(curInfo);
                    return result;
                }
                clientInfoSet.add(curInfo);
                return clientInfoSet;
            });
        }
        log.info("Source info of {} updated for {} files in {}  msec", clientInfo, fileIds.length, System.currentTimeMillis() - curInfo.getTimestamp());
    }

    @AllArgsConstructor
    @Getter
    private static final class ClientInfoWithTimestamp {
        private final ClientInfo clientInfo;
        private final long timestamp;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientInfoWithTimestamp that = (ClientInfoWithTimestamp) o;

            return clientInfo.equals(that.clientInfo);
        }

        @Override
        public int hashCode() {
            return clientInfo.hashCode();
        }
    }
}
