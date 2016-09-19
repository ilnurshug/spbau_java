package vcs.util;


import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VcsUtils {
    public static final String COMMIT = "commit";
    public static final String ADD = "add";
    public static final String CHECKOUT = "checkout";
    public static final String LOG = "log";
    public static final String INIT = "init";
    public static final String MERGE = "merge";

    public static String getFileHash(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(new File(filename));

        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);

        fis.close();

        return md5;
    }

    public static <T> void serialize(T obj, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(obj);

        oos.flush();
        oos.close();
    }

    public static Object deserialize(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream oin = new ObjectInputStream(fis);

        Object obj = oin.readObject();

        oin.close();

        return obj;
    }

    public static void log(String message) {
        Logger.getAnonymousLogger().log(Level.INFO, message);
    }
}
