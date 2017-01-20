package edu.spbau.master.java.torrent.client.data;

import edu.spbau.master.java.torrent.model.FileInfo;

import java.util.List;

public interface ClientSchedulerAPI {

    List<FileInfo> getAvailableFiles();

}
