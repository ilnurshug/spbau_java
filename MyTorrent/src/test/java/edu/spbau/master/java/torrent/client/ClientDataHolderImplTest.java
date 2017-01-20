package edu.spbau.master.java.torrent.client;

import edu.spbau.master.java.torrent.client.data.ClientDataHolderImpl;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.model.FileInfo;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class ClientDataHolderImplTest {

    @Test
    public void loadSave() throws Exception {
        ClientDataHolderImpl clientDataHolder = ClientDataHolderImpl.newInstance();
        FileInfo fileInfo1 = FileInfo.builder().id(121).name("FileName").size(1025).build();
        FileInfo fileInfo2 = FileInfo.builder().id(122).name("FileName2").size(10250).build();
        String path1 = "path1";
        clientDataHolder.addFile(fileInfo1, path1);
        String path2 = "path2";
        PartiallyLoadedFile partiallyLoadedFile = clientDataHolder.addLoadingFile(fileInfo2, path2);
        partiallyLoadedFile.loadPart(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        clientDataHolder.persist(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ClientDataHolderImpl clientDataHolderNew = ClientDataHolderImpl.load(inputStream);

        assertEquals(clientDataHolder.getAvailableFiles(), clientDataHolderNew.getAvailableFiles());
        assertEquals(clientDataHolder.getFilePath(121), clientDataHolderNew.getFilePath(121));
        assertEquals(clientDataHolder.getFilePath(122), clientDataHolderNew.getFilePath(122));
        assertArrayEquals(clientDataHolder.getLoadedFilePartNums(121), clientDataHolderNew.getLoadedFilePartNums(121));
        assertArrayEquals(clientDataHolder.getLoadedFilePartNums(122), clientDataHolderNew.getLoadedFilePartNums(122));
    }

    @Test
    public void getLoadedFilePartNums() throws Exception {
        ClientDataHolderImpl clientDataHolder = ClientDataHolderImpl.newInstance();

        FileInfo fileInfo1 = FileInfo.builder().id(121).name("FileName").size(FileInfo.PART_SIZE * 3).build();
        FileInfo fileInfo2 = FileInfo.builder().id(122).name("FileName2").size(FileInfo.PART_SIZE * 10).build();

        String path1 = "path1";
        clientDataHolder.addFile(fileInfo1, path1);
        String path2 = "path2";
        PartiallyLoadedFile partiallyLoadedFile = clientDataHolder.addLoadingFile(fileInfo2, path2);

        partiallyLoadedFile.loadPart(1);
        partiallyLoadedFile.loadPart(2);
        partiallyLoadedFile.loadPart(9);


        assertArrayEquals(new int[]{0, 1, 2}, clientDataHolder.getLoadedFilePartNums(121));
        assertArrayEquals(new int[]{1, 2, 9}, clientDataHolder.getLoadedFilePartNums(122));
    }

    @Test
    public void getFilePath() throws Exception {
        ClientDataHolderImpl clientDataHolder = ClientDataHolderImpl.newInstance();
        FileInfo fileInfo1 = FileInfo.builder().id(121).name("FileName").size(1025).build();
        String path1 = "path1";
        clientDataHolder.addFile(fileInfo1, path1);

        assertEquals(path1, clientDataHolder.getFilePath(121));
    }

    @Test
    public void addFile() throws Exception {
        ClientDataHolderImpl clientDataHolder = ClientDataHolderImpl.newInstance();
        FileInfo fileInfo1 = FileInfo.builder().id(121).name("FileName").size(1025).build();
        String path1 = "path1";
        clientDataHolder.addFile(fileInfo1, path1);

        assertTrue(clientDataHolder.getAvailableFiles().contains(fileInfo1));
    }

    @Test
    public void addLoadingFile() throws Exception {
        ClientDataHolderImpl clientDataHolder = ClientDataHolderImpl.newInstance();
        FileInfo fileInfo1 = FileInfo.builder().id(121).name("FileName").size(1025).build();
        String path1 = "path1";
        PartiallyLoadedFile partiallyLoadedFile = clientDataHolder.addLoadingFile(fileInfo1, path1);


        assertTrue(clientDataHolder.getPartiallyLoadedFiles().size() == 1);

        assertTrue(clientDataHolder.getAvailableFiles().size() == 0);
        partiallyLoadedFile.loadPart(0);
        assertTrue(clientDataHolder.getAvailableFiles().size() == 1);

        assertTrue(clientDataHolder.getPartiallyLoadedFiles().size() == 1);
        clientDataHolder.loadCompleted(121);
        assertTrue(clientDataHolder.getPartiallyLoadedFiles().size() == 0);
    }

}