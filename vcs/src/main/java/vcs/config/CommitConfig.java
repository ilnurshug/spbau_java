package vcs.config;

import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    private HashSet<String> supervisedFiles;
    private HashSet<String> deletedBranches;

    public CommitConfig() {
        supervisedFiles = new HashSet<>();
        deletedBranches = new HashSet<>();
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

    public boolean isDeletedBranch(String branch) {
        return deletedBranches.contains(branch);
    }

    public void addDeletedBranch(String branch) {
        deletedBranches.add(branch);
    }
}
