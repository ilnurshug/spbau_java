package vcs.commands.impl;

import com.beust.jcommander.Parameter;
import vcs.commands.Command;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;

public class BranchCommand extends Command {

    @Parameter(names = "-a", required = true, description = "Create(1) or delete(0) new branch")
    private int action = 1;

    @Parameter(names = "-b", required = true, description = "choose branch to switch")
    private String branch;

    public BranchCommand() {
    }

    public BranchCommand(int action, String branch) {
        this.action = action;
        this.branch = branch;
    }

    @Override
    public String name() {
        return "branch";
    }

    @Override
    protected void execImpl() {
        if (removeBranch() && GlobalConfig.instance.graph.isContains(branch)) {
            if (GlobalConfig.getCurrentBranch().equals(branch)) {
                System.err.println("can not delete current branch, please checkout to another branch");
            }
            else {
                GlobalConfig.instance.addDeletedBranch(branch);
            }
        }
        else if (createBranch()) {
            GlobalConfig.instance.graph.createBranch(branch);

            try {
                VcsUtils.serialize(GlobalConfig.instance, GlobalConfig.globalConfigFile());

                Command commit = new CommitCommand("new branch " + branch + " was created");
                commit.exec();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean removeBranch() {
        return action == 0;
    }

    private boolean createBranch() {
        return action == 1;
    }
}
