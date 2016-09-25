package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;

@Parameters(commandNames = VcsUtils.BRANCH, commandDescription = "Create or delete selected branch")
public class BranchCommand extends Command {

    @Parameter(names = "-a", description = "Create(1) or delete(0) new branch")
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
    protected void execImpl() {
        if (removeBranch() && GlobalConfig.instance.graph.isContains(branch)) {
            if (GlobalConfig.instance.graph.getHead().getBranch().equals(branch)) {
                System.err.println("can not delete current branch, please checkout to another branch");
            }
            else {
                CommitConfig.instance.addDeletedBranch(branch);
            }
        }
        else if (createBranch()) {
            GlobalConfig.instance.graph.createBranch(branch);

            try {
                VcsUtils.serialize(GlobalConfig.instance, VcsUtils.globalConfigFile());

                Command commit = new CommitCommand(null, "new branch " + branch + " was created");
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
