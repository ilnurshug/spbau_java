package edu.spbau.master.java.torrent.tracker;

import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.tracker.data.TrackerDataHolderImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackerDataHolderImplTest {


    @org.junit.Test
    public void loadSave() throws Exception {
        TrackerDataHolderImpl trackerDataHolder = TrackerDataHolderImpl.newInstance();

        trackerDataHolder.addFileInfo("File name1", 1024);
        trackerDataHolder.addFileInfo("File name2", 1025);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        trackerDataHolder.persist(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        TrackerDataHolderImpl trackerDataHolderNew = TrackerDataHolderImpl.load(inputStream);

        assertEquals(trackerDataHolder.getFileList(), trackerDataHolderNew.getFileList());
        assertEquals(trackerDataHolder.addFileInfo("File name 3", 1026),
                trackerDataHolderNew.addFileInfo("File name 3", 1026));
    }

    @org.junit.Test
    public void getFileList() throws Exception {
        TrackerDataHolderImpl trackerDataHolder = TrackerDataHolderImpl.newInstance();

        String fileName1 = "File name1";
        int fileSize1 = 1024;
        int id1 = trackerDataHolder.addFileInfo(fileName1, fileSize1);
        String fileName2 = "File name2";
        int fileSize2 = 1025;
        int id2 = trackerDataHolder.addFileInfo(fileName2, fileSize2);

        FileInfo fileInfo1 = FileInfo.builder().id(id1).name(fileName1).size(fileSize1).build();
        FileInfo fileInfo2 = FileInfo.builder().id(id2).name(fileName2).size(fileSize2).build();

        List<FileInfo> fileList = trackerDataHolder.getFileList();

        assertEquals(2, fileList.size());

        assertTrue(fileList.contains(fileInfo1));
        assertTrue(fileList.contains(fileInfo2));
    }

    @org.junit.Test
    public void getFileSources() throws Exception {
        TrackerDataHolderImpl trackerDataHolder = TrackerDataHolderImpl.newInstance();

        int id1 = trackerDataHolder.addFileInfo("File name1", 1024);
        int id2 = trackerDataHolder.addFileInfo("File name2", 1025);

        ClientInfo clientInfo = new ClientInfo(1024, (short) 256);
        trackerDataHolder.updateClientFileList(new int[]{id1}, clientInfo);

        List<ClientInfo> fileSources1 = trackerDataHolder.getFileSources(id1);
        assertEquals(1, fileSources1.size());
        assertTrue(fileSources1.contains(clientInfo));

        assertTrue(trackerDataHolder.getFileSources(id2).isEmpty());

//        Thread.sleep(TrackerDataHolderImpl.EXPIRED_TIME);
//
//        assertTrue(trackerDataHolder.getFileSources(id1).isEmpty());

    }


}