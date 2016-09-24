package vcs.config;

import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    public HashSet<String> supervisedFiles;

    public CommitConfig() {
        supervisedFiles = new HashSet<>();
    }
}
