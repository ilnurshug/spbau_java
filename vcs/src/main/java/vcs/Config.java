package vcs;

import java.io.Serializable;
import java.util.HashSet;

public class Config implements Serializable {
    public static Config INSTANCE = new Config();

    public HEAD head = new HEAD("master", 0);

    public final HashSet<String> supervisedFiles = new HashSet<>();
    public final String dir = System.getProperty("user.dir");;

    class HEAD {
        String branch;
        int commit;

        public HEAD(String branch, int commit) {
            this.branch = branch;
            this.commit = commit;
        }

        public String getBranch() {
            return branch;
        }

        public int getCommit() {
            return commit;
        }
    }

}
