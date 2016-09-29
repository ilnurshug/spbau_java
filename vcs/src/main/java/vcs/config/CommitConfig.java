package vcs.config;

import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    private HashSet<String> supervisedFiles;

    private HashMap<String, String> supervisedFilesHashes;

    private HashMap<String, String> supervisedFileCopyAddr;

    public CommitConfig() {
        supervisedFiles = new HashSet<>();
        supervisedFilesHashes = new HashMap<>();
        supervisedFileCopyAddr = new HashMap<>();
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
