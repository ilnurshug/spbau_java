package client.requests;

import utils.Requests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class List {

    static public int execute(final DataInputStream inputStream,
                              final DataOutputStream outputStream,
                              String path,
                              java.util.List<String> items,
                              java.util.List<Boolean> isDir)
            throws IOException
    {
        outputStream.writeInt(Requests.LIST);
        outputStream.writeUTF(path);
        outputStream.flush();

        final int size = inputStream.readInt();

        for (int i = 0; i < size; i++) {
            String item = inputStream.readUTF();
            Boolean isDirectory = inputStream.readBoolean();

            items.add(item);
            isDir.add(isDirectory);
        }

        return size;
    }

}
