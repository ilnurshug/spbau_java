package edu.spbau.master.java.torrent.client.data;

import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.FileInfo;

import java.util.List;

public interface ClientUserAPI {

    void addFile(FileInfo fileInfo, String path);

    PartiallyLoadedFile addLoadingFile(FileInfo fileInfo, String path);

    List<FileInfo> getAvailableFiles();

    List<PartiallyLoadedFile> getPartiallyLoadedFiles();

    void loadCompleted(int fileId);

}
