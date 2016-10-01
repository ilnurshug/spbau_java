package vcs.config;

import vcs.util.VcsUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    private List<String> deletedFiles;

    private HashSet<String> supervisedFiles;

    private HashMap<String, String> supervisedFilesHashes;

    private HashMap<String, String> supervisedFileCopyAddr;

    public CommitConfig() {
        supervisedFiles = new HashSet<>();
        supervisedFilesHashes = new HashMap<>();
        supervisedFileCopyAddr = new HashMap<>();

        deletedFiles = new ArrayList<>();
    }

    public List<String> getUnsupervisedFiles() {
        List<File> files = new LinkedList<>();
        VcsUtils.listAllFiles(GlobalConfig.projectDir(), files);

        return files.stream()
                .map(f -> new File(GlobalConfig.projectDir()).toURI().relativize(f.toURI()).getPath())
                .filter(f -> !CommitConfig.instance.isSupervised(f) && !f.startsWith(".vcs"))
                .collect(Collectors.toList());
    }

    public List<String> getDeletedFiles() {
        return deletedFiles;
    }

    public void addDeletedFile(String file) {
        deletedFiles.add(file);
    }

    public void clearDeletedFilesList() {
        deletedFiles = new ArrayList<>();
    }

    public List<String> getSupervisedFiles() {
        return supervisedFiles.stream().collect(Collectors.toList());
    }

    public boolean isSupervised(String file) {
        return supervisedFiles.contains(file);
    }

    public void addSupervisedFile(String filename) {
        supervisedFiles.add(filename);
    }

    public void removeFromSupervisedList(String filename) {
        supervisedFiles.remove(filename);
    }

    public void addSupervisedFileHash(String filename, String hash) {
        supervisedFilesHashes.put(filename, hash);
    }

    public String getSupervisedFileHash(String filename) {
        return supervisedFilesHashes.getOrDefault(filename, null);
    }

    public void setSupervisedFileCopyAddr(String filename, String addr) {
        supervisedFileCopyAddr.put(filename, addr);
    }

    public String getSupervisedFileCopyAddr(String filename) {
        return supervisedFileCopyAddr.getOrDefault(filename, null);
    }

    public void clearSupervisedFilesList() {
        supervisedFiles = new HashSet<>();
        supervisedFilesHashes = new HashMap<>();
    }

    public List<String> differentFiles() {
        return getSupervisedFiles().stream().filter(f -> {
            try {
                String current = VcsUtils.getFileHash(GlobalConfig.projectDir() + f);
                String old = getSupervisedFileHash(f);

                return !current.equals(old) || getSupervisedFileCopyAddr(f) == null;
            }
            catch (IOException e) {
                return true;
            }
        }).collect(Collectors.toList());
    }

    /*
    TODO: to use this class later
     */
    class SupervisedFile {
        String name;
        String hash;
        String copyAddr;

        public SupervisedFile(String name, String hash, String copyAddr) {
            this.name = name;
            this.hash = hash;
            this.copyAddr = copyAddr;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getCopyAddr() {
            return copyAddr;
        }

        public void setCopyAddr(String copyAddr) {
            this.copyAddr = copyAddr;
        }
    }
}
