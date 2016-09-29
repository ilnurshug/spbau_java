package vcs.graph;

import vcs.util.VcsUtils;

import java.io.Serializable;
import java.util.*;

public class CommitGraph implements Serializable {

    private final HashMap<String, Commit> branches = new HashMap<>();

    private Commit root = new Commit("new branch - master", "master", Collections.emptyList());

    private Commit head = root;

    private boolean createBranch = false;
    private String newBranchName;

    public CommitGraph() {
        branches.put("master", head);
    }

    public Commit getRoot() { return root; }

    public Commit getHead() {
        return head;
    }

    public Commit getLastCommitOnBranch(String branch) {
        return branches.getOrDefault(branch, null);
    }

    public boolean isCreateBranch() {
        return createBranch;
    }

    public boolean merge(String branch) {
        if (!isContains(branch)) {
            System.err.println("no such branch");
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
        if (isContains(branch)) {
            System.err.println("branch already exists");
            return false;
        }

        createBranch = true;
        newBranchName = branch;

        return true;
    }

    public boolean checkout(String branch) {
        if (!isContains(branch)) {
            System.err.println("no such branch");
            return false;
        }

        head = branches.get(branch);
        return true;
    }

    public boolean isContains(String branch) {
        return branches.containsKey(branch);
    }

    public boolean canCheckout(String branch, int commitId) {
        Commit branchHead = branches.getOrDefault(branch, null);
        if (branchHead == null || commitId < 0 ||  branchHead.getId() < commitId) {
            System.err.println("can not switch to selected commit on branch");
            return false;
        }
        return true;
    }

    public boolean checkout(String branch, int commitId) {
        Commit branchHead = branches.getOrDefault(branch, null);
        if (branchHead == null || commitId < 0 ||  branchHead.getId() < commitId) {
            System.err.println("can not switch to selected commit on branch");
            return false;
        }

        head = getCommit(branch, commitId);

        return true;
    }

    public Commit getCommit(String branch, int id) {
        Commit head = branches.get(branch);

        while (id != -1 && head.getId() != id) {
            head = getPrevCommit(head);
        }

        return head;
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

    public List<Commit> path(Commit a, Commit b) {
        if (a.equals(b)) return Collections.emptyList();

        HashSet<Commit> visited = new HashSet<>();

        LinkedList<Commit> parentsA = new LinkedList<>();
        LinkedList<Commit> parentsB = new LinkedList<>();

        Commit lca = null;

        Commit tmpA = a;
        Commit tmpB = b;

        while(lca == null) {
            visited.add(a);
            visited.add(b);

            if (!a.getParents().isEmpty()) {
                a = a.getParents().get(0);
                parentsA.add(a);
                if (visited.contains(a)) lca = a;
            }

            if (!b.getParents().isEmpty()) {
                b = b.getParents().get(0);
                parentsB.add(b);
                if (visited.contains(b)) lca = b;
            }
        }

        a = tmpA;
        b = tmpB;

        List<Commit> pa = new LinkedList<>();
        pa.add(a);
        if (!a.equals(lca)) {
            for (Commit x : parentsA) {
                if (x.equals(lca)) break;
                pa.add(x);
            }
        }

        LinkedList<Commit> pb = new LinkedList<>();
        pb.add(b);
        if (!b.equals(lca)) {
            for (Commit x : parentsB) {
                if (x.equals(lca)) break;
                pb.addFirst(x);
            }
        }

        LinkedList<Commit> path = new LinkedList<>();
        path.addAll(pa);

        if (!a.equals(lca) && !b.equals(lca)) path.add(lca);

        path.addAll(pb);

        return path;
    }
}
