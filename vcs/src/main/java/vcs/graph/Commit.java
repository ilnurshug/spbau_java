package vcs.graph;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Commit implements Serializable {

    private int id;
    private String message;
    private String branch;
    private List<Commit> parents = new LinkedList<>();
    private List<Commit> children = new LinkedList<>();

    public Commit(String message, String branch, List<Commit> parents) {
        this.message = message;
        this.branch = branch;
        this.parents = parents;

        parents.forEach(p -> p.addChild(this));

        Optional<Commit> opt = parents.stream().filter(p -> p.getBranch().equals(branch)).findAny();
        if (opt.isPresent()) {
            id = opt.get().getId() + 1;
        }
        else {
            id = 0;
        }
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getBranch() {
        return branch;
    }

    public List<Commit> getParents() {
        return parents;
    }

    public List<Commit> getChildren() {
        return children;
    }

    public void addChild(Commit child) {
        children.add(child);
    }
}
