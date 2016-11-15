package server.requests;


import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class Get {

    static public void execute(final DataInputStream inputStream,
                               final DataOutputStream outputStream) throws IOException {
        String path = System.getProperty("user.dir") + "/" + inputStream.readUTF();
        byte[] bytes;

        try {
            bytes = FileUtils.readFileToByteArray(new File(path));
        } catch (IOException error) {
            outputStream.writeInt(0);
            return;
        }

        outputStream.writeInt(bytes.length);

        for (byte b : bytes) {
            outputStream.writeByte(b);
        }

        outputStream.flush();
    }

}
