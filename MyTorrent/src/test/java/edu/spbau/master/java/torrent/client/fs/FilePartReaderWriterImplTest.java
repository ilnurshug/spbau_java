package edu.spbau.master.java.torrent.client.fs;

import edu.spbau.master.java.torrent.model.FileInfo;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertArrayEquals;

public class FilePartReaderWriterImplTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @org.junit.Test
    public void readWriteTest() throws Exception {
        File testFile = folder.newFile("TestFile");

        String path = testFile.getPath();

        RandomAccessFile f = new RandomAccessFile(testFile, "rw");
        int newLength = FileInfo.PART_SIZE * 3 + 100;
        f.setLength(newLength);

        byte[] bytes = new byte[FileInfo.PART_SIZE];
        ThreadLocalRandom.current().nextBytes(bytes);

        FilePartReaderImpl filePartReader = new FilePartReaderImpl();
        filePartReader.readFilePartFromStream(path, 0, new ByteArrayInputStream(bytes));

        FilePartWriterImpl filePartWriter = new FilePartWriterImpl();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        filePartWriter.writePartToStream(path, 0, outputStream);

        assertArrayEquals(bytes, outputStream.toByteArray());
    }


    @org.junit.Test
    public void readWriteShortTest() throws Exception {
        File testFile = folder.newFile("TestFile");

        String path = testFile.getPath();

        RandomAccessFile f = new RandomAccessFile(testFile, "rw");
        int newLength = FileInfo.PART_SIZE * 3 + 100;
        f.setLength(newLength);

        byte[] bytes = new byte[100];
        ThreadLocalRandom.current().nextBytes(bytes);

        FilePartReaderImpl filePartReader = new FilePartReaderImpl();
        filePartReader.readFilePartFromStream(path, 3, new ByteArrayInputStream(bytes));

        FilePartWriterImpl filePartWriter = new FilePartWriterImpl();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        filePartWriter.writePartToStream(path, 3, outputStream);

        assertArrayEquals(bytes, outputStream.toByteArray());
    }

}