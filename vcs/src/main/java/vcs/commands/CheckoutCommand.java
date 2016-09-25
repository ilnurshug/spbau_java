package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;

@Parameters(commandNames = VcsUtils.CHECKOUT, commandDescription = "Go to selected commit or branch")
public class CheckoutCommand extends Command {

    @Parameter(names = "-b", required = true, description = "choose branch to switch")
    private String branch;

    @Parameter(names = "-c", required = true, description = "commit id")
    private int id = -1;

    public CheckoutCommand() {}

    public CheckoutCommand(String branch, int id) {
        this.branch = branch;
        this.id = id;
    }

    /**
     * switch to selected branch
     * or to selected commit on specified branch
     */
    @Override
    protected void execImpl() {
        if (!canCheckout(branch, id)) {
            return;
        }

        String source = GlobalConfig.getHeadCommitDir();
        String dest = GlobalConfig.getProjectDir();

        try {
            VcsUtils.deleteFiles(
                    CommitConfig.instance.getSupervisedFiles(),
                    dest
            );

            if (id == -1) {
                smallCheckout(branch);
            }
            else {
                smallCheckout(branch, id);
            }

            VcsUtils.copyFiles(
                    CommitConfig.instance.getSupervisedFiles(),
                    source,
                    dest, true
            );

            serializeConfig();
        } catch (Exception e) {
            /*GlobalConfig.rollback();
            CommitConfig.rollback();*/
            e.printStackTrace();
        }
    }

    private boolean canCheckout(String branch, int commitId) {
        if (CommitConfig.instance.isDeletedBranch(branch)) {
            System.err.println("can not switch to deleted branch");
            return false;
        }
        else {
            if (commitId == -1) {
                return GlobalConfig.instance.graph.checkout(branch);
            }
            else {
                return GlobalConfig.instance.graph.checkout(branch, commitId);
            }
        }
    }

    static void smallCheckout(String branch) throws ClassNotFoundException, IOException {
        GlobalConfig.instance.graph.checkout(branch);
        CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + "config");
    }

    static void smallCheckout(String branch, int commitId) throws ClassNotFoundException, IOException {
        GlobalConfig.instance.graph.checkout(branch, commitId);
        CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + "config");
    }
}