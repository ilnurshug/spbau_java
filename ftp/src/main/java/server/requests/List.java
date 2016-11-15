package server.requests;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class List {

    static public void execute(final DataInputStream inputStream,
                               final DataOutputStream outputStream) throws IOException {
        final String path = System.getProperty("user.dir") + "/" + inputStream.readUTF();

        java.util.List<File> items = new ArrayList<>();
        java.util.List<Boolean> isDir = new ArrayList<>();

        try {
            listAllFiles(path, items, isDir);
        } catch (IOException e) {
            outputStream.writeInt(0);
            return;
        }

        outputStream.writeInt(items.size());
        for (int i = 0; i < items.size(); i++) {
            outputStream.writeUTF(items.get(i).getName());
            outputStream.writeBoolean(isDir.get(i));
        }

        outputStream.flush();
    }

    private static void listAllFiles(String directoryName,
                                     java.util.List<File> items,
                                     java.util.List<Boolean> isDir) throws IOException {
        File directory = new File(directoryName);

        File[] itemList = directory.listFiles();

        if (itemList == null) {
            throw new IOException();
        }

        for (File item : itemList) {
            items.add(item);
            isDir.add(item.isDirectory());
        }
    }

}
