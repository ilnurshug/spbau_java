package edu.spbau.master.java.torrent.client.data;

import edu.spbau.master.java.torrent.client.model.FullLoadedFile;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.exception.InvalidDataStreamException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class ClientDataHolderImpl implements ClientSchedulerAPI, ClientServerAPI, ClientUserAPI {
    private final Map<Integer, FullLoadedFile> fileIdToFileMap;
    private final Map<Integer, PartiallyLoadedFile> fileIdToLoadingFileMap;

    public static ClientDataHolderImpl newInstance() {
        return new ClientDataHolderImpl(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }


    public static ClientDataHolderImpl load(InputStream inputStream) throws IOException, InvalidDataStreamException {
        log.info("Start loading client files info.");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Map<Integer, FullLoadedFile> fileIdToFileMap = (Map<Integer, FullLoadedFile>) objectInputStream.readObject();
            Map<Integer, PartiallyLoadedFile> fileIdToLoadingFileMap = (Map<Integer, PartiallyLoadedFile>) objectInputStream.readObject();
            log.info("Loading client's files info is completed.");
            return new ClientDataHolderImpl(fileIdToFileMap, fileIdToLoadingFileMap);
        } catch (IOException e) {
            log.error("IOException while loading client files info", e);
            throw e;
        } catch (ClassNotFoundException | ClassCastException e) {
            log.error("Invalid input stream", e);
            throw new InvalidDataStreamException();
        }
    }

    public void persist(OutputStream outputStream) throws IOException {
        log.info("Start saving client's files info.");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(fileIdToFileMap);
            objectOutputStream.writeObject(fileIdToLoadingFileMap);
            log.info("Saving client's files info is completed.");
        } catch (IOException e) {
            log.error("IOException while saving client file info", e);
            throw e;
        }
    }


    private ClientDataHolderImpl(Map<Integer, FullLoadedFile> fileIdToFileMap, Map<Integer, PartiallyLoadedFile> fileIdToLoadingFileMap) {
        this.fileIdToFileMap = fileIdToFileMap;
        this.fileIdToLoadingFileMap = fileIdToLoadingFileMap;
    }

    @Override
    public int[] getLoadedFilePartNums(int fileId) throws FileNotFoundException {
        log.info("Get file loaded parts nums with id {}.", fileId);
        FullLoadedFile fullFile = fileIdToFileMap.get(fileId);
        if (fullFile != null) {
            int partCount = fullFile.getFileInfo().getPartCount();
            int[] result = new int[partCount];
            for (int i = 0; i < partCount; i++) {
                result[i] = i;
            }
            log.info("All parts of file with id {}.", fileId);
            return result;
        }
        PartiallyLoadedFile loadedFile = fileIdToLoadingFileMap.get(fileId);
        if (loadedFile != null) {
            int partCount = loadedFile.getLoadedPartCount().get();
            int[] result = new int[partCount];
            for (int i = 0, j = 0; i < partCount; i++, j++) {
                while (!loadedFile.getParts()[j]) {
                    ++j;
                }
                result[i] = j;
            }
            log.info("Found {} file loaded parts  with id {}.", result.length, fileId);
            return result;
        }
        throw new FileNotFoundException();
    }

    @Override
    public String getFilePath(int fileId) throws FileNotFoundException {
        log.info("Get file path with id {}.", fileId);
        FullLoadedFile info = fileIdToFileMap.get(fileId);
        if (info != null) {
            return info.getPath();
        }
        PartiallyLoadedFile file = fileIdToLoadingFileMap.get(fileId);
        if (file != null) {
            return file.getPath();
        }
        throw new FileNotFoundException();
    }

    @Override
    public void addFile(FileInfo fileInfo, String path) {
        log.info("Add new full loaded file with name {} and id {} on path", fileInfo.getName(), fileInfo.getId(), path);
        fileIdToFileMap.putIfAbsent(fileInfo.getId(), new FullLoadedFile(fileInfo, path));
    }

    @Override
    public PartiallyLoadedFile addLoadingFile(FileInfo fileInfo, String path) {
        log.info("Add new loading file with name {} and id {} on path", fileInfo.getName(), fileInfo.getId(), path);
        return fileIdToLoadingFileMap.computeIfAbsent(fileInfo.getId(), x -> new PartiallyLoadedFile(fileInfo, path));
    }

    @Override
    public List<FileInfo> getAvailableFiles() {
        log.info("Get all available files.");
        ArrayList<FileInfo> result = new ArrayList<>();
        for (FullLoadedFile fullLoadedFile : fileIdToFileMap.values()) {
            result.add(fullLoadedFile.getFileInfo());
        }
        int fullLoadedFilesCount = result.size();
        for (PartiallyLoadedFile partiallyLoadedFile : fileIdToLoadingFileMap.values()) {
            if (partiallyLoadedFile.getLoadedPartCount().get() > 0) {
                result.add(partiallyLoadedFile.getFileInfo());
            }
        }
        log.info("Got {} of full loaded files and {} partially loaded. So {} in total.",
                fullLoadedFilesCount, result.size() - fullLoadedFilesCount, result.size());
        return result;
    }

    @Override
    public List<PartiallyLoadedFile> getPartiallyLoadedFiles() {
        ArrayList<PartiallyLoadedFile> partiallyLoadedFiles = new ArrayList<>(fileIdToLoadingFileMap.values());
        log.info("Got {} of partially loaded files.", partiallyLoadedFiles.size());
        return partiallyLoadedFiles;
    }

    @Override
    public void loadCompleted(int fileId) {
        log.info("Loading completed for file with id {}.", fileId);
        fileIdToLoadingFileMap.compute(fileId, (id, partiallyLoadedFile) -> {
            if (partiallyLoadedFile != null) {
                if (partiallyLoadedFile.getLoadedPartCount().get() != partiallyLoadedFile.getFileInfo().getPartCount()) {
                    log.warn("Not all parts have been loaded, but load completed invoked. Loaded {} from {}.",
                            partiallyLoadedFile.getLoadedPartCount().get(),
                            partiallyLoadedFile.getFileInfo().getPartCount()
                    );
                }
                fileIdToFileMap.putIfAbsent(id, new FullLoadedFile(partiallyLoadedFile.getFileInfo(), partiallyLoadedFile.getPath()));
            } else {
                log.warn("No such file with id {}", id);
            }
            return null;
        });
    }
}

