package vcs.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    private HashSet<String> supervisedFiles;

    public CommitConfig() {
        supervisedFiles = new HashSet<>();
    }

    public List<String> getSupervisedFiles() {
        return supervisedFiles.stream().collect(Collectors.toList());
    }

    public void addSupervisedFile(String filename) {
        supervisedFiles.add(filename);
    }

    public void clearSupervisedFilesList() {
        supervisedFiles = new HashSet<>();
    }
}
