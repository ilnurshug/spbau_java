package vcs.config;

import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;

public class GlobalConfig implements Serializable {
    public static GlobalConfig instance = new GlobalConfig();

    private static String projectDir;

    public CommitGraph graph;

    public GlobalConfig() {
        projectDir = System.getProperty("user.dir");
        graph = new CommitGraph();
    }

    public static String getHeadCommitDir() {
        String branch = instance.graph.getHead().getBranch();
        int commitId = instance.graph.getHead().getId();
        return VcsUtils.branchesDir() + "/" + branch + "/" + commitId + "/";
    }

    public static String getLastCommitDir(String branch) {
        int commitId = instance.graph.getLastCommitOnBranch(branch).getId();
        return VcsUtils.branchesDir() + "/" + branch + "/" + commitId + "/";
    }

    public static String getProjectDir() {
        return projectDir + "/";
    }
}
