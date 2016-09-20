package vcs.graph;

import vcs.util.VcsUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class CommitGraph implements Serializable {

    private final HashMap<String, Commit> branches = new HashMap<>();

    private Commit head = new Commit("new branch - master", "master", Collections.emptyList());

    private boolean createBranch = false;
    private String newBranchName;

    public CommitGraph() {
        branches.put("master", head);
    }

    public Commit getHead() {
        return head;
    }

    public Commit getLastCommitOnBranch(String branch) {
        return branches.getOrDefault(branch, null);
    }

    public boolean merge(String branch) {
        if (!branch.contains(branch)) {
            VcsUtils.log("no such branch");
            return false;
        }

        head = new Commit("merge of " + head.getBranch() + " and " + branch,
                head.getBranch(),
                Arrays.asList(head, branches.get(branch))
        );

        branches.put(head.getBranch(), head);
        return true;
    }

    public void commit(String message) {
        head = new Commit(message,
                createBranch ? newBranchName : head.getBranch(),
                Collections.singletonList(head));

        createBranch = false;

        branches.put(head.getBranch(), head);
    }

    public boolean createBranch(String branch) {
        if (branch.contains(branch)) {
            VcsUtils.log("branch already exists");
            return false;
        }

        createBranch = true;
        newBranchName = branch;

        return true;
    }

    public boolean checkout(String branch) {
        if (!branch.contains(branch)) {
            VcsUtils.log("no such branch");
            return false;
        }

        head = branches.get(branch);
        return true;
    }

    public boolean checkout(String branch, int commitId) {
        Commit branchHead = branches.getOrDefault(branch, null);
        if (branchHead == null || commitId < 0 ||  branchHead.getId() < commitId) {
            VcsUtils.log("can not switch to selected commit on branch");
            return false;
        }

        head = branchHead;

        while (head.getId() != commitId) {
            head = getPrevCommit(head);
        }

        return true;
    }

    public static Commit getPrevCommit(Commit c) {
        Optional<Commit> opt = c.getParents().stream()
                .filter(p -> p.getBranch().equals(c.getBranch()))
                .findAny();

        if (opt.isPresent()) {
            return opt.get();
        }
        else {
            return null;
        }
    }
}
