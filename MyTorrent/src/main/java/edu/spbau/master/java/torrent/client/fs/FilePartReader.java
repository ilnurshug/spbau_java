package edu.spbau.master.java.torrent.client.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FilePartReader {

    void readFilePartFromStream(String filePath, int partNum, InputStream inputStream) throws IOException;

}
