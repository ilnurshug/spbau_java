package vcs.config;

import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

public class CommitConfig implements Serializable {
    public static CommitConfig instance = new CommitConfig();

    public HashSet<String> supervisedFiles = new HashSet<>();

    public static void rollback() {
        try {
            CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + "config");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
