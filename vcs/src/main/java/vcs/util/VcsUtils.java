package vcs.util;


import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class VcsUtils {

    public static <T> void serialize(T obj, String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }

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

    public static void copyFiles(List<String> files, String source, String dest, boolean overwrite) {
        files = files.stream().filter(f -> new File(source + f).exists()).collect(Collectors.toList());

        files.forEach(
                f -> {
                    try {
                        File sourceFile = new File(source + f);
                        File destFile = new File(dest + f);
                        if (overwrite || !destFile.exists()) {
                            FileUtils.copyFile(sourceFile, destFile);
                        }
                    } catch (IOException e) {
                        System.err.println("copy failure");
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static void deleteFiles(List<String> files, String dir) {
        files.stream()
                .filter(f -> new File(dir + f).exists())
                .forEach(f -> {
                    try {
                        Files.delete(Paths.get(dir + f));
                    } catch (IOException e) {
                        System.err.println("deletion failure");
                    }
                });
    }

    public static long diffDirFiles(List<String> files, String dirA, String dirB) {
        return files.stream().filter(f -> {
            try {
                return !VcsUtils.getFileHash(dirA + f).equals(VcsUtils.getFileHash(dirB + f));
            }
            catch (IOException e) {
                return true;
            }
        }).count();
    }

    public static List<String> diffDirFilesList(List<String> files, String dirA, String dirB) {
        List<String> different = new LinkedList<>();

        files.forEach(f -> {
            try {
                if (!VcsUtils.getFileHash(dirA + f).equals(VcsUtils.getFileHash(dirB + f))) {
                    different.add(f);
                }
            }
            catch (IOException e) {
                System.err.println("hashing failure");
            }
        });

        return different;
    }

    public static String getFileHash(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filename));

        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);

        fis.close();

        return md5;
    }

    public static void listAllFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listAllFiles(file.getAbsolutePath(), files);
            }
        }
    }
}
