package server.requests;


import utils.Requests;

import java.io.*;

public class Get {

    static public void execute(final DataInputStream inputStream,
                               final DataOutputStream outputStream) throws IOException {
        String path = System.getProperty("user.dir") + "/" + inputStream.readUTF();

        File f = new File(path);
        if (!f.exists())
        {
            outputStream.writeLong(0);
            outputStream.flush();
            return;
        }

        final long length = f.length();

        byte[] buffer = new byte[Requests.BUFFER_SIZE];
        FileInputStream in = new FileInputStream(path);
        while (in.read(buffer) != -1)
        {
            outputStream.writeLong(length);
            outputStream.write(buffer);
            outputStream.flush();
        }
    }

}
