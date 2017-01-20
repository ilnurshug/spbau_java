package edu.spbau.master.java.torrent.client.fs;

import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public final class FilePartReaderImpl implements FilePartReader {

    private final static int WRITE_BUF_SIZE = 1024 * 64;

    @Override
    public void readFilePartFromStream(String filePath, int partNum, InputStream inputStream) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            long offset = FileInfo.getOffset(partNum);
            long fileLength = randomAccessFile.length();

            if (fileLength < offset) {
                log.warn("File length less than offset.");
            }
            randomAccessFile.seek(offset);
            byte[] buf = new byte[WRITE_BUF_SIZE];

            while (true) {
                int write = inputStream.read(buf);
                if (write == -1) {
                    break;
                }

                randomAccessFile.write(buf, 0, write);
            }

        }
    }


}
