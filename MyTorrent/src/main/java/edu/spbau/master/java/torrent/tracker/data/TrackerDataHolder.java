package edu.spbau.master.java.torrent.tracker.data;


import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;

import java.util.List;

public interface TrackerDataHolder {

    List<FileInfo> getFileList();

    int addFileInfo(String fileName, long fileSize);

    List<ClientInfo> getFileSources(int fileId);

    void updateClientFileList(int[] fileIds, ClientInfo clientInfo);

}
