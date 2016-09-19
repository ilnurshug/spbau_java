package vcs;

import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

public class Config implements Serializable {
    public static Config INSTANCE = new Config();

    public CommitGraph graph = new CommitGraph();

    public final HashSet<String> supervisedFiles = new HashSet<>();
    public final String dir = System.getProperty("user.dir");

    public static void rollbackCommand() {
        try {
            INSTANCE = (Config)VcsUtils.deserialize(VcsUtils.CONFIG_FILE);
        } catch (IOException | ClassNotFoundException e) {
            VcsUtils.log("rollback failure");
            e.printStackTrace();
        }
    }
}
