package client.requests;

import utils.Requests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Get {
    static public byte[] execute(final DataInputStream inputStream,
                                 final DataOutputStream outputStream,
                                 String path)
            throws IOException
    {
        outputStream.writeInt(Requests.GET);
        outputStream.writeUTF(path);
        outputStream.flush();

        final int size = inputStream.readInt();
        byte[] data = new byte[size];
        int read = 0;
        while (read < size) {
            read += inputStream.read(data, read, size - read);
        }

        return data;
    }
}
