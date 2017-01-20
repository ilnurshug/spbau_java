package edu.spbau.master.java.torrent.client.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface FilePartWriter {

    void writePartToStream(String filePath, int partNum, OutputStream outputStream) throws IOException, FileNotFoundException;

}
