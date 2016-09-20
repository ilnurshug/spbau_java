package vcs;

import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;

public class GlobalConfig implements Serializable {
    public static GlobalConfig instance = new GlobalConfig();

    private static final String projectDir = System.getProperty("user.dir");

    public CommitGraph graph = new CommitGraph();

    public static String getHeadCommitDir() {
        return getLastCommitDir(instance.graph.getHead().getBranch());
    }

    public static String getLastCommitDir(String branch) {
        int commitId = instance.graph.getLastCommitOnBranch(branch).getId();
        return VcsUtils.BRANCHES_DIR + "/" + branch + "/" + commitId + "/";
    }

    public static String getProjectDir() {
        return projectDir + "/";
    }

    public static void rollback() {
        try {
            GlobalConfig.instance = (GlobalConfig) VcsUtils.deserialize(VcsUtils.GLOBAL_CONFIG_FILE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
