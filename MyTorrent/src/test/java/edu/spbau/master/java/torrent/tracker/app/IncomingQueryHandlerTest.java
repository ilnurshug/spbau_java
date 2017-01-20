package edu.spbau.master.java.torrent.tracker.app;

import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.ServerQueryType;
import edu.spbau.master.java.torrent.tracker.data.TrackerDataHolder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IncomingQueryHandlerTest {

    @Mock
    private
    TrackerDataHolder trackerDataHolder = Mockito.mock(TrackerDataHolder.class);


    @org.junit.Test
    public void handleList() throws Exception {
        int fileId = 1;
        String fileName = "FileName";
        long size = 1024;
        FileInfo fileInfo = FileInfo.builder().id(fileId).name(fileName).size(size).build();

        Mockito.when(trackerDataHolder.getFileList()).thenReturn(Collections.singletonList(fileInfo));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new DataOutputStream(outputStream).writeInt(ServerQueryType.List.value);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        new QueryHandler(trackerDataHolder).handle(new DataInputStream(inputStream), new DataOutputStream(resultStream), 1025);

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(1, checkStream.readInt());
        assertEquals(fileId, checkStream.readInt());
        assertEquals(fileName, checkStream.readUTF());
        assertEquals(size, checkStream.readLong());
    }

    @org.junit.Test
    public void handleUpload() throws Exception {

        long fileSize = 10000;
        String filename = "Filename";
        int fileId = 111;
        Mockito.when(trackerDataHolder.addFileInfo(filename, fileSize)).thenReturn(fileId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);


        dataOutputStream.writeInt(ServerQueryType.Upload.value);
        dataOutputStream.writeUTF(filename);
        dataOutputStream.writeLong(fileSize);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        new QueryHandler(trackerDataHolder).handle(new DataInputStream(inputStream), new DataOutputStream(resultStream), 1025);

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));
        assertEquals(fileId, checkStream.readInt());
    }

    @org.junit.Test
    public void handleSources() throws  Exception {
        int fileId = 1212;
        int ip = 1000;
        short port = (short) 111;
        ClientInfo clientInfo = new ClientInfo(ip, port);
        Mockito.when(trackerDataHolder.getFileSources(fileId)).thenReturn(Collections.singletonList(clientInfo));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(ServerQueryType.Sources.value);
        dataOutputStream.writeInt(fileId);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        new QueryHandler(trackerDataHolder).handle(new DataInputStream(inputStream), new DataOutputStream(resultStream), 1025);

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));
        assertEquals(1, checkStream.readInt());
        assertEquals(ip, checkStream.readInt());
        assertEquals(port, checkStream.readShort());
    }

    @org.junit.Test
    public void handleUpdate() throws Exception {
        int fileId = 111;
        short port = (short) 100;
        ClientInfo clientInfo = new ClientInfo(1025, port);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(ServerQueryType.Update.value);
        dataOutputStream.writeShort(port);
        int fileIdCount = 1;
        dataOutputStream.writeInt(fileIdCount);
        dataOutputStream.writeInt(fileId);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        new QueryHandler(trackerDataHolder).handle(new DataInputStream(inputStream), new DataOutputStream(resultStream), 1025);

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));
        assertEquals(1, checkStream.readInt());

        verify(trackerDataHolder, times(1)).updateClientFileList(new int[]{fileId}, clientInfo);
    }

}