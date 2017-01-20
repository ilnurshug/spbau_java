package edu.spbau.master.java.torrent.client.app;

import edu.spbau.master.java.torrent.client.fs.FilePartReader;
import edu.spbau.master.java.torrent.model.ClientInfo;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.ClientQueryType;
import edu.spbau.master.java.torrent.shared.ServerQueryType;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

public class QuerySenderTest {

    @Mock
    private
    FilePartReader filePartReader = Mockito.mock(FilePartReader.class);

    @Test
    public void handleListQuery() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(out);

        int fileCount = 1;
        outputStream.writeInt(fileCount);
        int fileId = 2;
        outputStream.writeInt(fileId);
        String fileName = "FileName";
        outputStream.writeUTF(fileName);
        int fileSize = 10000;
        outputStream.writeLong(fileSize);

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        List<FileInfo> fileInfos = new QuerySender(filePartReader).handleListQuery(
                new DataInputStream(new ByteArrayInputStream(out.toByteArray())), new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(ServerQueryType.List.value, checkStream.readInt());

        assertEquals(1, fileInfos.size());
        assertTrue(fileInfos.contains(FileInfo.builder().size(fileSize).name(fileName).id(fileId).build()));
    }

    @Test
    public void handleUploadQuery() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(out);

        int fileId = 1;
        outputStream.writeInt(fileId);

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        String fileName = "FileName";
        long fileSize = 10000;
        FileInfo fileInfo = new QuerySender(filePartReader).handleUploadQuery(fileName, fileSize,
                new DataInputStream(new ByteArrayInputStream(out.toByteArray())), new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(FileInfo.builder().id(fileId).name(fileName).size(fileSize).build(), fileInfo);

        assertEquals(ServerQueryType.Upload.value, checkStream.readInt());
        assertEquals(fileName, checkStream.readUTF());
        assertEquals(fileSize, checkStream.readLong());
    }

    @Test
    public void handleSourcesQuery() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(out);

        int clientCount = 1;
        outputStream.writeInt(clientCount);
        int ip = 102030;
        outputStream.writeInt(ip);
        short port = 100;
        outputStream.writeShort(port);

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        int fileId = 10;
        List<ClientInfo> clientInfos = new QuerySender(filePartReader).handleSourcesQuery(fileId,
                new DataInputStream(new ByteArrayInputStream(out.toByteArray())), new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(ServerQueryType.Sources.value, checkStream.readInt());
        assertEquals(fileId, checkStream.readInt());

        assertEquals(clientCount, clientInfos.size());
        assertTrue(clientInfos.contains(new ClientInfo(ip, port)));
    }

    @Test
    public void handleStatQuery() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(out);

        int partCount = 2;
        outputStream.writeInt(partCount);
        outputStream.writeInt(1);
        outputStream.writeInt(2);

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        int fileId = 10;
        int[] parts = new QuerySender(filePartReader).handleStatQuery(fileId,
                new DataInputStream(new ByteArrayInputStream(out.toByteArray())), new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(ClientQueryType.Stat.value, checkStream.readInt());
        assertEquals(fileId, checkStream.readInt());

        assertArrayEquals(new int[]{1, 2}, parts);
    }

    @Test
    public void handleGetQuery() throws Exception {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        String filePath = "FilePath";
        int fileId = 100;
        int partNum = 2;
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(new byte[]{}));
        new QuerySender(filePartReader).handleGetQuery(filePath, fileId, partNum,
                inputStream, new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(ClientQueryType.Get.value, checkStream.readInt());
        assertEquals(fileId, checkStream.readInt());
        assertEquals(partNum, checkStream.readInt());

        verify(filePartReader, times(1)).readFilePartFromStream(filePath, partNum, inputStream);
    }

}