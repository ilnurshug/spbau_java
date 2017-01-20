package edu.spbau.master.java.torrent.client.app;

import edu.spbau.master.java.torrent.client.data.ClientServerAPI;
import edu.spbau.master.java.torrent.client.fs.FilePartWriter;
import edu.spbau.master.java.torrent.shared.ClientQueryType;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IncomingQueryHandlerTest {

    @Mock
    private
    ClientServerAPI clientServerAPI = Mockito.mock(ClientServerAPI.class);

    @Mock
    private
    FilePartWriter filePartWriter = Mockito.mock(FilePartWriter.class);

    @Test
    public void handleStat() throws Exception {
        int fileId = 1231;
        Mockito.when(clientServerAPI.getLoadedFilePartNums(fileId)).thenReturn(new int[]{1, 2, 3});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(ClientQueryType.Stat.value);
        dataOutputStream.writeInt(fileId);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        new IncomingQueryHandler(clientServerAPI, filePartWriter).handle(
                new DataInputStream(inputStream), new DataOutputStream(resultStream));

        DataInputStream checkStream = new DataInputStream(new ByteArrayInputStream(resultStream.toByteArray()));

        assertEquals(3, checkStream.readInt());
        assertEquals(1, checkStream.readInt());
        assertEquals(2, checkStream.readInt());
        assertEquals(3, checkStream.readInt());
    }

    @Test
    public void handleGet() throws Exception {
        int fileId = 1235;
        int partNum = 12;
        String filePath = "FilePath";
        Mockito.when(clientServerAPI.getFilePath(fileId)).thenReturn(filePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(ClientQueryType.Get.value);
        dataOutputStream.writeInt(fileId);
        dataOutputStream.writeInt(partNum);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        DataOutputStream resultStream = new DataOutputStream(new ByteArrayOutputStream());

        new IncomingQueryHandler(clientServerAPI, filePartWriter).handle(
                new DataInputStream(inputStream), resultStream);

        verify(filePartWriter, times(1)).writePartToStream(filePath, partNum, resultStream);

    }


}