package client.requests;

import utils.Requests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Get {
    static public ByteStream execute(final DataInputStream inputStream,
                                 final DataOutputStream outputStream,
                                 String path)
            throws IOException
    {
        outputStream.writeInt(Requests.GET);
        outputStream.writeUTF(path);
        outputStream.flush();

        return new ByteStream(inputStream, path);
    }

    public static class ByteStream
    {
        private final DataInputStream inputStream;
        private final String path;

        private long readBytes;
        private long fileLength;

        ByteStream(DataInputStream inputStream,
                          String path) {
            this.inputStream = inputStream;
            this.path = path;

            readBytes = 0;
            fileLength = 0;
        }

        public long getFileLength() {
            return fileLength;
        }

        public java.util.List<Byte> readChunk() throws IOException
        {
            fileLength = inputStream.readLong();
            if (readBytes >= fileLength)
            {
                return null;
            }

            byte[] buffer = new byte[Requests.BUFFER_SIZE];
            int d = inputStream.read(buffer);
            if (d == -1)
            {
                return null;
            }
            java.util.List<Byte> chunk = new java.util.LinkedList<>();
            for (int i = 0; i < d; i++) {
                chunk.add(buffer[i]);
            }

            readBytes += d;

            return chunk;
        }
    }
}
