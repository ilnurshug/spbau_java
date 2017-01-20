package edu.spbau.master.java.torrent.client.data;

import java.io.FileNotFoundException;

public interface ClientServerAPI {

    int[] getLoadedFilePartNums(int fileId) throws FileNotFoundException;

    String getFilePath(int fileId) throws  FileNotFoundException;

}
