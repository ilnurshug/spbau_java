package vcs.config;

import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

import java.io.Serializable;
import java.util.HashSet;

public class GlobalConfig implements Serializable {
    public static GlobalConfig instance = new GlobalConfig();

    private static String projectDir;

    private HashSet<String> deletedBranches;

    public CommitGraph graph;

    public GlobalConfig() {
        projectDir = System.getProperty("user.dir");
        graph = new CommitGraph();
        deletedBranches = new HashSet<>();
    }

    public static String getCurrentBranch() {
        return instance.graph.getHead().getBranch();
    }

    public static String getHeadCommitDir() {
        String branch = getCurrentBranch();
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

    public boolean isDeletedBranch(String branch) {
        return deletedBranches.contains(branch);
    }

    public void addDeletedBranch(String branch) {
        deletedBranches.add(branch);
    }
}
