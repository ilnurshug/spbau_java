package edu.spbau.master.java.torrent.client.fs;

import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@AllArgsConstructor
public final class FilePartWriterImpl implements FilePartWriter {
    private final static int READ_BUF_SIZE = 1024 * 64;

    @Override
    public void writePartToStream(String filePath, int partNum, OutputStream outputStream) throws IOException, FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long offset = FileInfo.getOffset(partNum);
            long fileLength = randomAccessFile.length();

            if (fileLength < offset) {
                log.warn("File length less than offset.");
            }
            randomAccessFile.seek(offset);
            byte[] buf = new byte[READ_BUF_SIZE];

            int length = (int) (Math.min(FileInfo.PART_SIZE, fileLength - offset));

            int readCount = 0;
            while (readCount < length) {
                int read = randomAccessFile.read(buf);
                if (read == -1) {
                    log.warn("Unexpected end of file.");
                    break;
                }
                readCount += read;

                outputStream.write(buf, 0, read);
            }
        }
    }
}
